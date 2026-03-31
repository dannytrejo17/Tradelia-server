package com.tradelia.Dto;

import com.tradelia.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageResponse {

    private Long conversationId;
    private Long messageId;
    private String content;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime created_at;

    public static SendMessageResponse from(Long conversationId, Message message) {
        return new SendMessageResponse(
                conversationId,
                message.getId(),
                message.getContent(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getCreated_at()
        );
    }
}
