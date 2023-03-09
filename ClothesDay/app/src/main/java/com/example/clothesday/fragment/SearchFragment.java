package com.example.clothesday.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.Adapter.RecentSearchRecyclerViewAdapter;
import com.example.clothesday.Adapter.SearchResultRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.DAO.SearchDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.request.RecentSearchRequest;
import com.example.clothesday.request.SearchRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener, onBackPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
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
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    TextView recent_search_text, no_result_text, search_word;
    EditText search_content;
    ImageView delete_icon;
    View search_icon, search_line;

    int flag = 0;
    //프리퍼런스
    SharedPreferences pref;


    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;

    //데이터
    String ME_ID;
    String SE_CON;

    //리사이클러뷰
    ArrayList<SearchDTO> mList = new ArrayList<SearchDTO>();
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    RecentSearchRecyclerViewAdapter recentSearchRecyclerViewAdapter;
    ArrayList<PostDTO> postData = new ArrayList<PostDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    SearchResultRecyclerViewAdapter searchResultRecyclerViewAdapter;
    FragmentManager fm;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_search ,container,false);
        
        //뷰연결
        recent_search_text = root.findViewById(R.id.search_recent_search_word);
        delete_icon = root.findViewById(R.id.search_fragment_delete_btn);
        search_content = root.findViewById(R.id.search_fragment_search_edit);
        recyclerView = root.findViewById(R.id.search_fragment_recent_search_recyclerview);
        no_result_text = root.findViewById(R.id.search_fragment_no_result_text);
        recyclerView2 =root.findViewById(R.id.search_fragment_recyclerview);
        search_word = root.findViewById(R.id.search_fragment_search_text);
        search_icon = root.findViewById(R.id.search_fragment_search_icon);
        search_line = root.findViewById(R.id.search_fragment_line_36);

        //리사이클러뷰

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView2.setLayoutManager(layoutManager2);

        //키보드 올리기
        search_content.requestFocus();

        fm = getActivity().getSupportFragmentManager();


        // 프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_ID = pref.getString("ME_ID", null);

        //리스너 등록
        delete_icon.setOnClickListener(this);
        search_content.setOnEditorActionListener(this);


        if (flag == 0) {
            GetRecentSearch();
        } else
            Search();


        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_fragment_delete_btn:
                search_content.setText("");
                break;
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo
                    .IME_ACTION_SEARCH:
                SE_CON = search_content.getText().toString();
                Search();
                break;
        }
        return true;
    }

    public void GetRecentSearch() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList = new ArrayList<SearchDTO>();
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("recentSearch");
                    System.out.println(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SearchDTO searchDTO = new SearchDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        searchDTO.setSE_CON(jObject.getString("SE_CON"));

                        mList.add(searchDTO);
                    }
                    recentSearchRecyclerViewAdapter = new RecentSearchRecyclerViewAdapter(mList);
                    recyclerView.setAdapter(recentSearchRecyclerViewAdapter);

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
        RecentSearchRequest RecentSearchRequest =  new RecentSearchRequest(ME_ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(RecentSearchRequest);

    }

    public void Search() {

        recent_search_text.setVisibility(View.GONE);
        no_result_text.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.VISIBLE);
        search_icon.setVisibility(View.VISIBLE);
        search_line.setVisibility(View.VISIBLE);
        search_word.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_content.getWindowToken(), 0);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                postData = new ArrayList<PostDTO>();
                userData = new ArrayList<UserDTO>();
                String resultResponse = response;
                try {
                    org.json.JSONObject result = new org.json.JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("search");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        UserDTO user = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA"));
                        post.setPO_CATE(jObject.getString("PO_CATE"));
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_ME_ID(jObject.getString("PO_ME_ID"));
                        post.setPO_PIC(jObject.getString("PO_PIC"));
                        post.setPO_CON(jObject.getString("PO_CON"));
                        user.setME_PIC(jObject.getString("ME_PIC"));
                        user.setME_NICK(jObject.getString("ME_NICK"));
                        user.setME_ID(ME_ID);
                        userData.add(user);
                        postData.add(post);
                    }
                    searchResultRecyclerViewAdapter = new SearchResultRecyclerViewAdapter(context, postData,userData, activity, (fm==null)?getParentFragmentManager():fm);
                    recyclerView2.setAdapter(searchResultRecyclerViewAdapter);

                    if (searchResultRecyclerViewAdapter.getItemCount() == 0) {
                        no_result_text.setVisibility(View.VISIBLE);
                    }
                    flag = 1;


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
                        org.json.JSONObject response = new JSONObject(result);
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


        SearchRequest SearchRequest = new SearchRequest(SE_CON, ME_ID, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(SearchRequest);


    }


    @Override
    public void onBackPressed() {
        if (flag == 1) { // 검색 결과창 일 때

            recent_search_text.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView2.setVisibility(View.GONE);
            no_result_text.setVisibility(View.GONE);
            search_icon.setVisibility(View.GONE);
            search_line.setVisibility(View.GONE);
            search_word.setVisibility(View.GONE);
            flag = 0;
            GetRecentSearch();
        } else {
            GetRecentSearch();
            //  super.onBackPressed();
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(context, "\'뒤로가기\' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
            // 현재 표시된 Toast 취소
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
                activity.finishAffinity();
            }
        }

    }
}