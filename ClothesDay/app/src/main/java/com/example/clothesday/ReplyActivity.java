package com.example.clothesday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.Adapter.ReplyRecyclerViewAdapter;
import com.example.clothesday.DAO.ReplyDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.request.ReplyAddRequest;
import com.example.clothesday.request.ReplyGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity implements View.OnClickListener {

    //뷰
    ImageView add_reply;
    EditText edit_reply;

    // 프리퍼런스
    SharedPreferences pref;

    //인텐트
    Intent intent;

    // 정보
    String ME_ID, RE_CON;
    int PO_ID;

    //리사이클러뷰
    RecyclerView recyclerView;
    ArrayList<ReplyDTO> reply = new ArrayList<ReplyDTO>();
    ArrayList<UserDTO> user = new ArrayList<UserDTO>();
    ReplyRecyclerViewAdapter replyRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        //뷰 연결
        add_reply = findViewById(R.id.reply_activity_add_reply_btn);
        edit_reply = findViewById(R.id.reply_activity_reply_edit);
        recyclerView = findViewById(R.id.reply_activity_recyclerview);

        //프리퍼런스
        pref = getSharedPreferences("MEMBER",MODE_PRIVATE);
        ME_ID = pref.getString("ME_ID", null);
        
        //리사이클러뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        //인텐트
        intent = getIntent();
        PO_ID = intent.getIntExtra("PO_ID", MODE_PRIVATE);

        //리스너등록
        add_reply.setOnClickListener(this);

        GetReply();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reply_activity_add_reply_btn:
                if (edit_reply.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddReply();
                edit_reply.setText("");
                break;
        }

    }

    public void GetReply() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                reply = new ArrayList<ReplyDTO>();
                user = new ArrayList<UserDTO>();
                String resultResponse = response;
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("reply");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ReplyDTO replyDTO = new ReplyDTO();
                        UserDTO userDTO = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        replyDTO.setRE_ID(jObject.getInt("RE_ID"));
                        replyDTO.setRE_ME_ID(jObject.getString("RE_ME_ID"));
                        replyDTO.setRE_REG_DA(jObject.getString("RE_REG_DA"));
                        replyDTO.setRE_CON(jObject.getString("RE_CON"));
                        userDTO.setME_NICK(jObject.getString("ME_NICK"));
                        userDTO.setME_PIC(jObject.getString("ME_PIC"));
                        reply.add(replyDTO);
                        user.add(userDTO);
                    }
                    replyRecyclerViewAdapter = new ReplyRecyclerViewAdapter(getApplicationContext(), reply,user, ReplyActivity.this);
                    recyclerView.setAdapter( replyRecyclerViewAdapter);


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

            ReplyGetRequest ReplyGetRequest = new ReplyGetRequest(String.valueOf(PO_ID), responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(ReplyActivity.this);
            queue.add(ReplyGetRequest);

    }


    private void AddReply() {
        RE_CON = edit_reply.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultResponse = response;
                try {                    JSONObject result = new JSONObject(resultResponse);
                    int res = result.getInt("result");
                    if (res == 1){
                        Toast.makeText(getApplicationContext(), "댓글을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                    } else  {
                        Toast.makeText(getApplicationContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                    GetReply();
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
        ReplyAddRequest ReplyAddRequest  = new ReplyAddRequest( ME_ID,RE_CON ,String.valueOf(PO_ID), responseListener,errorListener  );
        RequestQueue queue = Volley.newRequestQueue( ReplyActivity.this );
        queue.add( ReplyAddRequest  );

    }

}