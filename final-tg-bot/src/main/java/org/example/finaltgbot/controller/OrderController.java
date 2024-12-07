package org.example.finaltgbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.response.OrderResponseDTO;
import org.example.finaltgbot.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    public final OrderService orderService;

    @GetMapping()
    public List<OrderResponseDTO> getAllOrders(){
        return orderService.getAllOrders();
    }
}
