package com.example.clothesday.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostReportRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/report/report.jsp";

    private Map<String, String> map;
    private Response.ErrorListener mErrorListener;

    public PostReportRequest(String ME_ID, String ME_PW, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("RE_VIL_ID", ME_ID);
        map.put("RE_ME_ID", ME_PW);
        map.put("RE_PO_ID", ME_PW);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }


}