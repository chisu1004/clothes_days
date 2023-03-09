package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckLikeRequest extends StringRequest {

    final static private String URL = "http://dlsxjsptb.cafe24.com/like/checkPostLike.jsp";
    private Map<String, String> map;

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;

    public CheckLikeRequest(String LIKE_PO_ID, String LIKE_ME_ID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, null);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("LIKE_PO_ID", LIKE_PO_ID);
        map.put("LIKE_ME_ID", LIKE_ME_ID);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
