package com.example.sparkchaindemo.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.AboutActivity;
import com.example.sparkchaindemo.activity.KeFuActivity;
import com.example.sparkchaindemo.activity.LoginActivity;
import com.example.sparkchaindemo.activity.SettingsActivity;
import com.example.sparkchaindemo.activity.WenJuanActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.leancloud.LCUser;

public class FourthFragment extends Fragment {
    private static final String TAG = "FourthFragment";
    private TextView mName,mKeFu,mWenJuan,mGuanYu,mSettings,mExit;
    private ImageView mAvatar;
    String mCurrentUserName;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private ProgressDialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        mExit = view.findViewById(R.id.tv_exit);
        mGuanYu = view.findViewById(R.id.tv_guanyu);
        mSettings = view.findViewById(R.id.tv_setting);
        mWenJuan = view.findViewById(R.id.tv_wjdc);
        mKeFu = view.findViewById(R.id.tv_kefu);
        mName = view.findViewById(R.id.tv_name);
        mAvatar = view.findViewById(R.id.iv_head);
        initListener();
        return view;
    }

    private void initListener() {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转相册选择头像
                pickFromGallery();


            }
        });
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个自定义布局的对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("修改用户名");
                mCurrentUserName = mName.getText().toString();
                // 加载自定义布局
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_username, null);
                final EditText etNewName = view.findViewById(R.id.et_new_username);
                etNewName.setText(mCurrentUserName); // 设置当前用户名
                etNewName.setSelection(mCurrentUserName.length()); // 光标放在文本末尾

                builder.setView(view);

                // 设置确认按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = etNewName.getText().toString().trim();

                        // 检查输入是否为空
                        if (TextUtils.isEmpty(newName)) {
                            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 检查输入长度
                        if (newName.length() < 2 || newName.length() > 16) {
                            Toast.makeText(getActivity(), "用户名长度需在2-16个字符之间", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 更新用户名（调用更新方法）
                        updateUserName(newName);

                        // 显示成功提示
                        Toast.makeText(getActivity(), "用户名已更新为: " + newName, Toast.LENGTH_SHORT).show();
                    }
                });

                // 设置取消按钮
                builder.setNegativeButton("取消", null);

                // 创建并显示对话框
                AlertDialog dialog = builder.create();
                dialog.show();

                // 自定义对话框按钮样式
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
            }
        });

        mKeFu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到客服界面
                Intent intent = new Intent(getActivity(), KeFuActivity.class);
                startActivity(intent);
            }
        });
        mWenJuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到问卷界面
                Intent intent = new Intent(getActivity(), WenJuanActivity.class);
                startActivity(intent);
            }
        });
        mGuanYu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到关于界面
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到设置界面
                // 这里可以添加设置界面的逻辑
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出应用
                // 显示确认退出对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("退出登录")
                        .setMessage("确定要退出当前账号吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LCUser.logOut();
                                // 清除本地存储的用户信息
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear(); // 清除所有键值对
                                editor.apply();

                                // 跳转到登录页面
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                // 关闭所有已打开的Activity
                                getActivity().finishAffinity();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }


        });
    }
    // 更新用户名的方法（需要实现具体逻辑）
    private void updateUserName(String newName) {
        // 1. 更新本地数据
        mCurrentUserName = newName;

        // 2. 更新UI显示
        mName.setText(newName);

        // 3. 更新SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", newName);
        editor.apply();
        // 3. 如果需要，更新到服务器
        // ApiService.updateUserName(newName);
    }
    // 从相册选择图片
    private void pickFromGallery() {
        // 创建相册选择意图
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        // 检查是否有应用可以处理该意图
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        } else {
            Toast.makeText(getActivity(), "没有找到图片选择应用", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理Activity返回结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                processSelectedImage(selectedImageUri);
            }
        }
    }
    // 处理选择的图片
    private void processSelectedImage(Uri imageUri) {
        // 显示加载对话框
        showLoadingDialog();

        // 在后台线程处理图片（调整大小、压缩等）
        new AsyncTask<Uri, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Uri... uris) {
                try {
                    // 获取图片的Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), uris[0]);

                    // 调整图片大小（可选）
                    return resizeBitmap(bitmap, 500, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                // 隐藏加载对话框
                hideLoadingDialog();

                if (bitmap != null) {
                    // 保存图片到本地
                    saveAvatarImage(bitmap);

                    // 更新UI显示
                    mAvatar.setImageBitmap(bitmap);

                    // 显示成功提示
                    Toast.makeText(getActivity(), "头像已更新", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(imageUri);
    }

    // 调整Bitmap大小
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        // 计算缩放比例
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // 创建缩放后的Bitmap
        return Bitmap.createScaledBitmap(bitmap,
                (int) (width * scaleFactor),
                (int) (height * scaleFactor),
                true);
    }

    // 保存头像图片到本地
    private void saveAvatarImage(Bitmap bitmap) {
        try {
            // 获取存储目录
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            // 创建头像文件
            File avatarFile = new File(storageDir, "avatar.jpg");

            // 将Bitmap写入文件
            FileOutputStream fos = new FileOutputStream(avatarFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();

            // 保存头像文件路径到SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences(
                    "user_info", Context.MODE_PRIVATE);
            prefs.edit().putString("avatar_path", avatarFile.getAbsolutePath()).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 显示加载对话框
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(getActivity());
            mLoadingDialog.setMessage("正在处理图片...");
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    // 隐藏加载对话框
    private void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}