package com.example.sparkchaindemo.fragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.AlarmReceiver;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.AddScheduleActivity;
import com.example.sparkchaindemo.adapter.ScheduleAdapter;
import com.example.sparkchaindemo.entity.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment implements ScheduleAdapter.OnItemClickListener{

    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter adapter;
    private ScheduleViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        initView(view);
        createNotificationChannel();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
    }

    private void initView(View view) {
        scheduleRecyclerView = view.findViewById(R.id.schedule_recycler_view);
        Button addButton = view.findViewById(R.id.add_button);

        // 设置 RecyclerView，使用 ViewModel 中的数据
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(getContext(), viewModel.getScheduleList(), this);
        scheduleRecyclerView.setAdapter(adapter);

        // 确保在 adapter 初始化后加载数据
        loadSchedules();


        // 模拟加载已有的日程
        // loadSchedules();

        addButton.setOnClickListener(v -> {
            // 打开添加日程页面
            Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void loadSchedules() {
        List<Schedule> list = viewModel.getScheduleList();
        list.clear(); // 清空原有数据（避免重复添加示例数据）

        // 为演示，添加几个示例日程（建议实际开发中从数据库加载）
        list.add(new Schedule("会议", "2025-05-20 14:30", 1, 1642500600000L));
        list.add(new Schedule("健身", "2025-05-21 08:00", 2, 1642584000000L));

        // 通知适配器数据变更（此时 adapter 已初始化）
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        Schedule schedule = viewModel.getScheduleList().get(position); // 直接从 ViewModel 取数据
        cancelAlarm(schedule.getAlarmId());
        viewModel.getScheduleList().remove(position); // 操作 ViewModel 列表
        adapter.notifyItemRemoved(position); // 通知适配器更新
        Toast.makeText(getContext(), "日程已删除", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String time = data.getStringExtra("time");
            int reminderType = data.getIntExtra("reminderType", 0);
            long triggerTime = data.getLongExtra("triggerTime", 0);

            Schedule newSchedule = new Schedule(name, time, reminderType, triggerTime);
            viewModel.getScheduleList().add(newSchedule); // 向 ViewModel 列表添加数据
            adapter.notifyDataSetChanged(); // 通知适配器更新


            // 设置提醒
            setAlarm(newSchedule);

            // 设置提醒
            setAlarm(newSchedule);
        }
    }

    private void setAlarm(Schedule schedule) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("name", schedule.getName());
        intent.putExtra("reminderType", schedule.getReminderType());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                schedule.getAlarmId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    schedule.getTriggerTime(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    schedule.getTriggerTime(),
                    pendingIntent
            );
        }
    }

    private void cancelAlarm(int alarmId) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ScheduleReminder";
            String description = "Channel for schedule reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 重要性必须设置为高
            NotificationChannel channel = new NotificationChannel("schedule_channel", name, importance);
            channel.setDescription(description);

            // 启用闪光和震动
            channel.enableLights(true);
            channel.enableVibration(true);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class Schedule {
        private String name;
        private String time;
        private int reminderType; // 0:无 1:闪光 2:震动 3:闪光+震动
        private long triggerTime;
        private int alarmId;

        public Schedule(String name, String time, int reminderType, long triggerTime) {
            this.name = name;
            this.time = time;
            this.reminderType = reminderType;
            this.triggerTime = triggerTime;
            this.alarmId = (int) (System.currentTimeMillis() / 1000); // 简单生成唯一ID
        }

        public String getName() { return name; }
        public String getTime() { return time; }
        public int getReminderType() { return reminderType; }
        public long getTriggerTime() { return triggerTime; }
        public int getAlarmId() { return alarmId; }
    }
}