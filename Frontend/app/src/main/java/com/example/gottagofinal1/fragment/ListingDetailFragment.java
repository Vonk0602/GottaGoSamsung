package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ImageSliderAdapter;
import com.example.gottagofinal1.model.Listing;
import com.example.gottagofinal1.util.NavigationHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ListingDetailFragment extends Fragment {

    private static final String ARG_LISTING = "listing";
    private static final String TAG = "ListingDetailFragment";
    private static final String PREFS_NAME = "user_prefs";

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
    private TextView ratingValue;
    private ImageView star1, star2, star3, star4, star5;
    private ImageView navHomeIcon, navFavoriteIcon, navListingsIcon, navMessagesIcon, navProfileIcon;
    private TextView navHomeText, navFavoriteText, navListingsText, navMessagesText, navProfileText;
    private ImageView settingsIcon;
    private ImageView backButton;
    private ImageView favoriteButton;
    private ImageView menuButton;
    private AppCompatButton contactButton;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private AuthApi authApi;
    private boolean isFavorite = false;

    public interface AuthApi {
        @GET("/api/auth/profile/{userId}")
        Call<LoginResponse> getProfile(@Path("userId") String userId);

        @GET("/api/reviews/user/{userId}/average")
        Call<Double> getAverageRating(@Path("userId") String userId);

        @GET("/api/favorites/check")
        Call<Boolean> checkFavorite(@Query("userId") String userId, @Query("listingId") String listingId);

        @POST("/api/favorites")
        Call<Void> addFavorite(@Query("userId") String userId, @Query("listingId") String listingId);

        @HTTP(method = "DELETE", path = "/api/favorites", hasBody = false)
        Call<Void> removeFavorite(@Query("userId") String userId, @Query("listingId") String listingId);

        @HTTP(method = "DELETE", path = "/api/listings/{listingId}", hasBody = true)
        Call<Void> deleteListing(@Path("listingId") String listingId, @Body DeleteRequest body);
    }

    public static class LoginResponse {
        public String userId;
        public String name;
        public String description;
        public String avatarUrl;
    }

    public static class DeleteRequest {
        private String userId;

        public DeleteRequest(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
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
            Log.e(TAG, "Нет переданных аргументов!");
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
                    String token = prefs.getString("auth_token", null);
                    if (token != null) {
                        Log.d(TAG, "Добавлен заголовок Authorization с токеном");
                        return chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + token)
                                        .build()
                        );
                    } else {
                        Log.w(TAG, "Токен не найден в SharedPreferences");
                    }
                    return chain.proceed(chain.request());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
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
        ratingValue = view.findViewById(R.id.rating_value);
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
        favoriteButton = view.findViewById(R.id.favorite_button);
        menuButton = view.findViewById(R.id.menu_button);
        contactButton = view.findViewById(R.id.contact_button);

        if (listing != null) {
            List<String> imageUrls = new ArrayList<>();
            try {
                imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                Log.e(TAG, "Ошибка парсинга URLs изображений: " + e.getMessage(), e);
            }
            ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls, null);
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
            checkFavoriteStatus();
            checkIfOwner();
        } else {
            Log.e(TAG, "Объявление null!");
            titleTextView.setText("Ошибка: Объявление не найдено");
            cityTextView.setText("");
            descriptionTextView.setText("");
            availabilityTextView.setText("");
            capacityTextView.setText("");
            imageSlider.setAdapter(new ImageSliderAdapter(new ArrayList<>(), null));
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
                Log.d(TAG, "Нажат профиль, переход к профилю пользователя: " + listing.getUserId());
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
                Log.e(TAG, "Невозможно перейти к профилю: объявление null");
            }
        };

        profileAvatar.setOnClickListener(profileClickListener);
        profileName.setOnClickListener(profileClickListener);

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Нажата кнопка назад!");
            getParentFragmentManager().popBackStack();
        });

        favoriteButton.setOnClickListener(v -> toggleFavorite());

        menuButton.setOnClickListener(v -> showMenu());

        contactButton.setOnClickListener(v -> {
            Log.d(TAG, "Нажата кнопка отправки сообщения!");
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
            String currentUserId = prefs.getString("user_id", null);
            if (currentUserId == null) {
                Log.e(TAG, "Пользователь не авторизован!");
                Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listing != null && listing.getUserId() != null) {
                Log.d(TAG, "Переход к чату с пользователем: " + listing.getUserId() + ", listingId: " + listing.getListingId());
                ChatFragment chatFragment = ChatFragment.newInstance(currentUserId, listing.getUserId(), listing.getListingId());
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.e(TAG, "Невозможно открыть чат: объявление или userId null");
                Toast.makeText(getContext(), "Ошибка: невозможно открыть чат", Toast.LENGTH_SHORT).show();
            }
        });

        navHomeIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка дома!");
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
            Log.d(TAG, "Нажата иконка избранного!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new FavoritesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка объявлений!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new AddListingFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navMessagesIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка сообщений!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ChatsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navProfileIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка профиля!");
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
            Log.d(TAG, "Нажата иконка настроек!");
        });
    }

    private void checkIfOwner() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", null);
        if (currentUserId != null && listing != null && currentUserId.equals(listing.getUserId())) {
            Log.d(TAG, "Пользователь является владельцем объявления: " + listing.getListingId());
            menuButton.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Пользователь не является владельцем объявления");
            menuButton.setVisibility(View.GONE);
        }
    }

    private void showMenu() {
        String[] options = {"Редактировать", "Удалить"};
        new AlertDialog.Builder(requireContext())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Log.d(TAG, "Выбрано редактирование объявления: " + listing.getListingId());
                        EditListingFragment editFragment = EditListingFragment.newInstance(listing);
                        getParentFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                )
                                .replace(R.id.fragment_container, editFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Log.d(TAG, "Выбрано удаление объявления: " + listing.getListingId());
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Подтверждение")
                                .setMessage("Вы уверены, что хотите удалить объявление?")
                                .setPositiveButton("Да", (d, w) -> deleteListing())
                                .setNegativeButton("Нет", null)
                                .show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteListing() {
        if (listing == null) {
            Log.e(TAG, "Объявление null при попытке удаления!");
            Toast.makeText(getContext(), "Ошибка: объявление не найдено", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", null);
        if (currentUserId == null) {
            Log.e(TAG, "Пользователь не авторизован!");
            Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        DeleteRequest request = new DeleteRequest(currentUserId);
        authApi.deleteListing(listing.getListingId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
                    Log.w(TAG, "Фрагмент не прикреплён или активность завершена, переход отменён");
                    return;
                }
                if (response.isSuccessful()) {
                    Log.d(TAG, "Объявление успешно удалено: " + listing.getListingId());
                    Toast.makeText(requireContext(), "Объявление удалено", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager()
                            .popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getParentFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right,
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                            )
                            .replace(R.id.fragment_container, new ListingsFragment())
                            .commitAllowingStateLoss();
                } else {
                    Log.e(TAG, "Ошибка удаления объявления: HTTP " + response.code());
                    String errorMessage = response.code() == 400 ? "Вы не можете удалить это объявление" : "Ошибка при удалении объявления";
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
                    Log.w(TAG, "Фрагмент не прикреплён или активность завершена, обработка ошибки отменена");
                    return;
                }
                Log.e(TAG, "Ошибка сети при удалении объявления: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOwnerProfile(String userId) {
        Log.d(TAG, "Получение профиля владельца для userId: " + userId);
        authApi.getProfile(userId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse owner = response.body();
                    Log.d(TAG, "Профиль владельца загружен: " + owner.name);
                    profileName.setText(owner.name != null ? owner.name : "Пользователь");
                    profileDescription.setText(owner.description != null ? owner.description : "");
                    Glide.with(ListingDetailFragment.this)
                            .load(owner.avatarUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(profileAvatar);
                    fetchAverageRating(userId);
                } else {
                    Log.e(TAG, "Не удалось загрузить профиль владельца: HTTP " + response.code() + " " + response.message());
                    profileName.setText("Неизвестный пользователь");
                    profileDescription.setText("");
                    profileAvatar.setImageResource(R.drawable.placeholder_image);
                    setRating(0);
                    Toast.makeText(getContext(), "Ошибка загрузки профиля владельца", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке профиля владельца: " + t.getMessage(), t);
                profileName.setText("Неизвестный пользователь");
                profileDescription.setText("");
                profileAvatar.setImageResource(R.drawable.placeholder_image);
                setRating(0);
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
                    Log.e(TAG, "Не удалось загрузить средний рейтинг: HTTP " + response.code() + " " + response.message());
                    setRating(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке среднего рейтинга: " + t.getMessage(), t);
                setRating(0);
            }
        });
    }

    private void setRating(double rating) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        int displayedStars = rating >= 4.5 ? (int) Math.ceil(rating) : (int) Math.floor(rating);

        for (int i = 0; i < stars.length; i++) {
            if (i < displayedStars) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }

        ratingValue.setText(String.format("%.2f", rating));
    }

    private void checkFavoriteStatus() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", null);
        if (currentUserId == null || listing == null) {
            Log.e(TAG, "Не удалось проверить статус избранного: пользователь или объявление null");
            return;
        }

        authApi.checkFavorite(currentUserId, listing.getListingId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = response.body();
                    favoriteButton.setImageResource(isFavorite ? R.drawable.ic_heart_red : R.drawable.ic_heart_black);
                    Log.d(TAG, "Статус избранного: " + isFavorite);
                } else {
                    Log.e(TAG, "Ошибка проверки статуса избранного: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при проверке статуса избранного: " + t.getMessage(), t);
            }
        });
    }

    private void toggleFavorite() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", null);
        if (currentUserId == null) {
            Log.e(TAG, "Пользователь не авторизован!");
            Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listing == null) {
            Log.e(TAG, "Объявление null!");
            Toast.makeText(getContext(), "Ошибка: объявление не найдено", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            authApi.removeFavorite(currentUserId, listing.getListingId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = false;
                        favoriteButton.setImageResource(R.drawable.ic_heart_black);
                        Log.d(TAG, "Объявление удалено из избранного");
                        Toast.makeText(getContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Ошибка удаления из избранного: HTTP " + response.code());
                        Toast.makeText(getContext(), "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e(TAG, "Ошибка сети при удалении из избранного: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            authApi.addFavorite(currentUserId, listing.getListingId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = true;
                        favoriteButton.setImageResource(R.drawable.ic_heart_red);
                        Log.d(TAG, "Объявление добавлено в избранное");
                        Toast.makeText(getContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Ошибка добавления в избранное: HTTP " + response.code());
                        Toast.makeText(getContext(), "Ошибка при добавлении в избранное", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e(TAG, "Ошибка сети при добавлении в избранное: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}