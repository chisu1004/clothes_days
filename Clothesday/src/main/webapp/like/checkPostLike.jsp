<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="like.LikeTableDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	LikeTableDAO like = new LikeTableDAO();	
	like.setLIKE_ME_ID(request.getParameter("LIKE_ME_ID"));
	try {
		like.setLIKE_PO_ID(Integer.parseInt(request.getParameter("LIKE_PO_ID")));
	} catch (Exception e) {
		e.printStackTrace();
	}
	int res = 0;
	res = like.checkPostLike(like);
	
	JSONObject jobj = new JSONObject();	

	jobj.put("check", res); // 1 : 좋아요 되어 있음 , 0 : 좋아요 x 
	out.print(jobj.toJSONString()); 
	
	like.closeConnection();
	
	
	response.setContentType("application/json;charset=UTF-8");
%>


