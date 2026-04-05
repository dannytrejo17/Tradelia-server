package com.tradelia.Dto;

import com.tradelia.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime created_at;

    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getCreated_at()
        );
    }
}
