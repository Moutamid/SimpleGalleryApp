package com.moutamid.simplegalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.moutamid.simplegalleryapp.Adapters.SlideViewPagerAdapter;
import com.moutamid.simplegalleryapp.Model.ImagesModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SlideShowImages extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<ImagesModel> modelDataList;
    private SlideViewPagerAdapter adapter;
    private int speed;
    private Handler handler = new Handler();
    private boolean stop;
    private int currentPage = 0;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show_images);
        viewPager = findViewById(R.id.viewPager);
        modelDataList = getIntent().getParcelableArrayListExtra("model");

        getDataWithCategory();


    }

    //Fetching Images and Videos from firebase data
    private void getDataWithCategory() {
        /*ArrayList<ImagesModel> imagesModelArrayList = new ArrayList<>();
        for (ImagesModel model : modelDataList){
            imagesModelArrayList.add(model);
        }*/
      //  Collections.shuffle(imagesModelArrayList);
        adapter = new SlideViewPagerAdapter(SlideShowImages.this,modelDataList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setupAutoPager(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    //Swiping to next slide automatically
    private void setupAutoPager(int pos) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(pos+1);
            }
        },2000);
       /* final Runnable update = new Runnable() {
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                   //     if (!stop) {
                        ++currentPage;

                            viewPager.setCurrentItem(currentPage);
                        }
             //       }
                });
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        },5000);*/

    }


    private void getRandomImages() {
        for (int i =0; i < modelDataList.size(); i++){
            //modelDataList.add(modelList.get(i));
            Collections.shuffle(modelDataList,new Random(i));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SlideShowImages.this,MainActivity.class));
        finish();
    }
}