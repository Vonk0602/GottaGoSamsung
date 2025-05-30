package com.example.gottagofinal1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.adapter.ListingAdapter;
import com.example.gottagofinal1.model.Listing;
import com.example.gottagofinal1.util.NavigationHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {

    private static final String SERVER_URL = "http://192.168.1.37:8080/api/listings";

    private RecyclerView recyclerView;
    private ListingAdapter adapter;
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
    private ImageView settingsIcon;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listings, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_listings);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ListingAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this::openListingDetail);
        recyclerView.setAdapter(adapter);

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
        settingsIcon = view.findViewById(R.id.settings_icon);

        NavigationHelper.updateNavigationStyles(
                getContext(),
                NavigationHelper.TAB_HOME,
                navHomeIcon, navHomeText,
                navFavoriteIcon, navFavoriteText,
                navListingsIcon, navListingsText,
                navMessagesIcon, navMessagesText,
                navProfileIcon, navProfileText
        );

        fetchListings();

        return view;
    }

    private void fetchListings() {
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ListingsFragment", "Error fetching listings: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() ->
                        adapter.updateListings(new ArrayList<>()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    List<Listing> listings = objectMapper.readValue(responseBody, new TypeReference<List<Listing>>(){});
                    requireActivity().runOnUiThread(() ->
                            adapter.updateListings(listings));
                } else {
                    Log.e("ListingsFragment", "Server error: " + response.message());
                    requireActivity().runOnUiThread(() ->
                            adapter.updateListings(new ArrayList<>()));
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navHomeIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Home icon clicked!");
        });

        navFavoriteIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Favorite icon clicked!");
        });

        navListingsIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Add listing button clicked!");
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
        });

        navMessagesIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Messages icon clicked!");
        });

        navProfileIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Profile icon clicked!");
            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        settingsIcon.setOnClickListener(v -> {
            Log.d("ListingsFragment", "Settings icon clicked!");
        });
    }

    private void openListingDetail(Listing listing) {
        if (listing == null) {
            Log.e("ListingsFragment", "Attempted to open ListingDetailFragment with null Listing!");
            return;
        }
        Log.d("ListingsFragment", "Opening ListingDetailFragment for listing: " + listing.getTitle());
        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, ListingDetailFragment.newInstance(listing))
                .addToBackStack(null)
                .commit();
    }
}