<%@ page language="java" contentType="multipart/form-data; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>


<%
	UserDAO user = new UserDAO();	
	user.setME_ID(request.getParameter("ME_ID"));
	user.setME_TOKEN(request.getParameter("ME_TOKEN"));

	
	int res =user.setToken(user);
	

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







