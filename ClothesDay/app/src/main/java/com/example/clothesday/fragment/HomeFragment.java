package com.example.clothesday.fragment;

import static com.example.clothesday.data.Address.getAddress;
import static com.example.clothesday.data.TransLocalPoint.convertGRID_GPS;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.clothesday.Adapter.OneLineLongRecyclerViewAdapter;
import com.example.clothesday.Adapter.OneLineRecyclerViewAdapter;
import com.example.clothesday.Adapter.RecentSearchRecyclerViewAdapter;
import com.example.clothesday.DAO.PostDTO;
import com.example.clothesday.DAO.SearchDTO;
import com.example.clothesday.DAO.UserDTO;
import com.example.clothesday.R;
import com.example.clothesday.data.GpsTracker;
import com.example.clothesday.data.LatXLngY;
import com.example.clothesday.data.PermissionSupport;
import com.example.clothesday.data.Time;
import com.example.clothesday.data.onBackPressedListener;
import com.example.clothesday.request.RecentSearchRequest;
import com.example.clothesday.request.RecommendedClothesRequest;
import com.example.clothesday.request.WeatherRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Transport;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
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
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String endPoint2 =  "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"; //????????????
    String endPoint1 =  "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"; //???????????????
    String serviceKey = "v4NMEDYfbbU0mrzJRFB7%2B7%2BaxBa45UEqciFP6ADjgk94y7TilQSWOSQcA%2BBy7MLo047cryigSW1f4QZU77Z65A%3D%3D";
    String pageNo = "1";
    String numOfRows = "70";
    String numOfRows2 = "250";
    String base_date = "20220606"; //?????????
    String base_date2 = "20220606"; //??????
    String base_time2 = "2300";//??????
    String base_time = "1500"; //?????????
    String nx = "98";
    String ny = "77";
    String URL ="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=v4NMEDYfbbU0mrzJRFB7%2B7%2BaxBa45UEqciFP6ADjgk94y7TilQSWOSQcA%2BBy7MLo047cryigSW1f4QZU77Z65A%3D%3D&numOfRows=" + numOfRows + "&pageNo=1&base_date=" + base_date + "&base_time=" + base_time + "&nx=" + nx +"&ny=" + ny + "&dataType=JSON";
    String URL2 ="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=v4NMEDYfbbU0mrzJRFB7%2B7%2BaxBa45UEqciFP6ADjgk94y7TilQSWOSQcA%2BBy7MLo047cryigSW1f4QZU77Z65A%3D%3D&numOfRows=" + numOfRows2 + "&pageNo=1&base_date=" + base_date + "&base_time=" + base_time + "&nx=" + nx +"&ny=" + ny + "&dataType=JSON";
    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    int end= 0;


    //?????????
    Object TMX, TMN, POP, T1H, REH, SKY, RAIN;
    String addr; //??????
    String today;
    String PO_TAG;

    RecyclerView recyclerView;
    ArrayList<PostDTO> postData = new ArrayList<PostDTO>();
    ArrayList<UserDTO> userData = new ArrayList<UserDTO>();
    OneLineLongRecyclerViewAdapter OneLineLongRecyclerViewAdapter;


    //???
    TextView temp, high, low, humidity, rainper, location, weather_get_text;
    ImageView sky;
    ViewGroup rootView;
    LottieAnimationView animationView;

    FragmentManager fm;


    private PermissionSupport permission; // ????????? ?????? ??????


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        temp = rootView.findViewById(R.id.main_weather_temp);
        high = rootView.findViewById(R.id.main_weather_high);
        low = rootView.findViewById(R.id.main_weather_low);
        humidity = rootView.findViewById(R.id.main_weather_humidity);
        rainper = rootView.findViewById(R.id.main_weather_rainper);
        sky = rootView.findViewById(R.id.main_weather_icon);
        location = rootView.findViewById(R.id.main_weather_location);
        animationView = rootView.findViewById(R.id.main_lottie_animation);
        weather_get_text = rootView.findViewById(R.id.main_weather_get_text);
        recyclerView = rootView.findViewById(R.id.main_recyclerview);

        //??????????????????
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        fm = getParentFragmentManager();



        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionCheck();
        }
        GetWeather();

        return rootView;

    }

    // ?????? ??????
    private void permissionCheck() {
        // PermissionSupport.java ????????? ?????? ??????
        permission = new PermissionSupport(activity, activity);

        // ?????? ?????? ??? ????????? false??? ????????????
        if (!permission.checkPermission()){
            permission.requestPermission();
            GetWeather();
        }
    }



    public void GetWeather() {
        
        
        SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy.MM.dd");

        long now = System.currentTimeMillis();
        today = sdf_today.format(now); 
        Date date = new Date(now);


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        base_date = sdf.format(date);  //  ?????? ??????
        base_date2 = sdf.format(date); //  ?????? ??????

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH00");
        String real_time = sdf2.format(cal.getTime()); // ?????? ??????
        cal.add(Calendar.HOUR, -1);
        base_time = sdf2.format(cal.getTime()); // ?????? ???????????? 1?????? ??? ??????

        if (base_time.equals("2300")) { // 00?????? ?????? ????????? ?????? ??????
            base_date = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
            base_date2 = sdf.format(cal.getTime());
        } else {
            cal.add(Calendar.DATE, -2);
            base_date2 = sdf.format(cal.getTime());
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            GpsTracker gpsTracker = new GpsTracker(activity);
            double latx = gpsTracker.getLatitude(); // ??????
            double laty = gpsTracker.getLongitude(); // ??????

            addr = getAddress(activity, latx, laty);


            LatXLngY tmp = convertGRID_GPS(TO_GRID, latx, laty);

            nx = String.valueOf((int) tmp.x);
            ny = String.valueOf((int) tmp.y);

            URL = endPoint1 + "?serviceKey=v4NMEDYfbbU0mrzJRFB7%2B7%2BaxBa45UEqciFP6ADjgk94y7TilQSWOSQcA%2BBy7MLo047cryigSW1f4QZU77Z65A%3D%3D&numOfRows=" + numOfRows + "&pageNo=1&base_date=" + base_date + "&base_time=" + base_time + "&nx=" + nx + "&ny=" + ny + "&dataType=JSON";
            URL2 = endPoint2 + "?serviceKey=v4NMEDYfbbU0mrzJRFB7%2B7%2BaxBa45UEqciFP6ADjgk94y7TilQSWOSQcA%2BBy7MLo047cryigSW1f4QZU77Z65A%3D%3D&numOfRows=" + numOfRows2 + "&pageNo=1&base_date=" + base_date2 + "&base_time=" + base_time2 + "&nx=" + nx + "&ny=" + ny + "&dataType=JSON";


            System.out.println(URL);
            System.out.println(URL2);

            animationView.setVisibility(View.VISIBLE);
            animationView.setSpeed(1f);
            animationView.playAnimation();
            weather_get_text.setVisibility(View.VISIBLE);

            new Thread(() -> {
                try {
                    //?????? 23??? ?????? 153?????? ???????????? ???????????? ????????? ????????? ????????? ??? ??? ??????
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);

                                // response ?????? ????????? ???????????? ??????
                                JSONObject parse_response = (JSONObject) obj.get("response");
                                // response ??? ?????? body ??????
                                JSONObject parse_body = (JSONObject) parse_response.get("body");
                                // body ??? ?????? items ??????
                                JSONObject parse_items = (JSONObject) parse_body.get("items");

                                // items??? ?????? itemlist ??? ??????
                                JSONArray parse_item = (JSONArray) parse_items.get("item");
                                String category;
                                JSONObject weather; // parse_item??? ?????????????????? ????????? ????????? ???????????? ????????? ???????????? ??????
                                // ??????????????? ?????? ????????????
                                String day = "";
                                String time = "";
                                for (int i = 0; i < parse_item.length(); i++) {
                                    weather = (JSONObject) parse_item.get(i);
                                    Object fcstValue = weather.get("fcstValue");
                                    Object fcstDate = weather.get("fcstDate");
                                    Object fcstTime = weather.get("fcstTime");

                                    //double????????? ??????????????? ???????????? ?????? ??????
                                    //double fcstValue = Double.parseDouble(weather.get("fcstValue").toString());
                                    category = (String) weather.get("category");
                                    // ??????
                                    if (!day.equals(fcstDate.toString())) {
                                        day = fcstDate.toString();
                                    }
                                    if (!time.equals(fcstTime.toString())) {
                                        time = fcstTime.toString();
                                        // System.out.println(day + "  " + time);
                                    }
                                    if (category.equals("T1H") && fcstTime.equals(real_time)) {
                                        T1H = fcstValue;
                                    }
                                    if (category.equals("REH") && fcstTime.equals(real_time)) { //??????
                                        REH = fcstValue;
                                    }
                                    if (category.equals("SKY") && fcstTime.equals(real_time)) { // ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
                                        SKY = fcstValue;
                                    }if (category.equals("PTY") && fcstTime.equals(real_time)) {  //????????????(PTY) ?????? : (?????????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(5), ??????????????????(6), ?????????(7)
                                        RAIN = fcstValue;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (end == 1 ){
                                SetWeatherInfo();
                            } else {
                                end = 1;
                            }

                        }
                    };
                    WeatherRequest WeatherRequest = new WeatherRequest(URL, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(activity);
                    queue.add(WeatherRequest);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);

                                // response ?????? ????????? ???????????? ??????
                                JSONObject parse_response = (JSONObject) obj.get("response");
                                // response ??? ?????? body ??????
                                JSONObject parse_body = (JSONObject) parse_response.get("body");
                                // body ??? ?????? items ??????
                                JSONObject parse_items = (JSONObject) parse_body.get("items");
                                // items??? ?????? itemlist ??? ??????
                                JSONArray parse_item = (JSONArray) parse_items.get("item");
                                String category;
                                JSONObject weather; // parse_item??? ?????????????????? ????????? ????????? ???????????? ????????? ???????????? ??????
                                // ??????????????? ?????? ????????????
                                String day = "";
                                String time = "";
                                for (int i = 0; i < parse_item.length(); i++) {
                                    weather = (JSONObject) parse_item.get(i);
                                    Object fcstValue = weather.get("fcstValue");
                                    Object fcstDate = weather.get("fcstDate");
                                    Object fcstTime = weather.get("fcstTime");


                                    //double????????? ??????????????? ???????????? ?????? ??????
                                    //double fcstValue = Double.parseDouble(weather.get("fcstValue").toString());
                                    category = (String) weather.get("category");
                                    // ??????
                                    if (!day.equals(fcstDate.toString())) {
                                        day = fcstDate.toString();
                                    }
                                    if (!time.equals(fcstTime.toString())) {
                                        time = fcstTime.toString();
                                    }
                                    if (category.equals("TMX") ) { // ???????????? TMX
                                        TMX = fcstValue;
                                    }
                                    if (category.equals("TMN")) { // ???????????? TMN
                                        TMN = fcstValue;
                                    }
                                    if (category.equals("POP") && fcstTime.equals(real_time)) { //????????????
                                        POP = fcstValue;
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (end == 1 ){
                               SetWeatherInfo();
                            } else {
                                end = 1;
                            }
                        }
                    };
                    WeatherRequest WeatherRequest2 = new WeatherRequest(URL2, responseListener2);
                    RequestQueue queue2 = Volley.newRequestQueue(activity);
                    queue2.add(WeatherRequest2);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            /*
             * ?????????	?????????	??????
             * POP	????????????	 %
             * PTY	????????????	?????????
             * R06	6?????? ?????????	?????? (1 mm)
             * REH	??????	 %
             * S06	6?????? ?????????	??????(1 cm)
             * SKY	????????????	?????????
             * T3H	3?????? ??????	 ???
             * TMN	??? ????????????	???	10
             * TMP	1?????? ??????	???	10
             * TMN	?????? ????????????	 ???
             * TMX	??? ????????????	 ???
             * UUU	??????(????????????)	 m/s
             * VVV	??????(????????????)	 m/s
             * WAV	??????	 M
             * VEC	??????	 m/s
             * WSD	??????	1
             */
        } else {
            weather_get_text.setText("?????? ?????? ?????? ????????? ????????? ?????????.");
            weather_get_text.setVisibility(View.VISIBLE);
        }


    }

    private void SetWeatherInfo() {
        Time time = new Time();
        if (T1H != null ) {
            temp.setText(String.valueOf(T1H));
        } else {
            temp.setText("?????? ??????");
        }
        humidity.setText("??????\n" + REH + "%");
        // ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        if (SKY != null) {
            if (SKY.equals("1"))
                sky.setImageResource(R.drawable.sun);
            else if (SKY.equals("3"))
                sky.setImageResource(R.drawable.sun_cloud);
            else if (SKY.equals("4"))
                sky.setImageResource(R.drawable.cloud);
        }

        if (RAIN != null) {//????????????(PTY) ?????? : (?????????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(5), ??????????????????(6), ?????????(7)
            if (RAIN.equals("1"))
                sky.setImageResource(R.drawable.rain);
            else if (RAIN.equals("3"))
                sky.setImageResource(R.drawable.snow);
        }

        if (POP != null ) {
            rainper.setText("?????? ??????\n" + POP + "%");
        } else {
            rainper.setText("?????? ??????\n" + "?????? ??????");
        }
        if (TMX != null ) {
                high.setText("????????????\n" + TMX);
            GetClothesRecommend();
        } else {
            high.setText("????????????\n" + "?????? ??????");
        }
        if (TMN != null ) {
                low.setText("????????????\n" + TMN);
        } else {
            low.setText("????????????\n" + "?????? ??????");
        }

        try {
            location.setText(addr + "\n\n" + today + " " + time.getDayOfWeek(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        animationView.pauseAnimation();
        animationView.setVisibility(View.GONE);
        weather_get_text.setVisibility(View.GONE);
    }

    private void GetClothesRecommend() {

        try {
            PO_TAG = String.valueOf((int)Double.parseDouble(TMX.toString()));
            System.out.println(TMX);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        System.out.println(PO_TAG);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    postData = new ArrayList<PostDTO>();

                    userData = new ArrayList<UserDTO>();

                    JSONObject result = new JSONObject(response);
                    JSONArray jsonArray = result.getJSONArray("post");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PostDTO post = new PostDTO();
                        UserDTO user = new UserDTO();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        post.setPO_ID(jObject.getInt("PO_ID"));
                        post.setPO_REG_DA(jObject.getString("PO_REG_DA")); // ???
                        post.setPO_CATE(jObject.getString("PO_CATE")); //???
                        post.setPO_TAG(jObject.getString("PO_TAG"));
                        post.setPO_PIC(jObject.getString("PO_PIC")); //???
                        post.setPO_CON(jObject.getString("PO_CON")); //???
                        post.setPO_ME_ID(jObject.getString("PO_ME_ID"));
                        post.setPO_LIKE(jObject.getInt("PO_LIKE")); // ???
                        user.setME_NICK(jObject.getString("ME_NICK")); //???
                        user.setME_PIC(jObject.getString("ME_PIC")); //???
                        userData.add(user);
                        postData.add(post);

                    }
                    OneLineLongRecyclerViewAdapter = new OneLineLongRecyclerViewAdapter(context, postData, userData, activity, (fm==null)?getParentFragmentManager():fm);
                    recyclerView.setAdapter(OneLineLongRecyclerViewAdapter);

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
        RecommendedClothesRequest RecommendedClothesRequest =  new RecommendedClothesRequest(PO_TAG, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(RecommendedClothesRequest);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}