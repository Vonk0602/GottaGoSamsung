package com.example.gottagobackend.dto;

public class DeleteRequest {
    private String userId;
    public DeleteRequest(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}