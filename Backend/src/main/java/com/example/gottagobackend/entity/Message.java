package com.example.gottagobackend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "sent_at")
    private Date sentAt;

    @Column(name = "status")
    private String status;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}