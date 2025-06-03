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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

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
}