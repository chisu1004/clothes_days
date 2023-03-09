<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	
	post.PostDAO post = new post.PostDAO();	
	

	JSONArray jarray = post.getBestPost();	
	
	JSONObject jobj = new JSONObject();
	
	if (jarray != null) {
		jobj.put("bestPost", jarray);		
	} else {
		jobj.put("success", false);
	}
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
	 post.closeConnection();
%>


