package com.example.gottagobackend.service;

import com.example.gottagobackend.entity.Profile;
import com.example.gottagobackend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private ProfileRepository profileRepository;

    public Profile findById(String userId) {
        return profileRepository.findById(userId).orElse(null);
    }
}