package com.example.clothesday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.fragment.FollowFragment;
import com.example.clothesday.fragment.HomeFragment;
import com.example.clothesday.fragment.MonthBoardFragment;
import com.example.clothesday.fragment.MypageFragment;
import com.example.clothesday.fragment.NoticeBoardFragment;
import com.example.clothesday.fragment.OtherUserPageFragment;
import com.example.clothesday.fragment.PostFragment;
import com.example.clothesday.fragment.PostManagementFragment;
import com.example.clothesday.fragment.SearchFragment;
import com.example.clothesday.request.TokenSetRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;




public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationBarView.OnItemSelectedListener {


    BottomNavigationView bottomView;

    //프래그먼트
    HomeFragment home_fragment;
    SearchFragment search_fragment;
    PostManagementFragment post_management_fragment;
    NoticeBoardFragment noticeBoard_fragment;

    //fcm
    String token;

    //프리퍼런스
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    String ME_ID;
    Activity activity = MainActivity.this;


    MypageFragment mypage_fragment;
    MonthBoardFragment monthBoardFragment;

    //인텐트
    Intent intent;
    String month = "";

    //메뉴바
    Toolbar toolbar;



    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트
        mypage_fragment = new MypageFragment();
        home_fragment = new HomeFragment();
        post_management_fragment = new PostManagementFragment();
        search_fragment = new SearchFragment();
        noticeBoard_fragment = new NoticeBoardFragment();


        //툴바 설정
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);

        //하단 메뉴
        bottomView = findViewById(R.id.main_bottomnavi);
        bottomView.setItemIconTintList(null);
        bottomView.setOnItemSelectedListener(this);
        bottomView.setBottom(4);

        //프리퍼런스
        pref = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_ID = pref.getString("ME_ID",null);
        token = pref.getString("ME_TOKEN", null);


        if (token == null) {
            // FCM 토큰 등록
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {

                                return;
                            }

                            // Get new FCM registration token
                            token = task.getResult();
                            System.out.println(token);
                            SetToken();
                        }
                    });
        }

        //인텐트
        intent = getIntent();
        month = intent.getStringExtra("month");
        if (month != null ) {
            goToMonthBoard(month);
        } else {
            //시작 프래그먼트
            getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();
        }
    }

    private void SetToken() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject result = new JSONObject(response);
                    boolean res = result.getBoolean("success");
                    if (res)
                        pref = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
                        edit = pref.edit();
                        edit.putString("ME_TOKEN", token);
                        edit.commit();

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


        TokenSetRequest TokenSetRequest = new TokenSetRequest(ME_ID, token, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(TokenSetRequest);


    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        //프래그먼트 onBackPressedListener사용
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        for(Fragment fragment : fragmentList){
//            if(fragment instanceof onBackPressedListener){
//                ((onBackPressedListener)fragment).onBackPressed();
//                return;
//            }
//        }
        Fragment test = getSupportFragmentManager().findFragmentById(R.id.container);
        PostFragment postFragment = new PostFragment();
        OtherUserPageFragment otherUserPageFragment = new OtherUserPageFragment();
        SearchFragment searchFragment = new SearchFragment();
        PostManagementFragment postManagementFragment = new PostManagementFragment();
        if (test != null && fragmentList.size() > 0 && (test.getClass().isInstance(postFragment)) || test.getClass().isInstance(otherUserPageFragment) ||test.getClass().isInstance(searchFragment) || test.getClass().isInstance(postManagementFragment) || test.getClass().isInstance(new FollowFragment()) ) {
            ((onBackPressedListener)test).onBackPressed();
            return;
        }


      //  super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로가기\' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
            finishAffinity();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent suIntent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(suIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mypage_fragment = new MypageFragment();
        Bundle result = new Bundle();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.mypage_btn:
                result.putString("bundleKey", "mypage");
                mypage_fragment.setArguments(result);
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(mypage_fragment).attach(mypage_fragment).replace(R.id.container, mypage_fragment).commit();
                return true;
            case R.id.home_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();
                return true;
            case R.id.search_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, search_fragment).commit();
                return true;
            case R.id.noticeboard_btn:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, noticeBoard_fragment).commit();
                return true;
            case R.id.scrap_btn:
                result.putString("bundleKey", "scrap");
                mypage_fragment.setArguments(result);
                if (Build.VERSION.SDK_INT >= 26) {
                   ft.setReorderingAllowed(false);
                }
                ft.detach(mypage_fragment).attach(mypage_fragment).replace(R.id.container, mypage_fragment).commit();


                return true;
        }
        return false;
    }

    public void goToMonthBoard(String month) {
        monthBoardFragment = new MonthBoardFragment();
        Bundle result = new Bundle();
        switch (month) {
            case "1":
                result.putString("month", "1");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "2":
                result.putString("month", "2");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "3":
                result.putString("month", "3");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "4":
                result.putString("month", "4");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "5":
                result.putString("month", "5");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "6":
                result.putString("month", "6");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "7":
                result.putString("month", "7");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "8":
                result.putString("month", "8");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "9":
                result.putString("month", "9");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "10":
                result.putString("month", "10");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "11":
                result.putString("month", "11");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            case "12":
                result.putString("month", "12");
                monthBoardFragment.setArguments(result);
                getSupportFragmentManager().beginTransaction().detach(monthBoardFragment).attach(monthBoardFragment).replace(R.id.container, monthBoardFragment).addToBackStack(null).commit();
                return;
            default:
                return;
        }

    }





}



