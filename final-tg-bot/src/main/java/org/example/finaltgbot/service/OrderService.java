package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.response.OrderResponseDTO;
import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.OrderStatus;
import org.example.finaltgbot.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public Order getCurrentOrderForUser(User user) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUser(user);
                    newOrder.setStatus(OrderStatus.PENDING);
                    return orderRepository.save(newOrder);
                });
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public Optional<Order> getCurrentOrderForUsers(User user) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);
    }

    public boolean deleteOrderByUserId(Long userId) {
        Optional<Order> order = orderRepository.findByUserAndStatus(userService.getUserById(userId), OrderStatus.ACTIVE);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
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

}
