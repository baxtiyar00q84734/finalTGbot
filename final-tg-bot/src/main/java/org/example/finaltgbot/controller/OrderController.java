package org.example.finaltgbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.response.OrderResponseDTO;
import org.example.finaltgbot.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    public final OrderService orderService;

    @GetMapping()
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.ok("Order deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteOrdersByUserId(@PathVariable Long userId) {
        boolean deleted = orderService.deleteOrderByUserId(userId);
        if (deleted) {
            return ResponseEntity.ok("All active orders deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("No active orders found for user ID: " + userId);
        }
    }
}
