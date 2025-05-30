package com.example.gottagobackend.dto;

import jakarta.validation.constraints.*;

public class ReviewRequest {

    @NotBlank(message = "Идентификатор рецензента обязателен")
    private String reviewerId;

    @NotBlank(message = "Идентификатор пользователя обязателен")
    private String userId;

    @NotBlank(message = "Текст отзыва обязателен")
    @Size(max = 500, message = "Текст отзыва не должен превышать 500 символов")
    private String text;

    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 0, message = "Рейтинг должен быть не менее 0")
    @Max(value = 5, message = "Рейтинг не должен превышать 5")
    private Double rating;

    public ReviewRequest() {
    }

    public ReviewRequest(String reviewerId, String userId, String text, Double rating) {
        this.reviewerId = reviewerId;
        this.userId = userId;
        this.text = text;
        this.rating = rating;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}