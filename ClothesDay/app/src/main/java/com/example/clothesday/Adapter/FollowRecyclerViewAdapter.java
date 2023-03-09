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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import com.example.clothesday.request.FollowCheckRequest;
import com.example.clothesday.request.FollowRequest;
import com.example.clothesday.request.ReplyDeleteRequest;
import com.example.clothesday.request.ReplyGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowRecyclerViewAdapter extends RecyclerView.Adapter<FollowRecyclerViewAdapter.MyViewHolder> {
    
    Context context;
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    SharedPreferences pref;
    Activity activity;
    String ME_ID;
    FragmentManager fm;
    int type = 0; // 내 프로필인지 다른 사용자 프로필인지  1 : 내 프로필              0 : 다른 사용자 프로필
        


    public FollowRecyclerViewAdapter(Context context, ArrayList<UserDTO> UserDTO, Activity activity,FragmentManager fm, int type)  {
        this.context = context;
        this.type = type;
        this.userData = UserDTO;
        this.activity = activity;
        this.fm = fm;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_follow, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserDTO user = userData.get(position);

        //프로필 이름
        holder.profile_name.setText(user.getME_NICK());


        //프로필 사진
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + user.getME_PIC());
        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(holder.profile_pic);


        //팔로우 버튼
        CheckFollow(holder, user.getME_ID());

    }


    @Override
    public int getItemCount() { return userData.size(); }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView profile_name;
        ImageView profile_pic;
        Button follow_btn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_name = itemView.findViewById(R.id.recycler_follow_profile_name);
            follow_btn = itemView.findViewById(R.id.recycler_follow_follow_btn);
            profile_pic = itemView.findViewById(R.id.recycler_follow_profile_picture);

            profile_name.setOnClickListener(this);
            profile_pic.setOnClickListener(this);
            follow_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                OtherUserPageFragment otherUserPageFragment = new OtherUserPageFragment();
                UserDTO userDTO = userData.get(pos);
                Bundle result = new Bundle();
                switch (v.getId()) {
                    case R.id.recycler_follow_profile_name:
                        result.putString("ME_ID", userDTO.getME_ID());
                        fm.setFragmentResult("requestKey", result);
                        otherUserPageFragment.setArguments(result);
                        fm.beginTransaction().detach(otherUserPageFragment).attach(otherUserPageFragment).replace(R.id.container, otherUserPageFragment).addToBackStack(null).commit();
                        break;
                    case R.id.recycler_follow_profile_picture:
                        result.putString("ME_ID", userDTO.getME_ID());
                        fm.setFragmentResult("requestKey", result);
                        otherUserPageFragment.setArguments(result);
                        fm.beginTransaction().detach(otherUserPageFragment).attach(otherUserPageFragment).replace(R.id.container, otherUserPageFragment).addToBackStack(null).commit();
                        break;
                    case R.id.recycler_follow_follow_btn:
                        FollowRequest(userDTO.getME_ID(), pos);
                        break;
                }
            }
        }

        private void FollowRequest(String FO_ME_ID, int pos) {
            pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
            String FO_FOL_ID = pref.getString("ME_ID", null);

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String resultResponse = response;
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        int res = result.getInt("result");
                        int check = result.getInt("check");
                        if (res == 1){
                            if (check == 1)
                                ;
                            else if (check == 0)
                                ;
                        } else  {
                            Toast.makeText(activity.getApplicationContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notifyItemChanged(pos);
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
            FollowRequest followRequest =  new FollowRequest(FO_ME_ID, FO_FOL_ID, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(followRequest);


        }


    }





    public void CheckFollow(MyViewHolder holder, String FO_ME_ID)  {
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        String FO_FOL_ID = pref.getString("ME_ID", null);
        if (FO_ME_ID.equals(FO_FOL_ID)) {
            holder.follow_btn.setVisibility(View.GONE);
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public  void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    int res = result.getInt("check"); // 1 : 팔로우 되어있음 0: 팔로우 x

                    if (String.valueOf(res).equals("1")) {
                        holder.follow_btn.setText("언팔로잉");
                    } else {
                        holder.follow_btn.setText("팔로잉");
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
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        };

        FollowCheckRequest FollowCheckRequest = new FollowCheckRequest(FO_ME_ID, FO_FOL_ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add( FollowCheckRequest );

        return;
    }


}
