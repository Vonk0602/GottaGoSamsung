package com.example.gottagofinal1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gottagofinal1.R;

public class ForgotPasswordFragment extends Fragment {

    private ImageView backButton;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        backButton = view.findViewById(R.id.back_button);
        newPasswordInput = view.findViewById(R.id.new_password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        newPasswordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (newPasswordInput.getRight() - newPasswordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width() - newPasswordInput.getPaddingEnd())) {
                        togglePasswordVisibility(newPasswordInput, true);
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPasswordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirmPasswordInput.getRight() - confirmPasswordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width() - confirmPasswordInput.getPaddingEnd())) {
                        togglePasswordVisibility(confirmPasswordInput, false);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean isNewPasswordField) {
        if (isNewPasswordField) {
            if (isNewPasswordVisible) {
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view_lock, 0);
                isNewPasswordVisible = false;
            } else {
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view, 0);
                isNewPasswordVisible = true;
            }
        } else {
            if (isConfirmPasswordVisible) {
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view_lock, 0);
                isConfirmPasswordVisible = false;
            } else {
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_view, 0);
                isConfirmPasswordVisible = true;
            }
        }
        editText.setSelection(editText.getText().length());
    }
}