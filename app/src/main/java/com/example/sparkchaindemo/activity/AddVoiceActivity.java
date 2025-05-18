package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.viewpager.widget.ViewPager;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.AddYytFragmentAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.utils.FileUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class AddVoiceActivity extends BaseActivity {

    private ImageView mBack;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddYytFragmentAdapter mAdapter;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private String VoicePackageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        VoicePackageName = intent.getStringExtra("VoicePackageName");
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_yyt);
        mBack = findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.add_yyt_tabLayout);
        viewPager = findViewById(R.id.add_yyt_viewpager);
        mAdapter  = new AddYytFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
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
                Intent intent = new Intent(AddVoiceActivity.this,YytActivity.class);
                intent.putExtra("VoicePackage",VoicePackageName);
                startActivity(intent);
                finish();
            }
        });
    }
}