package com.example.gottagofinal1.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gottagofinal1.R;
import com.example.gottagofinal1.model.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent() != null ? message.getContent() : "Сообщение отсутствует");

        String formattedTime = "Неизвестное время";
        if (message.getSentAt() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
                formattedTime = sdf.format(message.getSentAt());
            } catch (Exception e) {
                Log.e("MessageAdapter", "Ошибка форматирования времени: " + e.getMessage());
            }
        } else {
            Log.w("MessageAdapter", "Поле sentAt равно null для сообщения с ID: " + message.getMessageId());
        }
        holder.timeText.setText(formattedTime);

        String status = message.getStatus() != null ? message.getStatus() : "SENT";
        if (message.getSenderId().equals(currentUserId)) {
            if (holder.statusText != null) {
                holder.statusText.setText(status.equals("READ") ? "Прочитано" : status.equals("DELIVERED") ? "Доставлено" : "Отправлено");
                holder.statusText.setVisibility(View.VISIBLE);
            } else {
                Log.w("MessageAdapter", "statusText равен null для отправленного сообщения с ID: " + message.getMessageId());
            }
        } else {
            if (holder.statusText != null) {
                holder.statusText.setVisibility(View.GONE);
            } else {
                Log.w("MessageAdapter", "statusText равен null для полученного сообщения с ID: " + message.getMessageId());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? 0 : 1;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView statusText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.message_timestamp);
            statusText = itemView.findViewById(R.id.message_status);
        }
    }
}