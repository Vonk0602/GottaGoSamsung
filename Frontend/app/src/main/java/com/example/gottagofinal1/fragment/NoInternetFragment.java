package com.example.gottagofinal1.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gottagofinal1.MainActivity;
import com.example.gottagofinal1.R;

public class NoInternetFragment extends Fragment {

    private static final String TAG = "NoInternetFragment";
    private Handler handler;
    private Runnable checkInternetRunnable;
    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (isInternetAvailable()) {
                    Log.d(TAG, "Интернет подключён, уведомление MainActivity");
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).restoreLastFragment();
                    }
                } else {
                    Log.d(TAG, "Интернет отсутствует, повторная проверка через 5 секунд");
                    handler.postDelayed(this, 5000);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_internet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Экран отсутствия интернета отображён");
        handler.post(checkInternetRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(checkInternetRunnable);
        Log.d(TAG, "Проверка интернета остановлена");
    }

    private boolean isInternetAvailable() {
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}