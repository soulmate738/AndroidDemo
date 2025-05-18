package com.example.sparkchaindemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sparkchaindemo.fragment.LuyinFragment;
import com.example.sparkchaindemo.fragment.STTFragment;
import com.example.sparkchaindemo.fragment.TTS1Fragment;
import com.example.sparkchaindemo.fragment.TTSFragment;

/**
 * @author anjia
 */
public class STTAndTTSFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"语音转文字", "文字转语音"};

    public STTAndTTSFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new STTFragment();
        } else  {
            return new TTS1Fragment();
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
