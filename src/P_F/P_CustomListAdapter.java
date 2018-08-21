package com.example.kyh.real.P_F;

/**
 * Created by kyh on 2015. 1. 30..
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyh.real.R;


/**
 * Created by kyh on 2015. 1. 27..
 */


public class P_CustomListAdapter extends ArrayAdapter<String> {

    Context context;
    Typeface tf;
    String[] strings;
    Integer[] imgs;

    public P_CustomListAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        tf = Typeface.createFromAsset(context.getAssets(), "KoPubDotumMedium.ttf");
        strings = objects;
        imgs = new Integer[]{
                R.drawable.schedule3,
                R.drawable.p_cls_manage,
                R.drawable.search,
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