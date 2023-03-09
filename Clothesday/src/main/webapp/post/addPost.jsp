<%@ page language="java" contentType="multipart/form-data; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%> 
<%@ page trimDirectiveWhitespaces="true" %>

<%
	post.PostDAO post = new post.PostDAO();	

	int PO_PIC_COUNT = 0; 
	String PO_PIC = "";// 파일 경로	
	int max = 10*1024*1024;//최대크기
	String dir = application.getRealPath("/post/image");
	String mra = "";


	try {
		MultipartRequest mr = new MultipartRequest(request, dir, max, "UTF-8");
		post.setPO_CON(mr.getParameter("PO_CON").replaceAll("ㅅㅂ","\n"));	
		post.setPO_ME_ID(mr.getParameter("PO_ME_ID"));
		post.setPO_CATE(mr.getParameter("PO_CATE"));
		post.setPO_TAG(mr.getParameter("PO_TAG"));	
		
	    PO_PIC_COUNT = Integer.parseInt(mr.getParameter("PO_PIC_COUNT")); // 사진 개수	    
	    
		for (int i = 0; i < PO_PIC_COUNT; i++) {
	PO_PIC += mr.getFilesystemName("PO_PIC" + String.valueOf(i));		
	if (PO_PIC_COUNT - 1 > i)
		PO_PIC += ",";
		}
	    
	    
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	post.setPO_PIC(PO_PIC);
	
	int res = post.addPost(post);	

	JSONObject jobj = new JSONObject();
	
	 if (res == 1) {
		 jobj.put("success", true);	 		
	 } else {
			jobj.put("success", false);	 
	 }
	 post.closeConnection();
		out.print(jobj.toJSONString()); 
	 response.setContentType("application/json;charset=UTF-8");
%>







