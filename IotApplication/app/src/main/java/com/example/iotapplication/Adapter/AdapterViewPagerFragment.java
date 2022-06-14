package com.example.iotapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.iotapplication.Fragment.ControlFragment;
import com.example.iotapplication.Fragment.HistoryFragment;
import com.example.iotapplication.Fragment.HomeFragment;
import com.example.iotapplication.Fragment.ProfileFragment;


public class AdapterViewPagerFragment extends FragmentStateAdapter {


    public AdapterViewPagerFragment(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment() ;
            case 1:
                return new ControlFragment();
            case 2:
                return new ProfileFragment();
            case 3:
                return new HistoryFragment();
            default:
                return new HomeFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
