<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="search.SearchDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>


<%
	SearchDAO search = new SearchDAO();
	
	search.setSE_ME_ID(request.getParameter("SE_ME_ID"));
	
	JSONArray jarray = search.getRecentSearch(search);
	
	
	JSONObject jobj = new JSONObject();
	  
	
	if (jarray != null) {
		jobj.put("recentSearch", jarray);	
	} else {
		jobj.put("success", false);
	}


	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
	search.closeConnection();
%>
