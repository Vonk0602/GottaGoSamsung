package com.example.gottagofinal1.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private static final String PREFS_NAME = "user_prefs";

    private TextView loginTab;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private LinearLayout termsText;
    private AppCompatButton registerButton;
    private boolean isPasswordVisible = false;
    private boolean isPasswordConfirmVisible = false;
    private AuthApi authApi;

    public interface AuthApi {
        @POST("/api/auth/register")
        Call<RegisterResponse> register(@Body RegisterRequest request);
    }

    public static class RegisterRequest {
        public String email;
        public String phone;
        public String password;
        public String passwordConfirm;

        public RegisterRequest(String email, String phone, String password, String passwordConfirm) {
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.passwordConfirm = passwordConfirm;
        }
    }

    public static class RegisterResponse {
        public String userId;
        public String token;
        public String error;

        public RegisterResponse(String userId, String token, String error) {
            this.userId = userId;
            this.token = token;
            this.error = error;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
        android.util.Log.d(TAG, "Ретрофит инициализирован: http://95.142.42.129:8080/");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        loginTab = view.findViewById(R.id.login_tab);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);
        passwordInput = view.findViewById(R.id.password_input);
        passwordConfirmInput = view.findViewById(R.id.password_confirm_input);
        termsText = view.findViewById(R.id.terms_text);
        registerButton = view.findViewById(R.id.register_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerButton.setOnClickListener(v -> {
            android.util.Log.d(TAG, "Нажата кнопка регистрации");
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String passwordConfirm = passwordConfirmInput.getText().toString().trim();

            if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(getContext(), "Ошибка: заполните все поля", Toast.LENGTH_LONG).show();
                android.util.Log.w(TAG, "Обнаружены пустые поля");
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(getContext(), "Ошибка: пароли не совпадают", Toast.LENGTH_LONG).show();
                android.util.Log.w(TAG, "Пароли не совпадают");
                return;
            }

            registerButton.setEnabled(false);
            RegisterRequest request = new RegisterRequest(email, phone, password, passwordConfirm);
            android.util.Log.d(TAG, "Отправка запроса на регистрацию: email=" + email + ", phone=" + phone);

            authApi.register(request).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    registerButton.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().userId != null) {
                        String userId = response.body().userId;
                        String token = response.body().token;
                        Toast.makeText(getContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        android.util.Log.d(TAG, "Регистрация успешна, userId=" + userId);

                        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_id", userId);
                        if (token != null) {
                            editor.putString("auth_token", token);
                            android.util.Log.d(TAG, "Токен сохранен в SharedPreferences");
                        }
                        editor.apply();

                        CompleteProfileFragment fragment = new CompleteProfileFragment();
                        Bundle args = new Bundle();
                        args.putString("userId", userId);
                        fragment.setArguments(args);

                        getParentFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                )
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        String errorMessage = "Ошибка регистрации: HTTP " + response.code() + " " + response.message();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += ", " + response.errorBody().string();
                            } catch (Exception e) {
                                android.util.Log.e(TAG, "Не удалось прочитать тело ошибки", e);
                            }
                        } else if (response.body() != null && response.body().error != null) {
                            errorMessage = response.body().error;
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        android.util.Log.e(TAG, errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    registerButton.setEnabled(true);
                    String errorMessage = "Ошибка сети: " + t.getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    android.util.Log.e(TAG, errorMessage, t);
                }
            });
        });

        loginTab.setOnClickListener(v -> {
            android.util.Log.d(TAG, "Нажата вкладка входа");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        termsText.setOnClickListener(v -> {
            android.util.Log.d(TAG, "Нажат текст условий");
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
                    togglePasswordVisibility(passwordInput, true);
                    return true;
                }
            }
            return false;
        });

        passwordConfirmInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (passwordConfirmInput.getCompoundDrawables()[DRAWABLE_END] != null &&
                        event.getRawX() >= (passwordConfirmInput.getRight() - passwordConfirmInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width() - passwordConfirmInput.getPaddingEnd())) {
                    togglePasswordVisibility(passwordConfirmInput, false);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean isPasswordField) {
        boolean isVisible = isPasswordField ? isPasswordVisible : isPasswordConfirmVisible;
        if (isVisible) {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view_lock, 0);
            if (isPasswordField) isPasswordVisible = false;
            else isPasswordConfirmVisible = false;
        } else {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view, 0);
            if (isPasswordField) isPasswordVisible = true;
            else isPasswordConfirmVisible = true;
        }
        editText.setSelection(editText.getText().length());
    }
}