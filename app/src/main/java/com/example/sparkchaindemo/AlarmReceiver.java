package com.example.sparkchaindemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.sparkchaindemo.activity.ReminderDialogActivity;
import com.example.sparkchaindemo.utils.VibrationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmReceiver extends BroadcastReceiver {

    private static final Logger log = LoggerFactory.getLogger(AlarmReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        // String name = intent.getStringExtra("name");
        // int reminderType = intent.getIntExtra("reminderType", 0);
        //
        // // 启动前台服务处理闪光灯和弹窗
        // Intent serviceIntent = new Intent(context, ReminderService.class);
        // serviceIntent.putExtra("name", name);
        // serviceIntent.putExtra("reminderType", reminderType);
        //
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //     context.startForegroundService(serviceIntent);
        // } else {
        //     context.startService(serviceIntent);
        // }
        //
        // // 显示通知
        // showNotification(context, name, reminderType);

            String name = intent.getStringExtra("name");
            int reminderType = intent.getIntExtra("reminderType", 0);

            // 启动前台服务处理闪光灯和弹窗
            Intent serviceIntent = new Intent(context, ReminderService.class);
            serviceIntent.putExtra("name", name);
            serviceIntent.putExtra("reminderType", reminderType);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

            // 显示通知
            showNotification(context, name, reminderType);


    }

    private void showNotification(Context context, String name, int reminderType) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "schedule_channel")
                .setSmallIcon(R.mipmap.ic_lxq)
                .setContentTitle("日程提醒")
                .setContentText("您有一个日程: " + name)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 设置提醒方式
        if ((reminderType & 1) == 1) { // 闪光灯
            Log.d("AlarmReceiver", "--------闪光灯");
            builder.setLights(0xFFFFFF00, 1000, 1000); // 使用黄色闪光灯
        }

        if ((reminderType & 2) == 2) { // 震动
            Log.d("AlarmReceiver", "--------震动");
            long[] pattern = {0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000};
            builder.setVibrate(pattern);
        }

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        } else {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }

        notificationManager.notify(1, notification);
    }

    // 前台服务处理闪光灯和弹窗
    // public static class ReminderService extends Service {
    //
    //     private CameraManager cameraManager;
    //     private String cameraId;
    //     private Vibrator vibrator;
    //     private boolean isFlashOn = false;
    //     private int reminderType;
    //     private String name;
    //
    //     private static final int NOTIFICATION_ID = 2;
    //
    //     @Override
    //     public void onCreate() {
    //         super.onCreate();
    //         cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    //         vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    //
    //         try {
    //             cameraId = cameraManager.getCameraIdList()[0];
    //         } catch (CameraAccessException e) {
    //             e.printStackTrace();
    //         }
    //     }
    //
    //     @Override
    //     public int onStartCommand(Intent intent, int flags, int startId) {
    //         if (intent != null) {
    //             name = intent.getStringExtra("name");
    //             reminderType = intent.getIntExtra("reminderType", 0);
    //
    //             // 显示前台服务通知
    //             startForeground(NOTIFICATION_ID, createForegroundNotification());
    //
    //             // 启动闪光灯和震动
    //             startFlashlightAndVibrate();
    //
    //             // 5秒后停止
    //             new android.os.Handler().postDelayed(this::stopSelf, 5000);
    //         }
    //
    //         return START_NOT_STICKY;
    //     }
    //
    //     private Notification createForegroundNotification() {
    //         NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "schedule_channel")
    //                 .setContentTitle("正在提醒")
    //                 .setContentText("您有一个日程: " + name)
    //                 .setSmallIcon(R.mipmap.ic_lxq)
    //                 .setPriority(NotificationCompat.PRIORITY_LOW);
    //
    //         return builder.build();
    //     }
    //
    //     private void startFlashlightAndVibrate() {
    //         if ((reminderType & 1) == 1) { // 闪光灯
    //             toggleFlashlight(true);
    //
    //             // 闪烁效果
    //             final android.os.Handler handler = new android.os.Handler();
    //             handler.postDelayed(new Runnable() {
    //                 @Override
    //                 public void run() {
    //                     toggleFlashlight(false);
    //                     handler.postDelayed(() -> toggleFlashlight(true), 500);
    //                 }
    //             }, 500);
    //         }
    //
    //         if ((reminderType & 2) == 2) { // 震动
    //             long[] pattern = {0, 1000, 500, 1000};
    //             vibrator.vibrate(pattern, -1);
    //         }
    //
    //         // 显示弹窗
    //         showAlertDialog();
    //     }
    //
    //     private void toggleFlashlight(boolean on) {
    //         try {
    //             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //                 // 检查设备是否有闪光灯
    //                 CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
    //                 Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    //                 if (flashAvailable != null && flashAvailable) {
    //                     cameraManager.setTorchMode(cameraId, on);
    //                     isFlashOn = on;
    //                 } else {
    //                     Log.e("AlarmReceiver", "设备没有闪光灯");
    //                 }
    //             }
    //         } catch (CameraAccessException e) {
    //             Log.e("AlarmReceiver", "无法访问相机: " + e.getMessage());
    //             e.printStackTrace();
    //         } catch (Exception e) {
    //             Log.e("AlarmReceiver", "未知错误: " + e.getMessage());
    //             e.printStackTrace();
    //         }
    //     }
    //
    //     private void showAlertDialog() {
    //         Intent dialogIntent = new Intent(this, ReminderDialogActivity.class);
    //         dialogIntent.putExtra("name", name);
    //         dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //
    //         // 针对Android 8.0及以上版本
    //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //             dialogIntent.addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER);
    //         }
    //
    //         startActivity(dialogIntent);
    //     }
    //
    //     @Override
    //     public void onDestroy() {
    //         super.onDestroy();
    //         // 确保关闭闪光灯和震动
    //         if (isFlashOn) {
    //             toggleFlashlight(false);
    //         }
    //         if (vibrator != null && vibrator.hasVibrator()) {
    //             vibrator.cancel();
    //         }
    //     }
    //
    //     @Override
    //     public IBinder onBind(Intent intent) {
    //         return null;
    //     }
    // }
    // 前台服务处理闪光灯和弹窗
    public static class ReminderService extends Service {

        private static final String TAG = "ReminderService";
        private CameraManager cameraManager;
        private String cameraId;
        private Vibrator vibrator;
        private boolean isFlashOn = false;
        private int reminderType;
        private String name;
        private Thread flashThread; // 闪光灯线程
        private boolean isVibrating = false; // 震动状态
        private BroadcastReceiver dialogDismissReceiver;

        private static final int NOTIFICATION_ID = 2;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d(TAG, "ReminderService onCreate--------");
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            // 查找支持闪光灯的摄像头（复用 AlertActivity 逻辑）
            cameraId = findFlashSupportedCameraId();

            // 初始化广播接收器
            dialogDismissReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ("ACTION_DIALOG_DISMISSED".equals(intent.getAction())) {
                        Log.d(TAG, "收到弹窗关闭广播，停止提醒");
                        // flashThread.interrupt();
                        stopAlert();
                        // 5秒后停止服务
                        new Handler().postDelayed(() -> stopSelf(), 1000);
                    }
                }
            };

            // 注册广播接收器
            IntentFilter filter = new IntentFilter("ACTION_DIALOG_DISMISSED");
            registerReceiver(dialogDismissReceiver, filter);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent != null) {
                name = intent.getStringExtra("name");
                reminderType = intent.getIntExtra("reminderType", 0);
                startForeground(NOTIFICATION_ID, createForegroundNotification());
                startAlert(reminderType); // 启动提醒效果
            }
            return START_NOT_STICKY;
        }

        // 启动提醒效果（震动+闪光）
        private void startAlert(int reminderType) {
            // 震动逻辑（复用 AlertActivity 的 VibrationUtil）
            if ((reminderType & 2) == 2 && vibrator != null) {
                isVibrating = true;
                long[] pattern = {0, 500, 200, 500, 200, 500}; // 震动模式
                Log.d("AlarmReceiver", "--------震动开始");
                VibrationUtil.vibratePattern(this, pattern, 0); // 循环震动
            }

            // 闪光逻辑（复用 AlertActivity 的线程闪烁方式）
            if ((reminderType & 1) == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flashThread = new Thread(new FlashRunnable());
                flashThread.start();
                Log.d("AlarmReceiver", "--------闪烁开始");
            }

            // 显示弹窗（逻辑不变）
            showAlertDialog();
        }

        // 闪光灯闪烁线程（模仿 AlertActivity）
        private class FlashRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    // 快速闪烁10次
                    for (int i = 0; i < 100; i++) {
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
        }

        // 控制闪光灯开关（复用 AlertActivity）
        private void toggleFlashlight(boolean on) throws CameraAccessException {
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

        // 查找支持闪光灯的摄像头（复用 AlertActivity）
        private String findFlashSupportedCameraId() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return null; // 低版本不支持相机2 API
            }
            try {
                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                String[] cameraIds = cameraManager.getCameraIdList();
                for (String id : cameraIds) {
                    // 必须在 API 21+ 环境下才能调用 getCameraCharacteristics
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                    Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if (hasFlash != null && hasFlash) {
                        return id;
                    }
                }
                return cameraIds.length > 0 ? cameraIds[0] : null;
            } catch (CameraAccessException e) {
                Log.e(TAG, "Camera error: " + e.getMessage());
                return null;
            }
        }

        // 停止提醒效果（复用 AlertActivity）
        private void stopAlert() {
            // 停止震动
            if (vibrator != null && isVibrating) {
                vibrator.cancel();
                isVibrating = false;
            }

            // 停止闪光线程
            if (flashThread != null && flashThread.isAlive()) {
                flashThread.interrupt();
            }
            if (isFlashOn && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    toggleFlashlight(false);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Stop flash error: " + e.getMessage());
                }
            }
        }

        // 其他方法（弹窗、通知构建等）保持不变
        private void showAlertDialog() {
            Intent dialogIntent = new Intent(this, ReminderDialogActivity.class);
            dialogIntent.putExtra("name", name);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 针对Android 8.0及以上版本
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //     dialogIntent.addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER);
            // }

            startActivity(dialogIntent);
        }
        private Notification createForegroundNotification() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "schedule_channel")
                    .setContentTitle("正在提醒")
                    .setContentText("您有一个日程: " + name)
                    .setSmallIcon(R.mipmap.ic_lxq)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            return builder.build();
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            stopAlert(); // 销毁时停止所有效果
            // 注销广播接收器
            unregisterReceiver(dialogDismissReceiver);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}