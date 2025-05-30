package com.example.gottagobackend.controller;

import com.example.gottagobackend.dto.ReviewRequest;
import com.example.gottagobackend.entity.Review;
import com.example.gottagobackend.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> createReview(@Valid @RequestBody ReviewRequest request) {
        logger.debug("Получен запрос на создание отзыва: reviewerId={}, userId={}", request.getReviewerId(), request.getUserId());
        try {
            reviewService.createReview(request);
            logger.info("Отзыв успешно создан для пользователя с идентификатором: {}", request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при создании отзыва: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при создании отзыва: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable String userId) {
        logger.debug("Получен запрос на получение отзывов для пользователя с идентификатором: {}", userId);
        try {
            List<Review> reviews = reviewService.getReviewsByUserId(userId);
            logger.info("Получено {} отзывов для пользователя с идентификатором: {}", reviews.size(), userId);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при получении отзывов: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при получении отзывов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable String userId) {
        logger.debug("Получен запрос на получение среднего рейтинга для пользователя с идентификатором: {}", userId);
        try {
            Double averageRating = reviewService.getAverageRatingByUserId(userId);
            logger.info("Средний рейтинг для пользователя с идентификатором {}: {}", userId, averageRating);
            return ResponseEntity.ok(averageRating);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при получении среднего рейтинга: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при получении среднего рейтинга: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}