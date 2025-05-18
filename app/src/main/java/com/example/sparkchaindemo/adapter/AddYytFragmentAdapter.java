package com.example.sparkchaindemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sparkchaindemo.fragment.LuyinFragment;
import com.example.sparkchaindemo.fragment.TTSFragment;

/**
 * @author anjia
 */
public class AddYytFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"录音", "文字转语音"};

    public AddYytFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LuyinFragment();
        } else  {
            return new TTSFragment();
        }
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
