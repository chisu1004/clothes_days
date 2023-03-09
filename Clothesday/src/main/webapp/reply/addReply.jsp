<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="reply.ReplyDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	ReplyDAO reply = new ReplyDAO();
	try {
		reply.setRE_ME_ID(request.getParameter("RE_ME_ID"));
		reply.setRE_PO_ID(Integer.parseInt(request.getParameter("RE_PO_ID")));
		reply.setRE_CON(request.getParameter("RE_CON"));		
	} catch(Exception ex) {
		ex.printStackTrace();
	}
	
	int res = reply.addReply(reply);
	JSONObject jobj = new JSONObject();
	
	
	if (res == 1) {
		jobj.put("result", res);	
	} else {
		jobj.put("result", -1);
		
	}
	reply.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
%>


