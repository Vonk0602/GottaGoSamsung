package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.listingId = :listingId")
    void deleteByListingId(@Param("listingId") String listingId);

    boolean existsByUserIdAndListingId(String userId, String listingId);

    Optional<Favorite> findByUserIdAndListingId(String userId, String listingId);

    List<Favorite> findByUserId(String userId);
}