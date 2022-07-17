package com.gitpro.discoverqa;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

//Actionbar

public class ToursViewPagerAdapter extends FragmentStateAdapter {
    Context context;

    ArrayList<Fragment>fragmentList=new ArrayList<>();
    ArrayList<String>fragmentTitleList=new ArrayList<>();

    //that sets the fragment manager for the adapter. This is the equivalent of calling FragmentPagerAdapter(FragmentManager)
    public ToursViewPagerAdapter(Context context, FragmentManager fm, Lifecycle lifecycle) {
        super(fm,lifecycle);
       //  fm = FragmentManager: fragment manager that will interact with this adapter
        this.context = context;
    }

    //Return the Fragment associated with a specified position.

    public void addFragment(Fragment fragment,String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       Fragment fragment;
        switch (position) {
            case 0:
                 fragment=new TopRatedTourFragment();
                 return fragment;
            case 1:
                 fragment=new RecommendedTourFragment();
                 return fragment;
            default:
                 fragment=new TopRatedTourFragment();
                 return fragment;
        }
    }

//This method may be called by the ViewPager to obtain a title string to describe the specified page.

    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    //Return the number of views available.
    @Override
    public int getItemCount() {
        return fragmentList.size();
        //two available action bar top rated tour and recommended
    }
}
