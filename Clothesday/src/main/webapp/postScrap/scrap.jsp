<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="postScrap.PostScrapDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>



<%
	PostScrapDAO scrap = new PostScrapDAO();	
	try {
	scrap.setPS_PO_ID(Integer.parseInt(request.getParameter("PS_PO_ID")));	
	} catch (Exception e) {
		e.printStackTrace();
	}
	scrap.setPS_ME_ID(request.getParameter("PS_ME_ID"));
	
	int check = 0;	//스크랩 상태 확인
	int res = 0; // 스크랩 추가 취소 여부
	check = scrap.checkScrap(scrap); // 1 : 스크랩 되어 있음 , 0 : 스크랩 x
	
	
	
	JSONObject jobj = new JSONObject();
	  
	
	if (check == 0) { // 0 = 스크랩 추가   1 = 스크랩 해제
		res = scrap.addScrap(scrap);
	
	} else {
		res = scrap.deleteScrap(scrap);		
	}
	
	jobj.put("check", check); 
	jobj.put("result", res);

	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
	scrap.closeConnection();
%>
