package com.example.sparkchaindemo.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.BaoJingActivity;
import com.example.sparkchaindemo.activity.EmergencyContactActivity;
import com.example.sparkchaindemo.activity.QtxActivity;
import com.example.sparkchaindemo.activity.ShouYuActivity;
import com.example.sparkchaindemo.activity.SttActivity;
import com.example.sparkchaindemo.activity.YybActivity;
import com.example.sparkchaindemo.activity.ZimuActivity;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {
    private String TAG  = "FirstFragment";
    private Banner banner;
    private CardView item1Stt, item2Zimu, item3Qtx, item4Bj, item5Yyb, item6Hujiao, item7Class;
    private TextView cardChange;
    private ImageView cardImage;
    private List<Integer> banner_data;
    // 示例数据 - 可以替换为实际数据源
    private List<Integer> imageResources = new ArrayList<>();

    private int currentIndex = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        // 绑定控件
        banner = view.findViewById(R.id.banner);
        item1Stt = view.findViewById(R.id.item1_stt);
        item2Zimu = view.findViewById(R.id.item2_zimu);
        item3Qtx = view.findViewById(R.id.item3_qtx);
        item4Bj = view.findViewById(R.id.item4_bj);
        item5Yyb = view.findViewById(R.id.item5_yyb);
        item6Hujiao = view.findViewById(R.id.item6_hujiao);
        item7Class = view.findViewById(R.id.item7_class);
        cardImage = view.findViewById(R.id.iv_card);
        cardChange = view.findViewById(R.id.tv_card_change);
        banner_data = new ArrayList<>();
        banner_data.add(R.mipmap.banner1);
        banner_data.add(R.mipmap.banner2);
        banner_data.add(R.mipmap.banner3);
        banner.setAdapter(new BannerImageAdapter<Integer>(banner_data) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }
        });

        // 开启循环轮播
        banner.isAutoLoop(true);
        banner.setScrollBarFadeDuration(1000);
        // 设置指示器颜色(TODO 即选中时那个小点的颜色)
        banner.setIndicatorSelectedColor(Color.GREEN);
        // 开始轮播
        banner.start();
        initData();
        initListener();
        return view;
    }

    private void initData() {

        // 填充示例数据
        imageResources.add(R.mipmap.shouyu1);
        imageResources.add(R.mipmap.shouyu2);
        imageResources.add(R.mipmap.shouyu3);
        imageResources.add(R.mipmap.shouyu4);
        imageResources.add(R.mipmap.shouyu5);
        imageResources.add(R.mipmap.shouyu6);
        cardImage.setImageResource(imageResources.get(currentIndex));
    }

    private void initListener() {
        item1Stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到SttActivity
                Intent intent = new Intent(getActivity(), SttActivity.class);
                startActivity(intent);
            }
        });
        item2Zimu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到ZimuActivity
                Intent intent = new Intent(getActivity(), ZimuActivity.class);
                startActivity(intent);
            }
        });
        item3Qtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到QtxActivity
                Intent intent = new Intent(getActivity(), QtxActivity.class);
                startActivity(intent);
            }
        });
        item4Bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到BaoJingActivity
                Intent intent = new Intent(getActivity(), BaoJingActivity.class);
                startActivity(intent);
            }
        });
        item5Yyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到YybActivity
                Intent intent = new Intent(getActivity(), YybActivity.class);
                startActivity(intent);
            }
        });
        item6Hujiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到HujiaoActivity
                Intent intent = new Intent(getActivity(), EmergencyContactActivity.class);
                startActivity(intent);
            }
        });
        item7Class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到ShouYuActivity
                Intent intent = new Intent(getActivity(), ShouYuActivity.class);
                startActivity(intent);
            }
        });
        cardChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeContent();
            }
        });
    }

    private void changeContent() {
        // 更新索引
        currentIndex = (currentIndex + 1) % imageResources.size();

        // 图片淡入淡出动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(cardImage, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                cardImage.setImageResource(imageResources.get(currentIndex));

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(cardImage, "alpha", 0f, 1f);
                fadeIn.setDuration(300);
                fadeIn.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        fadeOut.start();

        // "换一换"按钮旋转动画
        ObjectAnimator rotate = ObjectAnimator.ofFloat(cardChange, "rotation", 0f, 360f);
        rotate.setDuration(500);
        rotate.start();
    }
}