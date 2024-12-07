package org.example.finaltgbot.repository;


import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);
}