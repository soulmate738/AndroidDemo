package com.example.sparkchaindemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.VoicePackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author anjia
 */
// VoicePackageAdapter.java
public class VoicePackageAdapter extends RecyclerView.Adapter<VoicePackageAdapter.VoicePackageViewHolder> {
    private static final String TAG = "VoicePackageAdapter";
    private Context context;
    private List<VoicePackage> voicePackages;
    private OnVoicePackageListener listener;

    public VoicePackageAdapter(Context context, List<VoicePackage> voicePackages, OnVoicePackageListener listener) {
        this.context = context;
        this.voicePackages = voicePackages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoicePackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voice_package, parent, false);
        return new VoicePackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoicePackageViewHolder holder, int position) {
        VoicePackage voicePackage = voicePackages.get(position);
        holder.nameTextView.setText(voicePackage.getName());
        // holder.countTextView.setText(context.getString(R.string.voice_count, voicePackage.getCount()));

        // 设置时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.timeTextView.setText(sdf.format(new Date(voicePackage.getCreateTime())));

    }

    @Override
    public int getItemCount() {
        return voicePackages.size();
    }

    public class VoicePackageViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView countTextView;
        TextView timeTextView;
        public View foregroundView;
        public View backgroundView;

        public VoicePackageViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            // countTextView = itemView.findViewById(R.id.countTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            foregroundView = itemView.findViewById(R.id.voicePackageInfo);
            // 点击事件
            foregroundView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onVoicePackageClick(position);
                    }
                }
            });

            // 编辑按钮
            itemView.findViewById(R.id.editButton).setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                        Log.d(TAG, "--------Edit button clicked for position: " + position);
                    }
                }
            });

            // 删除按钮
            itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                        Log.d(TAG, "-------Delete button clicked for position: " + position);
                    }
                }
            });
        }
    }

    public interface OnVoicePackageListener {
        void onVoicePackageClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }
}