package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.UserRequestDTO;
import org.example.finaltgbot.dto.response.UserResponseDTO;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.RegistrationStep;
import org.example.finaltgbot.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User  not found with ID: " + id));
        modelMapper.map(userRequestDTO, user);
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElse(null);
    }

    public User startRegistration(Long chatId) {
        User newUser = new User();
        newUser.setChatId(chatId);
        newUser.setRegistrationStep(RegistrationStep.ASK_NAME);
        newUser.setActive(true);
        return userRepository.save(newUser);
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

    public User getUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElse(null);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    public boolean isUserEligibleForOrder(Long chatId) {
        User user = findByChatId(chatId);
        if (user != null && user.getRegistrationStep() == RegistrationStep.COMPLETED) {
            return true;
        }
        return false;
    }


    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setActive(true);
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUserByEmail(String email) {
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        user.setActive(true);
        userRepository.save(user);
    }
}
