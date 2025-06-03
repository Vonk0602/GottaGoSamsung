package com.example.gottagofinal1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Chat {
    private String chatId;
    private String listingId;
    private String user1Id;
    private String user2Id;
    private String otherUserName;
    private String lastMessage;
    private int unreadCount;
    private Date createdAt;

    @JsonProperty("chatId")
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @JsonProperty("listingId")
    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    @JsonProperty("user1Id")
    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    @JsonProperty("user2Id")
    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    @JsonProperty("otherUserName")
    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    @JsonProperty("lastMessage")
    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @JsonProperty("unreadCount")
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @JsonProperty("createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}