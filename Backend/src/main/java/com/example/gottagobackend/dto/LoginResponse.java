package com.example.gottagobackend.dto;

public class LoginResponse {
    private String userId;
    private String name;
    private String description;
    private String avatarUrl;

    public LoginResponse(String userId, String name, String description, String avatarUrl) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
    }

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}