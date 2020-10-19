package com.example.memo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {
    private ItemTouchHelperCallBack mCallBack;

    public SimpleItemTouchHelper(ItemTouchHelperCallBack callBack) {
        mCallBack = callBack;
    }

    // 定义触发事件的滑动方向
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // 移动位置发生事件
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder fromViewHolder, @NonNull RecyclerView.ViewHolder toViewHolder) {
        mCallBack.onItemMoved(fromViewHolder.getAbsoluteAdapterPosition(), toViewHolder.getAbsoluteAdapterPosition());
        return true;
    }

    // 滑动发生事件
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mCallBack.onItemDismiss(viewHolder.getAbsoluteAdapterPosition());
    }
}
