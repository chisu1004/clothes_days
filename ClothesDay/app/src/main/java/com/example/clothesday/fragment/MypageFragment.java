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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.Adapter.MyPostRecyclerViewAdapter;
import com.example.clothesday.R;
import com.example.clothesday.Adapter.TwoLineRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.request.FollowCountRequest;
import com.example.clothesday.request.MyPageLikeRequest;
import com.example.clothesday.request.MyPagePostRequest;
import com.example.clothesday.request.MyPageScrapRequest;
import com.example.clothesday.request.ProfileGetRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MypageFragment() {
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
    public static MypageFragment newInstance(String param1, String param2) {
        MypageFragment fragment = new MypageFragment();
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
    
    //바텀내비
    MenuItem prevBottomNavigation;
    BottomNavigationView bottomNavigationView;


    //뷰
    Button post_add;
    TextView profile_name, follower_text, following_text, follower,following;
    ImageView profile_picture;

    PostManagementFragment post_management_fragment;
    String ME_NICK, ME_ID;

    String URL = "http://dlsxjsptb.cafe24.com/post/myPage.jsp";
    ImageView left_icon;

    //프리퍼런스
    SharedPreferences pref;

    //라디오 그룹
    RadioGroup radio_group;
    RadioButton scrap_radio, my_post_radio, like_radio;
    //
    FragmentManager fm;




    //리사이클러뷰
    ArrayList<PostDTO> mList = new ArrayList<PostDTO>();
    RecyclerView recyclerView;
    TwoLineRecyclerViewAdapter twoLineRecyclerViewAdapter;
    MyPostRecyclerViewAdapter myPostRecyclerViewAdapter;

    //데이터
    String ME_PIC;
    int follower_count = 0;
    int following_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_mypage ,container,false);

        // 프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_NICK = pref.getString("ME_NICK", "사용자 닉네임");
        ME_ID = pref.getString("ME_ID", null);
        fm = getActivity().getSupportFragmentManager();


        //뷰 연결
        recyclerView = root.findViewById(R.id.mypage_recyclerview);
        post_add = root.findViewById(R.id.mypage_post_add);
        profile_name = root.findViewById(R.id.mypage_profile_name);
        radio_group = root.findViewById(R.id.mypage_radio_group);
        like_radio = root.findViewById(R.id.mypage_my_like_radio_button);
        scrap_radio = root.findViewById(R.id.mypage_my_scrap_radio_button);
        my_post_radio = root.findViewById(R.id.mypage_my_post_radio_button);
        left_icon = root.findViewById(R.id.mypage_left_icon);
        profile_picture = root.findViewById(R.id.mypage_profile_picture);
        follower_text = root.findViewById(R.id.mypage_follower_word);
        following_text = root.findViewById(R.id.mypage_following_word);
        following = root.findViewById(R.id.mypage_following);
        follower = root.findViewById(R.id.mypage_follower);

        //리스너 등록
        post_add.setOnClickListener(this);
        radio_group.setOnCheckedChangeListener(this);
        following.setOnClickListener(this);
        follower.setOnClickListener(this);
        follower_text.setOnClickListener(this);
        follower_text.setOnClickListener(this);

        //리사이클러뷰      

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //초기상태
        Bundle bundle = getArguments();
        String result1 = "";
        if (bundle != null) {
            result1 = bundle.getString("bundleKey");
        }
        if (result1.equals("scrap")) {
            scrap_radio.setChecked(true);
            getMyScrap();

        }
        else {
            my_post_radio.setChecked(true);
            getMyPost();
            bottomNavigationView = activity.findViewById(R.id.main_bottomnavi);
            prevBottomNavigation = bottomNavigationView.getMenu().getItem(4);
            prevBottomNavigation.setChecked(true);

        }

        // 프래그먼트
        post_management_fragment = new PostManagementFragment();

        //라디오
        left_icon.setImageResource(R.drawable.board_icon);

        // 프로필
        profile_name.setText(ME_NICK);
        GetProfile();
        CountFollow();

        root.invalidate();

        return root;
    }


    @Override
    public void onClick(View v) {

        FollowFragment followFragment = new FollowFragment ();
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_ID = pref.getString("ME_ID", null);
        Bundle result = new Bundle();
        switch (v.getId()) {
            case R.id.mypage_post_add:
                fm.beginTransaction().detach(post_management_fragment).attach(post_management_fragment).replace(R.id.container, post_management_fragment).addToBackStack(null).commit();
                break;
            case R.id.mypage_follower:
                    result.putString("ME_ID", ME_ID);
                result.putString("ME_NICK", ME_NICK);
                    result.putString("type", "follower");
                    fm.setFragmentResult("requestKey", result);
                      followFragment.setArguments(result);
                    fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.mypage_following:
                result.putString("ME_ID", ME_ID);
                result.putString("ME_NICK", ME_NICK);
                result.putString("type", "following");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.mypage_follower_word:
                result.putString("ME_ID", ME_ID);
                result.putString("ME_NICK", ME_NICK);
                result.putString("type", "follower");
                fm.setFragmentResult("requestKey", result);
                followFragment.setArguments(result);
                fm.beginTransaction().detach(followFragment).attach(followFragment).replace(R.id.container, followFragment).addToBackStack(null).commit();
                break;
            case R.id.mypage_following_word:
                result.putString("ME_ID", ME_ID);
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
            case R.id.mypage_my_post_radio_button:
                left_icon.setImageResource(R.drawable.board_icon);
                getMyPost();
                bottomNavigationView = activity.findViewById(R.id.main_bottomnavi);
                prevBottomNavigation = bottomNavigationView.getMenu().getItem(4);
                prevBottomNavigation.setChecked(true);
                break;
            case R.id.mypage_my_scrap_radio_button: // 스크랩 버튼
                left_icon.setImageResource(R.drawable.scrap_icon);
                getMyScrap();
                bottomNavigationView = activity.findViewById(R.id.main_bottomnavi);
                prevBottomNavigation = bottomNavigationView.getMenu().getItem(3);
                prevBottomNavigation.setChecked(true);
                break;
            case R.id.mypage_my_like_radio_button: // 좋아요 버튼
                left_icon.setImageResource(R.drawable.mypage_like);
                getMyLikePost();
                break;

        }
    }

    public void getMyPost() { // 내 게시글

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
                    myPostRecyclerViewAdapter = new MyPostRecyclerViewAdapter(getContext(), mList, activity, fm);
                    recyclerView.setAdapter(myPostRecyclerViewAdapter);


                    
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
            mypageRequest = new MyPagePostRequest(ME_ID, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(mypageRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMyScrap() { // 내스크랩

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
                    twoLineRecyclerViewAdapter = new TwoLineRecyclerViewAdapter(context, mList, activity, fm, 0);
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
            mypageScrapRequest = new MyPageScrapRequest(ME_ID, responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add( mypageScrapRequest );
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        FollowCountRequest FollowCountRequest = new FollowCountRequest(ME_ID, responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(FollowCountRequest);

    }

    public void getMyLikePost() { // 내스크랩

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
                    twoLineRecyclerViewAdapter = new TwoLineRecyclerViewAdapter(context, mList, activity, fm, 0);
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
            mypageLikeRequest = new MyPageLikeRequest(ME_ID, responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add( mypageLikeRequest );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetProfilePicture() {
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + ME_PIC);

        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(profile_picture);
    }

    private void GetProfile() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    ME_PIC = result.getString("ME_PIC");
                    SetProfilePicture();
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
        ProfileGetRequest ProfileGetRequest = new ProfileGetRequest(ME_ID, responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(ProfileGetRequest);

    }

}





