package com.example.gottagofinal1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.model.Listing;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
    private List<Listing> listings;
    private OnItemClickListener listener;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public interface OnItemClickListener {
        void onItemClick(Listing listing);
    }

    public ListingAdapter(List<Listing> listings) {
        this.listings = listings != null ? listings : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateListings(List<Listing> newListings) {
        this.listings = newListings != null ? newListings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.titleTextView.setText(listing.getTitle());
        holder.cityTextView.setText(listing.getCity());

        try {
            List<String> imageUrls = objectMapper.readValue(listing.getImageUrls(), new TypeReference<List<String>>() {});
            if (!imageUrls.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrls.get(0))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.placeholder_image);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(listing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView cityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.listing_image);
            titleTextView = itemView.findViewById(R.id.listing_title);
            cityTextView = itemView.findViewById(R.id.listing_city);
        }
    }
}