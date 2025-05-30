package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findByChatIdOrderBySentAtAsc(String chatId);

    Optional<Message> findTopByChatIdOrderBySentAtDesc(String chatId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatId = :chatId AND m.senderId != :senderId AND m.status != :status")
    long countByChatIdAndSenderIdNotAndStatusNot(@Param("chatId") String chatId, @Param("senderId") String senderId, @Param("status") String status);
}