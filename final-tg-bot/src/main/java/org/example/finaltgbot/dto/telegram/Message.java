package org.example.finaltgbot.dto.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    public Long message_id;
    public From from;
    public Chat chat;
    public int date;
    public String text;
}


