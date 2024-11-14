package org.example.finaltgbot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {
    private String name;
    private double price;
    private boolean inStorage;

}
