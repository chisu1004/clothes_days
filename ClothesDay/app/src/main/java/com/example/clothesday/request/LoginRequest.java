package com.example.clothesday.request;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/login/login.jsp";
    final static private String URL2 = "http://10.0.2.2:8070/clothesday/login/login.jsp";
    private Map<String, String> map;
    private Response.ErrorListener mErrorListener;

    public LoginRequest(String ME_ID, String ME_PW, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("ME_ID", ME_ID);
        map.put("ME_PW", ME_PW);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }


}