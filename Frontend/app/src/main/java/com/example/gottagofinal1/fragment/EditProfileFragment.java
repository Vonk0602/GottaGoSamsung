package com.example.gottagofinal1.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class EditProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "currentUserId";
    private static final String SUPABASE_URL = "https://bjksntizdqldttldegiu.supabase.co";
    private static final String SUPABASE_BUCKET = "images";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqa3NudGl6ZHFsZHR0bGRlZ2l1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc4NDA5NTksImV4cCI6MjA2MzQxNjk1OX0.s98w7dh_Uyv3T5QJUMcTSMuhOegSaS-yd02dt94zSe8";
    private ImageView avatarImage;
    private TextInputEditText nameInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText currentPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;
    private Button saveProfileButton;
    private Button changePasswordButton;
    private String currentUserId;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private final OkHttpClient client = new OkHttpClient();
    private String pendingAvatarUrl;
    private final AtomicBoolean isUploading = new AtomicBoolean(false);

    public static class UpdateProfileRequest {
        public String name;
        public String description;
        public String avatarUrl;

        public UpdateProfileRequest(String name, String description, String avatarUrl) {
            this.name = name;
            this.description = description;
            this.avatarUrl = avatarUrl;
        }
    }

    interface AuthService {
        @PUT("/api/auth/profile/{userId}")
        retrofit2.Call<Void> updateProfile(@Path("userId") String userId, @Body UpdateProfileRequest request);
    }

    public static EditProfileFragment newInstance(String userId) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                Log.d("EditProfileFragment", "Выбран аватар: " + imageUri);
                try {
                    Bitmap bitmap = decodeSampledBitmapFromUri(imageUri, 200, 200);
                    avatarImage.setImageBitmap(bitmap);
                    isUploading.set(true);
                    uploadAvatarToSupabase(imageUri);
                } catch (IOException e) {
                    Log.e("EditProfileFragment", "Ошибка загрузки аватара: " + e.getMessage(), e);
                    showToast("Ошибка загрузки аватара");
                    isUploading.set(false);
                }
            }
        });
        Bundle args = getArguments();
        if (args != null) {
            currentUserId = args.getString(ARG_USER_ID);
        }
        if (currentUserId == null) {
            Log.e("EditProfileFragment", "currentUserId не передан");
            showToast("Ошибка: пользователь не определён");
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        avatarImage = view.findViewById(R.id.avatar_image);
        nameInput = view.findViewById(R.id.name_input);
        descriptionInput = view.findViewById(R.id.description_input);
        currentPasswordInput = view.findViewById(R.id.current_password_input);
        newPasswordInput = view.findViewById(R.id.new_password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        saveProfileButton = view.findViewById(R.id.save_profile_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);

        if (currentUserId != null) {
            loadProfile();
        }

        view.findViewById(R.id.back_button).setOnClickListener(v -> navigateToProfileFragment());
        avatarImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });
        saveProfileButton.setOnClickListener(v -> {
            if (isUploading.get()) {
                showToast("Подождите, загрузка аватара ещё не завершена");
                return;
            }
            saveProfile();
        });
        changePasswordButton.setOnClickListener(v -> showToast("Это временно недоступно"));

        return view;
    }

    private void loadProfile() {
        ProfileFragment.AuthApi authApi = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProfileFragment.AuthApi.class);

        authApi.getProfile(currentUserId).enqueue(new retrofit2.Callback<ProfileFragment.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ProfileFragment.LoginResponse> call, retrofit2.Response<ProfileFragment.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileFragment.LoginResponse profile = response.body();
                    Log.d("EditProfileFragment", "Профиль загружен: " + profile.name);
                    runOnUiThread(() -> {
                        nameInput.setText(profile.name != null ? profile.name : "");
                        descriptionInput.setText(profile.description != null ? profile.description : "");
                        if (profile.avatarUrl != null && !profile.avatarUrl.isEmpty()) {
                            Log.d("EditProfileFragment", "Загрузка аватара: " + profile.avatarUrl);
                            Glide.with(requireContext())
                                    .load(profile.avatarUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.placeholder_image)
                                    .into(avatarImage);
                            avatarImage.setTag(profile.avatarUrl);
                        } else {
                            Log.d("EditProfileFragment", "Аватар отсутствует");
                            avatarImage.setImageResource(R.drawable.placeholder_image);
                        }
                    });
                } else {
                    Log.e("EditProfileFragment", "Ошибка загрузки профиля: " + response.code());
                    showToast("Ошибка загрузки профиля");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ProfileFragment.LoginResponse> call, Throwable t) {
                Log.e("EditProfileFragment", "Сетевая ошибка: " + t.getMessage(), t);
                showToast("Ошибка соединения");
            }
        });
    }

    private void saveProfile() {
        if (currentUserId == null) {
            Log.e("EditProfileFragment", "currentUserId null");
            showToast("Ошибка: пользователь не определён");
            return;
        }

        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
        String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
        String avatarUrl = pendingAvatarUrl != null ? pendingAvatarUrl : (avatarImage.getTag() != null ? avatarImage.getTag().toString() : null);

        UpdateProfileRequest request = new UpdateProfileRequest(name.isEmpty() ? null : name, description, avatarUrl);
        AuthService authService = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService.class);

        Log.d("EditProfileFragment", "Сохранение профиля: name=" + name + ", description=" + description + ", avatarUrl=" + avatarUrl);
        authService.updateProfile(currentUserId, request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Log.d("EditProfileFragment", "Профиль сохранён");
                        showToast("Профиль сохранён");
                        pendingAvatarUrl = null;
                        navigateToProfileFragment();
                    } else {
                        Log.e("EditProfileFragment", "Ошибка сохранения: " + response.code());
                        showToast("Ошибка сохранения: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e("EditProfileFragment", "Сетевая ошибка: " + t.getMessage(), t);
                showToast("Ошибка соединения");
            }
        });
    }

    private void uploadAvatarToSupabase(Uri imageUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "avatar_" + System.currentTimeMillis() + "_" + timeStamp + ".jpg";
            String path = "avatars/" + fileName;
            File file = createTempFileFromUri(imageUri, fileName);

            if (!file.exists() || file.length() == 0) {
                Log.e("EditProfileFragment", "Файл не существует или пустой: " + file.getAbsolutePath());
                showToast("Ошибка: файл аватара недоступен или пуст");
                isUploading.set(false);
                return;
            }

            Log.d("EditProfileFragment", "Загрузка файла: " + file.getAbsolutePath());
            Log.d("EditProfileFragment", "Размер файла: " + file.length() + " байт");
            Log.d("EditProfileFragment", "Имя файла: " + fileName);
            Log.d("EditProfileFragment", "Путь: " + path);

            RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + path)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "image/jpeg")
                    .post(requestBody)
                    .build();

            Log.d("EditProfileFragment", "Заголовки запроса: " + request.headers());
            Log.d("EditProfileFragment", "URL запроса: " + request.url());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("EditProfileFragment", "Ошибка загрузки аватара: " + e.getMessage(), e);
                    showToast("Ошибка загрузки аватара");
                    isUploading.set(false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "Нет тела ответа";
                    if (response.isSuccessful()) {
                        pendingAvatarUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + path;
                        Log.d("EditProfileFragment", "Аватар загружен: " + pendingAvatarUrl);
                        runOnUiThread(() -> {
                            avatarImage.setTag(pendingAvatarUrl);
                            Glide.with(requireContext())
                                    .load(pendingAvatarUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.placeholder_image)
                                    .into(avatarImage);
                            showToast("Аватар загружен");
                        });
                    } else {
                        Log.e("EditProfileFragment", "Ошибка Supabase: " + response.code() + ", сообщение: " + response.message());
                        Log.e("EditProfileFragment", "Тело ответа: " + responseBody);
                        Log.e("EditProfileFragment", "Заголовки ответа: " + response.headers());
                        showToast("Ошибка загрузки аватара: " + response.code());
                    }
                    isUploading.set(false);
                    response.close();
                }
            });
        } catch (Exception e) {
            Log.e("EditProfileFragment", "Исключение при загрузке аватара: " + e.getMessage(), e);
            showToast("Ошибка загрузки аватара");
            isUploading.set(false);
        }
    }

    private File createTempFileFromUri(Uri uri, String fileName) throws IOException {
        File storageDir = requireContext().getExternalFilesDir(null);
        File file = new File(storageDir, fileName);

        try (InputStream input = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream output = new FileOutputStream(file)) {
            if (input != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytes = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                Log.d("EditProfileFragment", "Создан временный файл: " + file.getAbsolutePath() + ", размер: " + totalBytes + " байт");
                return file;
            }
        }
        Log.e("EditProfileFragment", "Не удалось создать временный файл из URI: " + uri);
        throw new IOException("Не удалось создать временный файл");
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(uri), null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(uri), null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void showToast(String message) {
        runOnUiThread(() -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(action);
        }
    }

    private void navigateToProfileFragment() {
        if (isAdded() && getActivity() != null) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, ProfileFragment.newInstance(currentUserId));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}