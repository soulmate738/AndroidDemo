package com.example.sparkchaindemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.XiaoXiFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class ThirdFragment extends Fragment {
    private ImageView ivSearch;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private XiaoXiFragmentAdapter xiaoXiFragmentAdapter;
    private TabLayout.Tab one;
    private TabLayout.Tab two;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        ivSearch = view.findViewById(R.id.iv_search);
        tabLayout = view.findViewById(R.id.xiao_xi_tabLayout);
        viewPager = view.findViewById(R.id.xiao_xi_viewpager);
        xiaoXiFragmentAdapter = new XiaoXiFragmentAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(xiaoXiFragmentAdapter);
        //将TabLayout与ViewPager绑定在一起
        tabLayout.setupWithViewPager(viewPager);

        //指定Tab的位置
        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);
        return view;
    }
}