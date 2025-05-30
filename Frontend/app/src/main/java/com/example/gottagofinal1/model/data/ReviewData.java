package com.example.gottagofinal1.model.data;

import com.example.gottagofinal1.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewData {

    private static final List<Review> reviews = new ArrayList<>();

    static {
        // Отзывы для user_1 (Алексей)
        reviews.add(new Review(1L, "user_2", "user_1", "Отличный хозяин! Квартира чистая, всё как на фото.", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(2L, "user_3", "user_1", "Хорошее место, но Wi-Fi иногда отключался.", 4.0, System.currentTimeMillis()));
        reviews.add(new Review(3L, "user_4", "user_1", "Уютная квартира, идеально для отдыха!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(4L, "user_5", "user_1", "Всё понравилось, отличное расположение.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(5L, "user_6", "user_1", "Алексей — замечательный хозяин, рекомендую!", 4.7, System.currentTimeMillis()));

        // Отзывы для user_2 (Мария)
        reviews.add(new Review(6L, "user_1", "user_2", "Квартира в центре — супер! Всё идеально.", 5.0, System.currentTimeMillis()));
        reviews.add(new Review(7L, "user_3", "user_2", "Мария была очень гостеприимна, всё понравилось.", 4.2, System.currentTimeMillis()));
        reviews.add(new Review(8L, "user_4", "user_2", "Чисто, уютно, прекрасный вид из окна.", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(9L, "user_5", "user_2", "Отличное место, но парковка была занята.", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(10L, "user_7", "user_2", "Всё на высшем уровне, рекомендую!", 4.4, System.currentTimeMillis()));

        // Отзывы для user_3 (Иван)
        reviews.add(new Review(11L, "user_1", "user_3", "Жильё уютное, Иван всегда на связи.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(12L, "user_2", "user_3", "Отличное расположение, всё соответствует описанию.", 4.7, System.currentTimeMillis()));
        reviews.add(new Review(13L, "user_5", "user_3", "Хорошая студия, но немного шумно ночью.", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(14L, "user_6", "user_3", "Всё было замечательно, спасибо Ивану!", 4.1, System.currentTimeMillis()));
        reviews.add(new Review(15L, "user_8", "user_3", "Чисто и комфортно, вернусь снова.", 4.8, System.currentTimeMillis()));

        // Отзывы для user_4 (Елена)
        reviews.add(new Review(16L, "user_1", "user_4", "Дом за городом — просто сказка!", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(17L, "user_2", "user_4", "Елена — отличный хозяин, всё идеально.", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(18L, "user_3", "user_4", "Тишина и природа, прекрасное место!", 4.4, System.currentTimeMillis()));
        reviews.add(new Review(19L, "user_7", "user_4", "Всё понравилось, но дорога до города долгая.", 4.2, System.currentTimeMillis()));
        reviews.add(new Review(20L, "user_8", "user_4", "Уютный дом, рекомендую для отдыха!", 4.7, System.currentTimeMillis()));

        // Отзывы для user_5 (Дмитрий)
        reviews.add(new Review(21L, "user_2", "user_5", "Апартаменты с видом на море — супер!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(22L, "user_3", "user_5", "Всё хорошо, но кондиционер шумел.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(23L, "user_4", "user_5", "Вид потрясающий, Дмитрий очень помог!", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(24L, "user_6", "user_5", "Чисто и комфортно, отличное место!", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(25L, "user_7", "user_5", "Всё на уровне, рекомендую!", 4.6, System.currentTimeMillis()));

        // Отзывы для user_6 (Анна)
        reviews.add(new Review(26L, "user_1", "user_6", "Студия Анны — идеальное место для отдыха.", 4.4, System.currentTimeMillis()));
        reviews.add(new Review(27L, "user_3", "user_6", "Всё чисто, но кровать немного скрипела.", 4.7, System.currentTimeMillis()));
        reviews.add(new Review(28L, "user_4", "user_6", "Анна — замечательный хозяин, всё понравилось!", 4.2, System.currentTimeMillis()));
        reviews.add(new Review(29L, "user_5", "user_6", "Уютная студия, отличное расположение.", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(30L, "user_8", "user_6", "Всё идеально, вернусь снова!", 4.5, System.currentTimeMillis()));

        // Отзывы для user_7 (Сергей)
        reviews.add(new Review(31L, "user_1", "user_7", "Жильё в центре — просто мечта!", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(32L, "user_2", "user_7", "Сергей был очень внимателен, всё супер.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(33L, "user_4", "user_7", "Отличное место, но шумновато из-за центра.", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(34L, "user_5", "user_7", "Всё чисто и удобно, рекомендую!", 4.4, System.currentTimeMillis()));
        reviews.add(new Review(35L, "user_6", "user_7", "Сергей — отличный хозяин, всё на уровне.", 4.7, System.currentTimeMillis()));

        // Отзывы для user_8 (Ольга)
        reviews.add(new Review(36L, "user_1", "user_8", "Квартира Ольги — идеальна для путешественников!", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(37L, "user_2", "user_8", "Всё чисто, уютно, Ольга очень помогла!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(38L, "user_3", "user_8", "Хорошее место, но парковка была проблемой.", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(39L, "user_5", "user_8", "Комфортно и удобно, рекомендую!", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(40L, "user_6", "user_8", "Всё на высшем уровне, спасибо Ольге!", 4.9, System.currentTimeMillis()));

        // Отзывы для user_9 (Виктория)
        reviews.add(new Review(41L, "user_1", "user_9", "Лофт Виктории — стильный и современный!", 4.7, System.currentTimeMillis()));
        reviews.add(new Review(42L, "user_3", "user_9", "Отличное место в центре, всё понравилось.", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(43L, "user_6", "user_9", "Чисто и уютно, но лифт был сломан.", 4.2, System.currentTimeMillis()));
        reviews.add(new Review(44L, "user_8", "user_9", "Виктория — отличный хозяин, рекомендую!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(45L, "user_10", "user_9", "Супер лофт, идеально для работы и отдыха.", 4.6, System.currentTimeMillis()));

        // Отзывы для user_10 (Павел)
        reviews.add(new Review(46L, "user_2", "user_10", "Коттедж в лесу — просто сказка!", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(47L, "user_4", "user_10", "Тишина и природа, Павел был на связи.", 4.4, System.currentTimeMillis()));
        reviews.add(new Review(48L, "user_5", "user_10", "Всё отлично, но дорога немного сложная.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(49L, "user_7", "user_10", "Уютный коттедж, идеально для уикенда.", 4.7, System.currentTimeMillis()));
        reviews.add(new Review(50L, "user_9", "user_10", "Павел — отличный хозяин, всё супер!", 4.5, System.currentTimeMillis()));

        // Отзывы для user_11 (Наталья)
        reviews.add(new Review(51L, "user_1", "user_11", "Квартира Натальи идеальна для семьи!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(52L, "user_3", "user_11", "Всё чисто, детям очень понравилось.", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(53L, "user_6", "user_11", "Уютно, но Wi-Fi был слабоват.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(54L, "user_8", "user_11", "Наталья очень гостеприимна, рекомендую!", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(55L, "user_10", "user_11", "Отличное место для семейного отдыха.", 4.7, System.currentTimeMillis()));

        // Отзывы для user_12 (Михаил)
        reviews.add(new Review(56L, "user_2", "user_12", "Апартаменты с видом на реку — восторг!", 4.9, System.currentTimeMillis()));
        reviews.add(new Review(57L, "user_4", "user_12", "Михаил был очень внимателен, всё супер.", 4.5, System.currentTimeMillis()));
        reviews.add(new Review(58L, "user_5", "user_12", "Чисто и комфортно, но шум от реки.", 4.2, System.currentTimeMillis()));
        reviews.add(new Review(59L, "user_7", "user_12", "Стильные апартаменты, рекомендую!", 4.6, System.currentTimeMillis()));
        reviews.add(new Review(60L, "user_9", "user_12", "Всё идеально, Михаил — отличный хозяин!", 4.8, System.currentTimeMillis()));

        // Отзывы для user_13 (Ксения)
        reviews.add(new Review(61L, "user_1", "user_13", "Жильё у парка — идеальное место!", 4.7, System.currentTimeMillis()));
        reviews.add(new Review(62L, "user_3", "user_13", "Ксения очень помогла, всё понравилось.", 4.4, System.currentTimeMillis()));
        reviews.add(new Review(63L, "user_6", "user_13", "Чисто и уютно, но парковка ограничена.", 4.3, System.currentTimeMillis()));
        reviews.add(new Review(64L, "user_8", "user_13", "Отличное место для прогулок, рекомендую!", 4.8, System.currentTimeMillis()));
        reviews.add(new Review(65L, "user_10", "user_13", "Ксения — замечательный хозяин, всё супер!", 4.6, System.currentTimeMillis()));
    }

    public static List<Review> getReviewsForUser(String userId) {
        List<Review> userReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (userId == null || review.getUserId().equals(userId)) { // Изменено с getToUserId на getUserId
                userReviews.add(review);
            }
        }
        return userReviews;
    }
}