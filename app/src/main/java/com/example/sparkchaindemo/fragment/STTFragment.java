package com.example.sparkchaindemo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sparkchaindemo.R;

import com.example.sparkchaindemo.activity.SttActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;


import java.util.ArrayList;
import java.util.List;

public class STTFragment extends Fragment {
    private static final String TAG = "STTFragment";
    private ImageView mBack;
    private CardView mStart, mStop;
    private TextView mConnect;
    private RTASR mRTASR;
    private String RTASRAPIKEY;
    boolean isrun = false;
    String asrFinalResult = "识别结果：\n";
    private String startMode = "NONE";
    private SttActivity.ASRMode language = SttActivity.ASRMode.CN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件

        View view = inflater.inflate(R.layout.fragment_stt, container, false);
        mStart = view.findViewById(R.id.start_stt);
        mStop = view.findViewById(R.id.stop_stt);
        mConnect = view.findViewById(R.id.tv_stt_content);
        initData();
        initListener();
        return view;
    }

    private void initData() {
        // System.loadLibrary("libs/libs/SparkChain.aar");
        RTASRAPIKEY = getResources().getString(R.string.RTASRAPIKEY);
        mRTASR = new RTASR(RTASRAPIKEY);//创建RTASR实例
        mRTASR.registerCallbacks(mRtAsrCallbacks);//注册监听回调

    }

    private void initListener() {
        mStart.setOnClickListener(v -> {
            // 开始录音
            mConnect.setText("识别结果：\n");
            asrFinalResult = "识别结果：\n";

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getPermission();
                }
            }).start();
        });
        mStop.setOnClickListener(v -> {
            // 停止录音
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mRTASR!=null&&isrun){
                        if("FILE".equals(startMode)){
                            mRTASR.stop();
                        }else{
                            mRTASR.stopListener();
                        }
                        startMode = "NONE";
                        isrun = false;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // btn_audio_start.setText("麦克风识别");
                                mStart.setEnabled(true);
                            }
                        });
                    }
                }
            }).start();

        });
        // mConnect.setOnClickListener(v -> {
        //     // 显示转文字内容
        // });

    }
    RTASRCallbacks mRtAsrCallbacks = new RTASRCallbacks() {
        @Override
        public void onResult(RTASR.RtAsrResult result, Object usrTag) {
            // 以下信息需要开发者根据自身需求，如无必要，可不需要解析执行。
            String data = result.getData();                     // 识别结果
            String rawResult = result.getRawResult();                // 云端识别的原始结果
            int status = result.getStatus();                   // 数据状态
            String sid = result.getSid();                      // 交互sid

            getActivity().runOnUiThread(new Runnable() {
                // 结果显示在界面上
                @Override
                public void run() {
                    if (status == 1) {// 子句流式结果
                        String asrText = asrFinalResult + data;
                        mConnect.setText(asrText);
                        toend(mConnect);
                    } else if (status == 2) {// 子句plain结果
                        asrFinalResult = asrFinalResult + data;
                    } else if (status == 3) {// end结果
                        mConnect.setText(asrFinalResult);
                        toend(mConnect);
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
                            startMode = "NONE";
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mStart.setEnabled(true);
                                    mStart.setEnabled(true);
                                }
                            });
                            isrun = false;
                        }
                    }
                }
            });
        }

        @Override
        public void onError(RTASR.RtAsrError error, Object usrTag) {
            int code = error.getCode();    // 错误码
            String msg = error.getErrMsg();  // 错误信息
            String sid = error.getSid();     // 交互sid
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
                startMode = "NONE";
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStart.setEnabled(true);
                        mStart.setEnabled(true);
                    }
                });
                isrun = false;
            }
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }
    };

    /*************************
     * 显示控件自动下移
     * *******************************/
    public void toend(TextView tv) {
        int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
        if (scrollAmount > 0) {
            tv.scrollTo(0, scrollAmount + 10);
        }
    }

    private void getPermission() {
        XXPermissions.with(getActivity()).permission(Permission.RECORD_AUDIO).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG, "SDK获取系统权限成功:" + all);
                for (int i = 0; i < granted.size(); i++) {
                    Log.d(TAG, "获取到的权限有：" + granted.get(i));
                }
                if (all) {
                    runRtasr_Audio(language);
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    Log.e(TAG, "onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(getActivity(), denied);
                } else {
                    Log.e(TAG, "onDenied:权限获取失败");
                }
            }
        });

    }

    int count = 0;// 用户自定义标识

    private void runRtasr_Audio(SttActivity.ASRMode mode) {
        if (isrun)
            return;
        count++;
        isrun = true;
        if (mRTASR == null) {
            mRTASR = new RTASR("be0dc8ddf2697ca01a62efdf9b190873");// 创建RTASR实例
            mRTASR.registerCallbacks(mRtAsrCallbacks);// 注册监听回调
        }
        if (mode == SttActivity.ASRMode.CN) {
            mRTASR.lang("cn");// 转写语种 cn:中文,en:英文。其他语种参考集成文档
        } else {
            mRTASR.lang("en");// 转写语种 cn:中文,en:英文。其他语种参考集成文档
        }

        asrFinalResult = "识别结果：\n";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnect.setText(asrFinalResult);
                mStart.setEnabled(false);
                mStart.setEnabled(false);
            }
        });
        startMode = "AUDIO";
        int ret = mRTASR.startListener(count + "");
        Log.d(TAG, "mRTASR.start ret:" + ret + "-count:" + count);
        if (ret != 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isrun = false;
                    mConnect.setText("转写启动出错，错误码:" + ret);

                }
            });
        }
    }

}