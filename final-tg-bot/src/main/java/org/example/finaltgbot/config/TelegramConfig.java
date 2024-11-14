package org.example.finaltgbot.config;


import org.example.finaltgbot.dto.telegram.TelegramRoot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "telegram-service", url = "https://api.telegram.org/bot7815460716:AAGELiXJ--Z0PxvopYHEcDeYy4Kf07yIsRI")
public interface TelegramConfig {


    @GetMapping("/getUpdates?offset={value}")
    TelegramRoot getUpdates(@PathVariable Integer value);


    @PostMapping("/sendMessage")
    String sendMessage(@RequestParam("chat_id") int chatId, @RequestParam("text") String message);
}
