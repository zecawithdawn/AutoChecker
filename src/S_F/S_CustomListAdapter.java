package com.example.kyh.real.S_F;

/**
 * Created by kyh on 2015. 1. 27..
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyh.real.R;

public class S_CustomListAdapter extends ArrayAdapter<String> {

    Context context;
    Typeface tf;
    String[] strings;
    Integer[] imgs;

    public S_CustomListAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        tf = Typeface.createFromAsset(context.getAssets(), "KoPubDotumMedium.ttf");
        strings = objects;
        imgs = new Integer[]{
                R.drawable.stamp3,
                R.drawable.schedule3,
                R.drawable.message3,
                R.drawable.option3
        };

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_listview, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.listTextView);
        tv.setText(strings[position]);
        tv.setTypeface(tf);

        ImageView im = (ImageView) convertView.findViewById(R.id.listImageView);
        im.setImageResource(imgs[position]);

        return convertView;
    }
}