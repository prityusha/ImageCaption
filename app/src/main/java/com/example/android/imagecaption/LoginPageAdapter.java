package com.example.android.imagecaption;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginPageAdapter extends FragmentPagerAdapter {
    private Context context;
    int TotalTabs;

    public LoginPageAdapter(FragmentManager fm, Context context, int TotalTabs){
        super(fm);
        this.context = context;
        this.TotalTabs=TotalTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignupTabFragment signupTabFragment = new SignupTabFragment();
                return signupTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TotalTabs;
    }
}
