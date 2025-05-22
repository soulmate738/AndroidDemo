package com.example.sparkchaindemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sparkchaindemo.fragment.FirstFragment;
import com.example.sparkchaindemo.fragment.FourthFragment;
import com.example.sparkchaindemo.fragment.SecondFragment;
import com.example.sparkchaindemo.fragment.ThirdFragment;

/**
 * @author anjia
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"首页", "日程", "动态","我的"};

    public MyFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FirstFragment();
        } else if (position == 1) {
            return new SecondFragment();
        }else if (position==2){
            return new ThirdFragment();
        }
        return new FourthFragment();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
