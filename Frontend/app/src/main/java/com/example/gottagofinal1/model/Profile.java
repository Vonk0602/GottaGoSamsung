package com.example.gottagofinal1.model;

public class Profile {
    private String id;
    private String name;
    private String description;
    private String avatarUrl;

    public Profile(String id, String name, String description, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}