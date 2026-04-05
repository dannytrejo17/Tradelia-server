package com.tradelia.Service;

import com.tradelia.Dto.ConversationInboxDto;
import com.tradelia.Dto.MessageDto;
import com.tradelia.Dto.SendMessageRequest;
import com.tradelia.Dto.SendMessageResponse;
import com.tradelia.Model.Conversation;
import com.tradelia.Model.Message;
import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import com.tradelia.Repository.ConversationRepository;
import com.tradelia.Repository.MessageRepository;
import com.tradelia.Repository.ProductRepository;
import com.tradelia.Repository.UserRepository;
import com.tradelia.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatService(MessageRepository messageRepository,
                       ConversationRepository conversationRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest req, Principal principal) {
        if (!StringUtils.hasText(req.getContent())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El mensaje no puede estar vacío");
        }

        User sender = loadUser(principal);
        Conversation conversation = resolveConversation(req, sender);
        touchConversation(conversation);

        Message message = buildAndSaveMessage(req.getContent().trim(), sender, conversation);
        publishMessage(conversation.getId(), message);

        return SendMessageResponse.from(conversation.getId(), message);
    }

    @Transactional(readOnly = true)
    public List<ConversationInboxDto> getMyConversations(Principal principal) {
        User user = loadUser(principal);
        List<Conversation> conversations = conversationRepository.findByUserIdOrderByLastMessageAtDesc(user.getId());

        if (conversations.isEmpty()) {
            return List.of();
        }

        List<Long> conversationIds = conversations.stream().map(Conversation::getId).toList();
        List<Message> lastMessages = messageRepository.findLatestByConversationIds(conversationIds);

        Map<Long, Message> lastByConversationId = new HashMap<>();
        for (Message message : lastMessages) {
            lastByConversationId.put(message.getConversation().getId(), message);
        }

        List<ConversationInboxDto> inbox = new ArrayList<>();
        for (Conversation conversation : conversations) {
            inbox.add(ConversationInboxDto.from(
                    conversation,
                    lastByConversationId.get(conversation.getId())
            ));
        }
        return inbox;
    }

    @Transactional(readOnly = true)
    public ConversationInboxDto getConversation(Long conversationId, Principal principal) {
        User user = loadUser(principal);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La conversación no existe"));
        assertParticipant(user, conversation);
        return ConversationInboxDto.from(conversation, null);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getConversationHistory(Long conversationId, Principal principal) {
        User user = loadUser(principal);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La conversación no existe"));
        assertParticipant(user, conversation);

        List<MessageDto> history = new ArrayList<>();
        for (Message message : messageRepository.findByConversationOrderByCreatedAtAsc(conversation)) {
            history.add(MessageDto.from(message));
        }
        return history;
    }

    private User loadUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "no autenticado"));
    }

    private Conversation resolveConversation(SendMessageRequest req, User sender) {
        if (req.getConversationId() != null) {
            return loadExistingConversation(req.getConversationId(), sender);
        }
        if (req.getProductId() != null) {
            return findOrCreateByProduct(req.getProductId(), sender);
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Falta productId o conversationId");
    }

    private Conversation loadExistingConversation(Long conversationId, User sender) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La conversación no existe"));
        assertParticipant(sender, conversation);
        return conversation;
    }

    private Conversation findOrCreateByProduct(Long productId, User sender) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no existe"));

        if (sender.getId().equals(product.getSeller().getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El vendedor debe usar conversationId");
        }

        return conversationRepository.findByProductAndBuyer(product, sender)
                .orElseGet(() -> createConversation(product, sender));
    }

    private Conversation createConversation(Product product, User buyer) {
        Conversation conversation = new Conversation();
        conversation.setProduct(product);
        conversation.setBuyer(buyer);
        conversation.setSeller(product.getSeller());
        conversation.setLast_message_at(LocalDateTime.now());
        conversation.setCreated_at(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    private void touchConversation(Conversation conversation) {
        conversation.setLast_message_at(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    private Message buildAndSaveMessage(String content, User sender, Conversation conversation) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(resolveReceiver(sender, conversation));
        message.setContent(content);
        message.setCreated_at(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private User resolveReceiver(User sender, Conversation conversation) {
        if (sender.getId().equals(conversation.getSeller().getId())) {
            return conversation.getBuyer();
        }
        if (sender.getId().equals(conversation.getBuyer().getId())) {
            return conversation.getSeller();
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "No eres participante de esta conversación");
    }

    private void assertParticipant(User user, Conversation conversation) {
        if (!user.getId().equals(conversation.getBuyer().getId())
                && !user.getId().equals(conversation.getSeller().getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No eres participante de esta conversación");
        }
    }

    private void publishMessage(Long conversationId, Message message) {
        simpMessagingTemplate.convertAndSend(
                "/topic/conversation." + conversationId,
                message
        );
    }
}
