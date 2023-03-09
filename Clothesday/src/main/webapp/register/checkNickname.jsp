<%@ page language="java" contentType="multipart/form-data; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>


<%
	UserDAO user = new UserDAO();	
	
	int res = user.checkNick(request.getParameter("ME_NICK"));
	

	JSONObject jobj = new JSONObject();
	
	 if (res == 1) {
		 jobj.put("success", true);	 		
	 } else {
			jobj.put("success", false);	 
	 }
		out.print(jobj.toJSONString()); 
	 response.setContentType("application/json;charset=UTF-8");
	 user.closeConnection();
%>







