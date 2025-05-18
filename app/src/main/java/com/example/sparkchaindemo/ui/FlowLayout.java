package com.example.sparkchaindemo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FlowLayout extends ConstraintLayout {
    private float lastX;
    private float lastY;
    private boolean isDragging = false;
    private boolean isScaling = false;
    private View mScale;
    private float startScaleX;
    private float startScaleY;

    public FlowLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isDragging = true;
                return true;
            }
        });
    }

    public void setScaleButton(View scaleButton) {
        this.mScale = scaleButton;
        this.mScale.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isScaling = true;
                        startScaleX = x;
                        startScaleY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isScaling) {
                            float dx = x - startScaleX;
                            float dy = y - startScaleY;
                            ViewGroup.LayoutParams layoutParams = getLayoutParams();
                            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                                ConstraintLayout.LayoutParams constraintParams = (ConstraintLayout.LayoutParams) layoutParams;
                                constraintParams.width += dx;
                                constraintParams.height += dy;
                                setLayoutParams(constraintParams);
                            }
                            startScaleX = x;
                            startScaleY = y;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isScaling = false;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScale != null && isPointInsideButton(x, y, mScale)) {
                    isScaling = true;
                    startScaleX = x;
                    startScaleY = y;
                    return true;
                }
                isDragging = true;
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float dx = x - lastX;
                    float dy = y - lastY;
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                        ConstraintLayout.LayoutParams constraintParams = (ConstraintLayout.LayoutParams) layoutParams;
                        constraintParams.leftMargin += dx;
                        constraintParams.topMargin += dy;
                        setLayoutParams(constraintParams);
                    }
                    lastX = x;
                    lastY = y;
                } else if (isScaling) {
                    float dx = x - startScaleX;
                    float dy = y - startScaleY;
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                        ConstraintLayout.LayoutParams constraintParams = (ConstraintLayout.LayoutParams) layoutParams;
                        constraintParams.width += dx;
                        constraintParams.height += dy;
                        setLayoutParams(constraintParams);
                    }
                    startScaleX = x;
                    startScaleY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                isScaling = false;
                break;
        }
        return true;
    }

    private boolean isPointInsideButton(float x, float y, View button) {
        int[] location = new int[2];
        button.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + button.getWidth();
        int bottom = top + button.getHeight();
        return x >= left && x <= right && y >= top && y <= bottom;
    }
}    