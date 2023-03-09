<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="report.ReportDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>

<%
	ReportDAO report = new ReportDAO();	
	try {
	report.setRP_PO_ID(Integer.parseInt(request.getParameter("RP_PO_ID")));	
	report.setRP_VIL_ID(request.getParameter("RP_VIL_ID"));
	report.setRP_ME_ID(request.getParameter("RP_ME_ID"));
	report.setRP_CON(request.getParameter("RP_CON"));
	} catch (Exception e) {
		e.printStackTrace();
	}	

	int res = report.addReport(report); 
	
	
	JSONObject jobj = new JSONObject();
	  

	jobj.put("result", res);

	out.print(jobj.toJSONString()); 
	response.setContentType("application/json; charset=UTF-8");
	report.closeConnection();
%>
