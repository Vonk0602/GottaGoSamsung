package com.example.gottagofinal1.model.data;

import com.example.gottagofinal1.model.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileData {
    private static final List<Profile> profiles = new ArrayList<>();
    private static final Map<String, UserCredentials> credentialsMap = new HashMap<>();

    static {
        // Существующие профили
        profiles.add(new Profile("user_1", "Алексей", "Люблю путешествовать и сдавать жильё!", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_2", "Мария", "Часто сдаю свою квартиру в центре.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_3", "Иван", "Предоставляю уютное жильё для туристов.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_4", "Елена", "Сдаю уютный дом за городом.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_5", "Дмитрий", "Предлагаю апартаменты с видом на море!", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_6", "Анна", "Люблю принимать гостей в своей студии.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_7", "Сергей", "Сдаю жильё в историческом центре.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_8", "Ольга", "Комфортные квартиры для путешественников.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));

        // Новые профили
        profiles.add(new Profile("user_9", "Виктория", "Сдаю современные лофты в центре города.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_10", "Павел", "Предлагаю уютные коттеджи в лесу.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_11", "Наталья", "Мои квартиры идеальны для семейного отдыха.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_12", "Михаил", "Сдаю стильные апартаменты с видом на реку.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));
        profiles.add(new Profile("user_13", "Ксения", "Гостеприимный хозяин, сдаю жильё у парка.", "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg"));

        // Учётные данные для существующих профилей
        credentialsMap.put("alexey@example.com", new UserCredentials("password123", "user_1"));
        credentialsMap.put("maria@example.com", new UserCredentials("password456", "user_2"));
        credentialsMap.put("ivan@example.com", new UserCredentials("password789", "user_3"));
        credentialsMap.put("elena@example.com", new UserCredentials("password101", "user_4"));
        credentialsMap.put("dmitry@example.com", new UserCredentials("password102", "user_5"));
        credentialsMap.put("anna@example.com", new UserCredentials("password103", "user_6"));
        credentialsMap.put("sergey@example.com", new UserCredentials("password104", "user_7"));
        credentialsMap.put("olga@example.com", new UserCredentials("password105", "user_8"));

        // Учётные данные для новых профилей
        credentialsMap.put("viktoria@example.com", new UserCredentials("password106", "user_9"));
        credentialsMap.put("pavel@example.com", new UserCredentials("password107", "user_10"));
        credentialsMap.put("natalia@example.com", new UserCredentials("password108", "user_11"));
        credentialsMap.put("mikhail@example.com", new UserCredentials("password109", "user_12"));
        credentialsMap.put("ksenia@example.com", new UserCredentials("password110", "user_13"));
    }

    public static List<Profile> getProfiles() {
        return profiles;
    }

    public static Profile getProfileById(String userId) {
        for (Profile profile : profiles) {
            if (profile.getId().equals(userId)) {
                return profile;
            }
        }
        return null;
    }

    public static boolean validateCredentials(String login, String password) {
        UserCredentials credentials = credentialsMap.get(login);
        if (credentials == null) {
            return false;
        }
        return credentials.getPassword().equals(password);
    }

    public static String getUserIdByLogin(String login) {
        UserCredentials credentials = credentialsMap.get(login);
        return credentials != null ? credentials.getUserId() : null;
    }

    private static class UserCredentials {
        private final String password;
        private final String userId;

        UserCredentials(String password, String userId) {
            this.password = password;
            this.userId = userId;
        }

        String getPassword() {
            return password;
        }

        String getUserId() {
            return userId;
        }
    }
}