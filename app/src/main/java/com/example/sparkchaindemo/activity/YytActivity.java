package com.example.sparkchaindemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.VoiceAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.entity.VoiceItem;
import com.example.sparkchaindemo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class YytActivity extends BaseActivity implements VoiceAdapter.OnItemActionListener{
    private static final String TAG = "YytActivity";
    private ImageView mBack, mAdd;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private VoiceAdapter adapter;
    private List<VoiceItem> voiceList = new ArrayList<VoiceItem>();
    String path = "/sdcard/LxqVoice";
    String VoicePackage;
    private boolean isPlaying = false;
    private boolean isRecording = false;
    private MediaPlayer mediaPlayer;
    private  String sdPath;
    private static final int REQUEST_ADD_ITEM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            VoicePackage = intent.getStringExtra("VoicePackage");
            Toast.makeText(this,
                    "Received: " + VoicePackage ,
                    Toast.LENGTH_SHORT).show();
        }
        path = path + "/" + VoicePackage;
        List<String> fileNamesWithoutExt = FileUtils.getFileNames(path);

        // 输出结果示例
        for (String name : fileNamesWithoutExt) {
            voiceList.add(new VoiceItem( name));
            Log.d("FileName", "不含后缀: " + name);
        }
        initView();
        initData();
        initListener();
    }
    @Override
    protected void initView() {
        setContentView(R.layout.activity_yyt);
        mBack = findViewById(R.id.iv_back);
        mAdd = findViewById(R.id.iv_add);
        mRecyclerView = findViewById(R.id.rv_yyt);
        mTitle = findViewById(R.id.tv_title);
        mTitle.setText(VoicePackage);

    }

    @Override
    protected void initData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoiceAdapter((Context) this, this);
        adapter.setVoiceList(voiceList);
        mRecyclerView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ITEM && resultCode == RESULT_OK && data != null) {
            // 从 Intent 中获取新数据
            VoiceItem newItem = data.getParcelableExtra("item");
            // 更新列表
            if (newItem != null) {
                voiceList.add(newItem); // items 是 RecyclerView 的数据源
                adapter.notifyItemInserted(voiceList.size() - 1);
                mRecyclerView.scrollToPosition(voiceList.size() - 1); // 滚动到新添加的项
            }
        }
    }


    @Override
    protected void initListener() {
        mBack.setOnClickListener(v -> finish());
        mAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVoiceActivity.class);
            intent.putExtra("VoicePackage", path);
            intent.putExtra("VoicePackageName", VoicePackage);
            startActivityForResult(intent, REQUEST_ADD_ITEM);
            finish();
        });
    }

    @Override
    public void onEdit(VoiceItem voice, int position) {
        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_voice_package, null);
        dialog.setContentView(view);
        EditText nameEditText = view.findViewById(R.id.et_voice_package_name);
        nameEditText.setText(voice.getName());
        nameEditText.setSelection(voice.getName().length());
        CardView saveButton = view.findViewById(R.id.save);
        ImageView closeButton = view.findViewById(R.id.close_image);
        TextView titleText = view.findViewById(R.id.title_text);
        titleText.setText("编辑语音条");
        String oldName = voice.getName();
        saveButton.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            if (!newName.isEmpty()) {
                if (FileUtils.renameFile(path, oldName, newName)) {
                    Toast.makeText(this, "文件重命名成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "文件重命名失败，可能缺少权限", Toast.LENGTH_SHORT).show();
                }
                voice.setName(newName);
                adapter.notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "请输入语音条名称", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDelete(VoiceItem voice, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除语音条 \"" + voice.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    String name = voice.getName();
                    if (FileUtils.deleteFile(path, name)) {
                        Toast.makeText(this, "文件删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "文件删除失败，可能缺少权限", Toast.LENGTH_SHORT).show();
                    }
                    voiceList.remove(position);
                    adapter.notifyItemRemoved(position);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onPlay(VoiceItem voiceItem, int position) {
        if (path != null) {
            if (isPlaying) {
                stopPlaying();
            } else {
                sdPath = path + "/" + voiceItem.getName();
                startPlaying();

            }
        }
    }

    private void startPlaying() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(sdPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}