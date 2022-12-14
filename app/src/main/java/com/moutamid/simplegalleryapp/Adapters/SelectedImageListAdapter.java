package com.moutamid.simplegalleryapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.simplegalleryapp.Listener.ItemClickListener;
import com.moutamid.simplegalleryapp.Listener.ItemLongClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.moutamid.simplegalleryapp.R;

import java.util.ArrayList;

public class SelectedImageListAdapter extends RecyclerView.Adapter<SelectedImageListAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<ImagesModel> imagesModelArrayList;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public SelectedImageListAdapter(Context mContext, ArrayList<ImagesModel> imagesModelArrayList) {
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
        if (model.isCheck()){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(350, 350);
            //params.height = params.width;
            holder.imageView.setLayoutParams(layoutParams);
            holder.cardView.setLayoutParams(layoutParams);
            holder.selectImg.setVisibility(View.VISIBLE);
        }else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(400, 400);
            //params.height = params.width;
            holder.imageView.setLayoutParams(layoutParams);
            holder.cardView.setLayoutParams(layoutParams);
            holder.selectImg.setVisibility(View.GONE);
        }

    }

    public void removeItem(int pos){
        imagesModelArrayList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeRemoved(pos,imagesModelArrayList.size());
    }

    @Override
    public int getItemCount() {
        return imagesModelArrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView,selectImg;
        private CardView cardView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            selectImg = itemView.findViewById(R.id.check);
            cardView = itemView.findViewById(R.id.card);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (itemLongClickListener != null){
                        itemLongClickListener.onItemClick(getAdapterPosition(),imageView);
                    }
                    return true;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(), itemView);
                    }
                }
            });

        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

}
