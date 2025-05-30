package com.example.gottagofinal1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ImageSliderAdapter;
import com.example.gottagofinal1.model.Listing;
import com.example.gottagofinal1.util.NavigationHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingDetailFragment extends Fragment {

    private static final String ARG_LISTING = "listing";
    private static final String TAG = "ListingDetailFragment";

    private Listing listing;
    private ViewPager2 imageSlider;
    private TextView titleTextView;
    private TextView cityTextView;
    private TextView descriptionTextView;
    private TextView availabilityTextView;
    private TextView capacityTextView;
    private ImageView profileAvatar;
    private TextView profileName;
    private TextView profileDescription;
    private ImageView star1, star2, star3, star4, star5;
    private ImageView navHomeIcon, navFavoriteIcon, navListingsIcon, navMessagesIcon, navProfileIcon;
    private TextView navHomeText, navFavoriteText, navListingsText, navMessagesText, navProfileText;
    private ImageView settingsIcon;
    private ImageView backButton;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private AuthApi authApi;

    public interface AuthApi {
        @GET("/api/auth/profile/{userId}")
        Call<LoginResponse> getProfile(@Path("userId") String userId);

        @GET("/api/reviews/user/{userId}/average")
        Call<AverageRatingResponse> getAverageRating(@Path("userId") String userId);
    }

    public static class LoginResponse {
        public String userId;
        public String name;
        public String description;
        public String avatarUrl;
    }

    public static class AverageRatingResponse {
        public double rating;
    }

    public static ListingDetailFragment newInstance(Listing listing) {
        ListingDetailFragment fragment = new ListingDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LISTING, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listing = (Listing) getArguments().getSerializable(ARG_LISTING);
        } else {
            Log.e(TAG, "No arguments provided!");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.37:8080/api/") // Ваш IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        imageSlider = view.findViewById(R.id.image_slider);
        titleTextView = view.findViewById(R.id.listing_title);
        cityTextView = view.findViewById(R.id.listing_city);
        descriptionTextView = view.findViewById(R.id.listing_description);
        availabilityTextView = view.findViewById(R.id.listing_availability);
        capacityTextView = view.findViewById(R.id.listing_capacity);
        profileAvatar = view.findViewById(R.id.profile_avatar);
        profileName = view.findViewById(R.id.profile_name);
        profileDescription = view.findViewById(R.id.profile_description);
        star1 = view.findViewById(R.id.star_1);
        star2 = view.findViewById(R.id.star_2);
        star3 = view.findViewById(R.id.star_3);
        star4 = view.findViewById(R.id.star_4);
        star5 = view.findViewById(R.id.star_5);
        navHomeIcon = view.findViewById(R.id.nav_home_icon);
        navFavoriteIcon = view.findViewById(R.id.nav_favorite_icon);
        navListingsIcon = view.findViewById(R.id.add_listing_button);
        navMessagesIcon = view.findViewById(R.id.nav_messages_icon);
        navProfileIcon = view.findViewById(R.id.nav_profile_icon);
        navHomeText = view.findViewById(R.id.nav_home_text);
        navFavoriteText = view.findViewById(R.id.nav_favorite_text);
        navListingsText = view.findViewById(R.id.nav_listings_text);
        navMessagesText = view.findViewById(R.id.nav_messages_text);
        navProfileText = view.findViewById(R.id.nav_profile_text);
        settingsIcon = view.findViewById(R.id.settings_icon);
        backButton = view.findViewById(R.id.back_button);

        if (listing != null) {
            List<String> imageUrls = new ArrayList<>();
            try {
                imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                Log.e(TAG, "Error parsing image URLs: " + e.getMessage(), e);
            }
            ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls);
            imageSlider.setAdapter(adapter);

            titleTextView.setText(listing.getTitle());
            cityTextView.setText(listing.getCity());
            descriptionTextView.setText(listing.getDescription());
            String availability = String.format("Доступно: с %s по %s",
                    dateFormatter.format(listing.getAvailableFrom()),
                    dateFormatter.format(listing.getAvailableTo()));
            availabilityTextView.setText(availability);
            capacityTextView.setText(String.format("Вместимость: %d человек", listing.getCapacity()));

            fetchOwnerProfile(listing.getUserId());
        } else {
            Log.e(TAG, "Listing is null!");
            titleTextView.setText("Ошибка: Объявление не найдено");
            cityTextView.setText("");
            descriptionTextView.setText("");
            availabilityTextView.setText("");
            capacityTextView.setText("");
            imageSlider.setAdapter(new ImageSliderAdapter(new ArrayList<>()));
            profileName.setText("Неизвестный пользователь");
            profileDescription.setText("");
            profileAvatar.setImageResource(R.drawable.placeholder_image);
            setRating(0);
        }

        NavigationHelper.updateNavigationStyles(
                getContext(),
                NavigationHelper.TAB_LISTINGS,
                navHomeIcon, navHomeText,
                navFavoriteIcon, navFavoriteText,
                navListingsIcon, navListingsText,
                navMessagesIcon, navMessagesText,
                navProfileIcon, navProfileText
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener profileClickListener = v -> {
            if (listing != null) {
                Log.d(TAG, "Profile clicked, navigating to user profile: " + listing.getUserId());
                ProfileFragment profileFragment = ProfileFragment.newInstance(listing.getUserId());
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, profileFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.e(TAG, "Cannot navigate to profile: listing is null");
            }
        };

        profileAvatar.setOnClickListener(profileClickListener);
        profileName.setOnClickListener(profileClickListener);

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked!");
            getParentFragmentManager().popBackStack();
        });

        navHomeIcon.setOnClickListener(v -> {
            Log.d(TAG, "Home icon clicked!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.fragment_container, new ListingsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navFavoriteIcon.setOnClickListener(v -> {
            Log.d(TAG, "Favorite icon clicked!");
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Listings icon clicked!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.fragment_container, new ListingsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navMessagesIcon.setOnClickListener(v -> {
            Log.d(TAG, "Messages icon clicked!");
        });

        navProfileIcon.setOnClickListener(v -> {
            Log.d(TAG, "Profile icon clicked!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        settingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Settings icon clicked!");
        });
    }

    private void fetchOwnerProfile(String userId) {
        Log.d(TAG, "Fetching owner profile for userId: " + userId);
        authApi.getProfile(userId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse owner = response.body();
                    Log.d(TAG, "Owner profile loaded: " + owner.name);
                    profileName.setText(owner.name != null ? owner.name : "Пользователь");
                    profileDescription.setText(owner.description != null ? owner.description : "");
                    Glide.with(ListingDetailFragment.this)
                            .load(owner.avatarUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(profileAvatar);
                    fetchAverageRating(userId);
                } else {
                    Log.e(TAG, "Failed to load owner profile: HTTP " + response.code() + " " + response.message());
                    profileName.setText("Неизвестный пользователь");
                    profileDescription.setText("");
                    profileAvatar.setImageResource(R.drawable.placeholder_image);
                    setRating(0);
                    Toast.makeText(getContext(), "Ошибка загрузки профиля владельца", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error while fetching owner profile: " + t.getMessage(), t);
                profileName.setText("Неизвестный пользователь");
                profileDescription.setText("");
                profileAvatar.setImageResource(R.drawable.placeholder_image);
                setRating(0);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAverageRating(String userId) {
        Log.d(TAG, "Fetching average rating for userId: " + userId);
        authApi.getAverageRating(userId).enqueue(new Callback<AverageRatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<AverageRatingResponse> call, @NonNull Response<AverageRatingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double rating = response.body().rating;
                    Log.d(TAG, "Average rating: " + rating);
                    setRating(rating);
                } else {
                    Log.e(TAG, "Failed to load average rating: HTTP " + response.code() + " " + response.message());
                    setRating(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AverageRatingResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error while fetching average rating: " + t.getMessage(), t);
                setRating(0);
            }
        });
    }

    private void setRating(double rating) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        int fullStars = (int) rating;
        boolean hasHalfStar = rating % 1 >= 0.5;

        for (int i = 0; i < stars.length; i++) {
            if (i < fullStars) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else if (i == fullStars && hasHalfStar) {
                stars[i].setImageResource(R.drawable.ic_star_half);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }
}