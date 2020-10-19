package com.example.memo;


public interface ItemTouchHelperCallBack {
    public void onItemMoved(int fromPosition, int toPosition);

    public void onItemDismiss(int position);
}
