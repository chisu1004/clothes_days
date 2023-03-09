package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReplyAddRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/reply/addReply.jsp";

    private Map<String, String> map;

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;



    public ReplyAddRequest (String RE_ME_ID,String RE_CON, String RE_PO_ID, Response.Listener<String> listener,
                               Response.ErrorListener errorListener)  {
        super(Method.POST, URL, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("RE_ME_ID", RE_ME_ID);
        map.put("RE_CON", RE_CON);
        map.put("RE_PO_ID", RE_PO_ID);
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return (map != null) ? map : super.getHeaders();
    }




}