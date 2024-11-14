package org.example.finaltgbot.dto.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class From {
    public int id;
    public boolean is_bot;
    public String first_name;
    public String last_name;
    public String language_code;
}
