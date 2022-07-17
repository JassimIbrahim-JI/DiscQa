package com.gitpro.discoverqa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;


public class TourFragment extends Fragment {

    ViewPager2 mViewPager;
    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_tour, container, false);

        TabLayout tabLayout = mView.findViewById(R.id.tablayout);
        mViewPager = mView.findViewById(R.id.tour_view_pager);
        ToursViewPagerAdapter adapter = new ToursViewPagerAdapter(mView.getContext(), getActivity().getSupportFragmentManager(), getLifecycle());
       adapter.addFragment(new TopRatedTourFragment(),"");
       adapter.addFragment(new RecommendedTourFragment(),"");

        mViewPager.setAdapter(adapter);
      tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
              mViewPager.setCurrentItem(tab.getPosition());
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {

          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {

          }
      });
      mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
          @Override
          public void onPageSelected(int position) {
             tabLayout.selectTab(tabLayout.getTabAt(position));
          }
      });

        return mView;
    }


}
