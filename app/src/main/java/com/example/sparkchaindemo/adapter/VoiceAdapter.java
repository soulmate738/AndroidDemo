package com.example.sparkchaindemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.VoiceItem;


import java.util.ArrayList;
import java.util.List;

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder> {

    private Context context;
    private List<VoiceItem> voiceList = new ArrayList<>();
    private OnItemActionListener listener;

    public VoiceAdapter(Context context, OnItemActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setVoiceList(List<VoiceItem> voiceList) {
        this.voiceList = voiceList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voice, parent, false);
        return new VoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceViewHolder holder, int position) {
        VoiceItem voice = voiceList.get(position);
        holder.tvName.setText(voice.getName());
        // holder.tvDuration.setText(voice.getFormattedDuration());
        holder.llVoice.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlay(voice, position);
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(voice, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(voice, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return voiceList.size();
    }

    public static class VoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        // TextView tvDuration;
        Button btnEdit;
        Button btnDelete;
        LinearLayout llVoice;

        public VoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_voice_name);
            // tvDuration = itemView.findViewById(R.id.tv_voice_duration);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            llVoice = itemView.findViewById(R.id.ll_voice);
        }
    }

    public interface OnItemActionListener {
        void onEdit(VoiceItem voice, int position);
        void onDelete(VoiceItem voice, int position);
        void onPlay(VoiceItem voice, int position);
    }
}