package com.example.gottagobackend.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

    @NotBlank(message = "Идентификатор объявления обязателен")
    private String listingId;

    @NotBlank(message = "Идентификатор первого пользователя обязателен")
    private String user1Id;

    @NotBlank(message = "Идентификатор второго пользователя обязателен")
    private String user2Id;

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }
}