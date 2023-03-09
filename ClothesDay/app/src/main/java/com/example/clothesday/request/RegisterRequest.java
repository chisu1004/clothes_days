package com.example.clothesday.request;

import static java.sql.DriverManager.println;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest{

    final static private String URL = "http://dlsxjsptb.cafe24.com/register/register.jsp";
    final static private String URL2 = "http://10.0.2.2:8070/clothesday/register/register.jsp";
    private Map<String, String> map;

    public RegisterRequest(String UserNick, String UserId, String UserPw, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("ME_PW", UserPw);
        map.put("ME_NICK", UserNick);
        map.put("ME_ID", UserId);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
