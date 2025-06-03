package com.example.gottagofinal1.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.MessageAdapter;
import com.example.gottagofinal1.model.Message;
import com.example.gottagofinal1.util.NavigationHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatFragment extends Fragment {

    private static final String ARG_CURRENT_USER_ID = "current_user_id";
    private static final String ARG_OTHER_USER_ID = "other_user_id";
    private static final String ARG_LISTING_ID = "listing_id";
    private static final String ARG_CHAT_ID = "chat_id";
    private static final String SERVER_URL = "http://192.168.1.37:8080/api/messages";
    private static final String CHAT_URL = "http://192.168.1.37:8080/api/chats";
    private static final long POLLING_INTERVAL_MS = 3000;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText messageInput;
    private ImageView sendButton;
    private ImageView backButton;
    private TextView otherUserName;
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
    private final Gson gson = new Gson();
    private String currentUserId;
    private String otherUserId;
    private String chatId;
    private String listingId;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            fetchMessages();
            handler.postDelayed(this, POLLING_INTERVAL_MS);
        }
    };

    public static ChatFragment newInstance(String currentUserId, String otherUserId, String listingId, String chatId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER_ID, currentUserId);
        args.putString(ARG_OTHER_USER_ID, otherUserId);
        args.putString(ARG_LISTING_ID, listingId);
        args.putString(ARG_CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChatFragment newInstance(String currentUserId, String otherUserId, String listingId) {
        return newInstance(currentUserId, otherUserId, listingId, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getString(ARG_CURRENT_USER_ID);
            otherUserId = getArguments().getString(ARG_OTHER_USER_ID);
            listingId = getArguments().getString(ARG_LISTING_ID);
            chatId = getArguments().getString(ARG_CHAT_ID);
            Log.d("ChatFragment", "Получены аргументы: currentUserId=" + currentUserId + ", otherUserId=" + otherUserId + ", listingId=" + listingId + ", chatId=" + chatId);
        } else {
            Log.e("ChatFragment", "Аргументы не переданы");
        }
        if (listingId == null || listingId.equals("DELETED_LISTING")) {
            Log.d("ChatFragment", "listingId отсутствует или удалён, установка значения DELETED_LISTING");
            listingId = "DELETED_LISTING";
        }
        if (currentUserId == null || otherUserId == null) {
            Log.e("ChatFragment", "currentUserId или otherUserId не переданы");
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Ошибка: пользователь не определён", Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        recyclerView.setAdapter(adapter);

        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        backButton = view.findViewById(R.id.back_button);
        otherUserName = view.findViewById(R.id.other_user_name);

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

        fetchOtherUserName();
        if (currentUserId != null && otherUserId != null) {
            if (chatId == null) {
                createOrFetchChat();
            } else {
                handler.post(pollingRunnable);
                fetchMessages();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (messageText.isEmpty()) {
                Toast.makeText(getContext(), "Введите сообщение", Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatId == null) {
                Log.e("ChatFragment", "chatId не установлен, невозможно отправить сообщение");
                Toast.makeText(getContext(), "Ошибка: чат не создан", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentUserId == null) {
                Log.e("ChatFragment", "currentUserId не определён, невозможно отправить сообщение");
                Toast.makeText(getContext(), "Ошибка: пользователь не определён", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(messageText);
        });

        backButton.setOnClickListener(v -> {
            Log.d("ChatFragment", "Нажата кнопка назад");
            getParentFragmentManager().popBackStack();
        });

        navHomeIcon.setOnClickListener(v -> {
            Log.d("ChatFragment", "Нажата иконка дома");
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
            Log.d("ChatFragment", "Нажата иконка избранного");
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
            Log.d("ChatFragment", "Нажата иконка объявлений");
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
            Log.d("ChatFragment", "Нажата иконка сообщений");
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
            Log.d("ChatFragment", "Нажата иконка профиля");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(pollingRunnable);
        Log.d("ChatFragment", "Polling остановлен");
    }

    private void createOrFetchChat() {
        Log.d("ChatFragment", "Создание или получение чата для пользователей: " + currentUserId + ", " + otherUserId + ", listingId=" + listingId);
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setListingId(listingId);
        chatRequest.setUser1Id(currentUserId);
        chatRequest.setUser2Id(otherUserId);
        String json = gson.toJson(chatRequest);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(CHAT_URL)
                .post(body)
                .addHeader("X-User-Id", currentUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Ошибка создания чата: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка создания чата", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    chatId = responseBody;
                    Log.d("ChatFragment", "Чат создан или получен с ID: " + chatId);
                    requireActivity().runOnUiThread(() -> {
                        handler.post(pollingRunnable);
                        fetchMessages();
                    });
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "нет ответа";
                    Log.e("ChatFragment", "Ошибка сервера при создании чата: HTTP " + response.code() + ", ответ: " + errorBody);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка создания чата: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void fetchMessages() {
        if (chatId == null) {
            Log.e("ChatFragment", "chatId не установлен, невозможно получить сообщения");
            return;
        }
        if (currentUserId == null) {
            Log.e("ChatFragment", "currentUserId не определён, невозможно получить сообщения");
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Ошибка: пользователь не определён", Toast.LENGTH_LONG).show();
            });
            return;
        }
        Request request = new Request.Builder()
                .url(SERVER_URL + "/chat/" + chatId)
                .addHeader("X-User-Id", currentUserId)
                .build();
        Log.d("ChatFragment", "Запрос сообщений для чата: " + chatId);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Ошибка получения сообщений: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка загрузки сообщений", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ChatFragment", "Получен ответ сервера для чата " + chatId + ": " + responseBody);
                    try {
                        List<Message> messages = objectMapper.readValue(responseBody, new TypeReference<List<Message>>(){});
                        Log.d("ChatFragment", "Десериализовано сообщений: " + messages.size());
                        requireActivity().runOnUiThread(() -> {
                            adapter.updateMessages(messages);
                            Log.d("ChatFragment", "Адаптер обновлён с " + messages.size() + " сообщениями");
                            if (!messages.isEmpty()) {
                                recyclerView.smoothScrollToPosition(messages.size() - 1);
                            }
                            for (Message message : messages) {
                                if (!message.getSenderId().equals(currentUserId) && !message.getStatus().equals("READ")) {
                                    markMessageAsRead(message.getMessageId());
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e("ChatFragment", "Ошибка десериализации сообщений: " + e.getMessage());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Ошибка обработки сообщений", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "нет ответа";
                    Log.e("ChatFragment", "Ошибка сервера при получении сообщений: HTTP " + response.code() + ", ответ: " + errorBody);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка загрузки сообщений: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void fetchOtherUserName() {
        if (currentUserId == null || otherUserId == null) {
            Log.e("ChatFragment", "currentUserId или otherUserId не определены, невозможно получить профиль");
            requireActivity().runOnUiThread(() -> {
                otherUserName.setText("Неизвестный пользователь");
                Toast.makeText(getContext(), "Ошибка: пользователь не определён", Toast.LENGTH_LONG).show();
            });
            return;
        }
        Request request = new Request.Builder()
                .url("http://192.168.1.37:8080/api/auth/profile/" + otherUserId)
                .addHeader("X-User-Id", currentUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Ошибка получения имени пользователя: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        otherUserName.setText("Неизвестный пользователь"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ListingDetailFragment.LoginResponse profile = objectMapper.readValue(responseBody, ListingDetailFragment.LoginResponse.class);
                    requireActivity().runOnUiThread(() ->
                            otherUserName.setText(profile.name != null ? profile.name : "Неизвестный пользователь"));
                } else {
                    Log.e("ChatFragment", "Ошибка сервера при получении профиля: HTTP " + response.code());
                    requireActivity().runOnUiThread(() ->
                            otherUserName.setText("Неизвестный пользователь"));
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setChatId(chatId);
        messageRequest.setSenderId(currentUserId);
        messageRequest.setContent(messageText);
        String json = gson.toJson(messageRequest);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .addHeader("X-User-Id", currentUserId)
                .build();
        Log.d("ChatFragment", "Отправка сообщения в чат: " + chatId + ", содержимое: " + messageText);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Ошибка отправки сообщения: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ChatFragment", "Сообщение отправлено, ответ сервера: " + responseBody);
                    requireActivity().runOnUiThread(() -> messageInput.setText(""));
                    fetchMessages();
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "нет ответа";
                    Log.e("ChatFragment", "Ошибка сервера при отправке сообщения: HTTP " + response.code() + ", ответ: " + errorBody);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка отправки сообщения: HTTP " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void markMessageAsRead(String messageId) {
        if (currentUserId == null) {
            Log.e("ChatFragment", "currentUserId не определён, невозможно пометить сообщение");
            return;
        }
        Request request = new Request.Builder()
                .url(SERVER_URL + "/read/" + messageId)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .addHeader("X-User-Id", currentUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChatFragment", "Ошибка пометки сообщения как прочитанного: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ChatFragment", "Сообщение с ID " + messageId + " помечено как прочитанное");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "нет ответа";
                    Log.e("ChatFragment", "Ошибка сервера при пометке сообщения: HTTP " + response.code() + ", ответ: " + errorBody);
                }
            }
        });
    }

    private static class ChatRequest {
        private String listingId;
        private String user1Id;
        private String user2Id;

        public String getListingId() {
            return listingId;
        }

        public void setListingId(String listingId) {
            this.listingId = listingId;
        }

        public String getUser1Id() {
            return user1Id;
        }

        public void setUser1Id(String user1Id) {
            this.user1Id = user1Id;
        }

        public String getUser2Id() {
            return user2Id;
        }

        public void setUser2Id(String user2Id) {
            this.user2Id = user2Id;
        }
    }

    private static class MessageRequest {
        private String chatId;
        private String senderId;
        private String content;

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}