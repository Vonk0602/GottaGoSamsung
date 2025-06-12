package com.example.gottagobackend.service;

import com.example.gottagobackend.entity.Favorite;
import com.example.gottagobackend.entity.Listing;
import com.example.gottagobackend.repository.FavoritesRepository;
import com.example.gottagobackend.repository.ListingRepository;
import com.example.gottagobackend.repository.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class FavoritesService {
    private static final Logger logger = LoggerFactory.getLogger(FavoritesService.class);

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean isFavorite(String userId, String listingId) {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        if (!listingRepository.existsById(listingId)) {
            throw new IllegalArgumentException("Объявление с ID " + listingId + " не существует");
        }
        return favoritesRepository.existsByUserIdAndListingId(userId, listingId);
    }

    public void addFavorite(String userId, String listingId) {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        if (!listingRepository.existsById(listingId)) {
            throw new IllegalArgumentException("Объявление с ID " + listingId + " не существует");
        }
        if (favoritesRepository.existsByUserIdAndListingId(userId, listingId)) {
            throw new IllegalArgumentException("Объявление уже в избранном");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setListingId(listingId);
        favorite.setCreatedAt(new Date());
        favoritesRepository.save(favorite);
    }

    public void removeFavorite(String userId, String listingId) {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        if (!listingRepository.existsById(listingId)) {
            throw new IllegalArgumentException("Объявление с ID " + listingId + " не существует");
        }
        Favorite favorite = favoritesRepository.findByUserIdAndListingId(userId, listingId)
                .orElseThrow(() -> new IllegalArgumentException("Объявление не найдено в избранном"));
        favoritesRepository.delete(favorite);
    }

    public List<Listing> getFavoritesByUserId(String userId) throws JsonProcessingException {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        List<Favorite> favorites = favoritesRepository.findByUserId(userId);
        List<Listing> listings = favorites.stream()
                .map(favorite -> listingRepository.findById(favorite.getListingId()).orElse(null))
                .filter(listing -> listing != null)
                .collect(Collectors.toList());

        for (Listing listing : listings) {
            if (listing.getImageUrls() != null) {
                List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>(){});
                listing.setImageUrls(objectMapper.writeValueAsString(imageUrls));
            }
        }
        return listings;
    }

    public List<Listing> getFilteredFavoritesByUserId(String userId, String search, String city, Integer capacity, String availableFrom, String availableTo) throws JsonProcessingException {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        List<Favorite> favorites = favoritesRepository.findByUserId(userId);
        List<Listing> listings = favorites.stream()
                .map(favorite -> listingRepository.findById(favorite.getListingId()).orElse(null))
                .filter(listing -> listing != null)
                .collect(Collectors.toList());

        if (search != null && !search.isEmpty()) {
            listings = listings.stream()
                    .filter(l -> l.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                            l.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (city != null && !city.isEmpty()) {
            listings = listings.stream()
                    .filter(l -> l.getCity().equalsIgnoreCase(city))
                    .collect(Collectors.toList());
        }
        if (capacity != null) {
            listings = listings.stream()
                    .filter(l -> l.getCapacity() >= capacity)
                    .collect(Collectors.toList());
        }
        if (availableFrom != null && !availableFrom.isEmpty()) {
            try {
                LocalDate fromDate = LocalDate.parse(availableFrom, DateTimeFormatter.ISO_LOCAL_DATE);
                listings = listings.stream()
                        .filter(l -> !l.getAvailableFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(fromDate))
                        .collect(Collectors.toList());
            } catch (DateTimeParseException e) {
                logger.warn("Неверный формат даты availableFrom: {}", availableFrom);
            }
        }
        if (availableTo != null && !availableTo.isEmpty()) {
            try {
                LocalDate toDate = LocalDate.parse(availableTo, DateTimeFormatter.ISO_LOCAL_DATE);
                listings = listings.stream()
                        .filter(l -> !l.getAvailableTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(toDate))
                        .collect(Collectors.toList());
            } catch (DateTimeParseException e) {
                logger.warn("Неверный формат даты availableTo: {}", availableTo);
            }
        }

        for (Listing listing : listings) {
            if (listing.getImageUrls() != null) {
                List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>(){});
                listing.setImageUrls(objectMapper.writeValueAsString(imageUrls));
            }
        }
        return listings;
    }
}