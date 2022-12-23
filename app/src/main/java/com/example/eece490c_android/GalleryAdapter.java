package com.example.eece490c_android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class GalleryAdapter extends BaseAdapter {
    Context galleryContext;

    public GalleryAdapter(Context context) {
        galleryContext = context;
    }

    @Override
    public int getCount() {
        return MainActivity.userPosts.size();
    }

    @Override
    public Object getItem(int i) {
        return MainActivity.userPosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        Log.d("GALLERY GETVIEW", String.valueOf(getCount()));
        ImageView imageView = new ImageView(galleryContext);
        File photoFileAtI = ((UserPost) getItem(i)).getLocalPhotoFile();
        imageView.setImageBitmap(BitmapFactory.decodeFile(photoFileAtI.getAbsolutePath()));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
        return imageView;
    }
}
