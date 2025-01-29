package org.example.finaltgbot.repository;


import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    List<Order> findByUserAndStatusNot(User user, OrderStatus status);
}