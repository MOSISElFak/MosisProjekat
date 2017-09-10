package com.example.marija.mosisproj;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marija on 9/10/2017.
 */

public class MyPlacesAdapter extends BaseAdapter {

        private Context context;
        private List<Spot> places;
        private int id;

        public MyPlacesAdapter(Context context, List<Spot> places, Integer id) {
            this.context = context;
            this.places = places;

         //   String uri = "drawable/i" + key+".png";
         //   id = context.getResources().getIdentifier(uri, null, context.getPackageName());
            this.id=id;

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
            Picture.setImageResource(id);



            return v;
        }
    }


