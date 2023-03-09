<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="search.SearchDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>


<%
	SearchDAO search = new SearchDAO();
	
	search.setSE_ME_ID(request.getParameter("SE_ME_ID"));
	search.setSE_CON(request.getParameter("SE_CON"));

	int addRes = search.addSearchRecord(search);
	
	JSONArray jarray = search.getSearchResult(search);
	
	
	
	JSONObject jobj = new JSONObject();
	  
	
	if (jarray != null) {
		jobj.put("search", jarray);	
	} else {
		jobj.put("success", false);
	}


	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
	search.closeConnection();
%>
