package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.example.gottagofinal1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class CompleteProfileFragment extends Fragment {

    private static final String TAG = "CompleteProfileFragment";

    private EditText nameInput;
    private EditText descriptionInput;
    private AppCompatButton completeProfileButton;
    private LinearLayout termsText;
    private AuthApi authApi;
    private String userId;

    public interface AuthApi {
        @POST("/api/auth/complete-profile")
        Call<Void> completeProfile(@Body CompleteProfileRequest request);
    }

    public static class CompleteProfileRequest {
        public String userId;
        public String name;
        public String description;

        public CompleteProfileRequest(String userId, String name, String description) {
            this.userId = userId;
            this.name = name;
            this.description = description;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.37:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
        Log.d(TAG, "Retrofit initialized with base URL: http://192.168.1.37:8080");

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            Log.d(TAG, "Received userId: " + userId);
        } else {
            Log.e(TAG, "No userId provided in arguments");
            Toast.makeText(getContext(), "Ошибка: отсутствует ID пользователя", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        nameInput = view.findViewById(R.id.name_input);
        descriptionInput = view.findViewById(R.id.description_input);
        completeProfileButton = view.findViewById(R.id.complete_profile_button);
        termsText = view.findViewById(R.id.terms_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        completeProfileButton.setOnClickListener(v -> {
            Log.d(TAG, "Complete profile button clicked");
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Name field is empty");
                return;
            }

            if (userId == null) {
                Toast.makeText(getContext(), "Ошибка: отсутствует ID пользователя", Toast.LENGTH_LONG).show();
                Log.e(TAG, "userId is null");
                return;
            }

            CompleteProfileRequest request = new CompleteProfileRequest(userId, name, description);
            Log.d(TAG, "Sending complete-profile request: userId=" + userId + ", name=" + name + ", description=" + description);

            completeProfileButton.setEnabled(false);
            authApi.completeProfile(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    completeProfileButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Profile completed successfully for userId: " + userId);
                        SharedPreferences prefs = getContext().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userId", userId);
                        editor.apply();

                        Toast.makeText(getContext(), "Профиль успешно завершен!", Toast.LENGTH_SHORT).show();
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
                    } else {
                        String errorMessage = "Ошибка при сохранении профиля: HTTP " + response.code() + " " + response.message();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += ", " + response.errorBody().string();
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to read error body", e);
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    completeProfileButton.setEnabled(true);
                    String errorMessage = "Ошибка сети: " + t.getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMessage, t);
                }
            });
        });

        termsText.setOnClickListener(v -> {
            Log.d(TAG, "Terms text clicked");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new TermsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}