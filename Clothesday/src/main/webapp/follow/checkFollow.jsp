<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="follow.FollowDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	FollowDAO follow = new FollowDAO();	

	follow.setFO_ME_ID(request.getParameter("FO_ME_ID"));
	follow.setFO_FOL_ID(request.getParameter("FO_FOL_ID"));

	int res = 0;
	res = follow.checkFollow(follow);
	
	JSONObject jobj = new JSONObject();	

	jobj.put("check", res); // 1 : 팔로우 되어 있음 , 0 : 팔로우 x 
	out.print(jobj.toJSONString()); 
	
	
	response.setContentType("application/json;charset=UTF-8");
%>


