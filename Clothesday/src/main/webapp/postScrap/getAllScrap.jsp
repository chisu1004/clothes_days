<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="postScrap.PostScrapDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	//게시글 정보 가져오기
	PostScrapDAO scrap = new PostScrapDAO();	
	scrap.setPS_ME_ID(request.getParameter("PS_ME_ID"));
	JSONArray jarray = scrap.getUserScrap(scrap);
	
	
	JSONObject jobj = new JSONObject();
	
	if (jarray != null) {
		jobj.put("scrap", jarray);
	} else {
		jobj.put("success", false);
	}
	
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
	scrap.closeConnection();
%>


