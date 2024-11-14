package org.example.finaltgbot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    private Long userId;
    private Long productId;
    private int quantity;

}
