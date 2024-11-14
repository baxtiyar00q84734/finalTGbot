package org.example.finaltgbot.dto.telegram.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramSendDTO {

    @JsonProperty("chat_id")
    private int chatId;
    private String text;

//    @JsonProperty("reply_markup")
//    private ReplyMarkup replyMarkup;
//    public void setReplyMarkup(ReplyKeyboardMarkupDTO replyMarkup) {
//    }
}
