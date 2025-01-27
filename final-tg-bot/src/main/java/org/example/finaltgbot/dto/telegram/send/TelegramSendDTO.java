package org.example.finaltgbot.dto.telegram.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.finaltgbot.dto.telegram.ReplyKeyboard;
import org.example.finaltgbot.dto.telegram.ReplyKeyboardMarkupDTO;

@Getter
@Setter
public class TelegramSendDTO {

    @JsonProperty("chat_id")
    private Long chatId;
    private String text;
    private ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO;
}
