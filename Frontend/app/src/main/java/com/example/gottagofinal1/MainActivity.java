package com.example.gottagofinal1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gottagofinal1.fragment.ListingsFragment;
import com.example.gottagofinal1.fragment.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }
}