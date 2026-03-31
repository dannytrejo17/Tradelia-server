package com.tradelia.Repository;

import com.tradelia.Model.Conversation;
import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByProductAndBuyer(Product product, User buyer);
}
