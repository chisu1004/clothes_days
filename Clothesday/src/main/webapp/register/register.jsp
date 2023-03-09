<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<% 
	String ME_ID = request.getParameter("ME_ID");
	String ME_PW = request.getParameter("ME_PW");
	String ME_NICK = request.getParameter("ME_NICK");

	
	String res = "";
	UserDAO user = new UserDAO();	
	int state;	
	
	user.setME_ID(ME_ID);
	user.setME_PW(ME_PW);
	user.setME_NICK(ME_NICK);

	
	state = user.join(user);
	if (state == -1)
		res="false";
	else
		res="true";
	user.closeConnection();
%>
<%=res%>