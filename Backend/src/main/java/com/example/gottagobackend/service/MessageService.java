package com.example.gottagobackend.service;

import com.example.gottagobackend.dto.MessageRequest;
import com.example.gottagobackend.dto.MessageResponse;
import com.example.gottagobackend.entity.Chat;
import com.example.gottagobackend.entity.Message;
import com.example.gottagobackend.repository.ChatRepository;
import com.example.gottagobackend.repository.MessageRepository;
import com.example.gottagobackend.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Transactional
    public String sendMessage(MessageRequest request) {
        logger.debug("Отправка сообщения в чат с ID: " + request.getChatId() + ", от пользователя: " + request.getSenderId() + ", содержимое: " + request.getContent());

        Chat chat = chatRepository.findById(request.getChatId()).orElse(null);
        if (chat == null) {
            logger.error("Чат с ID " + request.getChatId() + " не найден");
            throw new IllegalArgumentException("Чат не найден");
        }

        if (!profileRepository.existsById(request.getSenderId())) {
            logger.error("Пользователь с ID " + request.getSenderId() + " не найден");
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setChatId(request.getChatId());
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setSentAt(new Date());
        message.setStatus("SENT");

        messageRepository.save(message);
        logger.debug("Сообщение сохранено с ID: " + message.getMessageId() + " в чат: " + request.getChatId());

        chat.setLastMessage(request.getContent());
        chatRepository.save(chat);
        logger.debug("Обновлено последнее сообщение в чате с ID: " + chat.getChatId());

        return message.getMessageId();
    }

    public List<MessageResponse> getMessagesByChatId(String chatId) {
        logger.debug("Получение сообщений для чата с ID: " + chatId);
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            logger.error("Чат с ID " + chatId + " не найден");
            throw new IllegalArgumentException("Чат не найден");
        }
        List<Message> messages = messageRepository.findByChatIdOrderBySentAtAsc(chatId);
        logger.debug("Найдено сообщений для чата " + chatId + ": " + messages.size());
        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessageAsRead(String messageId) {
        logger.debug("Пометка сообщения с ID " + messageId + " как прочитанного");
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && !message.getStatus().equals("READ")) {
            message.setStatus("READ");
            messageRepository.save(message);
            logger.debug("Сообщение с ID " + messageId + " помечено как прочитанное");
        } else {
            logger.warn("Сообщение с ID " + messageId + " не найдено или уже прочитано");
        }
    }

    @Transactional
    public void markMessageAsDelivered(String messageId) {
        logger.debug("Пометка сообщения с ID " + messageId + " как доставленного");
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && message.getStatus().equals("SENT")) {
            message.setStatus("DELIVERED");
            messageRepository.save(message);
            logger.debug("Сообщение с ID " + messageId + " помечено как доставленное");
        } else {
            logger.warn("Сообщение с ID " + messageId + " не найдено или не в статусе SENT");
        }
    }

    public Message getMessageById(String messageId) {
        logger.debug("Получение сообщения с ID: " + messageId);
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            logger.warn("Сообщение с ID: " + messageId + " не найдено");
        }
        return message;
    }

    private MessageResponse mapToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setChatId(message.getChatId());
        response.setSenderId(message.getSenderId());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        response.setStatus(message.getStatus());
        return response;
    }
}