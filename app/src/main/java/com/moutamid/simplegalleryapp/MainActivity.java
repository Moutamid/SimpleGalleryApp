package com.moutamid.simplegalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moutamid.simplegalleryapp.Adapters.ImageListAdapter;
import com.moutamid.simplegalleryapp.Model.ImagesModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImagesModel> imagesModelArrayList;
    private RecyclerView recyclerView;
    private LinearLayout select_layout;
    private ImageView menuImg;
    private TextView cameraBtn,selectBtn;
    private ImageListAdapter obj_adapter;
    private Uri imageUri2;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private boolean select= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagesModelArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        select_layout = findViewById(R.id.bottom);
        menuImg = findViewById(R.id.menu);
        cameraBtn = findViewById(R.id.camera);
        selectBtn = findViewById(R.id.select);
        if (checkPermission()){
            getAllImages();
        }else {
           // requestPermission();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 100);
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    openCamera();
                }else {
                    //requestPermission();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA}, 100);
                }
            }
        });
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = true;
                startActivity(new Intent(MainActivity.this,SelectImageActivity.class));
                finish();
            }
        });

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri2 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE );
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED
                && result1 ==PackageManager.PERMISSION_GRANTED
                && result2 ==PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (readExternalStorage){
                getAllImages();
            }
        }

    }



    private void getAllImages() {
        imagesModelArrayList.clear();
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
                @SuppressLint("Range") int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
               // @SuppressLint("Range") int resolution = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.S));

                ImagesModel model = new ImagesModel();
                model.setName(name);
                model.setPath(path);
                model.setType(type);
                model.setDate(data);
                //model.setResolution(resolution);
                model.setSize(calculateFileSize(size) + " MB");
                imagesModelArrayList.add(model);
            } while (cursor.moveToNext());
        }


        Collections.sort(imagesModelArrayList, new Comparator<ImagesModel>() {
            @Override
            public int compare(ImagesModel imagesModel, ImagesModel t1) {
                return Long.compare(t1.getDate(),imagesModel.getDate());
            }
        });

        obj_adapter = new ImageListAdapter(MainActivity.this, imagesModelArrayList);
        recyclerView.setAdapter(obj_adapter);
        obj_adapter.notifyDataSetChanged();
    }


    public String calculateFileSize(int fileSizeInBytes) {
        //String filepathstr=filepath.toString();

        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;

        String calString = Float.toString(Math.round(fileSizeInMB));
        return calString;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //set the image captured to our ImageView
            //cameraBtn.setImageURI(imageUri2);
        }

    }
}