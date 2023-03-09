<%@ page language="java" contentType="multipart/form-data; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%> 
<%@ page trimDirectiveWhitespaces="true" %>


<%
	UserDAO user = new UserDAO();	

	int PO_PIC_COUNT = 0; 
	String ME_PIC = "";// 파일 경로	
	int max = 10*1024*1024;//최대크기
	String dir = null;
	dir = application.getRealPath("/profile/image");
	String mra = "";


	try {
		MultipartRequest mr = new MultipartRequest(request, dir, max, "UTF-8");
		user.setME_ID(mr.getParameter("ME_ID"));  
		ME_PIC = mr.getFilesystemName("ME_PIC");		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	user.setME_PIC(ME_PIC);
	
	int res = user.setProfile(user);	

	JSONObject jobj = new JSONObject();
	
	 if (res == 1) {
		 jobj.put("success", true);	 		
	 } else {
			jobj.put("success", false);	 
	 }
		out.print(jobj.toJSONString()); 
	 response.setContentType("application/json;charset=UTF-8");
	 user.closeConnection();
%>







