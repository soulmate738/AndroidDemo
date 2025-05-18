package com.example.sparkchaindemo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.VoicePackageAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.entity.VoiceItem;
import com.example.sparkchaindemo.entity.VoicePackage;
import com.example.sparkchaindemo.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YybActivity extends BaseActivity implements VoicePackageAdapter.OnVoicePackageListener{

    private static final Logger log = LoggerFactory.getLogger(YybActivity.class);
    private ImageView mBack,mAdd;
    private RecyclerView mRecyclerView;
    private VoicePackageAdapter adapter;
    private List<VoicePackage> voicePackages = new ArrayList<>();
    String path = "/sdcard/LxqVoice";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_yyb);
        mBack = findViewById(R.id.iv_back);
        mAdd = findViewById(R.id.iv_add);
        mRecyclerView = findViewById(R.id.rv_yyb);



        List<String> subfolders = FileUtils.getSubfolderNames(path);

        if (subfolders.isEmpty()) {
            Toast.makeText(this, "没有找到子文件夹", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "没有找到子文件夹");
        } else {
            StringBuilder message = new StringBuilder("子文件夹列表:\n");
            for (String name : subfolders) {
                message.append("- ").append(name).append("\n");

                // 获取子文件夹下多少文件
                int fileCount = FileUtils.getFileNames(path + "/" + name).size();
                voicePackages.add(new VoicePackage(UUID.randomUUID().toString(), name,fileCount));
            }
            Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "子文件夹列表:\n" + message.toString());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initData() {
    // 初始化RecyclerView
    //     repository = new VoicePackageRepository(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 查看本地文件夹中是否有语音包

        adapter = new VoicePackageAdapter(this, voicePackages, this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(v -> finish());
        mAdd.setOnClickListener(v -> {
            showAddVoicePackageDialog();
        });
    }

    private boolean createSubfolder(String parentPath, String subfolderName) {
        // 构建完整路径
        File subfolder = new File(parentPath, subfolderName);

        // 检查父文件夹是否存在
        File parentDir = new File(parentPath);
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            Log.e("Storage", "父文件夹不存在或不是目录: " + parentPath);
            return false;
        }

        // 检查子文件夹是否已存在
        if (subfolder.exists()) {
            Log.d("Storage", "子文件夹已存在: " + subfolder.getAbsolutePath());
            return true; // 已存在视为成功
        }

        // 创建子文件夹
        return subfolder.mkdirs();
    }
    private void showAddVoicePackageDialog() {
        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_voice_package, null);
        dialog.setContentView(view);
        EditText nameEditText = view.findViewById(R.id.et_voice_package_name);
        CardView saveButton = view.findViewById(R.id.save);
        ImageView closeButton = view.findViewById(R.id.close_image);
        TextView titleText = view.findViewById(R.id.title_text);
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();

            if (!name.isEmpty()) {
                // 创建文件夹
                String path = "/sdcard/LxqVoice";
                boolean success = createSubfolder(path, name);
                VoicePackage newPackage = new VoicePackage(
                        UUID.randomUUID().toString(),
                        name,0
                );
                // repository.addVoicePackage(newPackage);
                voicePackages.add(newPackage);
                adapter.notifyItemInserted(voicePackages.size() - 1);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "请输入语音包名称", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    @Override
    public void onVoicePackageClick(int position) {
        VoicePackage voicePackage = voicePackages.get(position);
        Intent intent = new Intent(this, YytActivity.class);
        intent.putExtra("VoicePackage", voicePackage.getName());
        startActivity(intent);
    }

    @Override
    public void onEditClick(int position) {
        VoicePackage voicePackage = voicePackages.get(position);
        showEditVoicePackageDialog(voicePackage, position);
    }

    @Override
    public void onDeleteClick(int position) {
        VoicePackage voicePackage = voicePackages.get(position);
        showDeleteConfirmationDialog(voicePackage, position);
    }
    private void showEditVoicePackageDialog(VoicePackage voicePackage, int position) {
        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_voice_package, null);
        dialog.setContentView(view);
        EditText nameEditText = view.findViewById(R.id.et_voice_package_name);
        nameEditText.setText(voicePackage.getName());
        nameEditText.setSelection(voicePackage.getName().length());
        CardView saveButton = view.findViewById(R.id.save);
        ImageView closeButton = view.findViewById(R.id.close_image);
        TextView titleText = view.findViewById(R.id.title_text);
        titleText.setText("编辑语音包");
        String oldName = voicePackage.getName();
        saveButton.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            if (!newName.isEmpty()) {
                if (FileUtils.renameFolder(path, oldName, newName)) {
                    Toast.makeText(this, "文件夹重命名成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "文件夹重命名失败，可能缺少权限或目标已存在", Toast.LENGTH_SHORT).show();
                }
                voicePackage.setName(newName);
                // repository.updateVoicePackage(voicePackage);
                adapter.notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "请输入语音包名称", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void showDeleteConfirmationDialog(VoicePackage voicePackage, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除语音包 \"" + voicePackage.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 删除语音包
                    if (FileUtils.deleteFolder(path, voicePackage.getName())) {
                        Toast.makeText(this, "文件夹删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "文件夹删除失败，可能缺少权限", Toast.LENGTH_SHORT).show();
                    }
                    voicePackages.remove(position);
                    adapter.notifyItemRemoved(position);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
