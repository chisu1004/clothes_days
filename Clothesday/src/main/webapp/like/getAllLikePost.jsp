<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="like.LikeTableDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	//게시글 정보 가져오기
	LikeTableDAO like = new LikeTableDAO();	
	like.setLIKE_ME_ID(request.getParameter("LIKE_ME_ID"));
	
	JSONArray jarray = like.getAllLikePost(like);
	
	
	JSONObject jobj = new JSONObject();
	
	if (jarray != null) {
		jobj.put("scrap", jarray);
	} else {
		jobj.put("success", false);
	}
	
	like.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
%>


