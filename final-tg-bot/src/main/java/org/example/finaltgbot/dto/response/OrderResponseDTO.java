package org.example.finaltgbot.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.finaltgbot.enums.OrderStep;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private double totalPrice;
    private OrderStep orderStep;
    private LocalDateTime orderDate;
}
