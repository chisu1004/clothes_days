package com.example.clothesday;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.request.ProfileGetRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView month_one,month_two,month_three,month_four,month_five,month_six,month_seven,month_eight,month_nine,month_ten,month_eleven,month_twelve;
    TextView profile_name, profile_email;
    ImageView profile_pic;
    ImageButton menu_option_btn;

    //프리퍼런스
    SharedPreferences pref;
    String ME_ID;
    String ME_NICK;
    String ME_PIC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        // 뷰연결
        month_eight = findViewById(R.id.menu_summer_words3);
        month_eleven = findViewById(R.id.menu_fall_words3);
        month_five = findViewById(R.id.menu_spring_words3);
        month_four = findViewById(R.id.menu_spring_words2);
        month_nine = findViewById(R.id.menu_fall_words1);
        month_one = findViewById(R.id.menu_winter_words2);
        month_seven = findViewById(R.id.menu_summer_words2);
        month_six = findViewById(R.id.menu_summer_words1);
        month_ten = findViewById(R.id.menu_fall_words2);
        month_three = findViewById(R.id.menu_spring_words1);
        month_twelve = findViewById(R.id.menu_winter_words1);
        month_two = findViewById(R.id.menu_winter_words3);
        menu_option_btn = findViewById(R.id.menu_option_set);
        profile_email = findViewById(R.id.menu_email);
        profile_name = findViewById(R.id.menu_profile_name);
        profile_pic = findViewById(R.id.menu_profile_picture);

        // 프리퍼런스
        pref = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_NICK = pref.getString("ME_NICK", "사용자 닉네임");
        ME_ID = pref.getString("ME_ID", null);
        profile_name.setText(ME_NICK);
        profile_email.setText(ME_ID);

        // 프로필 이미지
        GetProfile();

        //리스너 등록
        month_two.setOnClickListener(this);
        month_eight.setOnClickListener(this);
        month_eleven.setOnClickListener(this);
        month_five.setOnClickListener(this);
        month_four.setOnClickListener(this);
        month_nine.setOnClickListener(this);
        month_one.setOnClickListener(this);
        month_seven.setOnClickListener(this);
        month_six.setOnClickListener(this);
        month_ten.setOnClickListener(this);
        month_three.setOnClickListener(this);
        month_twelve.setOnClickListener(this);
        month_two.setOnClickListener(this);
        menu_option_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_option_set:
                Intent suIntent = new Intent(MenuActivity.this, SetActivity.class);
                startActivity(suIntent);
                break;
            case R.id.menu_spring_words1:
                Intent suIntent3 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent3.putExtra("month","3");
                startActivity(suIntent3);
                break;
            case R.id.menu_spring_words2:
                Intent suIntent4 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent4.putExtra("month","4");
                startActivity(suIntent4);
                break;
            case R.id.menu_spring_words3:
                Intent suIntent5 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent5.putExtra("month","5");
                startActivity(suIntent5);
                break;
            case R.id.menu_summer_words1:
                Intent suIntent6 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent6.putExtra("month","6");
                startActivity(suIntent6);
                break;
            case R.id.menu_summer_words2:
                Intent suIntent7 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent7.putExtra("month","7");
                startActivity(suIntent7);
                break;
            case R.id.menu_summer_words3:
                Intent suIntent8 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent8.putExtra("month","8");
                startActivity(suIntent8);
                break;
            case R.id.menu_fall_words1:
                Intent suIntent9 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent9.putExtra("month","9");
                startActivity(suIntent9);
                break;
            case R.id.menu_fall_words2:
                Intent suIntent10 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent10.putExtra("month","10");
                startActivity(suIntent10);
                break;
            case R.id.menu_fall_words3:
                Intent suIntent11 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent11.putExtra("month","11");
                startActivity(suIntent11);
                break;
            case R.id.menu_winter_words1:
                Intent suIntent12 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent12.putExtra("month","12");
                startActivity(suIntent12);
                break;
            case R.id.menu_winter_words2:
                Intent suIntent1 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent1.putExtra("month","1");
                startActivity(suIntent1);
                break;
            case R.id.menu_winter_words3:
                Intent suIntent2 = new Intent(MenuActivity.this, MainActivity.class);
                suIntent2.putExtra("month","2");
                startActivity(suIntent2);
                break;
        }
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(ProfileGetRequest);

    }


    private void SetProfilePicture() {
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + ME_PIC);
        Glide.with(getApplicationContext()).load(profile).apply(new RequestOptions().circleCrop()).into(profile_pic);
    }

}