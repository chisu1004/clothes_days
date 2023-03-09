<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="follow.FollowDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>


<%	
	FollowDAO follow = new FollowDAO();	

	follow.setFO_ME_ID(request.getParameter("FO_ME_ID"));
	follow.setFO_FOL_ID(request.getParameter("FO_FOL_ID"));

	int check = 0;	//팔로우 상태 확인
	int res = 0; // 팔로우 추가 취소 여부
	check = follow.checkFollow(follow); // 1 : 팔로우 되어 있음 , 0 : 팔로우 x
	
	
	JSONObject jobj = new JSONObject();
	  
	
	if (check == 0) { // 0 = 팔로우 추가   1 = 팔로우 해제
		res = follow.addFollow(follow);	
	} else {
		res = follow.unFollow(follow);		
	}
	
	jobj.put("check", check); 
	jobj.put("result", res);

	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
%>
