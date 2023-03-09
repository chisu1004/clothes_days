<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="like.LikeTableDAO" %> 
<%@ page import="post.PostDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>


<%
	LikeTableDAO like = new LikeTableDAO();	
	PostDAO post = new PostDAO();	
	try {
		like.setLIKE_PO_ID(Integer.parseInt(request.getParameter("LIKE_PO_ID")));	
		post.setPO_ID(Integer.parseInt(request.getParameter("LIKE_PO_ID")));	
	} catch (Exception e) {
		e.printStackTrace();
	}
	like.setLIKE_ME_ID(request.getParameter("LIKE_ME_ID"));

	int check = 0;	//좋아요 상태 확인
	int res = 0; // 좋아요 추가 취소 여부
	check = like.checkPostLike(like); // 1 : 좋아요 되어 있음 , 0 : 좋아요 x

	
	JSONObject jobj = new JSONObject();
	  
	
	if (check == 0) { // 0 = 좋아요 추가   1 = 좋아요 해제
		like.addPO_LIKE(like);	
		res = like.addLikeTable(like);
	
	} else {
		like.deletePO_LIKE(like);
		res = like.deleteLike(like);		
	}
	
	int likeCount = post.countLike(post);
	
	
	jobj.put("check", check); 
	jobj.put("result", res);
	jobj.put("count",likeCount);
	jobj.put("resu", request.getParameter("PS_PO_ID"));
	jobj.put("reaasu", request.getParameter("PS_ME_ID"));

	like.closeConnection();
	post.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
%>
