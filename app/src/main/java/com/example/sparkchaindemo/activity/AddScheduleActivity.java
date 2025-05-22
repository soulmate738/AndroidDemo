package com.example.sparkchaindemo.activity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private CheckBox flashCheckBox;
    private CheckBox vibrateCheckBox;
    private Calendar calendar;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        initView();
        calendar = Calendar.getInstance();
    }

    private void initView() {
        nameEditText = findViewById(R.id.schedule_name_edit);
        dateEditText = findViewById(R.id.date_edit);
        timeEditText = findViewById(R.id.time_edit);
        flashCheckBox = findViewById(R.id.flash_checkbox);
        vibrateCheckBox = findViewById(R.id.vibrate_checkbox);
        Button saveButton = findViewById(R.id.save_button);
        mBack = findViewById(R.id.btn_back_add);

        // 设置日期选择器
        dateEditText.setOnClickListener(v -> showDatePicker());

        // 设置时间选择器
        timeEditText.setOnClickListener(v -> showTimePicker());

        // 保存按钮点击事件
        saveButton.setOnClickListener(v -> saveSchedule());
        mBack.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateEditText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeEditText();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateEditText() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeEditText() {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        timeEditText.setText(sdf.format(calendar.getTime()));
    }

    private void saveSchedule() {
        String name = nameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 计算提醒类型
        int reminderType = 0;
        if (flashCheckBox.isChecked()) reminderType += 1;
        if (vibrateCheckBox.isChecked()) reminderType += 2;

        // 计算触发时间（毫秒）
        String datetimeString = date + " " + time;
        long triggerTime = getTimeInMillis(datetimeString);

        // 返回数据给调用者
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("time", datetimeString);
        intent.putExtra("reminderType", reminderType);
        intent.putExtra("triggerTime", triggerTime);
        setResult(RESULT_OK, intent);
        finish();
    }

    private long getTimeInMillis(String datetimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(datetimeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + 60 * 1000; // 默认一分钟后
        }
    }
}