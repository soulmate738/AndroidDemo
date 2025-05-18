package com.example.sparkchaindemo.fragment;

import static android.app.Activity.RESULT_OK;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.YytActivity;
import com.example.sparkchaindemo.ai.tts.TTSParams;
import com.example.sparkchaindemo.entity.VoiceItem;
import com.iflytek.sparkchain.core.tts.OnlineTTS;
import com.iflytek.sparkchain.core.tts.TTS;
import com.iflytek.sparkchain.core.tts.TTSCallbacks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TTSFragment extends Fragment {
    private String TAG = "TTSFragment";
    private EditText mText, mName;
    private CardView mStartTTS, mSaveVoice;
    private TextView mDuration;
    private String fileName = "";
    private String VoicePackageName;
    private String filePath;
    private String text;
    private OnlineTTS mOnlineTTS;
    private TTSParams mTTSParams = new TTSParams();
    private int sampleRate = 16000; // 合成音频的采样率，支持8K 16K音频，具体参见集成文档
    private String sdPath;
    private long startTime;
    private long endTime;
    private List<byte[]> audioDataList = new ArrayList<>(); // 存储音频数据片段
    private boolean isSaving = false;

    private long currentDuration = 0; // 当前已合成音频时长（毫秒）
    private boolean isUpdatingDuration = false; // 时长更新标记
    private AudioTrack playbackAudioTrack; // 播放时长点击时的音频轨道
    private Handler durationUpdateHandler = new Handler(); // 时长更新Handler
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            filePath = intent.getStringExtra("VoicePackage");
            VoicePackageName = intent.getStringExtra("VoicePackageName");
            sdPath = filePath;
            Log.d(TAG, "sdPath: " + sdPath);
            Toast.makeText(this.getActivity(),
                    "Received: " + filePath,
                    Toast.LENGTH_SHORT).show();
        }
        View view = inflater.inflate(R.layout.fragment_t_t_s, container, false);
        mDuration = view.findViewById(R.id.tv_duration);
        mText = view.findViewById(R.id.tv_tts_content);
        mName = view.findViewById(R.id.et_file_name);
        mStartTTS = view.findViewById(R.id.start_tts);
        mSaveVoice = view.findViewById(R.id.save_voice);
        mAudioPlayThread.start();
        initListener();
        return view;
    }

    private void initListener() {
        mStartTTS.setOnClickListener(v -> {
            text = mText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this.getActivity(),
                        "请输入要转换的文字",
                        Toast.LENGTH_SHORT).show();
            } else {
                startTTS();
            }
        });

        mSaveVoice.setOnClickListener(v -> {
            if (isPlaying) {
                Toast.makeText(getActivity(), "正在合成语音，请等待完成", Toast.LENGTH_SHORT).show();
                return;
            }

            if (audioDataList.isEmpty()) {
                Toast.makeText(getActivity(), "没有可保存的音频", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isSaving) {
                Toast.makeText(getActivity(), "正在保存音频，请稍候", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAudioFile();
        });
    }

    private void startTTS() {
        // 重置状态
        audioDataList.clear();
        mDuration.setText("00:00");
        startTime = System.currentTimeMillis();

        Log.d(TAG, "start-->");
        Log.d(TAG, "vcn = " + mTTSParams.vcn);     //发音人
        Log.d(TAG, "pitch = " + mTTSParams.pitch); //语调
        Log.d(TAG, "speed = " + mTTSParams.speed); //语速
        Log.d(TAG, "volume = " + mTTSParams.volume);//音量
        text = mText.getText().toString();
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
                // updateDuration();
                mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
            }
            // 计算新增音频时长（16K采样率，16位单声道，每样本占2字节）
            int sampleSize = 2; // 16位=2字节
            int channels = 1; // 单声道
            int bytesPerSecond = sampleRate * channels * sampleSize;
            int dataLength = result.getLen();
            long addedDuration = (dataLength / (channels * sampleSize)) * 1000 / sampleRate; // 转换为毫秒
            currentDuration += addedDuration;

            // 更新时长显示（主线程）
            updateDurationDisplay(currentDuration);
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
    private void updateDurationDisplay(long durationMs) {
        int seconds = (int) (durationMs / 1000) % 60;
        int minutes = (int) (durationMs / (1000 * 60));
        final String timeStr = String.format("%02d:%02d", minutes, seconds);

        // 主线程更新UI
        getActivity().runOnUiThread(() -> {
            mDuration.setText(timeStr);
            // 注册时长点击事件
            mDuration.setOnClickListener(v -> playSavedAudio());
        });
    }
    private void playSavedAudio() {
        if (audioDataList.isEmpty()) {
            Toast.makeText(getActivity(), "无合成音频可播放", Toast.LENGTH_SHORT).show();
            return;
        }

        if (playbackAudioTrack != null && playbackAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            playbackAudioTrack.play();
            return;
        }

        new Thread(() -> {
            int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT);
            playbackAudioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    minBufferSize,
                    AudioTrack.MODE_STREAM
            );

            playbackAudioTrack.play();
            for (byte[] data : audioDataList) {
                if (playbackAudioTrack != null && playbackAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    playbackAudioTrack.write(data, 0, data.length);
                }
            }
            playbackAudioTrack.stop();
            playbackAudioTrack.release();
            playbackAudioTrack = null;
        }).start();
    }
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
        if (playbackAudioTrack != null) {
            playbackAudioTrack.stop();
            playbackAudioTrack.release();
            playbackAudioTrack = null;
        }
        durationUpdateHandler.removeCallbacksAndMessages(null);
    }


    private void saveAudioFile() {
        isSaving = true;
        new Thread(() -> {
            try {
                // 获取保存目录
                File saveDir = new File(sdPath);
                if (!saveDir.exists()) {
                    if (!saveDir.mkdirs()) {
                        Log.e(TAG, "创建保存目录失败");
                        showToast("创建保存目录失败");
                        isSaving = false;
                        return;
                    }
                }

                // 获取文件名
                String fileName = mName.getText().toString().trim();
                if (fileName.isEmpty()) {
                    fileName = "tts_audio_" + System.currentTimeMillis();
                }

                // 确保文件名有扩展名
                if (!fileName.endsWith(".wav")) {
                    fileName += ".wav";
                }

                File audioFile = new File(saveDir, fileName);
                Log.d(TAG, "保存音频文件: " + audioFile.getAbsolutePath());

                // 写入WAV文件头和音频数据
                writeWavFile(audioFile, audioDataList, sampleRate);

                showToast("音频保存成功：" + audioFile.getAbsolutePath());
                Log.d(TAG, "音频保存成功");
                // 在 AddItemActivity 中保存数据后
                Intent resultIntent = new Intent(getActivity(), YytActivity.class);
                resultIntent.putExtra("item",fileName); // newItem 是你创建的新数据项
                resultIntent.putExtra("VoicePackage",VoicePackageName);
                getActivity().setResult(RESULT_OK, resultIntent);
                startActivity(resultIntent);
                getActivity().finish();
            } catch (Exception e) {
                Log.e(TAG, "保存音频失败: " + e.getMessage());
                e.printStackTrace();
                showToast("音频保存失败：" + e.getMessage());
            } finally {
                isSaving = false;
            }
        }).start();
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