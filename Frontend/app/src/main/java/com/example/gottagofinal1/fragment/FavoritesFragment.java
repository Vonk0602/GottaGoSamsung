package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ListingAdapter;
import com.example.gottagofinal1.model.Listing;
import com.example.gottagofinal1.util.NavigationHelper;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private static final String PREFS_NAME = "user_prefs";

    private RecyclerView recyclerView;
    private ListingAdapter listingAdapter;
    private ImageView navHomeIcon, navFavoriteIcon, navListingsIcon, navMessagesIcon, navProfileIcon;
    private TextView navHomeText, navFavoriteText, navListingsText, navMessagesText, navProfileText;
    private ImageView settingsIcon;
    private EditText searchInput;
    private FavoritesApi favoritesApi;
    private String currentSearch = "";
    private String currentCity = "";
    private Integer currentCapacity = null;

    public interface FavoritesApi {
        @GET("/api/favorites/user/{userId}")
        Call<List<Listing>> getFavoritesByUserId(@Path("userId") String userId);

        @GET("/api/favorites/user/{userId}/filtered")
        Call<List<Listing>> getFilteredFavoritesByUserId(
                @Path("userId") String userId,
                @Query("search") String search,
                @Query("city") String city,
                @Query("capacity") Integer capacity
        );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        favoritesApi = retrofit.create(FavoritesApi.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_favorites);
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
        searchInput = view.findViewById(R.id.search_input);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        listingAdapter = new ListingAdapter(new ArrayList<>());
        listingAdapter.setOnItemClickListener(listing -> {
            Log.d(TAG, "Нажато объявление с заголовком: " + listing.getTitle());
            ListingDetailFragment detailFragment = ListingDetailFragment.newInstance(listing);
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(listingAdapter);

        NavigationHelper.updateNavigationStyles(
                getContext(),
                NavigationHelper.TAB_FAVORITE,
                navHomeIcon, navHomeText,
                navFavoriteIcon, navFavoriteText,
                navListingsIcon, navListingsText,
                navMessagesIcon, navMessagesText,
                navProfileIcon, navProfileText
        );

        searchInput.setEnabled(true);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                fetchFavorites(currentSearch, currentCity, currentCapacity);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        settingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка настроек!");
            showFilterDialog();
        });

        fetchFavorites(currentSearch, currentCity, currentCapacity);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navHomeIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка дома!");
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
            Log.d(TAG, "Нажата иконка избранного!");
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d(TAG, "Нажата иконка объявлений!");
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
            Log.d(TAG, "Нажата иконка сообщений!");
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
            Log.d(TAG, "Нажата иконка профиля!");
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
                    .replace(R.id.fragment_container, ProfileFragment.newInstance(currentUserId))
                    .commit();
        });
    }

    private void fetchFavorites(String search, String city, Integer capacity) {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String currentUserId = prefs.getString("user_id", null);
        if (currentUserId == null) {
            Log.e(TAG, "Пользователь не авторизован!");
            Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        favoritesApi.getFilteredFavoritesByUserId(currentUserId, search, city, capacity).enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(@NonNull Call<List<Listing>> call, @NonNull Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Listing> favorites = response.body();
                    listingAdapter.updateListings(favorites);
                    Log.d(TAG, "Загружено избранных объявлений: " + favorites.size());
                } else {
                    Log.e(TAG, "Ошибка загрузки избранных объявлений: HTTP " + response.code());
                    Toast.makeText(getContext(), "Ошибка загрузки избранного", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Listing>> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка сети при загрузке избранных объявлений: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Фильтры");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText cityInput = new EditText(getContext());
        cityInput.setHint("Город");
        cityInput.setText(currentCity);
        layout.addView(cityInput);

        EditText capacityInput = new EditText(getContext());
        capacityInput.setHint("Вместимость");
        capacityInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentCapacity != null) {
            capacityInput.setText(String.valueOf(currentCapacity));
        }
        layout.addView(capacityInput);

        builder.setView(layout);

        builder.setPositiveButton("Применить", (dialog, which) -> {
            currentCity = cityInput.getText().toString();
            String capacityText = capacityInput.getText().toString();
            try {
                currentCapacity = capacityText.isEmpty() ? null : Integer.parseInt(capacityText);
            } catch (NumberFormatException e) {
                currentCapacity = null;
                Toast.makeText(getContext(), "Вместимость должна быть числом", Toast.LENGTH_SHORT).show();
            }
            fetchFavorites(currentSearch, currentCity, currentCapacity);
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}