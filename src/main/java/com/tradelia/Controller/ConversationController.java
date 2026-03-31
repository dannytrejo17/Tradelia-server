package com.tradelia.Controller;

import com.tradelia.Dto.SendMessageRequest;
import com.tradelia.Dto.SendMessageResponse;
import com.tradelia.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ChatService chatService;

    public ConversationController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @RequestBody SendMessageRequest request,
            Principal principal) {
        return ResponseEntity.ok(chatService.sendMessage(request, principal));
    }
}
