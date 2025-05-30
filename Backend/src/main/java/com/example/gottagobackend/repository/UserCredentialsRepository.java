package com.example.gottagobackend.repository;

import com.example.gottagobackend.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {
    Optional<UserCredentials> findByEmail(String email);
}