package com.example.gottagobackend.controller;

import com.example.gottagobackend.dto.*;
import com.example.gottagobackend.entity.Profile;
import com.example.gottagobackend.entity.UserCredentials;
import com.example.gottagobackend.repository.ProfileRepository;
import com.example.gottagobackend.repository.UserCredentialsRepository;
import com.example.gottagobackend.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileRepository profileRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Обработка запроса на вход для email: {}", loginRequest.getEmail());
        try {
            LoginResponse response = authService.login(loginRequest);
            logger.debug("Вход выполнен успешно для email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка входа: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при входе: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.debug("Обработка запроса на регистрацию для email: {}", registerRequest.getEmail());
        try {
            String userId = authService.register(registerRequest);
            logger.debug("Регистрация выполнена успешно, userId: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(userId, null));
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка регистрации: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new RegisterResponse(null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при регистрации: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RegisterResponse(null, e.getMessage()));
        }
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<Void> completeProfile(@Valid @RequestBody CompleteProfileRequest request) {
        logger.debug("Обработка завершения профиля для userId: {}", request.getUserId());
        try {
            Profile profile = profileRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Профиль не найден для user_id: " + request.getUserId()));
            profile.setName(request.getName() != null ? request.getName() : "Пользователь");
            profile.setDescription(request.getDescription());
            profileRepository.save(profile);
            logger.debug("Профиль успешно обновлён для userId: {}", request.getUserId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка обновления профиля: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обновлении профиля: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<LoginResponse> getProfile(@PathVariable String userId) {
        logger.debug("Обработка запроса на получение профиля для userId: {}", userId);
        try {
            Profile profile = profileRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Профиль не найден для user_id: " + userId));
            LoginResponse response = new LoginResponse(
                    profile.getUser_id(),
                    profile.getName() != null ? profile.getName() : "Пользователь",
                    profile.getDescription(),
                    profile.getAvatar_url()
            );
            logger.debug("Профиль успешно получен для userId: {}", userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка получения профиля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse(null, null, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при получении профиля: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<Void> updateProfile(@PathVariable String userId, @Valid @RequestBody UpdateProfileRequest request) {
        logger.debug("Обработка запроса на обновление профиля для userId: {}", userId);
        try {
            Profile profile = profileRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Профиль не найден для user_id: " + userId));
            if (request.getName() != null) {
                profile.setName(request.getName());
            }
            if (request.getDescription() != null) {
                profile.setDescription(request.getDescription());
            }
            if (request.getAvatarUrl() != null) {
                profile.setAvatar_url(request.getAvatarUrl());
            }
            profileRepository.save(profile);
            logger.debug("Профиль успешно обновлён для userId: {}", userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка обновления профиля: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обновлении профиля: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Void> changePassword(@PathVariable String userId, @RequestBody ChangePasswordRequest request) {
        logger.debug("Обработка запроса на смену пароля для userId: {}", userId);
        try {
            UserCredentials user = userCredentialsRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден для user_id: " + userId));
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Неверный текущий пароль");
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Пароли не совпадают");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userCredentialsRepository.save(user);
            logger.debug("Пароль успешно изменён для userId: {}", userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка смены пароля: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при смене пароля: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}