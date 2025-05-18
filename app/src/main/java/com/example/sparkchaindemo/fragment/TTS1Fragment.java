package com.example.sparkchaindemo.fragment;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.SttActivity;
import com.example.sparkchaindemo.activity.YytActivity;
import com.example.sparkchaindemo.adapter.FaxianFragmentAdapter;
import com.example.sparkchaindemo.adapter.STTAndTTSFragmentAdapter;
import com.example.sparkchaindemo.ai.tts.TTSParams;
import com.google.android.material.tabs.TabLayout;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;
import com.iflytek.sparkchain.core.tts.OnlineTTS;
import com.iflytek.sparkchain.core.tts.TTS;
import com.iflytek.sparkchain.core.tts.TTSCallbacks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TTS1Fragment extends Fragment {
    private static final String TAG = "TTS1Fragment";
    private EditText mEditText;
    private CardView mStart;
    private String text;
    private OnlineTTS mOnlineTTS;
    private TTSParams mTTSParams = new TTSParams();
    private int sampleRate = 16000; // 合成音频的采样率，支持8K 16K音频，具体参见集成文档
    private long startTime;
    private long endTime;
    private List<byte[]> audioDataList = new ArrayList<>(); // 存储音频数据片段
    private boolean isSaving = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_tts1, container, false);
        mEditText = view.findViewById(R.id.tts_content);
        mStart = view.findViewById(R.id.start_tts1);
        mAudioPlayThread.start();
        initData();
        initListener();
        return view;
    }

    private void initData() {


    }

    private void initListener() {
        mStart.setOnClickListener(v -> {
            text = mEditText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this.getActivity(),
                        "请输入要转换的文字",
                        Toast.LENGTH_SHORT).show();
            } else {
                startTTS();
            }
        });

    }
    private void startTTS() {
        // 重置状态
        audioDataList.clear();
        startTime = System.currentTimeMillis();

        Log.d(TAG, "start-->");
        Log.d(TAG, "vcn = " + mTTSParams.vcn);     //发音人
        Log.d(TAG, "pitch = " + mTTSParams.pitch); //语调
        Log.d(TAG, "speed = " + mTTSParams.speed); //语速
        Log.d(TAG, "volume = " + mTTSParams.volume);//音量
        text = mEditText.getText().toString();
        Log.d(TAG, "text = " + text);

        if (audioTrack == null) {
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_INIT);
        } else {
            if (isPlaying) {
                stop();
            }
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_START);
        }

        /******************
         * 在线合成发音人设置接口，发音人可从构造方法中设入，也可通过功能参数动态修改。
         * xiaoyan，晓燕，⼥：中⽂
         * *******************/
        mOnlineTTS = new OnlineTTS(mTTSParams.vcn);
        /********************
         * aue(必填):
         * 音频编码，可选值：raw：未压缩的pcm
         * lame：mp3 (当aue=lame时需传参sfl=1)
         * speex-org-wb;7： 标准开源speex（for speex_wideband，即16k）数字代表指定压缩等级（默认等级为8）
         * speex-org-nb;7： 标准开源speex（for speex_narrowband，即8k）数字代表指定压缩等级（默认等级为8）
         * speex;7：压缩格式，压缩等级1~10，默认为7（8k讯飞定制speex）
         * speex-wb;7：压缩格式，压缩等级1~10，默认为7（16k讯飞定制speex）
         * ****************************/
        mOnlineTTS.aue("raw");
        mOnlineTTS.auf("audio/L16;rate=" + sampleRate);
        mOnlineTTS.speed(mTTSParams.speed);//语速：0对应默认语速的1/2，100对应默认语速的2倍。最⼩值:0, 最⼤值:100
        mOnlineTTS.pitch(mTTSParams.pitch);//语调：0对应默认语速的1/2，100对应默认语速的2倍。最⼩值:0, 最⼤值:100
        mOnlineTTS.volume(mTTSParams.volume);//音量：0是静音，1对应默认音量1/2，100对应默认音量的2倍。最⼩值:0, 最⼤值:100
        mOnlineTTS.bgs(0);//合成音频的背景音 0:无背景音（默认值） 1:有背景音
        mOnlineTTS.registerCallbacks(mTTSCallback);
        int ret = mOnlineTTS.aRun(text);
        if (ret != 0) {
            Log.d(TAG, "合成出错!ret=" + ret);
            Toast.makeText(getActivity(), "语音合成失败，错误码：" + ret, Toast.LENGTH_SHORT).show();
        }
    }

    TTSCallbacks mTTSCallback = new TTSCallbacks() {
        @Override
        public void onResult(TTS.TTSResult result, Object o) {
            //解析获取的交互结果，示例展示所有结果获取，开发者可根据自身需要，选择获取。
            byte[] audio = result.getData();//音频数据
            int len = result.getLen();//音频数据长度
            int status = result.getStatus();//数据状态
            String ced = result.getCed();//进度
            String sid = result.getSid();//sid

            // 保存音频数据
            if (audio != null && len > 0) {
                byte[] dataCopy = new byte[len];
                System.arraycopy(audio, 0, dataCopy, 0, len);
                audioDataList.add(dataCopy);
            }

            Bundle bundle = new Bundle();
            bundle.putByteArray("audio", audio);
            Message msg = mAudioPlayHandler.obtainMessage();
            msg.what = AUDIOPLAYER_WRITE;
            msg.obj = bundle;
            mAudioPlayHandler.sendMessage(msg);

            if (status == 2) {
                //音频合成回调结束状态，注意，此状态不是播报完成状态
                endTime = System.currentTimeMillis();
                updateDuration();
                mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
            }
        }

        @Override
        public void onError(TTS.TTSError ttsError, Object o) {
            int errCode = ttsError.getCode();//错误码
            String errMsg = ttsError.getErrMsg();//错误信息
            String sid = ttsError.getSid();//sid
            String msg = "合成出错！code:" + errCode + ",msg:" + errMsg + ",sid:" + sid;
            Log.d(TAG, "onError:errCode:" + errCode + ",errMsg:" + errMsg);
            Log.d(TAG, msg);
            if (isPlaying) {
                //如果此时已经播报，则停止播报
                stop();
            }
            Toast.makeText(getActivity(), "语音合成错误：" + errMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private void stop() {
        if (mOnlineTTS != null) {
            mAudioPlayHandler.removeCallbacksAndMessages(null);
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
            mOnlineTTS.stop();
            mOnlineTTS = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WXW", "onDestory");
        if (isPlaying) {
            isPlaying = false;
            mAudioPlayHandler.removeCallbacksAndMessages(null);
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
        }
    }

    private void updateDuration() {
        long duration = endTime - startTime;
        int seconds = (int) (duration / 1000) % 60;
        int minutes = (int) ((duration / (1000 * 60)) % 60);
        // String timeStr = String.format("%02d:%02d", minutes, seconds);

        // getActivity().runOnUiThread(() -> {
        //     mDuration.setText(timeStr);
        // });
    }

    private File getAudioSaveDirectory() {
        // 使用应用专属目录，不需要WRITE_EXTERNAL_STORAGE权限
        return new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "TTS_Audio");
    }

    private void writeWavFile(File file, List<byte[]> audioDataList, int sampleRate) throws IOException {
        // 计算总音频数据长度
        int totalDataLength = 0;
        for (byte[] data : audioDataList) {
            totalDataLength += data.length;
        }

        // WAV文件头信息
        int channels = 1; // 单声道
        int bitDepth = 16; // 16位

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 写入WAV文件头
            writeWavHeader(fos, totalDataLength, sampleRate, channels, bitDepth);

            // 写入音频数据
            for (byte[] data : audioDataList) {
                fos.write(data);
            }
        }
    }

    private void writeWavHeader(FileOutputStream fos, int dataLength, int sampleRate, int channels, int bitDepth) throws IOException {
        int byteRate = sampleRate * channels * bitDepth / 8;
        int blockAlign = channels * bitDepth / 8;

        // RIFF header
        fos.write("RIFF".getBytes());
        fos.write(intToByteArray(36 + dataLength), 0, 4);
        fos.write("WAVE".getBytes());

        // fmt chunk
        fos.write("fmt ".getBytes());
        fos.write(intToByteArray(16), 0, 4); // 4 bytes: size of 'fmt ' chunk
        fos.write(shortToByteArray((short) 1), 0, 2); // 2 bytes: format (1 is PCM)
        fos.write(shortToByteArray((short) channels), 0, 2); // 2 bytes: number of channels
        fos.write(intToByteArray(sampleRate), 0, 4); // 4 bytes: sample rate
        fos.write(intToByteArray(byteRate), 0, 4); // 4 bytes: byte rate
        fos.write(shortToByteArray((short) blockAlign), 0, 2); // 2 bytes: block align
        fos.write(shortToByteArray((short) bitDepth), 0, 2); // 2 bytes: bits per sample

        // data chunk
        fos.write("data".getBytes());
        fos.write(intToByteArray(dataLength), 0, 4); // 4 bytes: size of 'data' chunk
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        });
    }

    // 辅助方法：将int转换为字节数组（小端序）
    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }

    // 辅助方法：将short转换为字节数组（小端序）
    private byte[] shortToByteArray(short value) {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF)
        };
    }

    private static final int AUDIOPLAYER_INIT = 0x0000;
    private static final int AUDIOPLAYER_START = 0x0001;
    private static final int AUDIOPLAYER_WRITE = 0x0002;
    private static final int AUDIOPLAYER_END = 0x0003;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO; // 单声道输出
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; // PCM 16位编码
    private AudioTrack audioTrack;
    private Handler mAudioPlayHandler;
    private boolean isPlaying = false;
    int count = 0;
    private Thread mAudioPlayThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mAudioPlayHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case AUDIOPLAYER_INIT:
                            Log.d(TAG, "audioInit");
                            int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT);
                            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize, AudioTrack.MODE_STREAM);
                            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_START);
                            break;
                        case AUDIOPLAYER_START:
                            Log.d(TAG, "audioStart");
                            if (audioTrack != null) {
                                isPlaying = true;
                                audioTrack.play();
                            }
                            break;
                        case AUDIOPLAYER_WRITE:
                            count++;
                            if (count % 5 == 0) {
                                Log.d(TAG, "audioWrite");
                                count = 0;
                            }
                            Bundle bundle = (Bundle) msg.obj;
                            byte[] audioData = bundle.getByteArray("audio");
                            if (audioTrack != null && audioData != null && audioData.length > 0) {
                                audioTrack.write(audioData, 0, audioData.length);
                            }
                            break;
                        case AUDIOPLAYER_END:
                            Log.d(TAG, "audioEnd");
                            if (audioTrack != null) {
                                audioTrack.stop();
                                isPlaying = false;
                            }
                            break;
                    }
                }
            };
            Looper.loop();
        }
    });

}