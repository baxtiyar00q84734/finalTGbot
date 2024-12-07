package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.response.UserResponseDTO;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.RegistrationStep;
import org.example.finaltgbot.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .toList();
    }

    public User getUserByChatId(int chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
}
