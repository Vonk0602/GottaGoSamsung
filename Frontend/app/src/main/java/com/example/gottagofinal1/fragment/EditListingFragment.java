package com.example.gottagofinal1.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ImageSliderAdapter;
import com.example.gottagofinal1.model.Listing;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditListingFragment extends Fragment {

    private static final String ARG_LISTING = "listing";
    private static final String TAG = "EditListingFragment";
    private static final String PREFS_NAME = "user_prefs";
    private static final String SUPABASE_URL = "https://bjksntizdqldttldegiu.supabase.co";
    private static final String SUPABASE_BUCKET = "images";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqa3NudGl6ZHFsZHR0bGRlZ2l1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc4NDA5NTksImV4cCI6MjA2MzQxNjk1OX0.s98w7dh_Uyv3T5QJUMcTSMuhOegSaS-yd02dt94zSe8";
    private static final String SERVER_URL = "http://95.142.42.129:8080/api/listings";

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText cityInput;
    private EditText addressInput;
    private EditText availableFromInput;
    private EditText availableToInput;
    private EditText capacityInput;
    private Button saveButton;
    private ImageView backButton;
    private ViewPager2 imagePager;
    private ImageSliderAdapter imagePagerAdapter;
    private List<String> imageUrls;
    private File cameraImageFile;
    private Uri cameraImageUri;
    private String userId;
    private Listing listing;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat serverDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static EditListingFragment newInstance(Listing listing) {
        EditListingFragment fragment = new EditListingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LISTING, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_listing, container, false);

        titleInput = view.findViewById(R.id.title_input);
        descriptionInput = view.findViewById(R.id.description_input);
        cityInput = view.findViewById(R.id.city_input);
        addressInput = view.findViewById(R.id.address_input);
        availableFromInput = view.findViewById(R.id.available_from_input);
        availableToInput = view.findViewById(R.id.available_to_input);
        capacityInput = view.findViewById(R.id.capacity_input);
        saveButton = view.findViewById(R.id.save_button);
        backButton = view.findViewById(R.id.back_button);
        imagePager = view.findViewById(R.id.image_pager);

        if (getArguments() != null) {
            listing = (Listing) getArguments().getSerializable(ARG_LISTING);
        }

        imageUrls = new ArrayList<>();
        if (listing != null) {
            try {
                imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                Log.e(TAG, "Ошибка парсинга URLs изображений: " + e.getMessage(), e);
            }
        }
        imagePagerAdapter = new ImageSliderAdapter(imageUrls, position -> showImageSourceDialog());
        imagePager.setAdapter(imagePagerAdapter);

        userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "userId не найден или пустой в SharedPreferences");
            Toast.makeText(getContext(), "Ошибка: пользователь не аутентифицирован", Toast.LENGTH_LONG).show();
            redirectToLogin();
            return view;
        }
        Log.d(TAG, "Извлечён userId из SharedPreferences: " + userId);

        if (listing != null) {
            titleInput.setText(listing.getTitle());
            descriptionInput.setText(listing.getDescription());
            cityInput.setText(listing.getCity());
            addressInput.setText(listing.getAddress());
            availableFromInput.setText(dateFormatter.format(listing.getAvailableFrom()));
            availableToInput.setText(dateFormatter.format(listing.getAvailableTo()));
            capacityInput.setText(String.valueOf(listing.getCapacity()));
        }

        setupDatePicker(availableFromInput);
        setupDatePicker(availableToInput);

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    try {
                        File file = createTempFileFromUri(imageUri);
                        if (file != null) {
                            uploadImageToSupabase(file);
                        } else {
                            Log.e(TAG, "Не удалось создать временный файл");
                            Toast.makeText(getContext(), "Ошибка: не удалось обработать изображение", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка обработки файла: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Ошибка обработки изображения", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                if (cameraImageFile != null && cameraImageFile.exists()) {
                    uploadImageToSupabase(cameraImageFile);
                } else {
                    Log.e(TAG, "Файл снимка не найден");
                    Toast.makeText(getContext(), "Ошибка: файл снимка не найден", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Требуется разрешение на использование камеры", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String city = cityInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String availableFrom = availableFromInput.getText().toString().trim();
            String availableTo = availableToInput.getText().toString().trim();
            String capacityStr = capacityInput.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || city.isEmpty() || address.isEmpty() ||
                    availableFrom.isEmpty() || availableTo.isEmpty() || capacityStr.isEmpty()) {
                Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUrls.isEmpty()) {
                Toast.makeText(getContext(), "Пожалуйста, добавьте хотя бы одно изображение", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    Toast.makeText(getContext(), "Вместимость должна быть больше 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Вместимость должна быть числом", Toast.LENGTH_SHORT).show();
                return;
            }

            Date fromDate, toDate;
            try {
                fromDate = dateFormatter.parse(availableFrom);
                toDate = dateFormatter.parse(availableTo);
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Неверный формат даты", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String json = objectMapper.writeValueAsString(new AddListingFragment.ListingRequest(
                        userId, title, description, city, address, imageUrls,
                        serverDateFormatter.format(fromDate), serverDateFormatter.format(toDate), capacity));
                Log.d(TAG, "Отправляемый JSON: " + json);
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
                SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
                String token = prefs.getString("auth_token", null);
                Request.Builder requestBuilder = new Request.Builder()
                        .url(SERVER_URL + "/" + listing.getListingId())
                        .put(body);
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                    Log.d(TAG, "Добавлен заголовок Authorization с токеном");
                } else {
                    Log.w(TAG, "Токен не найден в SharedPreferences");
                }
                Request request = requestBuilder.build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "Ошибка отправки: " + e.getMessage(), e);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Ошибка отправки объявления: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body() != null ? response.body().string() : "No response body";
                        if (response.isSuccessful()) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Объявление успешно обновлено!", Toast.LENGTH_SHORT).show();
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
                        } else {
                            Log.e(TAG, "Ошибка сервера: HTTP " + response.code() + " " + response.message());
                            Log.e(TAG, "Тело ответа: " + responseBody);
                            Log.e(TAG, "URL запроса: " + call.request().url());
                            Log.e(TAG, "Заголовки ответа: " + response.headers());
                            requireActivity().runOnUiThread(() -> {
                                String errorMessage = "Ошибка сервера: " + response.code() + " " + response.message();
                                if (responseBody.contains("User with ID")) {
                                    errorMessage = "Ошибка: пользователь не аутентифицирован";
                                    clearUserId();
                                    redirectToLogin();
                                }
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Ошибка формирования запроса: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Ошибка при отправке данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        Log.d(TAG, "Получен userId из SharedPreferences: " + userId);
        return userId;
    }

    private void clearUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_id");
        editor.remove("auth_token");
        editor.apply();
        Log.d(TAG, "userId и токен очищены из SharedPreferences");
    }

    private void redirectToLogin() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void setupDatePicker(EditText dateInput) {
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format(Locale.getDefault(), "%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear);
                        dateInput.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void showImageSourceDialog() {
        String[] options = {"Камера", "Галерея"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Выберите источник")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        permissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        openGallery();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        try {
            cameraImageFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    cameraImageFile
            );
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Log.e(TAG, "Ошибка создания файла: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Ошибка создания файла", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(imageFileName, ".jpg", storageDir);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    private void uploadImageToSupabase(File file) {
        String fileName = file.getName();
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Ошибка загрузки изображения в Supabase: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String imageUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + fileName;
                    requireActivity().runOnUiThread(() -> {
                        imageUrls.add(imageUrl);
                        imagePagerAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e(TAG, "Ошибка сервера Supabase: HTTP " + response.code());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка загрузки изображения: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}