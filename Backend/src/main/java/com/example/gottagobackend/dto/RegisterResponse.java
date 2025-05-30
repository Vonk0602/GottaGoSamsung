package com.example.gottagobackend.dto;

public class RegisterResponse {
    private String userId;
    private String error;

    public RegisterResponse(String userId, String error) {
        this.userId = userId;
        this.error = error;
    }

    public String getUserId() {
        return userId;
    }

    public String getError() {
        return error;
    }
}