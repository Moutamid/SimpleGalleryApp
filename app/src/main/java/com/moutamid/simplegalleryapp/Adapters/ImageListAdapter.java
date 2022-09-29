package com.moutamid.simplegalleryapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.simplegalleryapp.FullImageActivity;
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.moutamid.simplegalleryapp.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<ImagesModel> imagesModelArrayList;

    public ImageListAdapter(Context mContext, ArrayList<ImagesModel> imagesModelArrayList) {
        this.mContext = mContext;
        this.imagesModelArrayList = imagesModelArrayList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImagesModel model = imagesModelArrayList.get(position);
        Glide.with(mContext).load(model.getPath()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FullImageActivity.class);
                intent.putExtra("imageModel",model);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView,selectImg;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            selectImg = itemView.findViewById(R.id.check);
        }
    }
}
