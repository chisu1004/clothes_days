package com.example.clothesday;

import static com.example.clothesday.data.AppHelper.getFileDataFromBitmap;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clothesday.request.PasswordUpdateRequest;
import com.example.clothesday.request.ProfileGetRequest;
import com.example.clothesday.request.ProfileSetRequest;
import com.example.clothesday.request.WithdrawalRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class Set_MyInfoActivity extends AppCompatActivity implements View.OnClickListener {


    //프리퍼런스
    SharedPreferences pref;
    String ME_NICK;
    String ME_ID;

    //초기
    String ME_PIC;

    // 뷰
    ImageView profile_picture;
    TextView profile_name, email, withdrawal;
    Button save_btn;

    // 프로필 사진 정보
    Bitmap bitmap;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_my_info);
        //뷰 연결
        profile_picture = findViewById(R.id.set_my_info_profile_picture);
        profile_name = findViewById(R.id.set_my_info_profile_name);
        email = findViewById(R.id.set_my_info_email);
        save_btn = findViewById(R.id.set_my_info_save_button);
        withdrawal = findViewById(R.id.set_my_info_member_withdrawal);

        // 프리퍼런스
        pref = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        ME_NICK = pref.getString("ME_NICK", "사용자 닉네임");
        ME_ID = pref.getString("ME_ID", null);

        //초기 정보
        email.setText(ME_ID);
        profile_name.setText(ME_NICK);

        //리스너등록
        save_btn.setOnClickListener(this);
        profile_picture.setOnClickListener(this);
        withdrawal.setOnClickListener(this);

        GetProfile();

    }

    private void SetProfilePicture() {

        if (!ME_PIC.equals("null"))     {
            Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + ME_PIC);
            profile_picture.setBackgroundResource(R.drawable.layout);
            profile_picture.setBackground(getDrawable(R.drawable.layout));
            Glide.with(getApplicationContext()).load(profile).apply(new RequestOptions().circleCrop()).into(profile_picture);
        }
    }

    // 갤러리 열기
    public void openGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityResult.launch(intent);
        } else {
            Toast.makeText(getApplicationContext(), "파일 및 미디어 접근 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }



    private void SetUserProfile() {
        Response.Listener<NetworkResponse> responseListener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    boolean success = result.getBoolean("success");

                    if (success) {
                        // tell everybody you have succed upload image and post strings
                        Toast.makeText(getApplicationContext(), "프로필 사진을 변경하였습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "프로필 사진 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("Unexpected", "error");
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
        ProfileSetRequest ProfileSetRequest = null;
        try {
            ProfileSetRequest = new ProfileSetRequest(ME_ID, responseListener, errorListener) {
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, ProfileSetRequest.DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
                    String date = sdf.format(System.currentTimeMillis());

                     params.put("ME_PIC", new ProfileSetRequest.DataPart(ME_ID + "_" + "profile" + ".jpg", getFileDataFromBitmap(getBaseContext(), bitmap), "image/jpeg"));

                    return params;
                } @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ME_ID", ME_ID);
                    params.put("Content-Type", getBodyContentType());
                    return params;
                }

            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(ProfileSetRequest);
        
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_my_info_save_button:
                if (uri != null)
                    SetUserProfile();
                break;
            case R.id. set_my_info_profile_picture:
                openGallery();
                break;
            case R.id.set_my_info_member_withdrawal:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("회원 탈퇴").setMessage("정말 탈퇴하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Withdrawal();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }
    private void Withdrawal() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject result = null;
                try {
                    result = new JSONObject(response);
                    boolean success = result.getBoolean("success");

                    if (success) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        Intent suIntent = new Intent(Set_MyInfoActivity.this, LoginActivity.class);
                        startActivity(suIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "회원탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
        WithdrawalRequest WithdrawalRequest = new WithdrawalRequest(ME_ID,responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add( WithdrawalRequest );

    }

    // 이미지 선택
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() == null)    // 어떤 이미지도 선택하지 않은 경우
                        Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();

                    else{
                        Intent intent = result.getData();

                         uri = intent.getData();
                         try {
                                    //SDK 버전에 따라 다른 메소드 사용
                          if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.P) { //API 29 이상
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                         } else { // 구버전
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                         }
                             Glide.with(getApplicationContext()).load(uri).apply(new RequestOptions().circleCrop()).into(profile_picture);

                         } catch (Exception e) {
                               e.printStackTrace();
                         }


                }
            }
    });

}
