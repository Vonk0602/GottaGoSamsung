package com.example.gottagobackend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @Column(name = "listing_id")
    private String listingId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "available_from")
    @Temporal(TemporalType.DATE)
    private Date availableFrom;

    @Column(name = "available_to")
    @Temporal(TemporalType.DATE)
    private Date availableTo;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }
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
    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }
    public Date getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(Date availableFrom) { this.availableFrom = availableFrom; }
    public Date getAvailableTo() { return availableTo; }
    public void setAvailableTo(Date availableTo) { this.availableTo = availableTo; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}