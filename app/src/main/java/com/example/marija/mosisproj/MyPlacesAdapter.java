package com.example.marija.mosisproj;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marija on 9/10/2017.
 */

public class MyPlacesAdapter extends BaseAdapter {

        private Context context;
        private List<Spot> places;
        private ArrayList<String> images;

        public MyPlacesAdapter(Context context, List<Spot> places, ArrayList<String> images) {
            this.context = context;
            this.places = places;
            this.images=images;

        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v=View.inflate(context,R.layout.item_places,null);
            TextView ID=(TextView)v.findViewById(R.id.textViewId);
            TextView Header=(TextView)v.findViewById(R.id.textViewHeader);
            ImageView Picture=(ImageView) v.findViewById(R.id.picture);

            ID.setText(Integer.toString(position+1));
            Header.setText(places.get(position).getHeader());


            StorageReference storageRef;
            storageRef = FirebaseStorage.getInstance().getReference().child(images.get(position));




          Glide.with(context)
                 .using(new FirebaseImageLoader())
                  .load(storageRef)
                   .into(Picture);


            return v;
        }
    }


