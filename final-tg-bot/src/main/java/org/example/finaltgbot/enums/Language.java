package org.example.finaltgbot.enums;


public enum Language {
    EN("en"),
    FR("fr");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

