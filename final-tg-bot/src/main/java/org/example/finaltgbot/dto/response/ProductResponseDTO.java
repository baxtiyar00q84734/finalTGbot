package org.example.finaltgbot.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private double price;
    private boolean inStorage;
}
