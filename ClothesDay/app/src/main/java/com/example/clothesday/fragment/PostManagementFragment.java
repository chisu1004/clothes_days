package com.example.clothesday.fragment;


import static com.example.clothesday.data.AppHelper.getFileDataFromBitmap;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.Adapter.ImageSliderAdapter;
import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.request.PostAddRequest;
import com.example.clothesday.request.PostUpdateRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import  com.example.clothesday.data.Indicator;
import com.example.clothesday.request.PostGetRequest;
import com.example.clothesday.request.ProfileGetRequest;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostManagementFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, onBackPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostManagementFragment() {
        // Required empty public constructor
    }


    Activity activity;
    Context context;


    @Override
    public void onAttach(Context context) {
        this.context = context;

        if (context instanceof Activity) {
            activity = (Activity)context;
        }
        super.onAttach(context);
    }
    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostManagementFragment newInstance(String param1, String param2) {
        PostManagementFragment fragment = new PostManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    //뷰
    Spinner season_spinner, month_spinner;
    EditText post_content; // 게시글 내용
    TextView profile_name, add_update_btn;
    Button add_img_btn, add_post_btn, delete_btn;
    ImageView profile_picture;

    //데이터
    int image_count = 0;
    int indicator_count = 0;
    int selected = 0;


    String[] spring = {"3월", "4월", "5월"};
    String[] summer = {"6월", "7월", "8월"};
    String[] fall = {"9월", "10월", "11월"};
    String[] winter = {"12월", "1월", "2월"};

    FragmentManager fragmentManager;

    ArrayList<Uri> uriList = new ArrayList<>();

    //서버로 이미지 전송
    Bitmap[] bitmap = new Bitmap[5];

    //뷰페이저
    ImageSliderAdapter imageslideradapter;
    ViewPager2 viewPager2;
    LinearLayout layoutIndicator;
    Indicator indicator;

    //월별 스피너 어댑터
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    ArrayAdapter<String> adapter3;
    ArrayAdapter<String> adapter4;

    // 카테고리(스피너)
    String season = "봄", month = "3월";
    String PO_CON, PO_TAG = "", PO_CATE;
    String ME_NICK;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    final static private String URL = "http://dlsxjsptb.cafe24.com/post/postAdd.jsp";

    //정보
    String ME_PIC;
    String PO_ME_ID;
    int PO_ID;
    String type;

    //DAO
    PostDTO postdao;
    UserDTO userdao;

    //프래그먼트
    MypageFragment mypageFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_post_management, container, false);
        // 레이아웃 등록
        season_spinner = root.findViewById(R.id.post_management_season_spinner);
        month_spinner = root.findViewById(R.id.post_management_month_spinner);
        add_img_btn = root.findViewById(R.id.post_management_add_img_btn);
        post_content = root.findViewById(R.id.post_management_content);
        add_post_btn = root.findViewById(R.id.post_management_add_post_btn);
        viewPager2 = root.findViewById(R.id.post_management_image_viewer);
        layoutIndicator = root.findViewById(R.id.post_management_indicator);
        profile_name = root.findViewById(R.id.post_management_profile_name);
        delete_btn = root.findViewById(R.id.post_management_delete_button);
        profile_picture = root.findViewById(R.id.post_management_profile_picture);
        add_update_btn = root.findViewById(R.id.post_management_add_button_word);

        //뷰페이저
        viewPager2.setOffscreenPageLimit(1);


        //프래그먼트매니저
        fragmentManager = getActivity().getSupportFragmentManager();

        // 스피너 어댑터
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spring);
        adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, summer);
        adapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, fall);
        adapter4 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, winter);

        //프리퍼런스
        pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        editor = pref.edit();
        PO_ME_ID = pref.getString("ME_ID", "MEMBER");
        ME_NICK = pref.getString("ME_NICK", "사용자 닉네임");

        //프로필
        profile_name.setText(ME_NICK);

        // 리스너 등록
        season_spinner.setOnItemSelectedListener(this);
        month_spinner.setOnItemSelectedListener(this);
        add_post_btn.setOnClickListener(this);
        add_img_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);

        //초기상태
        GetProfile();

        Bundle bundle = getArguments();
        if (bundle != null) {
            PO_ID = bundle.getInt("PO_ID");
            type = bundle.getString("type");
        }
        if (type != null) {
            add_update_btn.setText("수정");
            GetPost();
        }
        // 프래그먼트
        mypageFragment = new MypageFragment();

        return root;
    }

    // 스피너 선택
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.post_management_season_spinner) {
            season = parent.getSelectedItem().toString();
            switch (season) {
                case "봄":
                    month_spinner.setAdapter(adapter);
                    break;
                case "여름":
                    month_spinner.setAdapter(adapter2);
                    break;
                case "가을":
                    month_spinner.setAdapter(adapter3);
                    break;
                case "겨울":
                    month_spinner.setAdapter(adapter4);
                    break;
            }
        }
        if (parent.getId() == R.id.post_management_month_spinner) {
            month = parent.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void DeleteImage() {
        if (imageslideradapter.getItemCount() != 0) {
            for (int i = selected; i < bitmap.length - 1; i++) {

                bitmap[i] = bitmap[i + 1];
            }
            imageslideradapter.DeleteImage(selected);
            indicator_count -= 1;
            indicator.deleteOneIndicator( layoutIndicator , selected, context);
            if (selected == imageslideradapter.getItemCount()) {
                indicator.setCurrentIndicator(selected-1,layoutIndicator ,context);
            } else
                indicator.setCurrentIndicator(selected,layoutIndicator ,context);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.post_management_delete_button:
                DeleteImage();
                break;
            case R.id.post_management_add_img_btn:
                openGallery();
                break;
            case R.id.post_management_add_post_btn:
                if (type != null) {
                    UpdatePost();
                } else {
                    AddPost();
                }

                break;
        }
    }


    // 갤러리 열기
    public void openGallery() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityResult.launch(intent);
        } else {
            Toast.makeText(context, "파일 및 미디어 접근 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }



    // 이미지 선택
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getData() == null)    // 어떤 이미지도 선택하지 않은 경우
                        Toast.makeText(context, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();

                    else{      // 이미지를 여러장 선택한 경우
                        ClipData clipData = result.getData().getClipData();

                        if(clipData.getItemCount() > 5){   // 선택한 이미지가 6장 이상인 경우
                            Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                        }
                        else{   // 선택한 이미지가 1장 이상 5장 이하인 경우

                            if ( indicator_count + clipData.getItemCount() > 5) {
                                Toast.makeText(context, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            for (int i = 0; i < clipData.getItemCount(); i++){
                                Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                try {
                                    // 스피너 이미지 업로드
                                    uriList.add(imageUri);  //uri를 list에 담는다.
                                    imageslideradapter = new ImageSliderAdapter(context, uriList, activity);
//                                    SDK 버전에 따라 다른 메소드 사용
                                    if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.P) { //API 29 이상
                                        bitmap[i + indicator_count] = ImageDecoder.decodeBitmap(ImageDecoder.createSource(activity.getContentResolver(), imageUri));
                                    } else { // 구버전
                                        bitmap[i + indicator_count] = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                                    }
                                } catch (Exception e) {
                                   e.printStackTrace();
                                }
                            }
                            indicator = new Indicator();
                            viewPager2.setAdapter(imageslideradapter);
                            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    indicator.setCurrentIndicator(position, layoutIndicator, context);
                                    selected = position;
                                }
                            });
                            indicator_count += clipData.getItemCount();
                            indicator.setupIndicators(indicator_count, layoutIndicator, context);

                        }
                    }
                }
            });

    public void GetPost() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                postdao = new PostDTO();
                userdao = new UserDTO();
                String resultResponse = response;
                try {
                    indicator = new Indicator();
                    JSONObject result = new JSONObject(resultResponse);
                    JSONArray jsonArray = result.getJSONArray("post");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        postdao.setPO_ID(jObject.getInt("PO_ID"));
                        postdao.setPO_CATE(jObject.getString("PO_CATE"));
                        postdao.setPO_TAG(jObject.getString("PO_TAG"));
                        postdao.setPO_PIC(jObject.getString("PO_PIC"));
                        postdao.setPO_CON(jObject.getString("PO_CON"));
                    }

                    //PO_CON
                    post_content.setText(postdao.getPO_CON());

                    //PO_CATE
                    String cate = postdao.getPO_CATE();
                    setCategory(cate);

                    //PO_PIC, 뷰페이저
                    StringTokenizer stk = new StringTokenizer(postdao.getPO_PIC(), ",");
                    image_count = stk.countTokens();

                    new Thread(() -> {
                    try {
                        if (postdao.getPO_PIC() != null) {
                            int i = 0;

                            while(stk.hasMoreTokens()){
                                Uri imageUri =  Uri.parse("http://dlsxjsptb.cafe24.com/post/image/" + stk.nextToken());
                                uriList.add(imageUri);
                                URL url = new URL("http://dlsxjsptb.cafe24.com/" + imageUri.getPath());
                                bitmap[i] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                i++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                    indicator_count = image_count;

                    imageslideradapter = new ImageSliderAdapter(context, uriList, activity);

                    viewPager2.setAdapter(imageslideradapter);
                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            indicator.setCurrentIndicator(position, layoutIndicator, context);
                            selected = position;
                        }
                    });
                    indicator.setupIndicators(indicator_count, layoutIndicator, context);


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
        PostGetRequest PostGetRequest = null;
        try {
            PostGetRequest = new PostGetRequest(String.valueOf(PO_ID), PO_ME_ID, responseListener, errorListener );
            RequestQueue queue = Volley.newRequestQueue(activity);
            queue.add(PostGetRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void  AddPost() {
        PO_CATE = month;
        PO_CON = post_content.getText().toString();
        if (PO_CON.equals("")) {
            Toast.makeText(context, "게시글 내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (uriList.size() == 0) {
            Toast.makeText(context, "사진을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (PO_CON.contains("ㅅㅂ")) {
            Toast.makeText(context, "비속어는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Response.Listener<NetworkResponse> responseListener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println(URL);
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    boolean success = result.getBoolean("success");

                    if (success) {
                        // tell everybody you have succed upload image and post strings
                        Toast.makeText(context, "게시글을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().replace(R.id.container, mypageFragment).commit();
                    } else {
                        Toast.makeText(context, "게시글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
        PostAddRequest postAddRequest = null;
        try {
            postAddRequest = new PostAddRequest(PO_CON, PO_ME_ID, PO_TAG, PO_CATE, String.valueOf(indicator_count), responseListener, errorListener) {
                @Override
                protected Map<String, PostAddRequest.DataPart> getByteData() {
                    Map<String, PostAddRequest.DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH_mm_ss");
                    String date = sdf.format(System.currentTimeMillis());

                    for (int i = 0; i < indicator_count; i++) {
                        params.put("PO_PIC" + String.valueOf(i), new PostAddRequest.DataPart(PO_ME_ID + "_" + date + "_" +  String.valueOf(i)  + ".jpg", getFileDataFromBitmap(activity.getBaseContext(), bitmap[i]), "image/jpeg"));
                    }
                    return params;
                } @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("PO_CON", PO_CON);
                    params.put("PO_ME_ID", PO_ME_ID);
                    params.put("PO_TAG", PO_TAG);
                    params.put("PO_CATE", PO_CATE);
                    params.put("PO_PIC_COUNT", String.valueOf(indicator_count));
                    params.put("Content-Type", getBodyContentType());
                    return params;
                }

            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(postAddRequest);

    }
    private void UpdatePost() {
        PO_CATE = month;
        PO_CON = post_content.getText().toString();
        if (PO_CON.equals("")) {
            Toast.makeText(context, "게시글 내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (uriList.size() == 0) {
            Toast.makeText(context, "사진을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (PO_CON.contains("ㅅㅂ")) {
            Toast.makeText(context, "비속어는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Response.Listener<NetworkResponse> responseListener = new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println(URL);
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    boolean success = result.getBoolean("success");

                    if (success) {
                        // tell everybody you have succed upload image and post strings
                        Toast.makeText(context, "게시글을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().replace(R.id.container, mypageFragment).commit();
                    } else {
                        Toast.makeText(context, "게시글 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
        PostUpdateRequest postUpdateRequest = null;
        try {
            postUpdateRequest = new PostUpdateRequest(String.valueOf(PO_ID), PO_CON, PO_ME_ID, PO_TAG, PO_CATE, String.valueOf(indicator_count), responseListener, errorListener) {
                @Override
                protected Map<String, PostUpdateRequest.DataPart> getByteData() {
                    Map<String, PostUpdateRequest.DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH_mm_ss");
                    String date = sdf.format(System.currentTimeMillis());

                    for (int i = 0; i < indicator_count; i++) {
                        if (bitmap[i] == null)
                            System.out.println(i + "is null");
                        else
                            System.out.println(i + "is not null");
                    }


                    for (int i = 0; i < indicator_count; i++) {
                        params.put("PO_PIC" + String.valueOf(i), new PostUpdateRequest.DataPart(PO_ME_ID + "_" + date + "_" +  String.valueOf(i)  + ".jpg", getFileDataFromBitmap(activity.getBaseContext(), bitmap[i]), "image/jpeg"));
                    }
                    return params;
                } @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("PO_ID", String.valueOf(PO_ID));
                    params.put("PO_CON", PO_CON);
                    params.put("PO_ME_ID", PO_ME_ID);
                    params.put("PO_TAG", PO_TAG);
                    params.put("PO_CATE", PO_CATE);
                    params.put("PO_PIC_COUNT", String.valueOf(indicator_count));
                    params.put("Content-Type", getBodyContentType());
                    return params;
                }

            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(postUpdateRequest);

    }



    private void SetProfilePicture() {
        Uri profile = Uri.parse("http://dlsxjsptb.cafe24.com/profile/image/" + ME_PIC);

        Glide.with(context).load(profile).apply(new RequestOptions().circleCrop()).into(profile_picture);
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
        ProfileGetRequest ProfileGetRequest = new ProfileGetRequest(PO_ME_ID, responseListener, errorListener );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(ProfileGetRequest);

    }


    private void setCategory(String cate) {
        switch (cate) {
            case "1월":
                season_spinner.setSelection(3);
                month_spinner.setSelection(1);
                return;
            case "2월":
                season_spinner.setSelection(3);
                month_spinner.setSelection(2);
                return;
            case "3월":
                season_spinner.setSelection(0);
                month_spinner.setSelection(0);
                return;
            case "4월":
                season_spinner.setSelection(0);
                month_spinner.setSelection(1);
                return;
            case "5월":
                season_spinner.setSelection(0);
                month_spinner.setSelection(2);
                return;
            case "6월":
                season_spinner.setSelection(1);
                month_spinner.setSelection(0);
                return;
            case "7월":
                season_spinner.setSelection(1);
                month_spinner.setSelection(1);
                return;
            case "8월":
                season_spinner.setSelection(1);
                month_spinner.setSelection(2);
                return;
            case "9월":
                season_spinner.setSelection(2);
                month_spinner.setSelection(0);
                return;
            case "10월":
                season_spinner.setSelection(2);
                month_spinner.setSelection(1);
                return;
            case "11월":
                season_spinner.setSelection(2);
                month_spinner.setSelection(2);
                return;
            case "12월":
                season_spinner.setSelection(3);
                month_spinner.setSelection(0);
                return;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed() {
        goToMain();
    }

    //프래그먼트 종료
    private void goToMain(){
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }


}