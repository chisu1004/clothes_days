package com.example.clothesday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.request.PasswordUpdateRequest;


import org.json.JSONException;
import org.json.JSONObject;



public class Set_PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText now_pass, new_pass, new_pass2;
    Button update_pass_btn;

    String NEW_ME_PW, NOW_ME_PW, NEW_ME_PW2;
    String ME_PW;
    String ME_ID;

    //프리퍼런스
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        //뷰 연결
        now_pass = findViewById(R.id.set_password_now_password_input);
        new_pass = findViewById(R.id.set_password_pw_input);
        new_pass2 = findViewById(R.id.set_password_pw_input2);
        update_pass_btn = findViewById(R.id.set_password_btn);

        // 프리퍼런스
        pref = getSharedPreferences("MEMBER", MODE_PRIVATE);
        ME_PW = pref.getString("ME_PW", "a");
        ME_ID = pref.getString("ME_ID", "a");


        update_pass_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NEW_ME_PW = new_pass.getText().toString().trim();
        NEW_ME_PW2 = new_pass2.getText().toString().trim();
        NOW_ME_PW = now_pass.getText().toString().trim();
        if (NEW_ME_PW.equals("") || NOW_ME_PW.equals("") || NEW_ME_PW2.equals("")) {
            Toast.makeText(getApplicationContext(), "모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println(ME_PW);
        System.out.println(NOW_ME_PW);
        switch (v.getId()) {
            case R.id.set_password_btn:
                if (NOW_ME_PW.equals(ME_PW)) {
                    if (NEW_ME_PW.equals(NEW_ME_PW2)) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject result = null;
                                try {
                                    result = new JSONObject(response);
                                    boolean success = result.getBoolean("success");

                                    if (success) {
                                        pref.edit();
                                        pref = getSharedPreferences("MEMBER",MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pref.edit();
                                        edit.putString("ME_PW", NEW_ME_PW);
                                        edit.commit();
                                        Toast.makeText(getApplicationContext(), "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Set_PasswordActivity.this,  Set_SecurityActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                            PasswordUpdateRequest PasswordUpdateRequest = new PasswordUpdateRequest(ME_ID, NEW_ME_PW, responseListener, errorListener );
                            RequestQueue queue = Volley.newRequestQueue(this);
                            queue.add( PasswordUpdateRequest );

                    } else {
                        Toast.makeText(getApplicationContext(), "새 비밀번호가 동일하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "현재 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }


                break;
        }

    }
}