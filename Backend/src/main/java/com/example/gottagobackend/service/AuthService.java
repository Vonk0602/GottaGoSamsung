package com.example.gottagobackend.service;

import com.example.gottagobackend.dto.LoginRequest;
import com.example.gottagobackend.dto.LoginResponse;
import com.example.gottagobackend.dto.RegisterRequest;
import com.example.gottagobackend.entity.Profile;
import com.example.gottagobackend.entity.UserCredentials;
import com.example.gottagobackend.repository.ProfileRepository;
import com.example.gottagobackend.repository.UserCredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        logger.debug("Обработка запроса на вход для email: {}", loginRequest.getEmail());
        UserCredentials user = userCredentialsRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.error("Ошибка входа: неверный пароль для email: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        Profile profile = profileRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден для пользователя"));

        logger.info("Вход выполнен успешно для email: {}", loginRequest.getEmail());
        return new LoginResponse(
                user.getUserId(),
                profile.getName(),
                profile.getDescription(),
                profile.getAvatar_url()
        );
    }

    public String register(RegisterRequest registerRequest) {
        logger.debug("Обработка запроса на регистрацию для email: {}", registerRequest.getEmail());
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
            logger.error("Ошибка регистрации: пароли не совпадают для email: {}", registerRequest.getEmail());
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        if (userCredentialsRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            logger.error("Ошибка регистрации: email уже зарегистрирован: {}", registerRequest.getEmail());
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }

        String userId = UUID.randomUUID().toString();
        UserCredentials user = new UserCredentials();
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserId(userId);
        userCredentialsRepository.save(user);

        Profile profile = new Profile();
        profile.setUser_id(userId);
        profile.setName("Новый пользователь");
        profile.setDescription("Описание отсутствует");
        profile.setAvatar_url("https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/default.jpg");
        profileRepository.save(profile);

        logger.info("Регистрация выполнена успешно, userId: {}", userId);
        return userId;
    }
}