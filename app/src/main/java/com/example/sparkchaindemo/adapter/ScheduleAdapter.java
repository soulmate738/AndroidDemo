package com.example.sparkchaindemo.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.fragment.SecondFragment;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<SecondFragment.Schedule> scheduleList;
    private OnItemClickListener listener;

    public ScheduleAdapter(Context context, List<SecondFragment.Schedule> scheduleList, OnItemClickListener listener) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        SecondFragment.Schedule schedule = scheduleList.get(position);
        holder.nameTextView.setText(schedule.getName());
        holder.timeTextView.setText(schedule.getTime());

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView timeTextView;
        Button deleteButton;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.schedule_name);
            timeTextView = itemView.findViewById(R.id.schedule_time);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}