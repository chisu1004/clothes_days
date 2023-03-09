<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>

<%
	PostDAO post = new PostDAO();	
	try {
		post.setPO_ID(Integer.parseInt(request.getParameter("PO_ID")));	
	} catch (Exception e) {
		e.printStackTrace();
	}


	int res = 0; // 좋아요 추가 취소 여부
	
	res = post.countLike(post); // 1 : 좋아요 되어 있음 , 0 : 좋아요 x	
	
	
	JSONObject jobj = new JSONObject();  

	jobj.put("result", res);

	post.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
%>
