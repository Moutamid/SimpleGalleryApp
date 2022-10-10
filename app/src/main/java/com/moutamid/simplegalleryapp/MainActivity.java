package com.moutamid.simplegalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moutamid.simplegalleryapp.Adapters.ImageListAdapter;
import com.moutamid.simplegalleryapp.Adapters.SelectedImageListAdapter;
import com.moutamid.simplegalleryapp.Listener.ItemClickListener;
import com.moutamid.simplegalleryapp.Listener.ItemLongClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImagesModel> imagesModelArrayList;
    private GridView gridView;
    private LinearLayout select_layout,bottom_layout;
    private ImageView menuImg;
    private TextView cameraBtn,selectBtn,selectionBtn,selectAllBtn,cancelBtn,shareBtn,deleteBtn,slideshowBtn;
    private SelectedImageListAdapter obj_adapter;
    private Uri imageUri2;
    private boolean menuSelect = false;
    private boolean selectAll = false;
    private int pos = 0;
    private CheckBox checkBox;
    private boolean longPress = false;
    private CardView menuCard;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private ArrayList<ImagesModel> modelArrayList = new ArrayList<>();
    private ImagesModel imagesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagesModelArrayList = new ArrayList<>();
        gridView = findViewById(R.id.gridView);
    //    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        bottom_layout = findViewById(R.id.bottom);
        select_layout = findViewById(R.id.select_layout);
        menuImg = findViewById(R.id.menu);
        cameraBtn = findViewById(R.id.camera);
        selectBtn = findViewById(R.id.select);
        selectionBtn = findViewById(R.id.select1);
        cancelBtn = findViewById(R.id.cancel);
        selectAllBtn = findViewById(R.id.selectAll);
        menuCard = findViewById(R.id.menuCard);
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        slideshowBtn = findViewById(R.id.slideshow);
        checkBox = findViewById(R.id.selectBox);
        if (checkPermission()){
            getAllImages(false);
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
                bottom_layout.setVisibility(View.GONE);
                select_layout.setVisibility(View.VISIBLE);
            }
        });
        getAllImages(false);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialig();
                //Toast.makeText(SelectImageActivity.this,imagesModel.getPath(),Toast.LENGTH_LONG).show();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ImagesModel model: modelArrayList) {
                    shareImage(model.getPath());
                }
                longPress = false;
                menuCard.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                //Toast.makeText(SelectImageActivity.this,imagesModel.getPath(),Toast.LENGTH_LONG).show();
            }
        });

        slideshowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SlideShowImages.class);
                intent.putExtra("model",modelArrayList);
                startActivity(intent);
                longPress = false;
                menuCard.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
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
                select_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
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
                getAllImages(false);
            }
        }

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
                @SuppressLint("Range") float byteImage = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String size = calculateFileSize(byteImage);
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

        obj_adapter = new SelectedImageListAdapter(MainActivity.this, imagesModelArrayList);
        gridView.setAdapter(obj_adapter);

        obj_adapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemClick(int position, ImageView imageView) {
                longPress = true;
                checkBox.setVisibility(View.VISIBLE);
                //    imagesModel = imagesModelArrayList.get(position);
                //  menuSelect = true;
                // imageView.setVisibility(View.VISIBLE);
                //modelArrayList.add(imagesModel);
            }
        });

        obj_adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, ImageView imageView,ImageView bigImg) {
                pos = position;
                imagesModel = imagesModelArrayList.get(position);
                if(longPress){
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(350, 350);
                    bigImg.setLayoutParams(layoutParams);
                    menuSelect = true;
                    imageView.setVisibility(View.VISIBLE);
                    modelArrayList.add(imagesModel);
                }else {
                    Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                    //intent.putExtra("imageModel",imagesModel);
                    intent.putExtra("pos",position);
                    intent.putExtra("list",imagesModelArrayList);
                    startActivity(intent);
                }
              /*  if (!menuSelect){
                    menuSelect = true;
                    imageView.setVisibility(View.VISIBLE);
                    modelArrayList.add(imagesModel);
                }else {
                    menuSelect = false;
                    imageView.setVisibility(View.GONE);
                    modelArrayList.remove(position);
                }*/
            }
        });
        obj_adapter.notifyDataSetChanged();
    }


    String calString;
    public String calculateFileSize(float fileSizeInBytes) {
        //String filepathstr=filepath.toString();

        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;

        if (fileSizeInKB != 0.0){
            calString = Math.round(fileSizeInKB) + " KB";
        }else {
           calString = Math.round(fileSizeInMB) + " MB";
        }
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


    private void showDeleteDialig(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Image");
        builder.setMessage("Do you want to delete this image?  ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (ImagesModel model: modelArrayList) {
                    refreshGallery(model.getPath());
                }

                obj_adapter.removeItem(pos);
                obj_adapter.notifyDataSetChanged();
                longPress = false;
                menuCard.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
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

    }

}