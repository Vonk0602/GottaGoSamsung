package com.example.gottagofinal1.model;

import java.io.Serializable;
import java.util.Date;

public class Listing implements Serializable {
    private String listingId;
    private String userId;
    private String title;
    private String description;
    private String city;
    private String address;
    private String imageUrls;
    private Date availableFrom;
    private Date availableTo;
    private int capacity;
    private Date createdAt;
    private String status;

    public Listing() {
    }

    public Listing(String listingId, String userId, String title, String description, String city, String address,
                   String imageUrls, Date availableFrom, Date availableTo, int capacity, Date createdAt, String status) {
        this.listingId = listingId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.city = city;
        this.address = address;
        this.imageUrls = imageUrls;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.capacity = capacity;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(Date availableTo) {
        this.availableTo = availableTo;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}