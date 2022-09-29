package com.moutamid.simplegalleryapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropImageActivity extends AppCompatActivity {

    private String path = "";
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

/*        path = getIntent().getStringExtra("DATA");
        fileUri = Uri.parse(path);

        File file = new File(path);

        String des = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();

        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),des)))
                .withOptions(options)
                .withAspectRatio(0,0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000,2000)
                .start(CropImageActivity.this);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri imageUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT",imageUri);
            setResult(-1,returnIntent);
            finish();

            Bitmap bitmap = BitmapFactory.decodeFile(path);

            Bitmap crop = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            bitmap.recycle();
            File file = new File(path);
            saveFile(crop,file);
        }else if (requestCode == UCrop.RESULT_ERROR){
            Log.d("Error",""+UCrop.getError(data));
        }
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

        Toast.makeText(CropImageActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

    }
}