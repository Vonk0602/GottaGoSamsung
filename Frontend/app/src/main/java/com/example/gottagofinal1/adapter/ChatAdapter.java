package com.example.gottagofinal1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.model.Chat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chats;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Chat chat);
    }

    public ChatAdapter(List<Chat> chats) {
        this.chats = chats;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateChats(List<Chat> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.userName.setText(chat.getOtherUserName() != null ? chat.getOtherUserName() : "Неизвестный пользователь");
        holder.lastMessage.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "Нет сообщений");
        holder.unreadCount.setText(chat.getUnreadCount() > 0 ? String.valueOf(chat.getUnreadCount()) : "");
        holder.unreadCount.setVisibility(chat.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView lastMessage;
        TextView unreadCount;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.chat_user_name);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
            unreadCount = itemView.findViewById(R.id.unread_count);
        }
    }
}