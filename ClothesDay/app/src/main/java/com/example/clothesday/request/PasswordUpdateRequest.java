package com.example.clothesday.request;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

public class PasswordUpdateRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/user/updatePassword.jsp";

    private Map<String, String> map;
    private Response.ErrorListener mErrorListener;
    private Response.Listener<String> mListener;

    public PasswordUpdateRequest(String ME_ID, String ME_PW, Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("ME_ID", ME_ID);
        map.put("ME_PW", ME_PW);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }


}