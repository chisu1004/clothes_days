<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	post.PostDAO post = new post.PostDAO();	
	post.setPO_ME_ID(request.getParameter("PO_ME_ID"));
	JSONArray jarray = post.getUserPost(post);
	int res = post.countPost(post);
	
	
	JSONObject jobj = new JSONObject();
	
	
	if (jarray != null) {
		jobj.put("post", jarray);
		jobj.put("postCount", res);		
	} else {
		jobj.put("success", false);
		
	}
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
	 post.closeConnection();
%>


