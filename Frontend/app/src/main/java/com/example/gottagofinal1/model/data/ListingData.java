/*
package com.example.gottagofinal1.model.data;

import com.example.gottagofinal1.model.Listing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListingData {

    private static final List<Listing> listings = new ArrayList<>();
    private static int listingCounter = 4;

    static {
        listings.add(new Listing(
                "listing_1",
                "user_1",
                "Уютная квартира в центре Москвы",
                "Сдаю 2-комнатную квартиру в центре Москвы. Подходит для пары или семьи с ребёнком. Есть всё необходимое: Wi-Fi, кухня, стиральная машина.",
                "Москва",
                "ул. Тверская, д. 10",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample3.jpg"
                ),
                "01.05.2025",
                "15.05.2025",
                3,
                "+7 (999) 123-45-67"
        ));

        listings.add(new Listing(
                "listing_2",
                "user_2",
                "Просторный дом у моря в Сочи",
                "Предлагаю дом у моря в Сочи. Идеально для отдыха. 3 спальни, большая терраса, мангал. Подходит для большой компании.",
                "Сочи",
                "ул. Морская, д. 5",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg"
                ),
                "10.06.2025",
                "20.06.2025",
                6,
                "+7 (912) 345-67-89"
        ));

        listings.add(new Listing(
                "listing_3",
                "user_3",
                "Студия в Санкт-Петербурге",
                "Сдаю небольшую студию в центре Санкт-Петербурга. Рядом метро, кафе, музеи. Отличный вариант для одного человека или пары.",
                "Санкт-Петербург",
                "Невский проспект, д. 20",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg"
                ),
                "20.05.2025",
                "25.05.2025",
                2,
                "+7 (921) 987-65-43"
        ));

        listings.add(new Listing(
                "listing_4",
                "user_4",
                "Квартира с видом на Кремль",
                "Сдаю квартиру с потрясающим видом на Кремль. Идеально для туристов, всё в шаговой доступности.",
                "Москва",
                "ул. Арбат, д. 15",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg"
                ),
                "05.06.2025",
                "10.06.2025",
                4,
                "+7 (915) 555-66-77"
        ));

        listings.add(new Listing(
                "listing_5",
                "user_5",
                "Квартира с видом на !!!",
                "Сдаю квартиру с потрясающим видом на Кремль. Идеально для туристов, всё в шаговой доступности.",
                "Москва",
                "ул. Арбат, д. 15",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg"
                ),
                "05.06.2025",
                "10.06.2025",
                4,
                "+7 (915) 555-66-77"
        ));

        listings.add(new Listing(
                "listing_6",
                "user_5",
                "Квартира вторая",
                "Сдаю квартиру с потрясающим видом на Кремль. Идеально для туристов, всё в шаговой доступности.",
                "Москва",
                "ул. Арбат, д. 15",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg"
                ),
                "05.06.2025",
                "10.06.2025",
                4,
                "+7 (915) 555-66-77"
        ));
        listings.add(new Listing(
                "listing_7",
                "user_5",
                "Квартира третья",
                "Сдаю квартиру с потрясающим видом на Кремль. Идеально для туристов, всё в шаговой доступности.",
                "Москва",
                "ул. Арбат, д. 15",
                Arrays.asList(
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample1.jpg",
                        "https://bjksntizdqldttldegiu.supabase.co/storage/v1/object/public/images/sample2.jpg"
                ),
                "05.06.2025",
                "10.06.2025",
                4,
                "+7 (915) 555-66-77"
        ));
    }

    public static List<Listing> getListings() {
        return new ArrayList<>(listings);
    }

    public static void addListing(Listing listing) {
        listingCounter++;
        listing.setId("listing_" + listingCounter);
        listings.add(listing);
    }

    public static Listing getListingById(String id) {
        for (Listing listing : listings) {
            if (listing.getId().equals(id)) {
                return listing;
            }
        }
        return null;
    }
} */
