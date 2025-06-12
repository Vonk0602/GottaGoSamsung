package com.example.gottagofinal1.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.model.Review;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = "ReviewAdapter";
    private List<Review> reviews;

    public interface ProfileApi {
        @GET("/api/auth/profile/{userId}")
        Call<ProfileResponse> getProfile(@Path("userId") String userId);
    }

    public static class ProfileResponse {
        public String userId;
        public String name;
        public String description;
        public String avatarUrl;
    }

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = new ArrayList<>(reviews);
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews.clear();
        this.reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://95.142.42.129:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProfileApi profileApi = retrofit.create(ProfileApi.class);

        profileApi.getProfile(review.getFromUserId()).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    holder.reviewerName.setText(profile.name != null ? profile.name : "Неизвестный пользователь");
                } else {
                    Log.e(TAG, "Failed to load reviewer profile: HTTP " + response.code());
                    holder.reviewerName.setText("Неизвестный пользователь");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error while fetching reviewer profile: " + t.getMessage(), t);
                holder.reviewerName.setText("Неизвестный пользователь");
            }
        });

        holder.reviewComment.setText(review.getText());
        String createdAt = review.getCreatedAt();
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(createdAt);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                holder.reviewDate.setText(outputFormat.format(date));
            } catch (ParseException e) {
                Log.e(TAG, "Failed to parse createdAt: " + createdAt, e);
                holder.reviewDate.setText("Неизвестная дата");
            }
        } else {
            Log.w(TAG, "createdAt is null or empty for review ID: " + review.getId());
            holder.reviewDate.setText("Неизвестная дата");
        }

        int fullStars = (int) review.getRating();
        boolean hasHalfStar = review.getRating() % 1 >= 0.5;
        ImageView[] stars = {holder.star1, holder.star2, holder.star3, holder.star4, holder.star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < fullStars) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else if (i == fullStars && hasHalfStar) {
                stars[i].setImageResource(R.drawable.ic_star_half);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName;
        TextView reviewComment;
        TextView reviewDate;
        ImageView star1, star2, star3, star4, star5;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewer_name);
            reviewComment = itemView.findViewById(R.id.review_comment);
            reviewDate = itemView.findViewById(R.id.review_date);
            star1 = itemView.findViewById(R.id.star_1);
            star2 = itemView.findViewById(R.id.star_2);
            star3 = itemView.findViewById(R.id.star_3);
            star4 = itemView.findViewById(R.id.star_4);
            star5 = itemView.findViewById(R.id.star_5);
        }
    }
}