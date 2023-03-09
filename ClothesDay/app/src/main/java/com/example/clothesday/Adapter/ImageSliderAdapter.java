package com.example.clothesday.Adapter;



import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.clothesday.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Uri> mData = null ;
    Activity activity;

    public ImageSliderAdapter(Context context,ArrayList<Uri> list, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mData = list ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Uri image_uri = mData.get(position) ;
        Glide.with(context).load(image_uri).into(holder. mImageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void DeleteImage(int pos) {

        mData.remove(pos);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
            mImageView.setOnCreateContextMenuListener(this);
            mImageView.setOnClickListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }

        @Override
        public void onClick(View v) {

        }
    }
}