package com.shivam.canvas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> itemsData;
 private  Context ctx;
    public ImageAdapter(ArrayList<Map<String, Object>> itemsData,Context ctx) {
        this.ctx=ctx;
        this.itemsData = itemsData;
    }

    @Override
    public ImageAdapter
            .ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

       Glide.with(ctx).load((String) itemsData.get(viewHolder.getAdapterPosition()).get("url")).into(viewHolder.imgViewIcon);
       viewHolder.text.setText("Created By : "+(String)itemsData.get(viewHolder.getAdapterPosition()).get("creator"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView imgViewIcon;
        public  TextView text;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.recycler_image);
            text=itemLayoutView.findViewById(R.id.textview_text);
        }
    }


    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
