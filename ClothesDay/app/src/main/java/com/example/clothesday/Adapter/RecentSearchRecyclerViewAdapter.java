package com.example.clothesday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothesday.DAO.SearchDTO;
import com.example.clothesday.R;
import com.example.clothesday.data.Indicator;
import com.example.clothesday.data.Time;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.fragment.PostFragment;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class RecentSearchRecyclerViewAdapter  extends RecyclerView.Adapter<RecentSearchRecyclerViewAdapter.MyViewHolder> {

    ArrayList<SearchDTO> mData = new ArrayList<SearchDTO>();


    public RecentSearchRecyclerViewAdapter(ArrayList<SearchDTO> searchDTO)  {
        this.mData = searchDTO;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_recent_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SearchDTO searchDTO = mData.get(position);

        holder.content.setText(searchDTO.getSE_CON());

    }

    @Override
    public int getItemCount() { return mData.size(); }


    public class MyViewHolder extends RecyclerView.ViewHolder  {

        TextView content;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.recyclerview_recent_search_text);


        }


    }

}
