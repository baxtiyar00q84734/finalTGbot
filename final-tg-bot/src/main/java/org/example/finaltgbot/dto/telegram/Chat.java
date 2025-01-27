package org.example.finaltgbot.dto.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
    public Long id;
    public String first_name;
    public String last_name;
    public String type;
}
