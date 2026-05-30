package com.tradelia.Controller;

import com.tradelia.Dto.ConversationInboxDto;
import com.tradelia.Dto.MessageDto;
import com.tradelia.Dto.SendMessageRequest;
import com.tradelia.Dto.SendMessageResponse;
import com.tradelia.Service.ChatService;
import com.tradelia.Service.DemoModeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ChatService chatService;
    private final DemoModeService demoModeService;

    public ConversationController(ChatService chatService, DemoModeService demoModeService) {
        this.chatService = chatService;
        this.demoModeService = demoModeService;
    }

    @PostMapping("/messages")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @RequestBody SendMessageRequest request,
            Principal principal) {
        demoModeService.ensureWriteAllowed();
        return ResponseEntity.ok(chatService.sendMessage(request, principal));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ConversationInboxDto>> getMyConversations(Principal principal) {
        return ResponseEntity.ok(chatService.getMyConversations(principal));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationInboxDto> getOne(
            @PathVariable Long conversationId,
            Principal principal) {
        return ResponseEntity.ok(chatService.getConversation(conversationId, principal));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getHistory(
            @PathVariable Long conversationId,
            Principal principal) {
        return ResponseEntity.ok(chatService.getConversationHistory(conversationId, principal));
    }
}
