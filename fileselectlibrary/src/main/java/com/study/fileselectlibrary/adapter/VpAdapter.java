package com.study.fileselectlibrary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.study.fileselectlibrary.fragment.BaseFragment;

import java.util.List;


/**
 * Created by Administrator on 2017/11/8.
 */

public class VpAdapter extends FragmentPagerAdapter {
    private final FragmentManager fm;
    private final List<BaseFragment> list;
    private String[] titles;

    public VpAdapter(FragmentManager fm, List<BaseFragment> fragmentList, String[] titles) {
        super(fm);
        this.fm = fm;
        list = fragmentList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
