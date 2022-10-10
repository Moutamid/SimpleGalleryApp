package com.moutamid.simplegalleryapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moutamid.simplegalleryapp.FullImageActivity;
import com.moutamid.simplegalleryapp.Listener.ItemClickListener;
import com.moutamid.simplegalleryapp.Listener.ItemLongClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.moutamid.simplegalleryapp.R;

import java.util.ArrayList;

public class SelectedImageListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ImagesModel> imagesModelArrayList;
    LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public SelectedImageListAdapter(Context mContext, ArrayList<ImagesModel> imagesModelArrayList) {
        this.mContext = mContext;
        this.imagesModelArrayList = imagesModelArrayList;
    }

    @Override
    public int getCount() {
        return imagesModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){

            convertView = inflater.inflate(R.layout.custom_layout,null);

        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        ImageView selectImg = convertView.findViewById(R.id.check);

        ImagesModel model = imagesModelArrayList.get(i);
        Glide.with(mContext).load(model.getPath()).into(imageView);

        if (model.isCheck()){
            selectImg.setVisibility(View.VISIBLE);
        }else {
            selectImg.setVisibility(View.GONE);
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (itemLongClickListener != null){
                    itemLongClickListener.onItemClick(i,imageView);
                }
                return true;
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null){
                    itemClickListener.onItemClick(i,selectImg,imageView);
                }
            }
        });
        return convertView;
    }

    public void removeItem(int pos){
        imagesModelArrayList.remove(pos);
    }


    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

}
