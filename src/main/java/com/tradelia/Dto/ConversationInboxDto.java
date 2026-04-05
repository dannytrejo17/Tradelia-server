package com.tradelia.Dto;

import com.tradelia.Model.Conversation;
import com.tradelia.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationInboxDto {

    private Long id;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private LocalDateTime last_message_at;
    private String lastMessageContent;
    private Long lastMessageSenderId;
    private String lastMessageSenderUsername;

    public static ConversationInboxDto from(Conversation conversation, Message lastMessage) {
        ConversationInboxDto dto = new ConversationInboxDto();
        dto.setId(conversation.getId());
        dto.setProductId(conversation.getProduct().getId());
        dto.setBuyerId(conversation.getBuyer().getId());
        dto.setSellerId(conversation.getSeller().getId());
        dto.setLast_message_at(conversation.getLast_message_at());
        if (lastMessage != null) {
            dto.setLastMessageContent(lastMessage.getContent());
            dto.setLastMessageSenderId(lastMessage.getSender().getId());
            dto.setLastMessageSenderUsername(lastMessage.getSender().getUsername());
        }
        return dto;
    }
}
