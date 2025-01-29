package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.response.OrderResponseDTO;
import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.OrderStatus;
import org.example.finaltgbot.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public List<Order> getCurrentOrderForUser(User user) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);
    }

    public double calculateOrderPrice(Order order) {
        return order.calculateTotalPrice();
    }

    public void delete(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Order not found with ID: " + id);
        }
    }

    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
    }

    public boolean deleteOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserAndStatusNot(userService.getUserById(userId), OrderStatus.COMPLETED);
        if (!orders.isEmpty()) {
            orderRepository.deleteAll(orders);
            return true;
        }
        return false;
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .toList();
    }

    public List<Order> getActiveOrdersByUser(User user) {
        return orderRepository.findByUserAndStatusNot(user, OrderStatus.COMPLETED);
    }
}