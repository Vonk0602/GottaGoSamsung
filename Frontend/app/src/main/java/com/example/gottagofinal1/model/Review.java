package com.example.gottagofinal1.model;

import com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review {
    private Long id;
    private String reviewerId;
    private String userId;
    private String text;
    private double rating;
    @SerializedName("createdAt")
    private String createdAt;
    private long timestamp;

    public Review() {}

    public Review(Long id, String reviewerId, String userId, String text, double rating, String createdAt) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.userId = userId;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
        this.timestamp = parseTimestamp(createdAt);
    }

    public Review(Long id, String reviewerId, String userId, String text, double rating, long timestamp) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.userId = userId;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;
        this.createdAt = formatTimestamp(timestamp);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUserId() {
        return reviewerId;
    }

    public void setFromUserId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        this.timestamp = parseTimestamp(createdAt);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.createdAt = formatTimestamp(timestamp);
    }

    private long parseTimestamp(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            return System.currentTimeMillis();
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(createdAt);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            try {
                SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = isoSdf.parse(createdAt);
                return date != null ? date.getTime() : System.currentTimeMillis();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return System.currentTimeMillis();
            }
        }
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp <= 0) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
} //