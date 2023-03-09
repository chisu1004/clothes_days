package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class WeatherRequest extends StringRequest {

    private Map<String, String> map;

    public WeatherRequest(String URL, Response.Listener<String> listener){
        super(Method.GET, URL, listener,null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}