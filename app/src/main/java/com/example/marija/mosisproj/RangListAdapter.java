package com.example.marija.mosisproj;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marija on 9/1/2017.
 */

public class RangListAdapter extends BaseAdapter{
    private Context context;
    private List<Korisnik> rangList;

    public RangListAdapter(Context context, List<Korisnik> rangList) {
        this.context = context;
        this.rangList = rangList;
    }

    @Override
    public int getCount() {
        return rangList.size();
    }

    @Override
    public Object getItem(int position) {
        return rangList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context,R.layout.item_rang_list,null);
        TextView Name=(TextView)v.findViewById(R.id.textViewFirstName);
        TextView LastName=(TextView)v.findViewById(R.id.textViewLastName);
        TextView Score=(TextView)v.findViewById(R.id.textViewScore);

        Name.setText(rangList.get(position).getFirstname());
        LastName.setText(rangList.get(position).getLastname());
        Score.setText(Integer.toString(rangList.get(position).getScore())+"pts");

        v.setTag(rangList.get(position).getEmail());

        return v;
    }
}
