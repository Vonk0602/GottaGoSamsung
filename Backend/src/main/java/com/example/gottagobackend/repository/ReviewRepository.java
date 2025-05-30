package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(String userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.userId = :userId")
    Double findAverageRatingByUserId(@Param("userId") String userId);

    boolean existsByReviewerIdAndUserId(String reviewerId, String userId);
}