<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="follow.FollowDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>

<%	
	FollowDAO follow = new FollowDAO();	

	follow.setFO_FOL_ID(request.getParameter("FO_FOL_ID"));	

	JSONArray jarray = follow.getFollowing(follow);	
	
	JSONObject jobj = new JSONObject();
	
	if (jarray != null) {
		jobj.put("follow", jarray);		
	} else {
		jobj.put("success", false);
	}
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
%>
