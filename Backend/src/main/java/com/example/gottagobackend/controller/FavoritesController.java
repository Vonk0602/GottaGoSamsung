package com.example.gottagobackend.controller;

import com.example.gottagobackend.entity.Listing;
import com.example.gottagobackend.service.FavoritesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesController.class);

    @Autowired
    private FavoritesService favoritesService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(@RequestParam String userId, @RequestParam String listingId) {
        logger.debug("Проверка избранного для userId: {}, listingId: {}", userId, listingId);
        try {
            boolean isFavorite = favoritesService.isFavorite(userId, listingId);
            logger.debug("Результат проверки избранного: {}", isFavorite);
            return ResponseEntity.ok(isFavorite);
        } catch (Exception e) {
            logger.error("Ошибка при проверке избранного: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestParam String userId, @RequestParam String listingId) {
        logger.debug("Добавление в избранное для userId: {}, listingId: {}", userId, listingId);
        try {
            favoritesService.addFavorite(userId, listingId);
            logger.debug("Объявление добавлено в избранное");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка добавления в избранное: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Ошибка при добавлении в избранное: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam String userId, @RequestParam String listingId) {
        logger.debug("Удаление из избранного для userId: {}, listingId: {}", userId, listingId);
        try {
            favoritesService.removeFavorite(userId, listingId);
            logger.debug("Объявление удалено из избранного");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка удаления из избранного: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении из избранного: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Listing>> getFavoritesByUserId(@PathVariable String userId) {
        logger.debug("Получение избранных объявлений для userId: {}", userId);
        try {
            List<Listing> favorites = favoritesService.getFavoritesByUserId(userId);
            logger.debug("Получено {} избранных объявлений", favorites.size());
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            logger.error("Ошибка при получении избранных объявлений: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}/filtered")
    public ResponseEntity<List<Listing>> getFilteredFavoritesByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String availableFrom,
            @RequestParam(required = false) String availableTo) {
        logger.debug("Получение отфильтрованных избранных объявлений для userId: {}, search: {}, city: {}, capacity: {}, availableFrom: {}, availableTo: {}",
                userId, search, city, capacity, availableFrom, availableTo);
        try {
            List<Listing> favorites = favoritesService.getFilteredFavoritesByUserId(userId, search, city, capacity, availableFrom, availableTo);
            logger.debug("Получено {} отфильтрованных избранных объявлений", favorites.size());
            return ResponseEntity.ok(favorites);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при получении отфильтрованных избранных объявлений: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Ошибка при получении отфильтрованных избранных объявлений: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}