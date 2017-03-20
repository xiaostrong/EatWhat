package gq.fokia.eatwhat;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-11.
 */

public class MyCallBack extends ItemTouchHelper.Callback {
    private int dragFlags;
    private int swipeFlags;
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        dragFlags = 0;
        swipeFlags = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager
                || recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            if (viewHolder.getAdapterPosition() != 0)
              swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        //这里的toPosition和fromPosition结合可以设置那一项不能改变
        if (toPosition >= 2 && fromPosition != 0 && fromPosition !=1){
            if (fromPosition < toPosition) {
                //向下拖动
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(foodList, i, i + 1);
                }
            }else {
                //向上拖动
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(foodList, i, i - 1);
                }
            }
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        recyclerView.getAdapter().notifyItemRemoved(position);
        Food food = foodList.get(position);
        Log.d("position food name", food.getName());
        db.delete("food", "name=?", new String[]{food.getName()});
        foodList.remove(position);
        String picturePath = Environment.getExternalStorageDirectory().toString() + "/EatWhat/";
        Log.d(getClass().toString(), picturePath);
        File dir = new File(picturePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(picturePath, food.getName()+".jpg");
        file.delete();
    }

    //选中放大viewHolder
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //松开后还原viewHolder
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }

}
