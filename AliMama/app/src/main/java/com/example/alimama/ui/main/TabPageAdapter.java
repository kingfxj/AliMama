package com.example.alimama.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.alimama.R;

import java.util.ArrayList;


public class TabPageAdapter extends FragmentPagerAdapter {
    private  static final int[] TAB_TITLE = new int[]{R.string.contact_Page, R.string.friend_page, R.string.request_page};
    private final Context mContext;

    private final ArrayList<Fragment> setFragment = new ArrayList<>();
    private final ArrayList<String> setTitle = new ArrayList<>();


    public TabPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//
        return setTitle.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return setFragment.get(position);
    }



    @Override
    public int getCount() {
        return setTitle.size();
    }


    public void addFragment(Fragment fragment, String title){
        setFragment.add(fragment);
        setTitle.add(title);
    }

}
