package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BestPostGetRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/post/getBestPost.jsp";

    private Map<String, String> map;

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;



    public BestPostGetRequest(Response.Listener<String> listener,
                              Response.ErrorListener errorListener) throws IOException {
        super(Method.POST, URL, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();

    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return (map != null) ? map : super.getHeaders();
    }




}