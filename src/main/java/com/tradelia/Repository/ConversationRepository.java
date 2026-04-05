package com.tradelia.Repository;

import com.tradelia.Model.Conversation;
import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByProductAndBuyer(Product product, User buyer);

    @Query("""
            SELECT c FROM Conversation c
            WHERE c.buyer.id = :userId OR c.seller.id = :userId
            ORDER BY c.last_message_at DESC
            """)
    List<Conversation> findByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
}
