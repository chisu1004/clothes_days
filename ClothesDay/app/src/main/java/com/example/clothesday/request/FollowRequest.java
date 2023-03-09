package com.example.clothesday.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FollowRequest extends StringRequest {

    final static private String URL = "http://dlsxjsptb.cafe24.com/follow/follow.jsp";
    private Map<String, String> map;

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;

    public FollowRequest(String FO_ME_ID, String FO_FOL_ID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, null);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("FO_ME_ID", FO_ME_ID);
        map.put("FO_FOL_ID", FO_FOL_ID);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}
