<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%


	
	post.PostDAO post = new post.PostDAO();	
	try {
		post.setPO_ID(Integer.parseInt(request.getParameter("PO_ID")));
	} catch (Exception ex) {
		ex.printStackTrace();
	}

	JSONArray jarray = post.getOneBestPost(post);	
	
	JSONObject jobj = new JSONObject();
	
	if (jarray != null) {
		jobj.put("post", jarray);		
	} else {
		jobj.put("success", false);
	}
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
	 post.closeConnection();
%>


