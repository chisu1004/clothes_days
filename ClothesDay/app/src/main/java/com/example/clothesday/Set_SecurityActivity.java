package com.example.clothesday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Set_SecurityActivity extends AppCompatActivity implements View.OnClickListener {

    TextView set_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_security);
        set_pass = findViewById(R.id.security_pwd);

        set_pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.security_pwd:
                Intent intent = new Intent(Set_SecurityActivity.this, Set_PasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}