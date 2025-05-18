package com.example.sparkchaindemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sparkchaindemo.fragment.FujinFragment;
import com.example.sparkchaindemo.fragment.HaoyouFragment;
import com.example.sparkchaindemo.fragment.TuijianFragment;

/**
 * @author anjia
 */
public class FaxianFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"附近", "推荐", "好友"};

    public FaxianFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FujinFragment();
        } else if (position == 1) {
            return new TuijianFragment();
        }
        return new HaoyouFragment();
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
