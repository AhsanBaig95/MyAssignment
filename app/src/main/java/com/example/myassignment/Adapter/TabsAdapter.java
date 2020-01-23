package com.example.myassignment.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myassignment.Fragments.FriendStatusFragment;
import com.example.myassignment.Fragments.MyStatusFragment;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                MyStatusFragment myStatus = new MyStatusFragment();
                return myStatus;
            case 1:
                FriendStatusFragment friendStatus = new FriendStatusFragment();
                return friendStatus;
            default:
                return null;
        }
    }
}
