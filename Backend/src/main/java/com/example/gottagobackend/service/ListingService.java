package com.example.gottagobackend.service;

import com.example.gottagobackend.dto.ListingRequest;
import com.example.gottagobackend.entity.Listing;
import com.example.gottagobackend.repository.ChatRepository;
import com.example.gottagobackend.repository.FavoritesRepository;
import com.example.gottagobackend.repository.ListingRepository;
import com.example.gottagobackend.repository.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private static final Logger logger = LoggerFactory.getLogger(ListingService.class);

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String createListing(ListingRequest request) throws JsonProcessingException {
        if (!profileRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Пользователь с ID " + request.getUserId() + " не существует");
        }

        Listing listing = new Listing();
        listing.setListingId(UUID.randomUUID().toString());
        listing.setUserId(request.getUserId());
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setCity(request.getCity());
        listing.setAddress(request.getAddress());
        listing.setImageUrls(objectMapper.writeValueAsString(request.getImageUrls()));
        LocalDate availableFrom = LocalDate.parse(request.getAvailableFrom());
        LocalDate availableTo = LocalDate.parse(request.getAvailableTo());
        listing.setAvailableFrom(Date.from(availableFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        listing.setAvailableTo(Date.from(availableTo.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        listing.setCapacity(request.getCapacity());
        listing.setCreatedAt(new Date());

        listingRepository.save(listing);
        return listing.getListingId();
    }

    public List<Listing> getAllListings() throws JsonProcessingException {
        List<Listing> listings = listingRepository.findAll();
        for (Listing listing : listings) {
            if (listing.getImageUrls() != null) {
                List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>(){});
                listing.setImageUrls(objectMapper.writeValueAsString(imageUrls));
            }
        }
        return listings;
    }

    public List<Listing> getFilteredListings(String search, String city, Integer capacity, String availableFrom, String availableTo) throws JsonProcessingException {
        List<Listing> listings = listingRepository.findAll();
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

    public List<Listing> getListingsByUserId(String userId) throws JsonProcessingException {
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        List<Listing> listings = listingRepository.findByUserId(userId);
        for (Listing listing : listings) {
            if (listing.getImageUrls() != null) {
                List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>(){});
                listing.setImageUrls(objectMapper.writeValueAsString(imageUrls));
            }
        }
        return listings;
    }

    public Listing getListingById(String listingId) throws JsonProcessingException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + listingId + " не найдено"));
        if (listing.getImageUrls() != null) {
            List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>(){});
            listing.setImageUrls(objectMapper.writeValueAsString(imageUrls));
        }
        return listing;
    }

    public void updateListing(String listingId, ListingRequest request) throws JsonProcessingException {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + listingId + " не найдено"));
        if (!profileRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("Пользователь с ID " + request.getUserId() + " не существует");
        }
        if (!listing.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Пользователь с ID " + request.getUserId() + " не является владельцем объявления");
        }

        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setCity(request.getCity());
        listing.setAddress(request.getAddress());
        listing.setImageUrls(objectMapper.writeValueAsString(request.getImageUrls()));
        LocalDate availableFrom = LocalDate.parse(request.getAvailableFrom());
        LocalDate availableTo = LocalDate.parse(request.getAvailableTo());
        listing.setAvailableFrom(Date.from(availableFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        listing.setAvailableTo(Date.from(availableTo.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        listing.setCapacity(request.getCapacity());

        listingRepository.save(listing);
    }

    public void deleteListing(String listingId, String userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + listingId + " не найдено"));
        if (!profileRepository.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует");
        }
        if (!listing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не является владельцем объявления");
        }

        logger.debug("Обновление listing_id на DELETED_LISTING для чатов с listingId: {}", listingId);
        chatRepository.updateListingId(listingId, "DELETED_LISTING");
        logger.debug("Чаты успешно обновлены для listingId: {}", listingId);

        listingRepository.delete(listing);
        logger.debug("Объявление с ID {} удалено", listingId);
    }
}