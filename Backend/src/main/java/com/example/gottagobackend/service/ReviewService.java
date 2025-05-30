package com.example.gottagobackend.service;

import com.example.gottagobackend.dto.ReviewRequest;
import com.example.gottagobackend.entity.Review;
import com.example.gottagobackend.repository.ProfileRepository;
import com.example.gottagobackend.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public Review createReview(ReviewRequest request) {
        logger.debug("Создание отзыва от reviewerId: {} для userId: {}", request.getReviewerId(), request.getUserId());

        if (!profileRepository.existsById(request.getReviewerId())) {
            logger.error("Профиль рецензента с ID {} не найден", request.getReviewerId());
            throw new IllegalArgumentException("Рецензент с ID " + request.getReviewerId() + " не существует");
        }
        if (!profileRepository.existsById(request.getUserId())) {
            logger.error("Профиль пользователя с ID {} не найден", request.getUserId());
            throw new IllegalArgumentException("Пользователь с ID " + request.getUserId() + " не существует");
        }

        if (request.getReviewerId().equals(request.getUserId())) {
            logger.error("Пользователь не может оставить отзыв о себе: {}", request.getReviewerId());
            throw new IllegalArgumentException("Пользователь не может оставить отзыв о себе");
        }

        if (reviewRepository.existsByReviewerIdAndUserId(request.getReviewerId(), request.getUserId())) {
            logger.error("Отзыв от reviewerId: {} для userId: {} уже существует", request.getReviewerId(), request.getUserId());
            throw new IllegalArgumentException("Вы уже оставили отзыв об этом пользователе");
        }

        Review review = new Review(
                request.getReviewerId(),
                request.getUserId(),
                request.getText(),
                request.getRating()
        );

        Review savedReview = reviewRepository.save(review);
        logger.info("Отзыв создан с ID: {}", savedReview.getId());
        return savedReview;
    }

    public List<Review> getReviewsByUserId(String userId) {
        logger.debug("Получение отзывов для userId: {}", userId);
        if (!profileRepository.existsById(userId)) {
            logger.error("Профиль пользователя с ID {} не найден", userId);
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        List<Review> reviews = reviewRepository.findByUserId(userId);
        logger.info("Получено {} отзывов для userId: {}", reviews.size(), userId);
        return reviews;
    }

    public Double getAverageRatingByUserId(String userId) {
        logger.debug("Расчет среднего рейтинга для userId: {}", userId);
        if (!profileRepository.existsById(userId)) {
            logger.error("Профиль пользователя с ID {} не найден", userId);
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        Double averageRating = reviewRepository.findAverageRatingByUserId(userId);
        logger.debug("Исходный средний рейтинг для userId {}: {}", userId, averageRating);
        Double result = (averageRating != null) ? Math.round(averageRating * 100.0) / 100.0 : 0.0;
        logger.info("Обработанный средний рейтинг для userId {}: {}", userId, result);
        return result;
    }
}