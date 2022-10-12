package com.moutamid.simplegalleryapp.Listener;

import android.content.Intent;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

public interface LoopingPagerAdapter {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    int getRealCount();
}
