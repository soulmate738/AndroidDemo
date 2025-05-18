package com.example.sparkchaindemo.helper;

import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.adapter.VoicePackageAdapter;

public class SwipeToActionCallback extends ItemTouchHelper.Callback {

    private final ActionCompletionListener listener;

    public interface ActionCompletionListener {
        void onSwipeLeft(RecyclerView.ViewHolder viewHolder);
        void onSwipeRight(RecyclerView.ViewHolder viewHolder);
    }

    public SwipeToActionCallback(ActionCompletionListener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 允许左滑和右滑
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false; // 不处理拖拽
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            listener.onSwipeLeft(viewHolder);
        } else if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipeRight(viewHolder);
        }
    }

    @Override
    public void onChildDraw(android.graphics.Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        // 自定义滑动动画
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 获取前景视图
            View foregroundView = ((VoicePackageAdapter.VoicePackageViewHolder) viewHolder).foregroundView;

            // 仅处理左滑（dX为负值）
            if (dX < 0) {
                // 滑动时背景显示，前景跟随滑动
                View backgroundView = ((VoicePackageAdapter.VoicePackageViewHolder) viewHolder).backgroundView;
                backgroundView.setVisibility(View.VISIBLE);

                // 限制滑动距离为背景宽度
                float limit = -backgroundView.getWidth();
                if (dX < limit) {
                    dX = limit;
                }

                // 设置前景视图的位置
                foregroundView.setTranslationX(dX);
            } else {
                // 右滑或复位时隐藏背景
                View backgroundView = ((VoicePackageAdapter.VoicePackageViewHolder) viewHolder).backgroundView;
                backgroundView.setVisibility(View.GONE);
                foregroundView.setTranslationX(0);
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        // 清除滑动状态，重置视图
        View foregroundView = ((VoicePackageAdapter.VoicePackageViewHolder) viewHolder).foregroundView;
        View backgroundView = ((VoicePackageAdapter.VoicePackageViewHolder) viewHolder).backgroundView;

        foregroundView.setTranslationX(0);
        backgroundView.setVisibility(View.GONE);
    }
}