package com.example.clothesday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.data.PermissionSupport;
import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    
    // 뷰
    Button loginBtn, signupBtn, findBtn;
    EditText idinput, pwinput;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;


    private PermissionSupport permission; // 권한을 위한 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        if (pref.getString("ME_ID",null) != null && pref.getString("ME_PW",null) != null && pref.getString("ME_NICK",null) != null) {
            Intent suIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(suIntent);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionCheck();
        }



        signupBtn = findViewById(R.id.login_signup_btn);
        loginBtn = findViewById(R.id.login_login_btn);
        findBtn = findViewById(R.id.login_find_btn);
        idinput = findViewById(R.id.login_id_input);
        pwinput = findViewById(R.id.login_pw_input);

        signupBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        findBtn.setOnClickListener(this);
    }

    // 권한 체크
    private void permissionCheck() {
        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()){
            //권한 요청
            permission.requestPermission();
        }
    }

    @Override
    public void onBackPressed() {

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
    public void onClick(View v) {
        if (v.getId() == R.id.login_signup_btn) {
            Intent suIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(suIntent);
        }

        if (v.getId() == R.id.login_find_btn) {
            Intent suIntent = new Intent(LoginActivity.this, FindActivity.class);
            startActivity(suIntent);
        }


        if (v.getId() == R.id.login_login_btn) {
            String ME_ID = idinput.getText().toString();
            String ME_PW = pwinput.getText().toString();
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //응답을 JSON형식으로 받아옴
                        JSONObject jsonObject = new JSONObject( response );
                        // success
                        boolean success = jsonObject.getBoolean( "success" );

                        if(success) {//로그인 성공시

                            String ME_ID = jsonObject.getString( "ME_ID" );
                            String ME_PW = jsonObject.getString( "ME_PW" );
                            String ME_NICK = jsonObject.getString("ME_NICK");
                            pref = getSharedPreferences("MEMBER",MODE_PRIVATE);
                            edit = pref.edit();
                            edit.putString("ME_ID", ME_ID);
                            edit.putString("ME_NICK", ME_NICK);
                            edit.putString("ME_PW", ME_PW);
                            edit.commit();
                            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                            //프리퍼런스에 로그인 정보 저장
                            startActivity( intent );


                        } else {//로그인 실패시
                            Toast.makeText( getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                    } catch (JSONException e) {
                        Toast.makeText( getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT ).show();
                        e.printStackTrace();
                    }
                }
        };
            LoginRequest loginRequest = new LoginRequest( ME_ID, ME_PW, responseListener );
            RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
            queue.add( loginRequest );
        }

    }
}