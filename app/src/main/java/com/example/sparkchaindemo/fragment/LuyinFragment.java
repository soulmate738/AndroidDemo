package com.example.sparkchaindemo.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat; // 添加这一行
import java.util.Date;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.YytActivity;


public class LuyinFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String CUSTOM_DIR = "MyRecordings";
    String sdPath;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName = "";
    private String VoicePackageName;
    private String filePath;
    private long startTime, endTime;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    private TextView tvAudioFile, tvDuration;
    private EditText etFileName;
    private Button btnStart, btnStop, btnSave;
    private LinearLayout audioBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            filePath = intent.getStringExtra("VoicePackage");
            VoicePackageName = intent.getStringExtra("VoicePackageName");
            Toast.makeText(this.getActivity(),
                    "Received: " + filePath ,
                    Toast.LENGTH_SHORT).show();
        }
        View view = inflater.inflate(R.layout.fragment_luyin, container, false);
        tvAudioFile = view.findViewById(R.id.tv_audio_file);
        tvDuration = view.findViewById(R.id.tv_duration);
        etFileName = view.findViewById(R.id.et_file_name);
        btnStart = view.findViewById(R.id.btn_start);
        btnStop = view.findViewById(R.id.btn_stop);
        // btnUpdate = view.findViewById(R.id.btn_update);
        btnSave = view.findViewById(R.id.btn_save);
        audioBar = view.findViewById(R.id.audio_bar);

        // 默认禁用按钮
        btnStop.setEnabled(false);
        // btnUpdate.setEnabled(false);
        btnSave.setEnabled(false);
        // 设置按钮点击事件
        setupButtonListeners();

        // 检查权限
        checkPermissions();
        return view;
    }
    private void setupButtonListeners() {
        // 开始录音按钮
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        // 结束录音按钮
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        // 更新时长按钮
        // btnUpdate.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         updateDuration();
        //     }
        // });

        // 保存录音按钮
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecording();
            }
        });

        // 语音条点击事件 - 播放录音
        audioBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    if (isPlaying) {
                        stopPlaying();
                    } else {
                        startPlaying();
                    }
                }
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予
            } else {
                Toast.makeText(this.getActivity(), "录音权限被拒绝，无法录音", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void startRecording() {
        // 生成默认文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        fileName = "recording_" + sdf.format(new Date()) + ".3gp";
        // etFileName.setText(fileName);

        // 设置文件路径
        sdPath = filePath + "/" + fileName;

        // 配置MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(sdPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTime = System.currentTimeMillis();

            // 更新UI状态
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            // btnUpdate.setEnabled(false);
            btnSave.setEnabled(false);

            Toast.makeText(this.getActivity(), "开始录音", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getActivity(), "录音失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            endTime = System.currentTimeMillis();

            // 更新UI状态
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            // btnUpdate.setEnabled(true);
            btnSave.setEnabled(true);
            updateDuration();
            Toast.makeText(this.getActivity(), "录音已停止", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDuration() {
        if (startTime != 0 && endTime != 0) {
            long durationMs = endTime - startTime;
            long durationSec = durationMs / 1000;
            long minutes = durationSec / 60;
            long seconds = durationSec % 60;

            String durationStr = String.format(Locale.getDefault(), "%d'%02d\"", minutes, seconds);
            tvDuration.setText(durationStr);
        }
    }

    private void startPlaying() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(sdPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;

            // 更新UI
            tvDuration.setText("播放中...");

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getActivity(), "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;

            // 恢复显示时长
            updateDuration();
        }
    }

    private void saveRecording() {
        // 获取用户输入的文件名
        String newFileName = etFileName.getText().toString().trim();
        if (!newFileName.isEmpty()) {
            if (!newFileName.endsWith(".3gp")) {
                newFileName += ".3gp";
            }

            // 如果文件名有变化，重命名文件
            if (!newFileName.equals(fileName)) {
                java.io.File oldFile = new java.io.File(filePath, fileName);
                java.io.File newFile = new java.io.File(filePath, newFileName);

                if (oldFile.exists()) {
                    if (oldFile.renameTo(newFile)) {
                        // filePath = newFile.getAbsolutePath();
                        fileName = newFileName;
                        Toast.makeText(this.getActivity(), "文件已重命名", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.getActivity(), "重命名失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // 返回目录路径和文件名到上一个界面
            // 在 AddItemActivity 中保存数据后
            Intent resultIntent = new Intent(getActivity(), YytActivity.class);
            resultIntent.putExtra("item",fileName); // newItem 是你创建的新数据项
            resultIntent.putExtra("VoicePackage",VoicePackageName);
            getActivity().setResult(RESULT_OK, resultIntent);
            startActivity(resultIntent);
            getActivity().finish();
            // Intent intent = new Intent(getActivity(), YytActivity.class);
            // intent.putExtra("VoicePackage",VoicePackageName);
            // startActivity(intent);
            // getActivity().finish();
        } else {
            Toast.makeText(this.getActivity(), "请输入文件名", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCustomDirectory() {
        java.io.File dir = new java.io.File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                CUSTOM_DIR);

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                return dir.getAbsolutePath();
            } else {
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                        .getAbsolutePath();
            }
        }
        return dir.getAbsolutePath();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}