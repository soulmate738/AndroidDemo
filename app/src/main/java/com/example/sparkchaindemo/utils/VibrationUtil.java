package com.example.sparkchaindemo.utils;

/**
 * @author anjia
 */
/**
 * 文件名: VibrationUtil.java
 * 描述: 封装手机震动功能，支持不同Android版本的震动方法
 */

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class VibrationUtil {

    private static final String TAG = "VibrationUtil";

    /**
     * 执行一次性震动，震动时长为milliseconds毫秒，幅度使用默认幅度
     *
     * @param context      应用上下文
     * @param milliseconds 震动时长，单位毫秒
     */
    public static void vibrateOnce(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            Log.e(TAG, "设备不支持震动");
            return;
        }

        // 判断系统版本，选择合适的震动方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 使用VibrationEffect创建单次震动效果（默认震动力度）
            VibrationEffect effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            // 旧版使用直接传入震动时长
            vibrator.vibrate(milliseconds);
        }
    }

    /**
     * 执行自定义震动模式
     * 该方法通过参数定义震动与休息的时长数组，以及重复索引
     *
     * @param context   应用上下文
     * @param pattern   长度数组，单位毫秒。例如：{0, 200, 100, 300} 表示延时0毫秒开始震动200毫秒，休息100毫秒后震动300毫秒
     * @param repeat    重复模式的索引，-1表示不重复；如果大于或等于0则从该索引开始重复
     */
    public static void vibratePattern(Context context, long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            Log.e(TAG, "设备不支持震动");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 使用VibrationEffect创建自定义震动模式，amplitudes为null表示均用默认震动力度
            VibrationEffect effect = VibrationEffect.createWaveform(pattern, repeat);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(pattern, repeat);
        }
    }

    /**
     * 停止震动
     *
     * @param context 应用上下文
     */
    public static void cancelVibration(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}