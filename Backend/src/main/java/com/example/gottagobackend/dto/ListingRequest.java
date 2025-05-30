package com.example.gottagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.util.List;

public class ListingRequest {

    @NotBlank(message = "Идентификатор пользователя обязателен")
    private String userId;

    @NotBlank(message = "Заголовок обязателен")
    @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
    private String title;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotBlank(message = "Город обязателен")
    @Size(max = 100, message = "Город не должен превышать 100 символов")
    private String city;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 255, message = "Адрес не должен превышать 255 символов")
    private String address;

    @NotEmpty(message = "Требуется хотя бы один URL изображения")
    @JsonProperty("image_urls")
    private List<String> imageUrls;

    @NotBlank(message = "Дата начала доступности обязательна")
    private String availableFrom;

    @NotBlank(message = "Дата окончания доступности обязательна")
    private String availableTo;

    @Min(value = 1, message = "Вместимость должна быть больше 0")
    private int capacity;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }
    public String getAvailableTo() { return availableTo; }
    public void setAvailableTo(String availableTo) { this.availableTo = availableTo; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}