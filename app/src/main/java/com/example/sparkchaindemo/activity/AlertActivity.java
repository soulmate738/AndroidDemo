package com.example.sparkchaindemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.utils.VibrationUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertActivity extends AppCompatActivity {
    private static final String TAG = "AlertActivity";
    private static final int PERMISSION_REQUEST_VIBRATE = 1001;

    private Vibrator vibrator;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;
    private Thread flashThread;
    private boolean isVibrating = false;
    private boolean vibrateEnabled = false;
    private boolean flashEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置为全屏、点亮屏幕并解锁
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        setContentView(R.layout.dialog_alert);

        // 获取用户设置
        Intent intent = getIntent();
        vibrateEnabled = intent.getBooleanExtra("vibrate", false);
        flashEnabled = intent.getBooleanExtra("flash", false);

        // 获取当前时间
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // 设置警报信息
        TextView txtAlertInfo = findViewById(R.id.txtAlertInfo);
        txtAlertInfo.setText("地震警报！\n\n时间: " + currentTime + "\n\n请立即采取防护措施！");

        // 设置确认按钮
        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlert();
                finish();
            }
        });

        // 初始化震动器
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // 初始化相机管理器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                cameraId = cameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                Log.e(TAG, "Camera initialization failed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 检查并请求权限
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "VIBRATE permission not granted, requesting...");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.VIBRATE},
                    PERMISSION_REQUEST_VIBRATE);
        } else {
            Log.d(TAG, "VIBRATE permission already granted");
            startAlertIfReady();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_VIBRATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "VIBRATE permission granted");
                    startAlertIfReady();
                } else {
                    Log.e(TAG, "VIBRATE permission denied");
                    vibrateEnabled = false;
                    Toast.makeText(this, "震动权限被拒绝，无法启动震动警报", Toast.LENGTH_SHORT).show();
                    startAlertIfReady();
                }
                return;
            }
        }
    }

    private void startAlertIfReady() {
        // 检查设备是否有震动器
        if (vibrateEnabled && vibrator != null && !vibrator.hasVibrator()) {
            Log.w(TAG, "Device does not have a vibrator");
            Toast.makeText(this, "设备没有震动器！", Toast.LENGTH_SHORT).show();
            vibrateEnabled = false;
        }

        // 启动警报效果
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startAlert(vibrateEnabled, flashEnabled);
            }
        });
    }

    // 启动警报效果
    private void startAlert(boolean vibrateEnabled, boolean flashEnabled) {
        if (vibrateEnabled && vibrator != null) {
            Log.d(TAG, "Starting vibration...");
            // 开始震动
            isVibrating = true;

            // 使用新线程确保震动不被UI线程阻塞
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 检查震动权限
                        if (ContextCompat.checkSelfPermission(AlertActivity.this,
                                android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {

                            // 定义震动模式
                            long[] pattern = {0, 500, 200, 500, 200, 500}; // 震动模式

                            // 根据Android版本选择不同的API
                            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //     Log.d(TAG, "Using VibrationEffect API");
                            //     vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0));
                            // } else {
                            //     Log.d(TAG, "Using legacy vibrate API");
                            //     vibrator.vibrate(pattern, 0);
                            // }
                            VibrationUtil.vibrateOnce(AlertActivity.this, 500);
                            VibrationUtil.vibratePattern(AlertActivity.this, pattern, -1);

                            Log.d(TAG, "Vibration started successfully");
                        } else {
                            Log.e(TAG, "VIBRATE permission not available when starting vibration");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting vibration: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Log.d(TAG, "Vibration not enabled or vibrator not available");
        }

        if (flashEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "Starting flashlight...");
            // 开始闪光
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 快速闪烁10次
                        for (int i = 0; i < 10; i++) {
                            if (Thread.interrupted()) {
                                throw new InterruptedException();
                            }
                            toggleFlashlight(true);
                            Thread.sleep(200);
                            toggleFlashlight(false);
                            Thread.sleep(200);
                        }
                        Log.d(TAG, "Flashlight sequence completed");
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Flashlight thread interrupted");
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in flashlight thread: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            flashThread.start();
        } else {
            Log.d(TAG, "Flashlight not enabled or API version not supported");
        }
    }

    // 停止警报效果
    private void stopAlert() {
        Log.d(TAG, "Stopping alert...");

        // 停止震动
        isVibrating = false;
        if (vibrator != null) {
            try {
                vibrator.cancel();
                Log.d(TAG, "Vibration stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping vibration: " + e.getMessage());
            }
        }

        // 停止闪光
        if (flashThread != null && flashThread.isAlive()) {
            flashThread.interrupt();
            Log.d(TAG, "Flashlight thread interrupted");
        }
        if (isFlashOn && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraId, false);
                isFlashOn = false;
                Log.d(TAG, "Flashlight turned off");
            } catch (CameraAccessException e) {
                Log.e(TAG, "Error turning off flashlight: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 控制闪光灯开关
    private void toggleFlashlight(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraId, on);
                isFlashOn = on;
                Log.d(TAG, "Flashlight toggled: " + on);
            } catch (CameraAccessException e) {
                Log.e(TAG, "Error toggling flashlight: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Stopping all alerts");
        stopAlert();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Stopping alerts");
        // 防止Activity被暂停时继续震动
        stopAlert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Checking alerts");
        // 如果Activity恢复且应该震动，则重新启动
        if (!isVibrating && vibrateEnabled) {
            startAlert(vibrateEnabled, flashEnabled);
        }
    }
}