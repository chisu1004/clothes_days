<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>


<%
	UserDAO user = new UserDAO();	
	user.setME_ID(request.getParameter("ME_ID"));
	user.setME_PW(request.getParameter("ME_PW"));
	

	int res = user.setPassword(user);


	JSONObject jobj = new JSONObject();
	
	
	if (res == 1) {
		jobj.put("success", true);
	
	} else {
		jobj.put("success", false);
	
	}
	response.setContentType("application/json");
	out.print(jobj.toJSONString()); // json 형식으로 출력	

	user.closeConnection();
%>
