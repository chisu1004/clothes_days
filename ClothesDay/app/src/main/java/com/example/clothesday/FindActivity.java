package com.example.clothesday;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.data.GMailSender;
import com.example.clothesday.request.FindPwRequest;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindActivity extends AppCompatActivity implements View.OnClickListener {

    Button findPw;
    EditText find_pw_input_id; // 비번 찾기

    private AlertDialog dialog;
    GMailSender gMailSender;
    String verifyCode = "hihi";
    LottieAnimationView animationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        findPw = findViewById(R.id.find_pw_btn);
        find_pw_input_id = findViewById(R.id.find_pw_input_id);


        findPw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.find_pw_btn:
                String UserId = find_pw_input_id.getText().toString();
                gMailSender = new GMailSender(animationView, getApplicationContext());
                if (UserId.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                    dialog = builder.setMessage("이메일을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(UserId).matches()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                    dialog = builder.setMessage("잘못된 이메일 형식입니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                } else {
                    try {
                        verifyCode = gMailSender.getEmailCode();
                            //GMailSender.sendMail(제목, 본문내용, 받는사람);
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject result = new JSONObject(response);
                                        boolean success = result.getBoolean("success");
                                        if (success) {
                                            gMailSender.sendMail("[옷날] 임시 비밀번호입니다.", verifyCode, UserId);
                                            Toast.makeText(getApplicationContext(), "임시 비밀번호를 전송하였습니다. 메일함을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }  catch (SendFailedException e) {
                                        Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                    } catch (MessagingException e) {
                                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (Exception e) {
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
                            FindPwRequest FindPwRequest = new FindPwRequest( UserId, verifyCode, responseListener, errorListener);
                            RequestQueue queue = Volley.newRequestQueue( this );
                            queue.add( FindPwRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }

        }
    }
}