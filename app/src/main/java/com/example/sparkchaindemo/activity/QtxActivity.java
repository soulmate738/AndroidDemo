package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.utils.QTXShared;

import java.util.ArrayList;
import java.util.List;

public class QtxActivity extends BaseActivity {
    private ImageView mBack;
    private CheckBox mDzQg,mDzZd,mLdQg,mLdZd,mWxQg,mWxZd,mQqQg,mQqZd,mDdQg,mDdZd;
    private CardView mSave;
    private SharedPreferences  sharedPreferences;
    private static final String TAG = "QtxActivity";
    private List<CheckBox> checkBoxList; // 存储 8 个 Checkbox
    private QTXShared manager; // 状态管理工具类
    TextView tv_dz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_qtx);
        mBack = findViewById(R.id.iv_back);
        mDzQg = findViewById(R.id.cb_dz_qg);
        mDzZd = findViewById(R.id.cb_dz_zd);
        mLdQg = findViewById(R.id.cb_ld_qg);
        mLdZd = findViewById(R.id.cb_ld_zd);
        mWxQg = findViewById(R.id.cb_wx_qg);
        mWxZd = findViewById(R.id.cb_wx_zd);
        mQqQg = findViewById(R.id.cb_qq_qg);
        mQqZd = findViewById(R.id.cb_qq_zd);
        mDdQg = findViewById(R.id.cb_dd_qg);
        mDdZd = findViewById(R.id.cb_dd_zd);
        mSave = findViewById(R.id.card_save);
        tv_dz = findViewById(R.id.tv_dz);

        // 初始化 Checkbox 列表（假设通过布局 ID 获取，如 checkBox1 至 checkBox8）
        checkBoxList = new ArrayList<>();
        checkBoxList.add(findViewById(R.id.cb_dz_qg));
        checkBoxList.add(findViewById(R.id.cb_dz_zd));
        checkBoxList.add(findViewById(R.id.cb_ld_qg));
        checkBoxList.add(findViewById(R.id.cb_ld_zd));
        checkBoxList.add(findViewById(R.id.cb_wx_qg));
        checkBoxList.add(findViewById(R.id.cb_wx_zd));
        checkBoxList.add(findViewById(R.id.cb_qq_qg));
        checkBoxList.add(findViewById(R.id.cb_qq_zd));
        checkBoxList.add(findViewById(R.id.cb_dd_qg));
        checkBoxList.add(findViewById(R.id.cb_dd_zd));

        // 初始化状态管理器
        manager = new QTXShared(this);

        // 加载已保存的状态
        loadStates();
    }

    private void loadStates() {
        // 加载已保存的状态
            for (int i = 0; i < checkBoxList.size(); i++) {
                CheckBox cb = checkBoxList.get(i);
                boolean isChecked = manager.getState(i);
                cb.setChecked(isChecked);
            }
    }

    @Override
    protected void initData() {
        // 使用sharedpreference保存勾选状态
        
    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(v -> finish());
        tv_dz.setOnClickListener(v -> {

            simulateEarthquake();

        });
        // mDzQg.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mDzZd.setChecked(false);
        //     }
        // });
        // mDzZd.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mDzQg.setChecked(false);
        //     }
        // });
        // mLdQg.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mLdZd.setChecked(false);
        //     }
        // });
        // mLdZd.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mLdQg.setChecked(false);
        //     }
        // });
        // mWxQg.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mWxZd.setChecked(false);
        //     }
        // });
        // mWxZd.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mWxQg.setChecked(false);
        //     }
        // });
        // mQqQg.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mQqZd.setChecked(false);
        //     }
        // });
        // mQqZd.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mQqQg.setChecked(false);
        //     }
        // });
        // mDdQg.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mDdZd.setChecked(false);
        //     }
        // });
        // mDdZd.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     if (isChecked) {
        //         mDdQg.setChecked(false);
        //     }
        // });
        mSave.setOnClickListener(v -> {
            saveAllStates();
        });
    }

    private void simulateEarthquake() {
        saveAllStates();

        // 启动全屏警报活动
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra("vibrate", mDdZd.isChecked());
        intent.putExtra("flash", mDzQg.isChecked());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void saveAllStates() {
        for (int i = 0; i < checkBoxList.size(); i++) {
            CheckBox cb = checkBoxList.get(i);
            manager.saveState(i, cb.isChecked());
        }
        // 可选：显示保存成功提示
        showToast("保存成功");
    }
}