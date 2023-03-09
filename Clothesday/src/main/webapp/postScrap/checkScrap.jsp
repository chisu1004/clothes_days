<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="postScrap.PostScrapDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	PostScrapDAO scrap = new PostScrapDAO();	
	scrap.setPS_ME_ID(request.getParameter("PS_ME_ID"));
	try {
	scrap.setPS_PO_ID(Integer.parseInt(request.getParameter("PS_PO_ID")));
	} catch (Exception e) {
		e.printStackTrace();
	}
	int res = 0;
	res = scrap.checkScrap(scrap);
	
	JSONObject jobj = new JSONObject();	

	jobj.put("check", res); // 1 : 스크랩 되어 있음 , 0 : 스크랩 x 
	out.print(jobj.toJSONString()); 
	
	scrap.closeConnection();
	
	response.setContentType("application/json;charset=UTF-8");
%>


