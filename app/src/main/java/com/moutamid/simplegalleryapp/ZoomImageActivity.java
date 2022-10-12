package com.moutamid.simplegalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ZoomImageActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;
    private String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        imageView = findViewById(R.id.imageView);
        image = getIntent().getStringExtra("image");
        Glide.with(ZoomImageActivity.this).load(image).into(imageView);
        mScaleGestureDetector = new ScaleGestureDetector(ZoomImageActivity.this,
                new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }


    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if (imageView != null) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                mScaleFactor = Math.max(0.1f,
                        Math.min(mScaleFactor, 10.0f));
                imageView.setScaleX(mScaleFactor);
                imageView.setScaleY(mScaleFactor);
            }else {
                Toast.makeText(ZoomImageActivity.this, "Scale not working..", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

}