package com.example.sparkchaindemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sparkchaindemo.fragment.HaoyouFragment;
import com.example.sparkchaindemo.fragment.RelativeFragment;
import com.example.sparkchaindemo.fragment.XiaoXiFragment;

import cn.leancloud.chatkit.activity.LCIMContactFragment;
import cn.leancloud.chatkit.activity.LCIMConversationListFragment;

/**
 * @author anjia
 */
public class XiaoXiFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"消息", "通讯录"};

    public XiaoXiFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new XiaoXiFragment();
        } else {
            return new HaoyouFragment();
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
