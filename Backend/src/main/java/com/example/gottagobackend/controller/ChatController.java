package com.example.gottagobackend.controller;

import com.example.gottagobackend.dto.ChatRequest;
import com.example.gottagobackend.dto.ChatResponse;
import com.example.gottagobackend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<String> createChat(@Valid @RequestBody ChatRequest request) {
        logger.debug("Получен запрос на создание чата: listingId=" + request.getListingId() + ", user1Id=" + request.getUser1Id() + ", user2Id=" + request.getUser2Id());
        try {
            String chatId = chatService.createChat(request);
            logger.debug("Чат создан или найден с ID: " + chatId);
            return ResponseEntity.ok(chatId);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при создании чата: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Внутренняя ошибка сервера: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка создания чата");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatResponse>> getChatsByUserId(@PathVariable String userId) {
        logger.debug("Получен запрос на получение чатов для пользователя с ID: " + userId);
        try {
            List<ChatResponse> chats = chatService.getChatsByUserId(userId);
            logger.debug("Найдено чатов: " + chats.size());
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            logger.error("Ошибка при получении чатов: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(@PathVariable String chatId) {
        logger.debug("Получен запрос на получение чата с ID: " + chatId);
        try {
            ChatResponse chat = chatService.getChatById(chatId);
            logger.debug("Чат найден с ID: " + chatId);
            return ResponseEntity.ok(chat);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при получении чата: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Внутренняя ошибка при получении чата: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}