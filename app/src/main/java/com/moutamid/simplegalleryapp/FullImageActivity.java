package com.moutamid.simplegalleryapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moutamid.simplegalleryapp.Adapters.SlideViewPagerAdapter;
import com.moutamid.simplegalleryapp.Listener.LoopingPagerAdapter;
import com.moutamid.simplegalleryapp.Listener.ViewClickListener;
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FullImageActivity extends AppCompatActivity {

    private ImageView menuImg;
    private CardView detailsView,menuCard,editCard;
    private LinearLayout bottom_layout,rename_layout;
    private TextView editBtn,zoomBtn,shareBtn,deleteBtn,wallpaperBtn,cropBtn,rotateBtn,renameBtn,detailsBtn;
    private ImagesModel model = null;
    private TextView nameTxt,typeTxt,sizeTxt,dateTxt,resolutionTxt,pathTxt,okBtn,cancelBtn;
    private EditText renameEdit;
    //private boolean zoomImage = false;
    private boolean menuSelect = false;
    private boolean detailSelect = false;
    private boolean menuEdit = false;
    private Bitmap rotatedBitmap = null;
    private Button applyBtn;
    private ViewPager viewPager;
    int pos = 0;
    private boolean enableClick = false;
    ArrayList<ImagesModel> modelArrayList = new ArrayList<>();
    //ArrayList<ImagesModel> imagesModelArrayList = new ArrayList<>();
    private ImageView imageView = null;
    private ImageView zoomImageView;
    //private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        menuImg = findViewById(R.id.menu);
        //      imageView = findViewById(R.id.imageView);
        editBtn = findViewById(R.id.edit);
        zoomBtn = findViewById(R.id.zoom);
        menuCard = findViewById(R.id.menuCard);
        detailsView = findViewById(R.id.details);
        editCard = findViewById(R.id.editCard);
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        wallpaperBtn = findViewById(R.id.wallpaper);
        cropBtn = findViewById(R.id.crop);
        rotateBtn = findViewById(R.id.rotate);
        renameBtn = findViewById(R.id.rename);
        detailsBtn = findViewById(R.id.detail);
        bottom_layout = findViewById(R.id.bottom);
        rename_layout = findViewById(R.id.rename_layout);
        nameTxt = findViewById(R.id.name);
        typeTxt = findViewById(R.id.type);
        sizeTxt = findViewById(R.id.size);
        applyBtn = findViewById(R.id.apply);
        resolutionTxt = findViewById(R.id.resolution);
        dateTxt = findViewById(R.id.date);
        pathTxt = findViewById(R.id.path);
        okBtn = findViewById(R.id.ok);
        cancelBtn = findViewById(R.id.cancel);
        renameEdit = findViewById(R.id.editTxt);
        viewPager = findViewById(R.id.viewPager);
        //model = getIntent().getParcelableExtra("imageModel");
      //  modelArrayList = getIntent().getParcelableArrayListExtra("list");
        getAllImages();
        pos = getIntent().getIntExtra("pos",0);
       SwipeViewPagerAdapter adapter = new SwipeViewPagerAdapter(FullImageActivity.this,
                modelArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);
        adapter.setViewClickListener(new ViewClickListener() {
            @Override
            public void onItemClick(int position, ImageView image) {
                if (menuSelect || menuEdit || detailSelect){
                    menuCard.setVisibility(View.GONE);
                    detailsView.setVisibility(View.GONE);
                    editCard.setVisibility(View.GONE);
                    menuImg.setVisibility(View.VISIBLE);
                }else {
                    imageView = image;
                    model = modelArrayList.get(position);
                  //  Glide.with(FullImageActivity.this).load(model.getPath()).into(imageView);
                    nameTxt.setText("Name: " + model.getName());
                    typeTxt.setText("Type: " + model.getType());
                    sizeTxt.setText("Size: " + model.getSize());
                    String resolution = getDropboxIMGSize(Uri.parse(model.getPath()));
                    resolutionTxt.setText("Resolution: " + resolution);

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis( model.getDate());
                    String date = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();
                    dateTxt.setText("Date: " + date);
                    pathTxt.setText("Path: " + model.getPath());
                    //   Glide.with(FullImageActivity.this).load(model.getPath()).into(imageView);
                    renameEdit.setText(model.getName());
                //    Toast.makeText(FullImageActivity.this, ""+model.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                model = modelArrayList.get(position);

                nameTxt.setText("Name: " + model.getName());
                typeTxt.setText("Type: " + model.getType());
                sizeTxt.setText("Size: " + model.getSize());
                String resolution = getDropboxIMGSize(Uri.parse(model.getPath()));
                resolutionTxt.setText("Resolution: " + resolution);

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis( model.getDate());
                String date = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();
                dateTxt.setText("Date: " + date);
                pathTxt.setText("Path: " + model.getPath());
                //   Glide.with(FullImageActivity.this).load(model.getPath()).into(imageView);
                renameEdit.setText(model.getName());
            }

            @Override
            public void onPageSelected(int position) {
                model = modelArrayList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.notifyDataSetChanged();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!menuEdit){
                    menuImg.setVisibility(View.GONE);
                    editCard.setVisibility(View.VISIBLE);
                    menuCard.setVisibility(View.GONE);
                    detailsView.setVisibility(View.GONE);
                    menuEdit = true;
                }else {
                    menuEdit = false;
                    editCard.setVisibility(View.GONE);
                    menuImg.setVisibility(View.VISIBLE);
                }
            }
        });
        zoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //zoomImage = true;
                if (model!=null) {
                    Intent intent = new Intent(FullImageActivity.this, ZoomImageActivity.class);
                    intent.putExtra("image", model.getPath());
                    startActivity(intent);
                }
            }
        });


        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView != null) {
                    imageView.setRotation(imageView.getRotation() + 90);

                    Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    imageView.draw(canvas);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    rotatedBitmap = Bitmap.createBitmap(bitmap, imageView.getLeft(), imageView.getTop(),
                            imageView.getWidth() - 20,
                            imageView.getHeight() - 100, matrix, true);
                    bitmap.recycle();
                    File filePathLocal = new File(model.getPath());
                    saveFile(rotatedBitmap, filePathLocal);
                }
                //  applyBtn.setVisibility(View.VISIBLE);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File filePathLocal = new File(model.getPath());
                saveFile(rotatedBitmap, filePathLocal);
                applyBtn.setVisibility(View.GONE);
                editCard.setVisibility(View.GONE);
                menuImg.setVisibility(View.VISIBLE);
            }
        });

        /*mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

            }
        });

         */

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!menuSelect){
                    menuCard.setVisibility(View.VISIBLE);
                    editCard.setVisibility(View.GONE);
                    detailsView.setVisibility(View.GONE);
                    menuSelect = true;
                }else {
                    menuCard.setVisibility(View.GONE);
                    menuSelect = false;
                }
            }
        });

        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuCard.setVisibility(View.GONE);
                editCard.setVisibility(View.GONE);
                detailsView.setVisibility(View.VISIBLE);
                menuImg.setVisibility(View.GONE);
                detailSelect = true;
            }
        });

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_layout.setVisibility(View.GONE);
                rename_layout.setVisibility(View.VISIBLE);
                menuImg.setVisibility(View.GONE);
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = renameEdit.getText().toString();
                if (!rename.isEmpty()){
                    renameFileName(rename);
                }
                editCard.setVisibility(View.GONE);
                rename_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
                menuImg.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rename_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
                menuImg.setVisibility(View.VISIBLE);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuCard.setVisibility(View.GONE);
                if (model != null) {
                    showDeleteDialig(model.getPath());
                }
                //Toast.makeText(SelectImageActivity.this,imagesModel.getPath(),Toast.LENGTH_LONG).show();
            }
        });
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage();
                editCard.setVisibility(View.GONE);
                menuImg.setVisibility(View.VISIBLE);
            }
        });
        wallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model != null) {
                    setWallpaperImage();
                    menuCard.setVisibility(View.GONE);
                }
            }
        });

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuSelect || menuEdit || detailSelect){
                    menuCard.setVisibility(View.GONE);
                    detailsView.setVisibility(View.GONE);
                    editCard.setVisibility(View.GONE);
                    menuImg.setVisibility(View.VISIBLE);
                }
            }
        });*/
    }

    private String getDropboxIMGSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return imageWidth+"x"+imageHeight;
    }

    private void setWallpaperImage() {
        File file = new File(model.getPath());
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(FullImageActivity.this,"Set Wallpaper Successfully",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllImages() {
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
                @SuppressLint("Range") long data = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                @SuppressLint("Range") float byteImage = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                //@SuppressLint("Range") String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RESOLUTION));
                String size = calculateFileSize(byteImage);
                ImagesModel model = new ImagesModel();
                model.setName(name);
                model.setPath(path);
                model.setType(type);
                model.setDate(data);
                model.setCheck(false);
                //model.setResolution(resolution);
                model.setSize(size);
                modelArrayList.add(model);
            } while (cursor.moveToNext());
        }

        Collections.sort(modelArrayList, new Comparator<ImagesModel>() {
            @Override
            public int compare(ImagesModel imagesModel, ImagesModel t1) {
                return Long.compare(t1.getDate(),imagesModel.getDate());
            }
        });

    }

    private void renameFileName(String rename) {
        if (imageView != null) {
            File file = new File(model.getPath());
      /*  int index = file.getPath().lastIndexOf("/");
        String path = file.getPath().substring(0, index);
        File newname = new File(path + "/");
        if (!newname.exists()) {
            success = newname.mkdir();
        }*/

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap bitmap = imageView.getDrawingCache();

            String imagePath = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    rename,
                    "demo_image"
            );

            Uri uri = Uri.parse(imagePath);
            ContentResolver resolver = getContentResolver();
            resolver.delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA +
                            "=?", new String[]{file.getAbsolutePath()});

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
            } else {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(file.getAbsolutePath())));
            }

            Toast.makeText(FullImageActivity.this, "Saving Image..." + uri.getPath(), Toast.LENGTH_LONG).show();
        }
    }


    private void cropImage() {
        File file =  new File(model.getPath());

        String des = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        // UCrop.of(Uri.fromFile(file),Uri.fromFile(file)).start(FullImageActivity.this);
        UCrop.Options options = new UCrop.Options();

        UCrop.of(Uri.fromFile(file),Uri.fromFile(file))
                .withOptions(options)
                .withAspectRatio(0,0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000,2000)
                .start(FullImageActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri imageUri = UCrop.getOutput(data);
            if (imageView != null) {
                imageView.setImageURI(imageUri);

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = imageView.getDrawingCache();

                Bitmap crop = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight());
                bitmap.recycle();
                File file = new File(model.getPath());
                saveFile(crop, file);
            }
        }else if (requestCode == UCrop.RESULT_ERROR){
            Log.d("Error",""+UCrop.getError(data));
        }
    }


    private void shareImage() {
        Uri imagePath = Uri.parse(model.getPath());
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    private void showDeleteDialig(String path){
        AlertDialog.Builder builder = new AlertDialog.Builder(FullImageActivity.this);
        builder.setTitle("Delete Image");
        builder.setMessage("Do you want to delete this image?  ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refreshGallery(path);
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

        startActivity(new Intent(FullImageActivity.this,MainActivity.class));
        finish();
    }


    private void saveFile(Bitmap bitmap, File file) {
        String imagePath = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                file.getName(),
                "demo_image"
        );

        Uri uri = Uri.parse(imagePath);

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

        Toast.makeText(FullImageActivity.this, "Image Saved Successfully" + uri.getPath(), Toast.LENGTH_LONG).show();

    }

    String calString;
    public String calculateFileSize(float fileSizeInBytes) {
        //String filepathstr=filepath.toString();

        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;
        //  calString = Math.round(fileSizeInKB) + " KB";
      //  calString = Math.round(fileSizeInMB) + " MB";
        if (fileSizeInKB < 1024){
            calString = Math.round(fileSizeInKB) + " KB";
        }else {
           calString = Math.round(fileSizeInMB) + " MB";
        }
        return calString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // startActivity(new Intent(FullImageActivity.this,MainActivity.class));
        finish();
    }

    public class SwipeViewPagerAdapter extends PagerAdapter implements LoopingPagerAdapter {

        Context ctx;
        ArrayList<ImagesModel> modelDataArrayList;
        public ImageView image;
        private ViewClickListener viewClickListener;

        public SwipeViewPagerAdapter(Context ctx, ArrayList<ImagesModel> modelDataArrayList) {
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
            image =view.findViewById(R.id.imageView);
            Glide.with(ctx).load(model.getPath()).into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewClickListener != null){
                        viewClickListener.onItemClick(position,image);
                    }
                }
            });



            container.addView(view);

            return view;
        }

        public void setViewClickListener(ViewClickListener viewClickListener){
            this.viewClickListener = viewClickListener;
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
    }

}