package com.tradelia.Repository;

import com.tradelia.Model.Conversation;
import com.tradelia.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            WHERE m.conversation = :conversation
            ORDER BY m.created_at ASC
            """)
    List<Message> findByConversationOrderByCreatedAtAsc(@Param("conversation") Conversation conversation);

    @Query("""
            SELECT m FROM Message m
            WHERE m.id IN (
                SELECT MAX(m2.id) FROM Message m2
                WHERE m2.conversation.id IN :conversationIds
                GROUP BY m2.conversation.id
            )
            """)
    List<Message> findLatestByConversationIds(@Param("conversationIds") List<Long> conversationIds);
}
