package com.example.sparkchaindemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.FaxianFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class SecondFragment extends Fragment {
    private ImageView ivAdd, ivSearch;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FaxianFragmentAdapter faxianFragmentAdapter;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        ivAdd = view.findViewById(R.id.iv_add);
        ivSearch = view.findViewById(R.id.iv_search);
        tabLayout = view.findViewById(R.id.fa_xian_tabLayout);
        viewPager = view.findViewById(R.id.fa_xian_viewpager);
        faxianFragmentAdapter = new FaxianFragmentAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(faxianFragmentAdapter);
        //将TabLayout与ViewPager绑定在一起
        tabLayout.setupWithViewPager(viewPager);

        //指定Tab的位置
        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);
        three = tabLayout.getTabAt(2);

        //设置Tab的图标，假如不需要则把下面的代码删去
        // one.setIcon(R.mipmap.shouye);
        // two.setIcon(R.mipmap.faxian);
        // three.setIcon(R.mipmap.xiaoxi_1);

        return view;
    }
}