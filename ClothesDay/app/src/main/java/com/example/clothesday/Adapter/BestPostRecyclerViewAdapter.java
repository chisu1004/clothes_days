package com.example.clothesday.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.data.Indicator;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.data.Time;
import com.example.clothesday.request.BestPostGetOneRequest;
import com.example.clothesday.request.PostGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BestPostRecyclerViewAdapter extends RecyclerView.Adapter< BestPostRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<PostDTO> postData = new ArrayList<PostDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    Activity activity;
    RecyclerView recyclerView;
    FragmentManager fm;
    int selected = 0;
    int last_selected = 10;
    int once = 1;



    public BestPostRecyclerViewAdapter(Context context, ArrayList<PostDTO> PostDTO, ArrayList<UserDTO> UserDTO, Activity activity, RecyclerView recyclerView, FragmentManager fm)  {
        this.context = context;
        this.postData = PostDTO;
        this.userData = UserDTO;
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.fm = fm;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_best_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Indicator indicator = new Indicator();
        ArrayList<Uri> uriList = new ArrayList<>();
        PostDTO post = postData.get(position);
        UserDTO user = userData.get(position);

        //게시글 순위
        holder.best_rank.setText((position + 1) + "위 게시글");


        //프로필 이름
        holder.best_profile_name.setText(user.getME_NICK());


        //프로필 사진
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + user.getME_PIC());
        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(holder.best_profile_pic);


        if (position == selected) {
            if (once == 1) {
                getOneBestPost(post.getPO_ID(), recyclerView);
                holder.best_background.setBackgroundResource(R.drawable.best_background_selected);
            } else {
                holder.best_background.setBackgroundResource(R.drawable.best_background_selected);

            }

        } else
            holder.best_background.setBackgroundResource(R.drawable.best_background);




    }

    @Override
    public int getItemCount() { return postData.size(); }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView best_rank, best_profile_name;
        ImageView best_profile_pic;
        View best_background;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            best_rank = itemView.findViewById(R.id.best_post_rank);
            best_profile_name = itemView.findViewById(R.id.best_profile_name);
            best_background = itemView.findViewById(R.id.best_background);
            best_profile_pic = itemView.findViewById(R.id.best_profile_picture);

            best_rank.setOnClickListener(this);
            best_profile_name.setOnClickListener(this);
            best_background.setOnClickListener(this);
            best_profile_pic.setOnClickListener(this);

        }

        public void RequestBestPost() {
            int pos = getBindingAdapterPosition() ;
            if (pos != RecyclerView.NO_POSITION) {
                notifyItemChanged(selected);
                PostDTO post =  postData.get(pos);
                getOneBestPost(post.getPO_ID(), recyclerView);
                selected = pos;
                notifyItemChanged(pos);
            }
        }

        @Override
        public void onClick(View v) {
            RequestBestPost();
         }
      }

    public void getOneBestPost(int PO_ID, RecyclerView recyclerView) {
        ArrayList<PostDTO> postData2 = new ArrayList<PostDTO>();
        ArrayList<UserDTO> userData2 = new ArrayList<UserDTO>();

        once = 0;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String resultResponse = response;
                try {
                    Indicator indicator = new Indicator();
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("post");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PostDTO postdao = new PostDTO();
                        UserDTO userdao = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        postdao.setPO_ID(jObject.getInt("PO_ID"));
                        postdao.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        postdao.setPO_CATE(jObject.getString("PO_CATE"));
                        postdao.setPO_TAG(jObject.getString("PO_TAG"));
                        postdao.setPO_PIC(jObject.getString("PO_PIC")); //완
                        postdao.setPO_ME_ID(jObject.getString("PO_ME_ID"));
                        userdao.setME_NICK(jObject.getString("ME_NICK")); //완
                        userdao.setME_PIC(jObject.getString("ME_PIC")); //완
                        postData2.add(postdao);
                        userData2.add(userdao);
                    }
                    System.out.println("Asdas");
                    OneLineRecyclerViewAdapter oneLineRecyclerViewAdapter = new OneLineRecyclerViewAdapter(context, postData2, userData2, activity, fm);
                    recyclerView.setAdapter(oneLineRecyclerViewAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        };
        BestPostGetOneRequest BestPostGetOneRequest = null;
        try {
            BestPostGetOneRequest = new   BestPostGetOneRequest(String.valueOf(PO_ID), responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add( BestPostGetOneRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


 }

