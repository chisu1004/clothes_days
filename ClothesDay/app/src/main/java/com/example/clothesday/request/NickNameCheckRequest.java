package com.example.clothesday.request;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

public class NickNameCheckRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/register/checkNickname.jsp";

    private Map<String, String> map;
    private Response.ErrorListener mErrorListener;
    private Response.Listener<String> mListener;

    public NickNameCheckRequest(String ME_NICK, Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("ME_NICK", ME_NICK);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }


}