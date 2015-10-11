package com.example.fady.mymovies;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fady on 8/22/2015.
 */
public class ListAdapter extends ArrayAdapter<MoviePic> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<MoviePic> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.movieitem, null);
        }

        MoviePic p = getItem(position);

        if (p != null) {
            ImageView profile = (ImageView) v.findViewById(R.id.movieimageView);
            profile.setScaleType(ImageView.ScaleType.FIT_XY);

            if (profile != null) {
                profile.setImageBitmap(p.getMovie_pic());
            }
        }

        return v;
    }

}

