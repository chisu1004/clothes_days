<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="follow.FollowDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>

<%	
	FollowDAO follow = new FollowDAO();	

	follow.setFO_ME_ID(request.getParameter("ME_ID"));
	follow.setFO_FOL_ID(request.getParameter("ME_ID"));
	

	int follower = 0; // 나를 팔로우한
	int following = 0;  // 내가 팔로우한
	
	follower = follow.countFollower(follow);
	following = follow.countFollowing(follow);
	
	JSONObject jobj = new JSONObject();  

	
	jobj.put("follower", follower); 
	jobj.put("following", following);

	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
%>
