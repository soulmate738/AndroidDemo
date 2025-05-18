package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;


public class ClassActivity extends BaseActivity {

    private static final String TAG = "ClassActivity";
    private String title;
    private String content;
    private int resource;
    private VideoView videoView;
    private ImageView backButton;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private LinearLayout controlLayout;
    private ImageView playPauseButton;
    // private ImageView fullScreenButton;
    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private ImageView speedButton;
    private float currentSpeed = 1.0f;
    private final Handler handler = new Handler();
    private boolean isFullScreen = false;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        resource = intent.getIntExtra("resource", 0);
        initView();
        initData();
        setupVideoView();
        setupEventListeners();
        initListener();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_class);
        backButton = findViewById(R.id.backButton);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        videoView = findViewById(R.id.videoView);
        controlLayout = findViewById(R.id.controlLayout);
        playPauseButton = findViewById(R.id.playPauseButton);
        // fullScreenButton = findViewById(R.id.fullScreenButton);
        seekBar = findViewById(R.id.seekBar);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);
        speedButton = findViewById(R.id.speedButton);

        // 设置标题和描述
        titleTextView.setText(title);
        descriptionTextView.setText(content);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
    private void setupVideoView() {
        // 设置视频源，这里使用本地资源，你可以替换为网络视频链接
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resource));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(videoView.getDuration());
                totalTimeTextView.setText(formatTime(videoView.getDuration()));
                playPauseButton.setImageResource(R.drawable.ic_play);
                // fullScreenButton.setImageResource(R.drawable.ic_fullscreen);
                updateSpeedIcon();
                isPlaying = false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPauseButton.setImageResource(R.drawable.ic_play);
                isPlaying = false;
            }
        });
    }
    private void toggleControlVisibility() {
        if (controlLayout.getVisibility() == View.VISIBLE) {
            controlLayout.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            controlLayout.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else {
            controlLayout.setVisibility(View.VISIBLE);
            controlLayout.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        }
    }
    private void setupEventListeners() {
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlVisibility();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                } else {
                    videoView.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                    updateSeekBar();
                }
            }
        });

        // fullScreenButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         toggleFullScreen();
        //     }
        // });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                if (isPlaying) {
                    updateSeekBar();
                }
            }
        });

        speedButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (currentSpeed == 1.0f) {
                        currentSpeed = 1.5f;
                    } else if (currentSpeed == 1.5f) {
                        currentSpeed = 2.0f;
                    } else {
                        currentSpeed = 1.0f;
                    }
                    setPlaybackSpeed(currentSpeed);
                    updateSpeedIcon();

            }
        });
    }
    private void setPlaybackSpeed(float speed) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                // 通过反射获取 VideoView 内部的 MediaPlayer
                java.lang.reflect.Field field = VideoView.class.getDeclaredField("mMediaPlayer");
                field.setAccessible(true);
                MediaPlayer mediaPlayer = (MediaPlayer) field.get(videoView);

                if (mediaPlayer != null) {
                    mediaPlayer.setPlaybackParams(
                            mediaPlayer.getPlaybackParams().setSpeed(speed));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                // 处理反射失败的情况，可以显示一个提示给用户
                Toast.makeText(this, "倍速功能在当前设备上不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 处理 Android 6.0 以下版本的情况
            Toast.makeText(this, "倍速功能需要 Android 6.0 及以上版本", Toast.LENGTH_SHORT).show();
        }
    }
    // private void toggleFullScreen() {
    //     if (isFullScreen) {
    //         // 退出全屏
    //         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    //         fullScreenButton.setImageResource(R.drawable.ic_fullscreen);
    //         titleTextView.setVisibility(View.VISIBLE);
    //         descriptionTextView.setVisibility(View.VISIBLE);
    //     } else {
    //         // 进入全屏
    //         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    //         fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit);
    //         titleTextView.setVisibility(View.GONE);
    //         descriptionTextView.setVisibility(View.GONE);
    //     }
    //     isFullScreen = !isFullScreen;
    // }

    private void updateSpeedIcon() {
        switch ((int) (currentSpeed * 10)) {
            case 10:
                speedButton.setImageResource(R.mipmap.ic_speed_1x);
                break;
            case 15:
                speedButton.setImageResource(R.mipmap.ic_speed_1_5x);
                break;
            case 20:
                speedButton.setImageResource(R.mipmap.ic_speed_12x);
                break;
        }
    }

    private void updateSeekBar() {
        if (videoView.isPlaying()) {
            seekBar.setProgress(videoView.getCurrentPosition());
            currentTimeTextView.setText(formatTime(videoView.getCurrentPosition()));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            }, 1000);
        }
    }

    private String formatTime(int time) {
        int seconds = time / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // @Override
    // public void onBackPressed() {
    //     if (isFullScreen) {
    //         // toggleFullScreen();
    //     } else {
    //         super.onBackPressed();
    //     }
    // }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}