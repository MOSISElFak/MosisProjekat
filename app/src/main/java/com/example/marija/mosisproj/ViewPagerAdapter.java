package com.example.marija.mosisproj;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Marija on 9/3/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {


    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Integer> images;

    public ViewPagerAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewSlideItem);
        imageView.setImageResource(images.get(position));

        ViewPager vp=(ViewPager) container;
        vp.addView(view,0);

        return  view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp=(ViewPager)container;
        View view=(View) object;
        vp.removeView(view);
    }
}

