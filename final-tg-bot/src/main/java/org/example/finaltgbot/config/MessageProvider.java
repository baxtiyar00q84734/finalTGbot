package org.example.finaltgbot.config;


import org.example.finaltgbot.enums.Language;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageProvider {

    private final MessageSource messageSource;

    public MessageProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Language language) {
        Locale locale = new Locale(language.getCode()); // `language.getCode()` should return language code like "en", "fr", etc.
        return messageSource.getMessage(key, null, locale);
    }
}

