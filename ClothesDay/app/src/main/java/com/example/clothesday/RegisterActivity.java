package com.example.clothesday;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.data.GMailSender;
import com.example.clothesday.request.NickNameCheckRequest;
import com.example.clothesday.request.RegisterRequest;
import com.example.clothesday.request.ValidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText join_id,join_password, join_nickname, join_pwck, join_verify;
    private Button join_button, check_button, verify_button, nick_check_btn;

    private boolean validate = false;
    private boolean nick_check = false;
    TextView txt, verify_txt, nick_check_text;

    private AlertDialog dialog;
    GMailSender gMailSender;
    String verifyCode = "hihi";
    LottieAnimationView animationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register);

        //인터넷 사용 권한 허용
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitDiskReads().permitDiskWrites() .permitNetwork().build());

        //뷰연결
        join_id = findViewById(R.id.register_id_input);
        join_password = findViewById(R.id.register_pw_input );
        join_nickname = findViewById(R.id.register_nickname_input);
        join_pwck = findViewById(R.id.register_pw_input2);
        join_verify = findViewById(R.id.register_verify_input);
        txt = findViewById(R.id.register_id_check_text);
        verify_button = findViewById(R.id.register_id_verify_btn);
        verify_txt = findViewById(R.id.register_verify_complete_text);
        animationView = findViewById(R.id.register_lottie_animation);
        join_button = findViewById( R.id.register_btn );
        check_button = findViewById(R.id.register_id_check_btn);
        nick_check_text = findViewById(R.id.register_nick_check_text);
        nick_check_btn = findViewById(R.id.register_nick_check_btn);

        //애니메이션

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //리스너 등록
        verify_button.setOnClickListener(this);
        check_button.setOnClickListener(this);
        join_button.setOnClickListener(this);
        nick_check_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String UserPwd = join_password.getText().toString().trim();
        final String UserNickname = join_nickname.getText().toString().trim();
        final String PassCk = join_pwck.getText().toString().trim();
        final String UserId = join_id.getText().toString().trim();

        switch (v.getId()) {
            case R.id.register_nick_check_btn:
                Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {
                            result = new JSONObject(response);
                            boolean success = result.getBoolean("success");

                            if (success) {
                                nick_check_text.setText("사용할 수 있는 닉네임입니다.");
                                nick_check = true;
                                nick_check_text.setVisibility(View.VISIBLE);
                            } else {
                                nick_check_text.setText("이미 존재하는 닉네임입니다.");
                                nick_check = false;
                                nick_check_text.setVisibility(View.VISIBLE);
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
                NickNameCheckRequest NickNameCheckRequest = new NickNameCheckRequest(UserNickname , responseListener2, errorListener );
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add( NickNameCheckRequest );
                break;
            case R.id.register_id_check_btn:
                gMailSender = new GMailSender(animationView, this);
                if (UserId.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(UserId).matches()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("잘못된 이메일 형식입니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                } else {


                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String res = response.trim();
                                if (res.contains("true")) {
                                    verifyCode = gMailSender.getEmailCode();

                                    try {
                                        //GMailSender.sendMail(제목, 본문내용, 받는사람);

                                        gMailSender.sendMail("[옷날] 회원가입 인증코드입니다.", verifyCode, UserId);

                                    } catch (SendFailedException e) {
                                        Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                    } catch (MessagingException e) {
                                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    txt.setText("사용 가능한 이메일 입니다. 잠시 후 메일함을 확인해 주세요.");
                                    txt.setVisibility(View.VISIBLE);
                                } else if (res.contains("false")) {
                                    txt.setText("이미 존재하는 이메일입니다.");
                                    validate = false;
                                    txt.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    };

                    ValidateRequest validateRequest = new ValidateRequest(UserId,validate , responseListener);
                    validateRequest.setShouldCache(false);
                    RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                    queue2.add(validateRequest);

                }
                break;

            case R.id.register_id_verify_btn:

                String verify = join_verify.getText().toString();
                if(v.getId() == R.id.register_id_verify_btn) {
                    if(verify.equals(verifyCode)) {
                        validate = true;
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String res = response.trim();
                                if (res.contains("false")) {
                                    verify_txt.setText("이메일 인증에 실패하였습니다.");
                                    verify_txt.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                        };
                        ValidateRequest validateRequest2 = new ValidateRequest(UserId, validate , responseListener);
                        validateRequest2.setShouldCache(false);
                        RequestQueue queue4 = Volley.newRequestQueue(getApplicationContext());
                        queue4.add(validateRequest2);
                        check_button.setEnabled(false);
                        verify_button.setEnabled(false);
                        join_verify.setEnabled(false);
                        join_id.setEnabled(false);
                        verify_txt.setText("이메일 인증이 완료되었습니다.");
                        verify_txt.setVisibility(View.VISIBLE);
                    } else {
                        verify_txt.setText("유효하지 않은 인증번호입니다.");
                        verify_txt.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.register_btn:
                //이메일 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일 인증을 진행해 주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 닉네임 중복체크 했는지 확인
                if (!nick_check) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("닉네임 중복확인을 진행해 주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (UserId.equals("") || UserPwd.equals("") || UserNickname.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String success = response;

                            //회원가입 성공시
                            if(UserPwd.equals(PassCk)) {
                                if (success.contains("true")) {

                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserNickname), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                    //회원가입 실패시
                                } else if (success.contains("false")) {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                RegisterRequest registerRequest = new RegisterRequest( UserNickname.trim(), UserId.trim(), UserPwd.trim(), responseListener);
                RequestQueue queue3 = Volley.newRequestQueue( RegisterActivity.this );
                queue3.add( registerRequest );
                break;
        }
    }
}