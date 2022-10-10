package com.moutamid.simplegalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moutamid.simplegalleryapp.Adapters.SelectedImageListAdapter;
import com.moutamid.simplegalleryapp.Listener.ItemClickListener;
import com.moutamid.simplegalleryapp.Listener.ItemLongClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SelectImageActivity extends AppCompatActivity {

    private CardView menuCard;
    private TextView shareBtn,deleteBtn,slideshowBtn,selectBtn,selectAllBtn,cancelBtn;
    private ArrayList<ImagesModel> imagesModelArrayList;
    private GridView gridView;
    private ImageView menuImg;
    private SelectedImageListAdapter obj_adapter;
    private ImagesModel imagesModel;
    private boolean menuSelect = false;
    private boolean selectAll = false;
    private int pos = 0;
    private boolean longPress = false;
    private ArrayList<ImagesModel> modelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        imagesModelArrayList = new ArrayList<>();
        gridView = findViewById(R.id.gridView);
     //   recyclerView.setLayoutManager(new GridLayoutManager(SelectImageActivity.this,2));
        menuImg = findViewById(R.id.menu);
        selectBtn = findViewById(R.id.select);
        cancelBtn = findViewById(R.id.cancel);
        selectAllBtn = findViewById(R.id.selectAll);
        menuCard = findViewById(R.id.menuCard);
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        slideshowBtn = findViewById(R.id.slideshow);
        getAllImages(false);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ImagesModel model: modelArrayList) {
                    refreshGallery(model.getPath());
                }
                menuCard.setVisibility(View.GONE);
                //Toast.makeText(SelectImageActivity.this,imagesModel.getPath(),Toast.LENGTH_LONG).show();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ImagesModel model: modelArrayList) {
                    shareImage(model.getPath());
                }
                menuCard.setVisibility(View.GONE);
                //Toast.makeText(SelectImageActivity.this,imagesModel.getPath(),Toast.LENGTH_LONG).show();
            }
        });

        slideshowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectImageActivity.this, SlideShowImages.class);
                intent.putExtra("model",modelArrayList);
                startActivity(intent);
                menuCard.setVisibility(View.GONE);
            }
        });
        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuSelect){
                    menuCard.setVisibility(View.VISIBLE);
                    menuSelect = false;
                }else {
                    menuCard.setVisibility(View.GONE);
                    menuSelect = true;
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectImageActivity.this,MainActivity.class));
                finish();
            }
        });
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectAll) {
                    getAllImages(true);
                    menuSelect = true;
                    selectAllBtn.setText("Deselect All");
                    selectAll = true;
                }else {
                    menuSelect = false;
                    getAllImages(false);
                    selectAllBtn.setText("Select All");
                    selectAll = false;
                }
            }
        });
    }

    public void refreshGallery(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        ContentResolver resolver = getContentResolver();
        resolver.delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA +
                        "=?", new String[] { file.getAbsolutePath() });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(file.getAbsolutePath())));
        }

        obj_adapter.removeItem(pos);
    }


    private void shareImage(String path) {
        Uri imagePath = Uri.parse(path);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    private void getAllImages(boolean check) {
        imagesModelArrayList.clear();
        modelArrayList.clear();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
//               String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                @SuppressLint("Range") long data = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                //@SuppressLint("Range") String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RESOLUTION));
                ImagesModel model = new ImagesModel();
                model.setName(name);
                model.setPath(path);
                model.setType(type);
                model.setDate(data);
                model.setCheck(check);
                //model.setResolution(resolution);
                model.setSize(size);
                imagesModelArrayList.add(model);

                if (check){
                    modelArrayList.add(model);
                }
            } while (cursor.moveToNext());
        }

        Collections.sort(imagesModelArrayList, new Comparator<ImagesModel>() {
            @Override
            public int compare(ImagesModel imagesModel, ImagesModel t1) {
                return Long.compare(t1.getDate(),imagesModel.getDate());
            }
        });

        obj_adapter = new SelectedImageListAdapter(SelectImageActivity.this, imagesModelArrayList);
        gridView.setAdapter(obj_adapter);

        obj_adapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemClick(int position, ImageView imageView) {
                longPress = true;
            //    imagesModel = imagesModelArrayList.get(position);
              //  menuSelect = true;
               // imageView.setVisibility(View.VISIBLE);
                //modelArrayList.add(imagesModel);
            }
        });


        obj_adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SelectImageActivity.this,MainActivity.class));
        finish();
    }
}