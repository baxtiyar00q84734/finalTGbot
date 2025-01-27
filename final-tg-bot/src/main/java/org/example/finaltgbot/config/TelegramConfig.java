package org.example.finaltgbot.config;


import org.example.finaltgbot.dto.telegram.SendMessageDTO;
import org.example.finaltgbot.dto.telegram.TelegramRoot;
import org.example.finaltgbot.dto.telegram.send.TelegramSendDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "telegram-service", url = "https://api.telegram.org/bot7815460716:AAGELiXJ--Z0PxvopYHEcDeYy4Kf07yIsRI")
public interface TelegramConfig {


    @GetMapping("/getUpdates?offset={value}")
    TelegramRoot getUpdates(@PathVariable Long value);


    @PostMapping("/sendMessage")
    String sendSimpleMessage(@RequestParam("chat_id") Long chatId, @RequestParam("text") String message);

    @PostMapping("/sendMessage")
    void sendStructuredMessage(@RequestBody SendMessageDTO sendMessageDTO);
}
