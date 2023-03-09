<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>


<%
	UserDAO user = new UserDAO();	
	user.setME_ID(request.getParameter("ME_ID"));
	
	user = user.getProfile(user);

	JSONObject jobj = new JSONObject();
	  
	
	jobj.put("ME_PIC", user.getME_PIC());
	jobj.put("ME_NICK", user.getME_NICK());
	out.print(jobj.toJSONString()); 

	response.setContentType("application/json;charset=UTF-8");
	user.closeConnection();
%>
