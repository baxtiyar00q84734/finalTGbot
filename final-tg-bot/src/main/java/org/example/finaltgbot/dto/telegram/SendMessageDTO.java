package org.example.finaltgbot.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageDTO {


    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("reply_markup")
    private ReplyKeyboard replyKeyboard;

    @Override
    public String toString() {
        return "SendMessageDTO{" +
                "chatId=" + chatId +
                ", text='" + text + '\'' +
                ", replyKeyboard=" + replyKeyboard +
                '}';
    }
}
