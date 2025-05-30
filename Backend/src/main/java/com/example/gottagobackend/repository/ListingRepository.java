package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, String> {
    List<Listing> findByUserId(String userId);
}