package com.example.sparkchaindemo.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.SttActivity;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;

import java.util.List;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


public class FloatingWindowService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private TextView mtextView;
    private ImageView closeButton, resizeButton;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private float initialWidth, initialHeight;
    private boolean isResizing = false;
    private String TAG = "FloatingWindowService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "FloatingWindowChannel";
    private RTASR mRTASR;
    private String RTASRAPIKEY;
    private boolean isrun = false;
    private String asrFinalResult = "识别结果：\n";
    private String startMode = "NONE";
    private SttActivity.ASRMode language = SttActivity.ASRMode.CN;

    // 添加主线程 Handler
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

// 创建前台服务通知
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
        // 初始化 WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 创建悬浮窗布局
        floatingView = LayoutInflater.from(this).inflate(R.layout.flow_layout, null);

        // 设置布局参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // 初始位置
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        // 获取视图组件
        mtextView = floatingView.findViewById(R.id.textView);
        closeButton = floatingView.findViewById(R.id.closeButton);
        resizeButton = floatingView.findViewById(R.id.scaleButton);
        initData();
        // 设置关闭按钮点击事件
        closeButton.setOnClickListener(v -> {
            stopRecognition();
            stopSelf();
        });

        // 设置缩放按钮触摸事件
        resizeButton.setOnTouchListener((v, event) -> handleResizeTouch(event));

        // 设置拖动事件（除按钮外的区域）
        floatingView.setOnTouchListener((v, event) -> handleDragTouch(event));

        // 添加悬浮窗到窗口管理器
        windowManager.addView(floatingView, params);
    }

    private void initData() {
        RTASRAPIKEY = getResources().getString(R.string.RTASRAPIKEY);
        mRTASR = new RTASR(RTASRAPIKEY);
        mRTASR.registerCallbacks(mRtAsrCallbacks);
        mtextView.setText(asrFinalResult);
        startPermissionCheck();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Floating Window Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("悬浮窗服务运行中")
                .setContentText("点击返回应用")
                .setSmallIcon(R.mipmap.ic_lxq) // 使用你自己的图标
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    // RTASR 回调
    private final RTASRCallbacks mRtAsrCallbacks = new RTASRCallbacks() {
        @Override
        public void onResult(RTASR.RtAsrResult result, Object usrTag) {
            final String data = result.getData();
            final int status = result.getStatus();

            // 通过 Handler 更新 UI
            mainHandler.post(() -> handleAsrResult(status, data));
        }

        @Override
        public void onError(RTASR.RtAsrError error, Object usrTag) {
            // mainHandler.post(this::stopRecognition);
            Log.e(TAG, "错误码：" + error.getCode() + "，错误信息：" + error.getErrMsg());
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        // 其他回调方法保持不变
    };

    private void handleAsrResult(int status, String data) {
        if (status == 1) {// 子句流式结果
            String asrText = asrFinalResult + data;
            mtextView.setText(asrText);
            toend(mtextView);
        } else if (status == 2) {// 子句plain结果
            asrFinalResult = asrFinalResult + data;
        } else if (status == 3) {// end结果
            mtextView.setText(asrFinalResult);
            toend(mtextView);
            if (isrun) {
                if ("AUDIO".equals(startMode)) {
                    if (mRTASR != null) {
                        mRTASR.stopListener();
                    }
                } else {
                    if (mRTASR != null) {
                        mRTASR.stop();// 停止
                    }
                }
            }
        }
    }

    // 权限检查
    private void startPermissionCheck() {
        // XXPermissions.with(this) // 使用 Service 上下文
        //         .permission(Permission.RECORD_AUDIO)
        //         .request(new OnPermission() {
        //             @Override
        //             public void hasPermission(@NonNull List<String> granted, boolean all) {
        //                 if (all) {
        //                     startRecognition(language);
        //                 }
        //             }
        //
        //             @Override
        //             public void noPermission(@NonNull List<String> denied, boolean quick) {
        //                 Log.e(TAG, "权限获取失败");
        //             }
        //         });
        startRecognition(language);
    }

    // 启动识别
    private void startRecognition(SttActivity.ASRMode mode) {
        if (isrun) return;

        isrun = true;
        startMode = "AUDIO";

        if (mRTASR == null) {
            mRTASR = new RTASR(RTASRAPIKEY);
            mRTASR.registerCallbacks(mRtAsrCallbacks);
        }

        mRTASR.lang(mode == SttActivity.ASRMode.CN ? "cn" : "en");
        asrFinalResult = "识别结果：\n";
        int ret = mRTASR.startListener("" + System.currentTimeMillis());

        if (ret != 0) {
            Log.e(TAG, "转写启动出错，错误码: " + ret);
            stopRecognition();
        }
    }

    // 停止识别
    private void stopRecognition() {
        if (isrun && mRTASR != null) {
            if ("AUDIO".equals(startMode)) {
                mRTASR.stopListener();
            } else {
                mRTASR.stop();
            }
            isrun = false;
            startMode = "NONE";
        }
    }

    // 缩放处理
    private boolean handleResizeTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isResizing = true;
                initialWidth = floatingView.getWidth();
                initialHeight = floatingView.getHeight();
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isResizing) {
                    float deltaX = event.getRawX() - initialTouchX;
                    float deltaY = event.getRawY() - initialTouchY;

                    int newWidth = (int) Math.max(150, initialWidth + deltaX);
                    int newHeight = (int) Math.max(150, initialHeight + deltaY);

                    params.width = newWidth;
                    params.height = newHeight;
                    windowManager.updateViewLayout(floatingView, params);
                }
                return true;

            case MotionEvent.ACTION_UP:
                isResizing = false;
                return true;
        }
        return false;
    }

    // 拖动处理
    private boolean handleDragTouch(MotionEvent event) {
        if (isResizing) return false;

        if (isInButtonArea(event, closeButton) || isInButtonArea(event, resizeButton)) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(floatingView, params);
                return true;
        }
        return false;
    }

    // 判断触摸点是否在按钮区域
    private boolean isInButtonArea(MotionEvent event, View button) {
        float x = event.getX();
        float y = event.getY();
        return x >= button.getLeft() && x <= button.getRight() &&
                y >= button.getTop() && y <= button.getBottom();
    }

    // 文本滚动到底部
    public void toend(TextView tv) {
        if (tv != null) {
            // 检查 Layout 是否为 null
            if (tv.getLayout() != null) {
                int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
                if (scrollAmount > 0) {
                    tv.scrollTo(0, scrollAmount + 10);
                }
            } else {
                // 如果 Layout 为 null，等待布局完成后再执行滚动
                tv.post(() -> {
                    if (tv.getLayout() != null) {
                        int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
                        if (scrollAmount > 0) {
                            tv.scrollTo(0, scrollAmount + 10);
                        }
                    }
                });
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecognition();
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }
}
