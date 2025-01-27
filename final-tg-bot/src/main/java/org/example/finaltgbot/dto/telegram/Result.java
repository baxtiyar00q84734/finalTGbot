package org.example.finaltgbot.dto.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    public Long update_id;
    public Message message;
}
