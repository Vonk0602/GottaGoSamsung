package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    boolean existsById(String userId);
}