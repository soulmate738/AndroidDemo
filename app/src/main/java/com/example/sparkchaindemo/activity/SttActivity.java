package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.viewpager.widget.ViewPager;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.STTAndTTSFragmentAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.google.android.material.tabs.TabLayout;


public class SttActivity extends BaseActivity {
    private String TAG = "SttActivity";
    private ImageView mBack;
    private TabLayout  tabLayout;
    private ViewPager viewPager;
    private STTAndTTSFragmentAdapter sttAndTTSFragmentAdapter;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // System.loadLibrary("libs/SparkChain.aar");
    }

    public enum ASRMode {
        CN,
        EN
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_stt);
        tabLayout = findViewById(R.id.stt_tabLayout);
        viewPager = findViewById(R.id.stt_tts);
        mBack = findViewById(R.id.iv_back);
        sttAndTTSFragmentAdapter = new STTAndTTSFragmentAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(sttAndTTSFragmentAdapter);
        //将TabLayout与ViewPager绑定在一起
        tabLayout.setupWithViewPager(viewPager);
        //指定Tab的位置
        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}