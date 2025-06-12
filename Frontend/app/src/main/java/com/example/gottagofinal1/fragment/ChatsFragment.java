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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ChatAdapter;
import com.example.gottagofinal1.model.Chat;
import com.example.gottagofinal1.util.NavigationHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatsFragment extends Fragment {

    private static final String ARG_CURRENT_USER_ID = "current_user_id";
    private static final String PREFS_NAME = "user_prefs";
    private static final String CHATS_URL = "http://95.142.42.129:8080/api/chats/user/";
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private String currentUserId;
    private ImageView navHomeIcon;
    private ImageView navFavoriteIcon;
    private ImageView navListingsIcon;
    private ImageView navMessagesIcon;
    private ImageView navProfileIcon;
    private TextView navHomeText;
    private TextView navFavoriteText;
    private TextView navListingsText;
    private TextView navMessagesText;
    private TextView navProfileText;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ChatsFragment newInstance(String currentUserId) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER_ID, currentUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getString(ARG_CURRENT_USER_ID);
            Log.d("ChatsFragment", "Получен currentUserId из аргументов: " + currentUserId);
        }
        if (currentUserId == null || currentUserId.isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
            currentUserId = prefs.getString("user_id", null);
            Log.d("ChatsFragment", "Получен currentUserId из SharedPreferences: " + currentUserId);
        }
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("ChatsFragment", "currentUserId не определён");
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Пожалуйста, войдите в аккаунт", Toast.LENGTH_LONG).show();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(new ArrayList<Chat>());
        adapter.setOnItemClickListener(chat -> {
            if (chat == null) {
                Log.e("ChatsFragment", "Чат равен null");
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка: чат не определён", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            Log.d("ChatsFragment", "Чат: chatId=" + chat.getChatId() + ", user1Id=" + chat.getUser1Id() + ", user2Id=" + chat.getUser2Id() + ", listingId=" + chat.getListingId());
            String otherUserId = currentUserId.equals(chat.getUser1Id()) ? chat.getUser2Id() : chat.getUser1Id();
            if (otherUserId == null || chat.getListingId() == null) {
                Log.e("ChatsFragment", "otherUserId или listingId равны null для чата: " + chat.getChatId());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка: неверные данные чата", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            ChatFragment chatFragment = ChatFragment.newInstance(currentUserId, otherUserId, chat.getListingId(), chat.getChatId());
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

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

        NavigationHelper.updateNavigationStyles(
                getContext(),
                NavigationHelper.TAB_MESSAGES,
                navHomeIcon, navHomeText,
                navFavoriteIcon, navFavoriteText,
                navListingsIcon, navListingsText,
                navMessagesIcon, navMessagesText,
                navProfileIcon, navProfileText
        );

        if (currentUserId != null && !currentUserId.isEmpty()) {
            fetchChats();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navHomeIcon.setOnClickListener(v -> {
            Log.d("ChatsFragment", "Нажата иконка дома");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ListingsFragment())
                    .commit();
            NavigationHelper.updateNavigationStyles(
                    getContext(),
                    NavigationHelper.TAB_HOME,
                    navHomeIcon, navHomeText,
                    navFavoriteIcon, navFavoriteText,
                    navListingsIcon, navListingsText,
                    navMessagesIcon, navMessagesText,
                    navProfileIcon, navProfileText
            );
        });

        navFavoriteIcon.setOnClickListener(v -> {
            Log.d("ChatsFragment", "Нажата иконка избранного");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ListingsFragment())
                    .commit();
            NavigationHelper.updateNavigationStyles(
                    getContext(),
                    NavigationHelper.TAB_FAVORITE,
                    navHomeIcon, navHomeText,
                    navFavoriteIcon, navFavoriteText,
                    navListingsIcon, navListingsText,
                    navMessagesIcon, navMessagesText,
                    navProfileIcon, navProfileText
            );
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d("ChatsFragment", "Нажата иконка объявлений");
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
            NavigationHelper.updateNavigationStyles(
                    getContext(),
                    NavigationHelper.TAB_LISTINGS,
                    navHomeIcon, navHomeText,
                    navFavoriteIcon, navFavoriteText,
                    navListingsIcon, navListingsText,
                    navMessagesIcon, navMessagesText,
                    navProfileIcon, navProfileText
            );
        });

        navMessagesIcon.setOnClickListener(v -> {
            Log.d("ChatsFragment", "Нажата иконка сообщений");
            NavigationHelper.updateNavigationStyles(
                    getContext(),
                    NavigationHelper.TAB_MESSAGES,
                    navHomeIcon, navHomeText,
                    navFavoriteIcon, navFavoriteText,
                    navListingsIcon, navListingsText,
                    navMessagesIcon, navMessagesText,
                    navProfileIcon, navProfileText
            );
        });

        navProfileIcon.setOnClickListener(v -> {
            Log.d("ChatsFragment", "Нажата иконка профиля");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
            NavigationHelper.updateNavigationStyles(
                    getContext(),
                    NavigationHelper.TAB_PROFILE,
                    navHomeIcon, navHomeText,
                    navFavoriteIcon, navFavoriteText,
                    navListingsIcon, navListingsText,
                    navMessagesIcon, navMessagesText,
                    navProfileIcon, navProfileText
            );
        });
    }

    private void fetchChats() {
        Request request = new Request.Builder()
                .url(CHATS_URL + currentUserId)
                .build();
        Log.d("ChatsFragment", "Запрос чатов для пользователя: " + currentUserId);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatsFragment", "Ошибка получения чатов: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка загрузки чатов", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ChatsFragment", "Получен ответ сервера: " + responseBody);
                    try {
                        List<Chat> chats = objectMapper.readValue(responseBody, new TypeReference<List<Chat>>(){});
                        Log.d("ChatsFragment", "Десериализовано чатов: " + chats.size());
                        for (Chat chat : chats) {
                            Log.d("ChatsFragment", "Чат: chatId=" + chat.getChatId() + ", user1Id=" + chat.getUser1Id() + ", user2Id=" + chat.getUser2Id() + ", listingId=" + chat.getListingId());
                        }
                        requireActivity().runOnUiThread(() -> {
                            adapter.updateChats(chats);
                            Log.d("ChatsFragment", "Адаптер обновлён с " + chats.size() + " чатами");
                        });
                    } catch (Exception e) {
                        Log.e("ChatsFragment", "Ошибка десериализации чатов: " + e.getMessage());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Ошибка обработки чатов", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("ChatsFragment", "Ошибка сервера при получении чатов: HTTP " + response.code());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка загрузки чатов: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}