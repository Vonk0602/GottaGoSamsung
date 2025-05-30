package com.example.gottagobackend.dto;

import jakarta.validation.constraints.NotBlank;

public class CompleteProfileRequest {
    @NotBlank(message = "ID пользователя не может быть пустым")
    private String userId;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    private String description;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}