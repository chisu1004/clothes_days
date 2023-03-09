package com.example.clothesday.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.DAO.ReplyDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.ReplyActivity;
import com.example.clothesday.data.Indicator;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.data.Time;
import com.example.clothesday.fragment.MypageFragment;
import com.example.clothesday.fragment.OtherUserPageFragment;
import com.example.clothesday.fragment.PostFragment;
import com.example.clothesday.request.ReplyDeleteRequest;
import com.example.clothesday.request.ReplyGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReplyRecyclerViewAdapter extends RecyclerView.Adapter<ReplyRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ReplyDTO> replyData = new ArrayList<ReplyDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    SharedPreferences pref;
    Activity activity;
    String ME_ID;

    public ReplyRecyclerViewAdapter(Context context, ArrayList<ReplyDTO> ReplyDTO, ArrayList<UserDTO> UserDTO, Activity activity)  {
        this.context = context;
        this.replyData = ReplyDTO;
        this.userData = UserDTO;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_reply, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ReplyDTO reply = replyData.get(position);
        UserDTO user = userData.get(position);
        
        //댓글 등록일
        try {
            Time time = new Time();
            String reg = reply.getRE_REG_DA() + " " + time.getDayOfWeek(reply.getRE_REG_DA());
            holder.reply_reg.setText(reg + "요일");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //댓글내용
        holder.reply_con.setText(reply.getRE_CON());

        
        //프로필 이름
        holder.profile_name.setText(user.getME_NICK());


        //프로필 사진
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + user.getME_PIC());
        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(holder.profile_pic);

        //삭제 버튼
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_ID = pref.getString("ME_ID", null);
        System.out.println(ME_ID + " : " + reply.getRE_ME_ID());
        if (ME_ID.equals(reply.getRE_ME_ID())) {
            holder.reply_delete.setVisibility(View.VISIBLE);
        } else {
            holder.reply_delete.setVisibility(View.GONE);
        }




    }

    public void DeleteItem(int pos) {
        replyData.remove(pos);
        userData.remove(pos);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() { return replyData.size(); }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView reply_con, reply_reg, profile_name, reply_delete;
        ImageView profile_pic;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_name = itemView.findViewById(R.id.reply_profile_name);
            reply_reg = itemView.findViewById(R.id.reply_reg_date);
            profile_pic = itemView.findViewById(R.id.reply_profile_picture);
            reply_con = itemView.findViewById(R.id.reply_content);
            reply_delete = itemView.findViewById(R.id.reply_delete_reply);

            reply_delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                int RE_ID = replyData.get(pos).getRE_ID();
            switch (v.getId()) {
                case R.id.reply_delete_reply:                  
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("댓글 삭제").setMessage("댓글을 삭제하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            DeleteReply(RE_ID, pos);
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();             
                    break;

            }
        }
    }
    public void DeleteReply(int PO_ID, int pos) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    int res = result.getInt("result");

                    if (String.valueOf(res).equals("1")) {
                        DeleteItem(pos);
                        Toast.makeText(context, "댓글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "댓글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }

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

        ReplyDeleteRequest ReplyDeleteRequest = new ReplyDeleteRequest(String.valueOf(PO_ID), responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(ReplyDeleteRequest);

    }
    }


}
