package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final String PREFS_NAME = "user_prefs";

    private EditText emailPhoneInput;
    private EditText passwordInput;
    private AppCompatButton loginButton;
    private TextView registerTab;
    private TextView forgotPassword;
    private TextView loginWithPhonePart1;
    private LinearLayout termsText;
    private boolean isPasswordVisible = false;
    private AuthApi authApi;

    public interface AuthApi {
        @POST("/api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String userId;
        public String name;
        public String description;
        public String avatarUrl;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.37:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
        Log.d(TAG, "Retrofit initialized with base URL: http://192.168.1.37:8080/api/");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailPhoneInput = view.findViewById(R.id.email_phone_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        registerTab = view.findViewById(R.id.register_tab);
        forgotPassword = view.findViewById(R.id.forgot_password);
        loginWithPhonePart1 = view.findViewById(R.id.login_with_phone_part1);
        termsText = view.findViewById(R.id.terms_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            String email = emailPhoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Empty fields detected");
                return;
            }

            boolean isEmail = email.contains("@");
            if (!isEmail && !email.matches("\\d{10}")) {
                Toast.makeText(getContext(), "Введите корректный email или номер телефона", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Invalid email format: " + email);
                return;
            }

            loginButton.setEnabled(false);
            LoginRequest request = new LoginRequest(email, password);
            Log.d(TAG, "Sending login request: email=" + email);

            authApi.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    loginButton.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse.userId == null || loginResponse.userId.isEmpty()) {
                            Toast.makeText(getContext(), "Ошибка: получен пустой userId", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Received empty or null userId from server");
                            return;
                        }
                        Log.d(TAG, "Login successful, userId: " + loginResponse.userId);
                        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_id", loginResponse.userId);
                        editor.apply();
                        Log.d(TAG, "Saved userId to SharedPreferences: " + loginResponse.userId);
                        Toast.makeText(getContext(), "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();

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
                        String errorMessage = "Ошибка входа: HTTP " + response.code() + " " + response.message();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += ", " + response.errorBody().string();
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to read error body: ", e);
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMessage);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    loginButton.setEnabled(true);
                    String errorMessage = "Ошибка сети: " + t.getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMessage, t);
                }
            });
        });

        registerTab.setOnClickListener(v -> {
            Log.d(TAG, "Register tab clicked");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        forgotPassword.setOnClickListener(v -> {
            Log.d(TAG, "Forgot password clicked");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loginWithPhonePart1.setOnClickListener(v -> {
            Log.d(TAG, "Login with phone clicked");
            try {
                getParentFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, new PhoneLoginFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to PhoneLoginFragment: ", e);
            }
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

        passwordInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (passwordInput.getCompoundDrawables()[DRAWABLE_END] != null &&
                        event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width() - passwordInput.getPaddingEnd())) {
                    togglePasswordVisibility(passwordInput);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText editText) {
        if (isPasswordVisible) {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view_lock, 0);
            isPasswordVisible = false;
            Log.d(TAG, "Password visibility toggled off");
        } else {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view, 0);
            isPasswordVisible = true;
            Log.d(TAG, "Password visibility toggled on");
        }
        editText.setSelection(editText.getText().length());
    }
}