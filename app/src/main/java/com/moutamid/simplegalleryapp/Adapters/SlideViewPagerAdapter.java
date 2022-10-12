package com.moutamid.simplegalleryapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.moutamid.simplegalleryapp.Listener.LoopingPagerAdapter;
import com.moutamid.simplegalleryapp.Listener.ViewClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.moutamid.simplegalleryapp.R;

import java.util.ArrayList;
import java.util.List;


public class SlideViewPagerAdapter extends PagerAdapter implements LoopingPagerAdapter {

    Context ctx;
    ArrayList<ImagesModel> modelDataArrayList;
    private ImageView imageView;
    private ViewClickListener viewClickListener;

    public SlideViewPagerAdapter(Context ctx, ArrayList<ImagesModel> modelDataArrayList) {
        this.ctx = ctx;
        this.modelDataArrayList = modelDataArrayList;
    }

    @Override
    public int getCount() {
        return modelDataArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater= (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_screen,container,false);
        ImagesModel model = modelDataArrayList.get(position);
        imageView =view.findViewById(R.id.imageView);
        Glide.with(ctx).load(model.getPath()).into(imageView);

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public int getRealCount() {
        return modelDataArrayList.size();
    }

    public void setViewClickListener(ViewClickListener viewClickListener){
        this.viewClickListener = viewClickListener;
    }
}
