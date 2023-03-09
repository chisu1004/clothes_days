package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {

    final static  private String URL="http://dlsxjsptb.cafe24.com/register/validate.jsp";
    final static private String URL2 = "http://10.0.2.2:8070/clothesday/login/login.jsp";
    private Map<String, String> map;

    public ValidateRequest(String UserId, boolean verify, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);
        map = new HashMap<String, String>();
        String validate;
        if (verify)
            validate = "true";
        else
            validate = "false";
        map.put("UserId", UserId);
        map.put("ME_VERIFY", validate);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {


        return map;
    }
}