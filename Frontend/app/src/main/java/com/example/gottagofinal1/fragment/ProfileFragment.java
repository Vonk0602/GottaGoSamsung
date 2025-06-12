package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ListingAdapter;
import com.example.gottagofinal1.adapter.ReviewAdapter;
import com.example.gottagofinal1.model.Listing;
import com.example.gottagofinal1.model.Review;
import com.example.gottagofinal1.util.NavigationHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String TAG = "ProfileFragment";
    private static final String PREFS_NAME = "user_prefs";

    private ImageView profileAvatar;
    private TextView profileName;
    private TextView profileDescription;
    private TextView ratingValue;
    private ImageView navHomeIcon, navFavoriteIcon, navListingsIcon, navMessagesIcon, navProfileIcon;
    private TextView navHomeText, navFavoriteText, navListingsText, navMessagesText, navProfileText;
    private TextView tabListings, tabReviews;
    private RecyclerView recyclerViewContent;
    private ImageView star1, star2, star3, star4, star5;
    private ImageView backButton;
    private Button addReviewButton;
    private ImageView menuButton;
    private ListingAdapter listingAdapter;
    private ReviewAdapter reviewAdapter;
    private boolean isListingsTabSelected = true;
    private String userId;
    private String currentUserId;
    private AuthApi authApi;

    public interface AuthApi {
        @GET("/api/auth/profile/{userId}")
        Call<LoginResponse> getProfile(@Path("userId") String userId);

        @GET("/api/listings/user/{userId}")
        Call<List<Listing>> getUserListings(@Path("userId") String userId);

        @GET("/api/reviews/user/{userId}")
        Call<List<Review>> getUserReviews(@Path("userId") String userId);

        @GET("/api/reviews/user/{userId}/average")
        Call<Double> getAverageRating(@Path("userId") String userId);

        @POST("/api/reviews")
        Call<Void> addReview(@Body ReviewRequest reviewRequest);
    }

    public static class LoginResponse {
        public String userId;
        public String name;
        public String description;
        public String avatarUrl;
    }

    public static class ReviewRequest {
        private String reviewerId;
        private String userId;
        private String text;
        private float rating;

        public ReviewRequest(String reviewerId, String userId, String text, float rating) {
            this.reviewerId = reviewerId;
            this.userId = userId;
            this.text = text;
            this.rating = rating;
        }

        public String getReviewerId() { return reviewerId; }
        public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public float getRating() { return rating; }
        public void setRating(float rating) { this.rating = rating; }
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
        Log.d(TAG, "Retrofit инициализирован с базовым URL: http://95.142.42.129:8080/");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView начался");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        try {
            profileAvatar = view.findViewById(R.id.profile_avatar);
            profileName = view.findViewById(R.id.profile_name);
            profileDescription = view.findViewById(R.id.profile_description);
            ratingValue = view.findViewById(R.id.rating_value);
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
            tabListings = view.findViewById(R.id.tab_listings);
            tabReviews = view.findViewById(R.id.tab_reviews);
            recyclerViewContent = view.findViewById(R.id.recycler_view_content);
            star1 = view.findViewById(R.id.star_1);
            star2 = view.findViewById(R.id.star_2);
            star3 = view.findViewById(R.id.star_3);
            star4 = view.findViewById(R.id.star_4);
            star5 = view.findViewById(R.id.star_5);
            backButton = view.findViewById(R.id.back_button);
            addReviewButton = view.findViewById(R.id.add_review_button);
            menuButton = view.findViewById(R.id.menu_button);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации view-элементов: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Ошибка загрузки интерфейса", Toast.LENGTH_SHORT).show();
            return view;
        }

        recyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID)) {
            userId = getArguments().getString(ARG_USER_ID);
            if (!isValidUUID(userId)) {
                Log.e(TAG, "Некорректный userId из аргументов: " + userId);
                Toast.makeText(getContext(), "Некорректный идентификатор пользователя", Toast.LENGTH_SHORT).show();
                redirectToLogin();
                return view;
            }
            backButton.setVisibility(View.VISIBLE);
        } else {
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
            userId = prefs.getString("user_id", null);
            if (userId == null || !isValidUUID(userId)) {
                Log.e(TAG, "userId не найден в SharedPreferences или некорректен: " + userId);
                Toast.makeText(getContext(), "Ошибка: Пользователь не авторизован", Toast.LENGTH_SHORT).show();
                redirectToLogin();
                return view;
            }
            backButton.setVisibility(View.GONE);
            addReviewButton.setVisibility(View.GONE);
        }
        Log.d(TAG, "Получен userId: " + userId);

        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", null);
        if (currentUserId != null && !isValidUUID(currentUserId)) {
            Log.e(TAG, "Некорректный currentUserId: " + currentUserId);
            currentUserId = null;
        }
        Log.d(TAG, "Текущий currentUserId: " + currentUserId);
        addReviewButton.setVisibility(currentUserId != null && !currentUserId.equals(userId) ? View.VISIBLE : View.GONE);
        menuButton.setVisibility(currentUserId != null && currentUserId.equals(userId) ? View.VISIBLE : View.GONE);

        listingAdapter = new ListingAdapter(new ArrayList<>());
        listingAdapter.setOnItemClickListener(this::openListingDetail);
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        recyclerViewContent.setAdapter(listingAdapter);

        fetchProfile(userId);
        fetchUserListings(userId);
        fetchUserReviews(userId);

        NavigationHelper.updateNavigationStyles(
                getContext(),
                NavigationHelper.TAB_PROFILE,
                navHomeIcon, navHomeText,
                navFavoriteIcon, navFavoriteText,
                navListingsIcon, navListingsText,
                navMessagesIcon, navMessagesText,
                navProfileIcon, navProfileText
        );

        updateTabStyles();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated начался");

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка назад нажата");
            getParentFragmentManager().popBackStack();
        });

        navHomeIcon.setOnClickListener(v -> {
            Log.d(TAG, "Иконка домой нажата");
            getParentFragmentManager()
                    .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.fragment_container, new ListingsFragment())
                    .commit();
        });

        navFavoriteIcon.setOnClickListener(v -> {
            Log.d(TAG, "Иконка избранное нажата");
            getParentFragmentManager()
                    .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new FavoritesFragment())
                    .commit();
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Иконка объявления нажата");
            getParentFragmentManager()
                    .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new AddListingFragment())
                    .commit();
        });

        navMessagesIcon.setOnClickListener(v -> {
            Log.d(TAG, "Иконка сообщения нажата");
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
            String currentUserId = prefs.getString("user_id", null);
            getParentFragmentManager()
                    .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, ChatsFragment.newInstance(currentUserId))
                    .commit();
        });

        navProfileIcon.setOnClickListener(v -> {
            Log.d(TAG, "Иконка профиля нажата");
        });

        tabListings.setOnClickListener(v -> {
            Log.d(TAG, "Вкладка объявления нажата");
            if (!isListingsTabSelected) {
                isListingsTabSelected = true;
                recyclerViewContent.setAdapter(listingAdapter);
                updateTabStyles();
            }
        });

        tabReviews.setOnClickListener(v -> {
            Log.d(TAG, "Вкладка отзывы нажата");
            if (isListingsTabSelected) {
                isListingsTabSelected = false;
                recyclerViewContent.setAdapter(reviewAdapter);
                updateTabStyles();
            }
        });

        addReviewButton.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка добавления отзыва нажата для userId: " + userId);
            showAddReviewDialog();
        });

        menuButton.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка меню нажата");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Меню");
            builder.setItems(new String[]{"Редактировать", "Выйти"}, (dialog, which) -> {
                if (which == 0) {
                    Log.d(TAG, "Выбрано редактирование профиля");
                    getParentFragmentManager()
                            .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, EditProfileFragment.newInstance(currentUserId))
                            .commit();
                } else {
                    Log.d(TAG, "Выбран выход из профиля");
                    SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    getParentFragmentManager()
                            .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new LoginFragment())
                            .commit();
                }
            });
            builder.show();
        });
    }

    private void fetchProfile(String userId) {
        Log.d(TAG, "Получение профиля для userId: " + userId);
        authApi.getProfile(userId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse profile = response.body();
                    Log.d(TAG, "Профиль загружен: " + profile.name);
                    profileName.setText(profile.name != null ? profile.name : "Пользователь");
                    profileDescription.setText(profile.description != null ? profile.description : "");
                    Glide.with(ProfileFragment.this)
                            .load(profile.avatarUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(profileAvatar);
                    fetchAverageRating(userId);
                } else {
                    Log.e(TAG, "Ошибка загрузки профиля: HTTP " + response.code());
                    profileName.setText("Пользователь");
                    profileDescription.setText("");
                    profileAvatar.setImageResource(R.drawable.placeholder_image);
                    setRating(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке профиля: " + t.getMessage());
                profileName.setText("Пользователь");
                profileDescription.setText("");
                profileAvatar.setImageResource(R.drawable.placeholder_image);
                setRating(0);
                Toast.makeText(getContext(), "Ошибка сети: ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserListings(String userId) {
        Log.d(TAG, "Получение объявлений для userId: " + userId);
        authApi.getUserListings(userId).enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(@NonNull Call<List<Listing>> call, @NonNull Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Listing> listings = response.body();
                    Log.d(TAG, "Получено " + listings.size() + " объявлений для userId: " + userId);
                    listingAdapter.updateListings(listings);
                    if (isListingsTabSelected) {
                        recyclerViewContent.setAdapter(listingAdapter);
                    }
                } else {
                    Log.e(TAG, "Ошибка загрузки объявлений: HTTP " + response.code());
                    listingAdapter.updateListings(new ArrayList<>());
                    if (isListingsTabSelected) {
                        recyclerViewContent.setAdapter(listingAdapter);
                    }
                    Toast.makeText(getContext(), "Ошибка загрузки объявлений", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Listing>> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке объявлений: " + t.getMessage());
                listingAdapter.updateListings(new ArrayList<>());
                if (isListingsTabSelected) {
                    recyclerViewContent.setAdapter(listingAdapter);
                }
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserReviews(String userId) {
        Log.d(TAG, "Получение отзывов для userId: " + userId);
        authApi.getUserReviews(userId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(@NonNull Call<List<Review>> call, @NonNull Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    Log.d(TAG, "Получено " + reviews.size() + " отзывов для userId: " + userId);
                    reviewAdapter.updateReviews(reviews);
                    if (!isListingsTabSelected) {
                        recyclerViewContent.setAdapter(reviewAdapter);
                    }
                } else {
                    Log.e(TAG, "Ошибка загрузки отзывов: HTTP " + response.code());
                    reviewAdapter.updateReviews(new ArrayList<>());
                    if (!isListingsTabSelected) {
                        recyclerViewContent.setAdapter(reviewAdapter);
                    }
                    Toast.makeText(getContext(), "Ошибка загрузки отзывов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке отзывов: " + t.getMessage());
                reviewAdapter.updateReviews(new ArrayList<>());
                if (!isListingsTabSelected) {
                    recyclerViewContent.setAdapter(reviewAdapter);
                }
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAverageRating(String userId) {
        Log.d(TAG, "Получение среднего рейтинга для userId: " + userId);
        authApi.getAverageRating(userId).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double rating = response.body();
                    Log.d(TAG, "Средний рейтинг: " + rating);
                    setRating(rating);
                } else {
                    Log.e(TAG, "Ошибка загрузки среднего рейтинга: HTTP " + response.code());
                    setRating(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке среднего рейтинга: " + t.getMessage());
                setRating(0);
            }
        });
    }

    private void showAddReviewDialog() {
        Log.d(TAG, "Показ диалога добавления отзыва для userId: " + userId);
        if (getContext() == null) {
            Log.e(TAG, "Контекст null");
            return;
        }
        if (currentUserId == null || userId == null || currentUserId.equals(userId)) {
            Log.e(TAG, "Невозможно добавить отзыв: некорректные идентификаторы или попытка оставить отзыв самому себе");
            Toast.makeText(getContext(), "Вы не можете оставить отзыв самому себе", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_review, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.review_rating_bar);
        EditText reviewTextInput = dialogView.findViewById(R.id.review_text_input);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button submitButton = dialogView.findViewById(R.id.submit_button);

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String reviewText = reviewTextInput.getText().toString().trim();

            if (reviewText.isEmpty()) {
                reviewTextInput.setError("Введите текст отзыва");
                Log.d(TAG, "Ошибка: пустой текст отзыва");
                return;
            }
            if (rating == 0) {
                Log.d(TAG, "Ошибка: рейтинг не выбран");
                Toast.makeText(getContext(), "Выберите рейтинг", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reviewText.length() > 500) {
                reviewTextInput.setError("Отзыв не должен превышать 500 символов");
                Log.d(TAG, "Ошибка: отзыв слишком длинный");
                return;
            }

            ReviewRequest reviewRequest = new ReviewRequest(currentUserId, userId, reviewText, rating);
            authApi.addReview(reviewRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Отзыв успешно добавлен", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Отзыв успешно добавлен для userId: " + userId);
                        fetchUserReviews(userId);
                        fetchAverageRating(userId);
                    } else {
                        Log.e(TAG, "Ошибка добавления отзыва: HTTP " + response.code());
                        String errorMessage = "Ошибка добавления отзыва";
                        if (response.code() == 400) {
                            errorMessage = "Вы уже оставили отзыв или профиль не существует";
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e(TAG, "Ошибка сети при добавлении отзыва: " + t.getMessage());
                    Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setRating(double rating) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        int displayedStars = rating >= 4.5 ? (int) Math.ceil(rating) : (int) Math.floor(rating);

        for (int i = 0; i < stars.length; i++) {
            if (stars[i] == null) continue;
            if (i < displayedStars) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }

        ratingValue.setText(String.format("%.2f", rating));
    }

    private void updateTabStyles() {
        if (isListingsTabSelected) {
            tabListings.setBackgroundResource(R.drawable.tab_selected_background);
            tabListings.setTextColor(getResources().getColor(R.color.green_text));
            tabReviews.setBackgroundResource(R.drawable.tab_unselected_background);
            tabReviews.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            tabListings.setBackgroundResource(R.drawable.tab_unselected_background);
            tabListings.setTextColor(getResources().getColor(android.R.color.white));
            tabReviews.setBackgroundResource(R.drawable.tab_selected_background);
            tabReviews.setTextColor(getResources().getColor(R.color.green_text));
        }
    }

    private void openListingDetail(Listing listing) {
        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, ListingDetailFragment.newInstance(listing))
                .addToBackStack(null)
                .commit();
    }

    private void redirectToLogin() {
        getParentFragmentManager()
                .popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private boolean isValidUUID(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}