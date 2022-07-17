package com.gitpro.discoverqa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;
//PagerAdapter is more general than the adapters used for AdapterViews. Instead of providing
// a View recycling mechanism directly ViewPager uses callbacks
// to indicate the steps taken during an update. A PagerAdapter may implement a form of View recycling if desired or use a more
// sophisticated method of managing page Views such as Fragment transactions where each page is represented by its own Fragment.


class ViewPagerAdapter extends PagerAdapter {

   private List<String> imageUrls;
   private LayoutInflater layoutInflater;
   private Context context;


   public ViewPagerAdapter(List<String> imageUrls, Context context) {
       this.imageUrls = imageUrls;
       this.context = context;
   }

   //Return the number of views available.
   @Override
   public int getCount() {

       return imageUrls.size();
   }

    //Determines whether a page View is associated with a specific key object as returned by instantiateItem(ViewGroup, int)..
   @Override
   public boolean isViewFromObject(View view, Object object) {
       return view.equals(object);
   }
    //Create the page for the given position. The adapter is responsible for adding the view to the container
   @Override
   public Object instantiateItem(ViewGroup container, int position) {
      //ViewGroup: The containing View in which the page will be shown.
      //int: The page position to be instantiated.

       //blue picture on navigation drawer
       layoutInflater = LayoutInflater.from(context);
       View view = layoutInflater.inflate(R.layout.viewpager_item, container, false);
       ImageView imageView;
       imageView = view.findViewById(R.id.viewPager_img);
       Picasso.get().load(imageUrls.get(position)).fit().into(imageView);
       container.addView(view, 0);

       //Returns an Object representing the new page. This does not need to be a View, but can be some other container of the page.
       return view;
   }


   //Remove a page for the given position. The adapter is responsible for removing the view from its container,
   // although it only must ensure this is done by the time it returns from finishUpdate(ViewGroup).
   @Override
   public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
     //ViewGroup: The containing View from which the page will be removed.
     //int: The page position to be removed.
     //Object: The same object that was returned by PagerAdapter.instantiateItem(View, int).
       container.removeView((View) object);
   }

}
