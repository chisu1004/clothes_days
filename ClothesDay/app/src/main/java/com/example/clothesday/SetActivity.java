package com.example.clothesday;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetActivity extends AppCompatActivity implements View.OnClickListener {

    //버튼
    Button btn_notice, btn_info, btn_protect, btn_logout;

    //프리퍼런스
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        btn_info = (Button) findViewById(R.id.option_myinfo);
        btn_info.setOnClickListener(this);


        btn_notice = (Button) findViewById(R.id.option_notice);
        btn_notice.setOnClickListener(this);


        btn_protect = (Button) findViewById(R.id.option_security);
        btn_protect.setOnClickListener(this);

        btn_logout = (Button) findViewById(R.id.set_logout_btn);
        btn_logout.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.option_myinfo) {
            Intent intent = new Intent(SetActivity.this, Set_MyInfoActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.option_notice) {
            Intent intent = new Intent(SetActivity.this, Set_NoticeActivity.class);
            startActivity(intent);
        }



        if (v.getId() == R.id.option_security) {
            Intent intent = new Intent(SetActivity.this, Set_SecurityActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.set_logout_btn) {
            pref = getSharedPreferences("MEMBER", MODE_PRIVATE);
            editor = pref.edit();
            editor.clear();
            editor.commit();
            Intent suIntent = new Intent(SetActivity.this, LoginActivity.class);
            startActivity(suIntent);
        }


    }
}