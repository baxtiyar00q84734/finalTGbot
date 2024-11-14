package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.RegistrationStep;
import org.example.finaltgbot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByChatId(int chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User startRegistration(int chatId) {
        User user = new User();
        user.setChatId(chatId);
        user.setRegistrationStep(RegistrationStep.ASK_NAME);
        return userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
