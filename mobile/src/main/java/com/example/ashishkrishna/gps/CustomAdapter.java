package com.example.ashishkrishna.gps;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Rymico on 8/4/2015.
 */
public class CustomAdapter extends SimpleAdapter {

    private int[] backgroundColors = new int[]{R.color.favoritesBackgroundWhite, R.color.favoritesBackgroundPink};

    public CustomAdapter(Context context, List<Map<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        int colorPosition = position % backgroundColors.length;
        view.setBackgroundResource(backgroundColors[colorPosition]);
        /*TextView address = (TextView) view.findViewById(R.id.address);
        if (position % 2 == 0) {
            address.setText("Sutardja Dai 3rd Floor");
        } else {
            address.setText("Sutardja Dai 1st Floor");
        }*/
        return view;
    }
}
