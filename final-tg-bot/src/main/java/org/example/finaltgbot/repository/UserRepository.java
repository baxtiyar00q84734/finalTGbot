package org.example.finaltgbot.repository;

import org.example.finaltgbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByChatId(int chatId);

    @Query("SELECT u FROM User u WHERE u.chatId = :chatId")
    Optional<User> findByChatId(@Param("chatId") int chatId);


    @Modifying
    @Query("UPDATE User u SET u.active = true WHERE u.active = false")
    void activateAllUsers();

    Optional<Object> findByEmail(String email);
}
