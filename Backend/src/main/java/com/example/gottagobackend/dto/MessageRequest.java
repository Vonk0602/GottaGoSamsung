package com.example.gottagobackend.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {

    @NotBlank(message = "Идентификатор чата обязателен")
    private String chatId;

    @NotBlank(message = "Идентификатор отправителя обязателен")
    private String senderId;

    @NotBlank(message = "Содержимое сообщения обязательно")
    private String content;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}