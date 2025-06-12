package com.example.gottagofinal1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.gottagofinal1.fragment.ListingsFragment;
import com.example.gottagofinal1.fragment.LoginFragment;
import com.example.gottagofinal1.fragment.NoInternetFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_CURRENT_FRAGMENT = "current_fragment";
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private String lastFragmentTag;
    private boolean isNoInternetFragmentShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Активность создана");

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();

        if (savedInstanceState == null) {
            Log.d(TAG, "Инициализация первого фрагмента");
            if (isInternetAvailable()) {
                Log.d(TAG, "Интернет доступен, загрузка LoginFragment");
                loadFragment(new LoginFragment(), LoginFragment.class.getSimpleName());
                lastFragmentTag = LoginFragment.class.getSimpleName();
            } else {
                Log.d(TAG, "Интернет отсутствует, загрузка NoInternetFragment");
                loadFragment(new NoInternetFragment(), NoInternetFragment.class.getSimpleName());
                isNoInternetFragmentShowing = true;
                lastFragmentTag = LoginFragment.class.getSimpleName();
            }
        } else {
            lastFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAGMENT, LoginFragment.class.getSimpleName());
            isNoInternetFragmentShowing = getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof NoInternetFragment;
            Log.d(TAG, "Восстановление состояния: последний фрагмент=" + lastFragmentTag + ", показан NoInternetFragment=" + isNoInternetFragmentShowing);
            if (!isInternetAvailable() && !isNoInternetFragmentShowing) {
                Log.d(TAG, "Интернет отсутствует при восстановлении, показ NoInternetFragment");
                showNoInternetFragment();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_FRAGMENT, lastFragmentTag);
        Log.d(TAG, "Сохранение состояния: последний фрагмент=" + lastFragmentTag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            Log.d(TAG, "Слушатель сети отключён");
        }
    }

    private void setupNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> {
                    if (isNoInternetFragmentShowing) {
                        Log.d(TAG, "Интернет восстановлен, возврат к последнему фрагменту: " + lastFragmentTag);
                        restoreLastFragment();
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    if (!isNoInternetFragmentShowing) {
                        Log.d(TAG, "Интернет потерян, показ NoInternetFragment");
                        showNoInternetFragment();
                    }
                });
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
        Log.d(TAG, "Слушатель сети зарегистрирован");
    }

    private boolean isInternetAvailable() {
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            Log.d(TAG, "Нет активной сети");
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        boolean isAvailable = capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        Log.d(TAG, "Проверка интернета: доступен=" + isAvailable);
        return isAvailable;
    }

    private void showNoInternetFragment() {
        isNoInternetFragmentShowing = true;
        loadFragment(new NoInternetFragment(), NoInternetFragment.class.getSimpleName());
    }

    public void restoreLastFragment() {
        isNoInternetFragmentShowing = false;
        Fragment fragment;
        switch (lastFragmentTag) {
            case "LoginFragment":
                fragment = new LoginFragment();
                break;
            case "ListingsFragment":
                fragment = new ListingsFragment();
                break;
            default:
                fragment = new LoginFragment();
                Log.w(TAG, "Неизвестный фрагмент: " + lastFragmentTag + ", загрузка LoginFragment по умолчанию");
                lastFragmentTag = LoginFragment.class.getSimpleName();
        }
        loadFragment(fragment, lastFragmentTag);
    }

    public void loadFragment(Fragment fragment, String tag) {
        if (!isNoInternetFragmentShowing && !tag.equals(NoInternetFragment.class.getSimpleName())) {
            lastFragmentTag = tag;
            Log.d(TAG, "Обновление последнего фрагмента: " + lastFragmentTag);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        Log.d(TAG, "Фрагмент загружен: " + tag);
    }
}