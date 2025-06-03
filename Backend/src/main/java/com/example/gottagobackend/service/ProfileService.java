    package com.example.gottagobackend.service;

    import com.example.gottagobackend.entity.Profile;
    import com.example.gottagobackend.repository.ProfileRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class ProfileService {

        @Autowired
        private ProfileRepository profileRepository;

        public List<Profile> getAllProfiles() {
            return profileRepository.findAll();
        }

        public Profile getProfileById(String id) {
            return profileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Профиль не найден"));
        }
    }