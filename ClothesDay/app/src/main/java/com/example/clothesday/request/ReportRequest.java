package com.example.clothesday.request;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

public class ReportRequest extends StringRequest {


    final static private String URL = "http://dlsxjsptb.cafe24.com/report/report.jsp";

    private Map<String, String> map;
    private Response.ErrorListener mErrorListener;
    private Response.Listener<String> mListener;

    public  ReportRequest(String RP_VIL_ID, String RP_ME_ID,String RP_CON, String RP_PO_ID, Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        map = new HashMap<>();
        map.put("RP_VIL_ID", RP_VIL_ID);
        map.put("RP_ME_ID", RP_ME_ID);
        map.put("RP_CON", RP_CON);
        map.put("RP_PO_ID", RP_PO_ID);

    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }


}