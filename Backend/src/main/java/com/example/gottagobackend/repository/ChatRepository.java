package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    List<Chat> findByUser1IdOrUser2Id(String user1Id, String user2Id);

    @Query("SELECT c FROM Chat c WHERE c.listingId = :listingId AND " +
            "((c.user1Id = :user1Id AND c.user2Id = :user2Id) OR (c.user1Id = :user2Id AND c.user2Id = :user1Id))")
    List<Chat> findByListingIdAndUsers(@Param("listingId") String listingId,
                                       @Param("user1Id") String user1Id,
                                       @Param("user2Id") String user2Id);

    Chat findByListingIdAndUser1IdAndUser2Id(String listingId, String user1Id, String user2Id);
}