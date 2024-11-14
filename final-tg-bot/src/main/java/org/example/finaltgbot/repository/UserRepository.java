package org.example.finaltgbot.repository;

import org.example.finaltgbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByChatId(int chatId);

    User findByChatId(int chatId);
}
