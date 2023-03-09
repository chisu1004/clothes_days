package com.example.clothesday.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.Adapter.BestPostRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.request.BestPostGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NoticeBoardFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoticeBoardFragment() {
    }

    Activity activity;
    Context context;

    @Override
    public void onAttach(Context context) {
        this.context = context;

        if (context instanceof Activity) {
            activity = (Activity)context;
        }
        super.onAttach(context);
    }



    // TODO: Rename and change types and number of parameters
    public static NoticeBoardFragment newInstance(String param1, String param2) {
        NoticeBoardFragment fragment = new NoticeBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // 리사이클러뷰
    RecyclerView best_post_recyclerView, show_post_recyclerView;
    ArrayList<PostDTO> postData = new ArrayList<PostDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    BestPostRecyclerViewAdapter bestPostRecyclerViewAdapter;

    //프리퍼런스
    SharedPreferences pref;

    //사용자정보
    String ME_ID, ME_NICK;

    FragmentManager fm;

    //요청 주소
    final static private String URL = "http://dlsxjsptb.cafe24.com/post/noticeBoard.jsp";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_notice_board ,container,false);

        //프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", context.MODE_PRIVATE);
        ME_ID = (pref.getString("ME_ID",null));

        // 뷰 연결
        best_post_recyclerView = root.findViewById(R.id.noticeboard_best_post_recyclerview);
        show_post_recyclerView = root.findViewById(R.id.noticeboard_post_recyclerview);
        
        // 리사이클러뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        best_post_recyclerView.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        show_post_recyclerView.setLayoutManager(layoutManager2);

        fm =fm;


        GetBestPosts();
        return root;
    }


    public void GetBestPosts() {
        //인기 게시글 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    postData = new ArrayList<PostDTO>();
                    userData = new ArrayList<UserDTO>();
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("bestPost");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        UserDTO user = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        user.setME_PIC(jObject.getString("ME_PIC"));
                        user.setME_NICK(jObject.getString("ME_NICK"));
                        user.setME_ID(ME_ID);
                        userData.add(user);
                        postData.add(post);
                    }
                    bestPostRecyclerViewAdapter = new BestPostRecyclerViewAdapter(context, postData, userData, activity, show_post_recyclerView,fm);
                    best_post_recyclerView.setAdapter(bestPostRecyclerViewAdapter);

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
        BestPostGetRequest BestPostGetRequest = null;
        try {
            BestPostGetRequest = new BestPostGetRequest(responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(BestPostGetRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}