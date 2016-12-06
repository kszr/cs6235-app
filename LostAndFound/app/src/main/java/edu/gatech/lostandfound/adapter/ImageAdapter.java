package edu.gatech.lostandfound.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by abhishekchatterjee on 11/29/16.
 */

public class ImageAdapter extends BaseAdapter {
    private static final int PADDING = 8;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private final Context mContext;
    private List<Bitmap> listMap;

    public ImageAdapter(Context context, List<Bitmap> listMap) {
        this.mContext = context;
        this.listMap = listMap;
    }

    @Override
    public int getCount() {
        return listMap.size();
    }

    @Override
    public Object getItem(int position) {
        return listMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;

        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
            imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        Bitmap bmp = listMap.get(position);
        imageView.setImageBitmap(bmp);
        return imageView;
    }
}
