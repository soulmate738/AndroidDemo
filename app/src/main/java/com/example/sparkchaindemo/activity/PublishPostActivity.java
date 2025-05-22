package com.example.sparkchaindemo.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class PublishPostActivity extends AppCompatActivity {
    private static final String TAG = "PublishPostActivity";
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_PICK_VIDEO = 2;

    private EditText etContent;
    private Button btnAddImage, btnAddVideo, btnPublish, btnCancel;
    private FrameLayout flPreview;
    private ImageView ivPreview, ivRemoveMedia;
    private ProgressDialog mLoadingDialog;
    private  Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_post);

        // 初始化视图
        etContent = findViewById(R.id.et_content);
        btnAddImage = findViewById(R.id.btn_add_image);
        btnPublish = findViewById(R.id.btn_publish);
        btnCancel = findViewById(R.id.btn_cancel);
        flPreview = findViewById(R.id.fl_preview);
        ivPreview = findViewById(R.id.iv_preview);
        ivRemoveMedia = findViewById(R.id.iv_remove_media);

        // 设置点击事件
        btnAddImage.setOnClickListener(v -> pickImage());
        btnPublish.setOnClickListener(v -> publishPost());
        btnCancel.setOnClickListener(v -> finish());
        ivRemoveMedia.setOnClickListener(v -> clearMedia());
    }

    // 选择图片
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    // 处理选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // processSelectedImage(selectedImageUri);
            if (requestCode == REQUEST_PICK_IMAGE) {
                    showImagePreview(selectedImageUri);
            }
        }
    }
    // 处理选择的图片
    private void processSelectedImage(Uri imageUri) {
        // 显示加载对话框
        flPreview.setVisibility(View.VISIBLE);
        ivPreview.setVisibility(View.VISIBLE);
        showLoadingDialog();

        // 在后台线程处理图片（调整大小、压缩等）
        new AsyncTask<Uri, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Uri... uris) {
                try {
                    // 获取图片的Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                          getContentResolver(), uris[0]);

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
                    // saveAvatarImage(bitmap);

                    // 更新UI显示
                    ivPreview.setImageBitmap(bitmap);

                    // 显示成功提示
                } else {
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

    // // 保存头像图片到本地
    // private void saveAvatarImage(Bitmap bitmap) {
    //     try {
    //         // 获取存储目录
    //         File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    //         if (!storageDir.exists()) {
    //             storageDir.mkdirs();
    //         }
    //
    //         // 创建头像文件
    //         File avatarFile = new File(storageDir, "avatar.jpg");
    //
    //         // 将Bitmap写入文件
    //         FileOutputStream fos = new FileOutputStream(avatarFile);
    //         bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
    //         fos.flush();
    //         fos.close();
    //
    //         // 保存头像文件路径到SharedPreferences
    //         SharedPreferences prefs = this.getSharedPreferences(
    //                 "user_info", Context.MODE_PRIVATE);
    //         prefs.edit().putString("avatar_path", avatarFile.getAbsolutePath()).apply();
    //
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    // 显示加载对话框
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(this);
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
    // 显示图片预览
    private void showImagePreview(Uri uri) {
        flPreview.setVisibility(View.VISIBLE);
        ivPreview.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .into(ivPreview);
    }

    // 清除媒体选择
    private void clearMedia() {
        flPreview.setVisibility(View.GONE);
    }

    // 发布动态
    private void publishPost() {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content) && selectedImageUri == null) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();

        } else {
            // 创建新的Post对象（使用imagePath替代url）
            Post newPost = new Post(
                    R.mipmap.head1, // 当前用户头像
                    "旋风小陀螺",   // 当前用户名
                    content,
                    selectedImageUri.toString(),
                    System.currentTimeMillis(),
                    0,
                    0,
                    false
            );

            // 通过接口回调传递数据给ThirdFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_post", newPost);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

}