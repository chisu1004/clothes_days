package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RecommendedClothesRequest extends StringRequest {

    final static private String URL = "http://dlsxjsptb.cafe24.com/post/recommendClothesPost.jsp";
    private Map<String, String> map;


    public RecommendedClothesRequest(String PO_TAG, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        map = new HashMap<>();
        map.put("PO_TAG", PO_TAG);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
