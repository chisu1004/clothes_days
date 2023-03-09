package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostDeleteRequest extends StringRequest {

    final static private String URL = "http://dlsxjsptb.cafe24.com/post/deletePost.jsp";
    private Map<String, String> map;

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;

    public PostDeleteRequest(int PO_ID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, null);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("PO_ID", String.valueOf(PO_ID));
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
