package com.wkq.media.view.crop.gestures;

import android.view.MotionEvent;

public interface GestureDetector
{
    boolean onTouchEvent(MotionEvent ev);

    boolean isDragging();

    boolean isScaling();

    void setOnGestureListener(OnGestureListener listener);
}