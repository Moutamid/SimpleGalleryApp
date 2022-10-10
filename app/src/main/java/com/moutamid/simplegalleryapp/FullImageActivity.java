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
import com.moutamid.simplegalleryapp.Model.ImagesModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FullImageActivity extends AppCompatActivity {

    private ImageView menuImg,imageView;
    private CardView detailsView,menuCard,editCard;
    private LinearLayout bottom_layout,rename_layout;
    private TextView editBtn,zoomBtn,shareBtn,deleteBtn,wallpaperBtn,cropBtn,rotateBtn,renameBtn,detailsBtn;
    private ImagesModel model;
    private TextView nameTxt,typeTxt,sizeTxt,dateTxt,resolutionTxt,pathTxt,okBtn,cancelBtn;
    private EditText renameEdit;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private boolean zoomImage = false;
    private boolean menuSelect = false;
    private boolean detailSelect = false;
    private boolean menuEdit = false;
    private Bitmap rotatedBitmap = null;
    private Button applyBtn;
    private ViewPager viewPager;
    int pos = 0;
    ArrayList<ImagesModel> modelArrayList = new ArrayList<>();
    ArrayList<ImagesModel> imagesModelArrayList = new ArrayList<>();
    //private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        menuImg = findViewById(R.id.menu);
      //  imageView = findViewById(R.id.imageView);
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
        modelArrayList = getIntent().getParcelableArrayListExtra("list");
        pos = getIntent().getIntExtra("pos",0);
        for (int i = pos ; i < modelArrayList.size(); i++){
            //if (i < pos || i == pos || i > pos){
       //         model = modelArrayList.get(i);
                imagesModelArrayList.add(modelArrayList.get(i));
            //}
        }

       // Toast.makeText(this, ""+ modelArrayList.size(), Toast.LENGTH_SHORT).show();
        SwipeViewPagerAdapter adapter = new SwipeViewPagerAdapter(FullImageActivity.this,
                imagesModelArrayList);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        imageView = adapter.image;

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
                zoomImage = true;
                mScaleGestureDetector = new ScaleGestureDetector(FullImageActivity.this, new ScaleListener());
            }
        });

        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               imageView.setRotation(imageView.getRotation() + 90);

                Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                imageView.draw(canvas);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotatedBitmap =Bitmap.createBitmap(bitmap,imageView.getLeft(), imageView.getTop(),
                        imageView.getWidth()-20,
                        imageView.getHeight()-100,matrix,true);
                bitmap.recycle();
                File filePathLocal = new File(model.getPath());
                saveFile(rotatedBitmap, filePathLocal);
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
                showDeleteDialig(model.getPath());
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
                setWallpaperImage();
                menuCard.setVisibility(View.GONE);
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

    private void renameFileName(String rename) {
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
                        "=?", new String[] { file.getAbsolutePath() });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(file.getAbsolutePath())));
        }

        Toast.makeText(FullImageActivity.this,"Saving Image...", Toast.LENGTH_LONG).show();
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
            imageView.setImageURI(imageUri);

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = imageView.getDrawingCache();

            Bitmap crop = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            bitmap.recycle();
            File file = new File(model.getPath());
            saveFile(crop,file);
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

        Toast.makeText(FullImageActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (zoomImage) {
            mScaleGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    @SuppressLint("NewApi")
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
  //      startActivity(new Intent(FullImageActivity.this,MainActivity.class));
        finish();
    }
    public class SwipeViewPagerAdapter extends PagerAdapter implements LoopingPagerAdapter {

        Context ctx;
        ArrayList<ImagesModel> modelDataArrayList;
        public ImageView image;

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
            model = modelDataArrayList.get(position);
            image =view.findViewById(R.id.imageView);
            Glide.with(ctx).load(model.getPath()).into(image);

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
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuSelect || menuEdit || detailSelect){
                        menuCard.setVisibility(View.GONE);
                        detailsView.setVisibility(View.GONE);
                        editCard.setVisibility(View.GONE);
                        menuImg.setVisibility(View.VISIBLE);
                    }
                }
            });
            container.addView(view);

            return view;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getRealCount() {
            return modelDataArrayList.size();
        }
    }

}