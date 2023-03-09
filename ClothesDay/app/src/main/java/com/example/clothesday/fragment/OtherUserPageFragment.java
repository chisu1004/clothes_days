package com.example.clothesday.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.R;
import com.example.clothesday.Adapter.TwoLineRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.request.FollowCheckRequest;
import com.example.clothesday.request.FollowCountRequest;
import com.example.clothesday.request.FollowRequest;
import com.example.clothesday.request.MyPageLikeRequest;
import com.example.clothesday.request.MyPagePostRequest;
import com.example.clothesday.request.MyPageScrapRequest;
import com.example.clothesday.request.ProfileGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherUserPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherUserPageFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, onBackPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherUserPageFragment() {
        // Required empty public constructor
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MypageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherUserPageFragment newInstance(String param1, String param2) {
        OtherUserPageFragment fragment = new OtherUserPageFragment();
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

    //뷰
    TextView profile_name, follower_text, following_text, follower, following;
    ImageView profile_picture;
    Button follow_btn;
    ImageView left_icon;

    //프리퍼런스
    SharedPreferences pref;

    //라디오 그룹
    RadioGroup radio_group;
    RadioButton scrap_radio, my_post_radio, like_radio;
    FragmentManager fragmentManager;

    //리사이클러뷰
    ArrayList<PostDTO> mList = new ArrayList<PostDTO>();
    RecyclerView recyclerView;
    TwoLineRecyclerViewAdapter twoLineRecyclerViewAdapter;

    //데이터
    String ME_NICK, FO_ME_ID, FO_FOL_ID;
    String ME_PIC;
    int follower_count = 0;
    int following_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_other_user_page ,container,false);

        // 프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        FO_FOL_ID = pref.getString("ME_ID", null);

        //뷰 연결
        recyclerView = root.findViewById(R.id.other_user_recyclerview);
        profile_name = root.findViewById(R.id.other_user_profile_name);
        radio_group = root.findViewById(R.id.other_user_radio_group);
        like_radio = root.findViewById(R.id.other_user_my_like_radio_button);
        scrap_radio = root.findViewById(R.id.other_user_my_scrap_radio_button);
        my_post_radio = root.findViewById(R.id.other_user_my_post_radio_button);
        left_icon = root.findViewById(R.id.other_user_left_icon);
        profile_picture = root.findViewById(R.id.other_user_profile_picture);
        follow_btn = root.findViewById(R.id.other_user_follow_btn);
        follower_text = root.findViewById(R.id.other_user_follow_word);
        following_text = root.findViewById(R.id.other_user_following_word);
        follower = root.findViewById(R.id.other_user_follow);
        following = root.findViewById(R.id.other_user_following);


        //리스너 등록
        radio_group.setOnCheckedChangeListener(this);
        follow_btn.setOnClickListener(this);
        follower_text.setOnClickListener(this);
        following.setOnClickListener(this);
        following_text.setOnClickListener(this);
        follower.setOnClickListener(this);

        //리사이클러뷰

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity.getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //초기상태
        Bundle bundle = getArguments();
        if (bundle != null) {
            FO_ME_ID = bundle.getString("ME_ID");
        }


        //라디오
        left_icon.setImageResource(R.drawable.board_icon);

        //프래그먼트매니저
        fragmentManager = getParentFragmentManager();

        CheckFollow();
        GetProfile();
        GetMyPost();
        CountFollow();

        root.invalidate();

        return root;
    }


    @Override
    public void onClick(View v) {
        FragmentManager fm = getParentFragmentManager();
        FollowFragment followFragment = new FollowFragment ();
        Bundle result = new Bundle();
        switch (v.getId()) {
            case R.id.other_user_follow_btn:
                FollowRequest();
                break;
            case R.id.other_user_follow:
                result.putString("ME_ID", FO_ME_ID);
                result.putString("ME_NICK", ME_NICK);
                result.putString("type", "follower");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.other_user_following:
                result.putString("ME_ID", FO_ME_ID);
                result.putString("ME_NICK", ME_NICK);
                result.putString("type", "following");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.other_user_follow_word:
                result.putString("ME_ID", FO_ME_ID);
                result.putString("MME_NICK", ME_NICK);
                result.putString("type", "follower");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.other_user_following_word:
                result.putString("ME_ID", FO_ME_ID);
                result.putString("ME_NICK", ME_NICK);
                result.putString("type", "following");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.other_user_my_post_radio_button:
                left_icon.setImageResource(R.drawable.board_icon);
                GetMyPost();
                break;
            case R.id.other_user_my_scrap_radio_button: // 스크랩 버튼
                left_icon.setImageResource(R.drawable.scrap_icon);
                GetMyScrap();
                break;
            case R.id.other_user_my_like_radio_button: // 좋아요 버튼
                left_icon.setImageResource(R.drawable.mypage_like);
                GetMyLikePost();
                break;

        }
    }

    private void FollowRequest() {
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
                            follow_btn.setText("팔로잉");
                        else if (check == 0)
                            follow_btn.setText("언팔로잉");
                    } else  {
                        Toast.makeText(activity.getApplicationContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                    CountFollow();
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
        FollowRequest followRequest =  new FollowRequest(FO_ME_ID, FO_FOL_ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(followRequest);


    }

    public void CheckFollow()  {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public  void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    int res = result.getInt("check"); // 1 : 팔로우 되어있음 0: 팔로우 x

                    if (String.valueOf(res).equals("1")) {
                        follow_btn.setText("언팔로잉");
                    } else {
                        follow_btn.setText("팔로잉");
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


    public void GetMyPost() { // 내 게시글

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList = new ArrayList<PostDTO>();
                String resultResponse = response;
                try {
                    int post_count = 0;
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("post");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        post.setPO_CATE(jObject.getString("PO_CATE"));
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_PIC(jObject.getString("PO_PIC"));
                        post_count++;
                        mList.add(post);
                    }
                    twoLineRecyclerViewAdapter = new TwoLineRecyclerViewAdapter(activity.getApplicationContext(), mList, activity, getParentFragmentManager(), 1);
                    recyclerView.setAdapter(twoLineRecyclerViewAdapter);

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
        MyPagePostRequest mypageRequest = null;
        try {
            mypageRequest = new MyPagePostRequest(FO_ME_ID, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(mypageRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GetMyScrap() { // 내스크랩

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList = new ArrayList<PostDTO>();
                String resultResponse = response;
                try {
                    int post_count = 0;
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("scrap");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        post.setPO_CATE(jObject.getString("PO_CATE"));
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_PIC(jObject.getString("PO_PIC"));
                        mList.add(post);
                        post_count++;
                    }
                    twoLineRecyclerViewAdapter = new TwoLineRecyclerViewAdapter(activity.getApplicationContext(), mList, activity, getParentFragmentManager(), 0);
                    recyclerView.setAdapter(twoLineRecyclerViewAdapter);


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
        MyPageScrapRequest mypageScrapRequest = null;
        try {
            mypageScrapRequest = new MyPageScrapRequest(FO_ME_ID, responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add( mypageScrapRequest );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GetMyLikePost() { // 좋아요 게시글

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList = new ArrayList<PostDTO>();
                String resultResponse = response;
                try {
                    int post_count = 0;
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("scrap");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        post.setPO_CATE(jObject.getString("PO_CATE"));
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_PIC(jObject.getString("PO_PIC"));
                        mList.add(post);
                        post_count++;
                    }
                    twoLineRecyclerViewAdapter = new TwoLineRecyclerViewAdapter(activity.getApplicationContext(), mList, activity, getParentFragmentManager(), 0);
                    recyclerView.setAdapter(twoLineRecyclerViewAdapter);


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
        MyPageLikeRequest mypageLikeRequest = null;
        try {
            mypageLikeRequest = new MyPageLikeRequest(FO_ME_ID, responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add( mypageLikeRequest );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetProfile() {
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + ME_PIC);
        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(profile_picture);
        profile_name.setText(ME_NICK);
    }

    private void GetProfile() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    ME_PIC = result.getString("ME_PIC");
                    ME_NICK = result.getString("ME_NICK");
                    SetProfile();
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

        ProfileGetRequest ProfileGetRequest = new ProfileGetRequest(FO_ME_ID, responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(ProfileGetRequest);

    }

    private void CountFollow() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    follower_count = result.getInt("follower");
                    following_count = result.getInt("following");
                    follower_text.setText(String.valueOf(follower_count));
                    following_text.setText(String.valueOf(following_count));

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

        FollowCountRequest FollowCountRequest = new FollowCountRequest(FO_ME_ID, responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(FollowCountRequest);

    }

    @Override
    public void onBackPressed() {
        goToMain();
    }

    //프래그먼트 종료
    private void goToMain(){
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }



}

