package com.example.gottagobackend.service;

import com.example.gottagobackend.dto.ChatRequest;
import com.example.gottagobackend.dto.ChatResponse;
import com.example.gottagobackend.entity.Chat;
import com.example.gottagobackend.entity.Message;
import com.example.gottagobackend.entity.Profile;
import com.example.gottagobackend.repository.ChatRepository;
import com.example.gottagobackend.repository.ListingRepository;
import com.example.gottagobackend.repository.MessageRepository;
import com.example.gottagobackend.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MessageRepository messageRepository;

    public String createChat(ChatRequest request) {
        logger.debug("Создание чата для listingId: " + request.getListingId() + ", user1Id: " + request.getUser1Id() + ", user2Id: " + request.getUser2Id());

        if (!listingRepository.existsById(request.getListingId())) {
            logger.error("Объявление с ID " + request.getListingId() + " не найдено");
            throw new IllegalArgumentException("Объявление не найдено");
        }

        if (!profileRepository.existsById(request.getUser1Id()) || !profileRepository.existsById(request.getUser2Id())) {
            logger.error("Один из пользователей не найден: user1Id: " + request.getUser1Id() + ", user2Id: " + request.getUser2Id());
            throw new IllegalArgumentException("Пользователь не найден");
        }

        List<Chat> existingChats = chatRepository.findByListingIdAndUsers(request.getListingId(), request.getUser1Id(), request.getUser2Id());
        if (!existingChats.isEmpty()) {
            String existingChatId = existingChats.get(0).getChatId();
            logger.debug("Чат уже существует с ID: " + existingChatId + ", user1Id: " + existingChats.get(0).getUser1Id() + ", user2Id: " + existingChats.get(0).getUser2Id());
            return existingChatId;
        }

        Chat chat = new Chat();
        chat.setChatId(UUID.randomUUID().toString());
        chat.setListingId(request.getListingId());
        chat.setUser1Id(request.getUser1Id());
        chat.setUser2Id(request.getUser2Id());
        chat.setCreatedAt(new Date());

        chatRepository.save(chat);
        logger.debug("Чат создан с ID: " + chat.getChatId() + ", user1Id: " + chat.getUser1Id() + ", user2Id: " + chat.getUser2Id());
        return chat.getChatId();
    }

    public List<ChatResponse> getChatsByUserId(String userId) {
        logger.debug("Получение чатов для пользователя с ID: " + userId);
        List<Chat> chats = chatRepository.findByUser1IdOrUser2Id(userId, userId);
        return chats.stream().map(chat -> mapToChatResponse(chat, userId)).collect(Collectors.toList());
    }

    public ChatResponse getChatById(String chatId) {
        logger.debug("Получение чата с ID: " + chatId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> {
                    logger.error("Чат с ID " + chatId + " не найден");
                    return new IllegalArgumentException("Чат не найден");
                });
        return mapToChatResponse(chat, null);
    }

    private ChatResponse mapToChatResponse(Chat chat, String currentUserId) {
        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setListingId(chat.getListingId());
        response.setUser1Id(chat.getUser1Id());
        response.setUser2Id(chat.getUser2Id());
        response.setCreatedAt(chat.getCreatedAt());

        String otherUserId = currentUserId != null && currentUserId.equals(chat.getUser1Id()) ? chat.getUser2Id() : chat.getUser1Id();
        Profile otherProfile = profileRepository.findById(otherUserId).orElse(null);
        response.setOtherUserName(otherProfile != null && otherProfile.getName() != null ? otherProfile.getName() : "Неизвестный пользователь");

        Message lastMessage = messageRepository.findTopByChatIdOrderBySentAtDesc(chat.getChatId()).orElse(null);
        response.setLastMessage(lastMessage != null && lastMessage.getContent() != null ? lastMessage.getContent() : chat.getLastMessage());

        if (currentUserId != null) {
            long unreadCount = messageRepository.countByChatIdAndSenderIdNotAndStatusNot(chat.getChatId(), currentUserId, "READ");
            response.setUnreadCount((int) unreadCount);
        }

        return response;
    }
}