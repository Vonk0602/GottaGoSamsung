package com.example.gottagobackend.controller;

import com.example.gottagobackend.dto.MessageRequest;
import com.example.gottagobackend.dto.MessageResponse;
import com.example.gottagobackend.entity.Chat;
import com.example.gottagobackend.entity.Message;
import com.example.gottagobackend.repository.ChatRepository;
import com.example.gottagobackend.service.MessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRepository chatRepository;

    @PostMapping
    public ResponseEntity<String> sendMessage(@Valid @RequestBody MessageRequest request, @RequestHeader("X-User-Id") String userId) {
        logger.debug("Получен запрос на отправку сообщения в чат с ID: " + request.getChatId() + ", userId: " + userId);
        if (!isUserInChat(userId, request.getChatId())) {
            logger.error("Пользователь с ID: " + userId + "не имеет доступа к чату: " + request.getChatId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещён");
        }
        try {
            String messageId = messageService.sendMessage(request);
            logger.debug("Сообщение отправлено с ID: " + messageId);
            return ResponseEntity.status(HttpStatus.CREATED).body(messageId);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при отправке сообщения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Внутреняя ошибка при отправке сообщения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка отправки сообщения");
        }
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByChatId(@PathVariable String chatId, @RequestHeader("X-User-Id") String userId) {
        logger.debug("Получен запрос на получение сообщений для чата с ID: " + chatId + ", userId: " + userId);
        if (!isUserInChat(userId, chatId)) {
            logger.error("Пользователь с ID " + userId + "не имеет доступа к чату: " + chatId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            List<MessageResponse> messages = messageService.getMessagesByChatId(chatId);
            logger.debug("Найдено сообщений: " + messages.size());
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при получении сообщений: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Внутреняя ошибка при получении сообщений: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable String messageId, @RequestHeader("X-User-Id") String userId) {
        logger.debug("Получен запрос на пометку сообщения с ID: " + messageId + ", userId: " + userId);
        Message message = messageService.getMessageById(messageId);
        if (message == null || !isUserInChat(userId, message.getChatId())) {
            logger.error("Пользователь с ID: " + userId + "не имеет доступа к сообщению: " + messageId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            messageService.markMessageAsRead(messageId);
            logger.debug("Сообщение с ID: " + messageId + " успешно помечено как прочитанное");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Ошибка при пометке сообщения как прочитанного: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/delivered/{messageId}")
    public ResponseEntity<Void> markMessageAsDelivered(@PathVariable String messageId, @RequestHeader("X-User-Id") String userId) {
        logger.debug("Получен запрос на пометку сообщения с ID: " + messageId + ", userId: " + userId);
        Message message = messageService.getMessageById(messageId);
        if (message == null || !isUserInChat(userId, message.getChatId())) {
            logger.error("Пользователь с ID: " + userId + "не имеет доступа к сообщению: " + messageId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            messageService.markMessageAsDelivered(messageId);
            logger.debug("Сообщение с ID: " + messageId + " успешно завершено как доставленное");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Ошибка при доставке сообщения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isUserInChat(String userId, String chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            logger.error("Чат с ID: " + chatId + " не найден");
            return false;
        }
        boolean isInChat = chat.getUser1Id().equals(userId) || chat.getUser2Id().equals(userId);
        logger.debug("Пользователь с ID: " + userId + " имеет доступ к чату: " + chatId + ": " + isInChat);
        return isInChat;
    }
}