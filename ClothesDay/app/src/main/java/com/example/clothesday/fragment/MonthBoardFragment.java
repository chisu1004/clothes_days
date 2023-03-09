package com.example.clothesday.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.Adapter.OneLineRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.request.GetMonthBoardPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthBoardFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MonthBoardFragment() {
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
     * @return A new instance of fragment MonthBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthBoardFragment newInstance(String param1, String param2) {
        MonthBoardFragment fragment = new MonthBoardFragment();
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


    RadioButton radio_month_first, radio_month_second, radio_month_third;
    RadioGroup radioGroup;
    //리사이클러뷰
    RecyclerView recyclerView;
    ArrayList<PostDTO> postData = new ArrayList<PostDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    OneLineRecyclerViewAdapter oneLineRecyclerViewAdapter;

    FragmentManager fm;
    //프리퍼런스
    SharedPreferences pref;


    //사용자정보
    String ME_ID, ME_NICK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month_board ,container,false);
        //뷰연결
        recyclerView = root.findViewById(R.id.month_board_recyclerview);
        radio_month_first = root.findViewById(R.id.month_board_first_month);
        radio_month_second = root.findViewById(R.id.month_board_second_month);
        radio_month_third = root.findViewById(R.id.month_board_third_month);
        radioGroup = root.findViewById(R.id.month_board_radio_group);

        //리스너등록
        radioGroup.setOnCheckedChangeListener(this);

        fm = getActivity().getSupportFragmentManager();

        //프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", context.MODE_PRIVATE);
        ME_ID = (pref.getString("ME_ID",null));

        // 리사이클러뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        //초기상태
        radio_month_first.setTextSize(16);
        radio_month_second.setTextSize(16);
        radio_month_third.setTextSize(16);

        Bundle bundle = getArguments();
        String month = "";
        if (bundle != null) {
            month = bundle.getString("month");
        }

        checkMonth(month);
        root.invalidate();

        return root;
    }

    void checkMonth(String month2) {
        switch (month2) {
            case "1" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.WHITE);
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setChecked(true);
                radio_month_first.setText("12월");
                radio_month_second.setText("1월");
                radio_month_third.setText("2월");
                getMonthPost(month2 + "월");
                return;
            case "2" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.WHITE);
                radio_month_third.setChecked(true);
                radio_month_first.setText("12월");
                radio_month_second.setText("1월");
                radio_month_third.setText("2월");
                getMonthPost(month2 + "월");
                return;
            case "3" :
                radio_month_first.setTextColor(Color.WHITE);
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_first.setChecked(true);
                radio_month_first.setText("3월");
                radio_month_second.setText("4월");
                radio_month_third.setText("5월");
                getMonthPost(month2 + "월");
                return;
            case "4" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.WHITE);
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setChecked(true);
                radio_month_first.setText("3월");
                radio_month_second.setText("4월");
                radio_month_third.setText("5월");
                getMonthPost(month2 + "월");
                return;
            case "5" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.WHITE);
                radio_month_third.setChecked(true);
                radio_month_first.setText("3월");
                radio_month_second.setText("4월");
                radio_month_third.setText("5월");
                getMonthPost(month2 + "월");
                return;
            case "6" :
                radio_month_first.setTextColor(Color.WHITE);
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_first.setChecked(true);
                radio_month_first.setText("6월");
                radio_month_second.setText("7월");
                radio_month_third.setText("8월");
                getMonthPost(month2 + "월");
                return;
            case "7" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.WHITE);
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setChecked(true);
                radio_month_first.setText("6월");
                radio_month_second.setText("7월");
                radio_month_third.setText("8월");
                getMonthPost(month2 + "월");
                return;
            case "8" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.WHITE);
                radio_month_third.setChecked(true);
                radio_month_first.setText("6월");
                radio_month_second.setText("7월");
                radio_month_third.setText("8월");
                getMonthPost(month2 + "월");
                return;
            case "9" :
                radio_month_first.setTextColor(Color.WHITE);
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_first.setChecked(true);
                radio_month_first.setText("9월");
                radio_month_second.setText("10월");
                radio_month_third.setText("11월");
                getMonthPost(month2 + "월");
                return;
            case "10" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.WHITE);
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setChecked(true);
                radio_month_first.setText("9월");
                radio_month_second.setText("10월");
                radio_month_third.setText("11월");
                getMonthPost(month2 + "월");
                return;
            case "11" :
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.WHITE);
                radio_month_third.setChecked(true);
                radio_month_first.setText("9월");
                radio_month_second.setText("10월");
                radio_month_third.setText("11월");
                getMonthPost(month2 + "월");
                return;
            case "12" :
                radio_month_first.setChecked(true);
                radio_month_first.setText("12월");
                radio_month_second.setText("1월");
                radio_month_third.setText("2월");
                radio_month_first.setTextColor(Color.WHITE);
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                getMonthPost(month2 + "월");
                return;
            default:
                return;
        }
    }
    void getMonthPost(String month3) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {
                    postData = new ArrayList<PostDTO>();
                    userData = new ArrayList<UserDTO>();
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("post");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        UserDTO user = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        post.setPO_CATE(jObject.getString("PO_CATE"));
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_PIC(jObject.getString("PO_PIC"));
                        post.setPO_ME_ID(jObject.getString("PO_ME_ID"));
                        user.setME_PIC(jObject.getString("ME_PIC"));
                        user.setME_NICK(jObject.getString("ME_NICK"));
                        user.setME_ID(ME_ID);
                        userData.add(user);
                        postData.add(post);
                    }

                    oneLineRecyclerViewAdapter = new OneLineRecyclerViewAdapter(context, postData, userData, activity, (fm==null)?getParentFragmentManager():fm);
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
       GetMonthBoardPostRequest GetMonthBoardPostRequest = null;
        try {
            GetMonthBoardPostRequest = new GetMonthBoardPostRequest(month3 ,responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(GetMonthBoardPostRequest );

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.month_board_first_month:
                radio_month_first.setTextColor(Color.WHITE);
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                getMonthPost(radio_month_first.getText().toString());
                return;
            case R.id.month_board_second_month:
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.WHITE);
                radio_month_third.setTextColor(Color.parseColor("#646CB7"));
                getMonthPost(radio_month_second.getText().toString());
                return;
            case R.id.month_board_third_month:
                radio_month_first.setTextColor(Color.parseColor("#646CB7"));
                radio_month_second.setTextColor(Color.parseColor("#646CB7"));
                radio_month_third.setTextColor(Color.WHITE);
                getMonthPost(radio_month_third.getText().toString());
                return;

        }
    }

}