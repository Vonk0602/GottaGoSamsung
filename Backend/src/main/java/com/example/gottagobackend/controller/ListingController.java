package com.example.gottagobackend.controller;

import com.example.gottagobackend.dto.ListingRequest;
import com.example.gottagobackend.entity.Listing;
import com.example.gottagobackend.service.ListingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    @Autowired
    private ListingService listingService;

    @PostMapping
    public ResponseEntity<String> createListing(@Valid @RequestBody ListingRequest request) {
        logger.debug("Получен запрос на создание объявления: {}", request);
        try {
            String listingId = listingService.createListing(request);
            logger.debug("Объявление создано с идентификатором: {}", listingId);
            return ResponseEntity.status(HttpStatus.CREATED).body(listingId);
        } catch (Exception e) {
            logger.error("Ошибка при создании объявления: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при создании объявления");
        }
    }

    @GetMapping
    public ResponseEntity<List<Listing>> getAllListings() {
        logger.debug("Обработка запроса на получение всех объявлений");
        try {
            List<Listing> listings = listingService.getAllListings();
            logger.debug("Получено {} объявлений", listings.size());
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            logger.error("Ошибка при получении объявлений: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Listing>> getFilteredListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String availableFrom,
            @RequestParam(required = false) String availableTo) {
        logger.debug("Обработка запроса на получение отфильтрованных объявлений: search={}, city={}, capacity={}, availableFrom={}, availableTo={}",
                search, city, capacity, availableFrom, availableTo);
        try {
            List<Listing> listings = listingService.getFilteredListings(search, city, capacity, availableFrom, availableTo);
            logger.debug("Получено {} объявлений", listings.size());
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            logger.error("Ошибка при получении отфильтрованных объявлений: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Listing>> getListingsByUserId(@PathVariable String userId) {
        logger.debug("Обработка запроса на получение объявлений для пользователя с идентификатором: {}", userId);
        try {
            List<Listing> listings = listingService.getListingsByUserId(userId);
            logger.debug("Получено {} объявлений для пользователя с идентификатором: {}", listings.size(), userId);
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            logger.error("Ошибка при получении объявлений для пользователя с идентификатором {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<Listing> getListingById(@PathVariable String listingId) {
        logger.debug("Обработка запроса на получение объявления с идентификатором: {}", listingId);
        try {
            Listing listing = listingService.getListingById(listingId);
            logger.debug("Получено объявление: {}", listing.getTitle());
            return ResponseEntity.ok(listing);
        } catch (IllegalArgumentException e) {
            logger.error("Объявление не найдено для идентификатора {}: {}", listingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Ошибка при получении объявления с идентификатором {}: {}", listingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{listingId}")
    public ResponseEntity<Void> updateListing(@PathVariable String listingId, @Valid @RequestBody ListingRequest request) {
        logger.debug("Получен запрос на обновление объявления с идентификатором: {}", listingId);
        try {
            listingService.updateListing(listingId, request);
            logger.debug("Объявление с идентификатором {} успешно обновлено", listingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при обновлении объявления с идентификатором {}: {}", listingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обновлении объявления с идентификатором {}: {}", listingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<Void> deleteListing(@PathVariable String listingId, @RequestBody DeleteRequest request) {
        logger.debug("Получен запрос на удаление объявления с идентификатором: {}", listingId);
        try {
            listingService.deleteListing(listingId, request.getUserId());
            logger.debug("Объявление с идентификатором {} успешно удалено", listingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при удалении объявления с идентификатором {}: {}", listingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при удалении объявления с идентификатором {}: {}", listingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public static class DeleteRequest {
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}