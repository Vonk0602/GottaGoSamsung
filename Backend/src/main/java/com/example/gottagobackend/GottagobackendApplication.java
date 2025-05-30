package com.example.gottagobackend;

import com.example.gottagobackend.entity.Profile;
import com.example.gottagobackend.entity.UserCredentials;
import com.example.gottagobackend.repository.ProfileRepository;
import com.example.gottagobackend.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class GottagobackendApplication implements CommandLineRunner {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserCredentialsRepository userCredentialsRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(GottagobackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (profileRepository.count() == 0) {
			String[] names = {
					"Алексей", "Мария", "Иван", "Елена", "Дмитрий", "Анна",
					"Сергей", "Ольга", "Виктория", "Павел", "Наталья", "Михаил", "Ксения"
			};
			String[] emails = {
					"alexey@example.com", "maria@example.com", "ivan@example.com", "elena@example.com",
					"dmitry@example.com", "anna@example.com", "sergey@example.com", "olga@example.com",
					"viktoria@example.com", "pavel@example.com", "natalia@example.com", "mikhail@example.com",
					"ksenia@example.com"
			};
			String[] descriptions = {
					"Люблю путешествовать и сдавать жильё!",
					"Часто сдаю свою квартиру в центре.",
					"Предоставляю уютное жильё для туристов.",
					"Сдаю уютный дом за городом.",
					"Предлагаю апартаменты с видом на море!",
					"Люблю принимать гостей в своей студии.",
					"Сдаю жильё в историческом центре.",
					"Комфортные квартиры для путешественников.",
					"Сдаю современные лофты в центре города.",
					"Предлагаю уютные коттеджи в лесу.",
					"Мои квартиры идеальны для семейного отдыха.",
					"Сдаю стильные апартаменты с видом на реку.",
					"Гостеприимный хозяин, сдаю жильё у парка."
			};
			String[] passwords = {
					"password123", "password456", "password789", "password101", "password102",
					"password103", "password104", "password105", "password106", "password107",
					"password108", "password109", "password110"
			};

			for (int i = 0; i < names.length; i++) {
				String userId = UUID.randomUUID().toString();

				UserCredentials userCredentials = new UserCredentials();
				userCredentials.setUserId(userId);
				userCredentials.setEmail(emails[i]);
				userCredentials.setPhone(null);
				userCredentials.setPasswordHash(passwordEncoder.encode(passwords[i]));
				userCredentialsRepository.save(userCredentials);

				Profile profile = new Profile();
				profile.setUser_id(userId);
				profile.setName(names[i]);
				profile.setDescription(descriptions[i]);
				profile.setAvatar_url("https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/avatar1.jpg");
				profileRepository.save(profile);
			}
		}
	}
}