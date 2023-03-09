<%@ page language="java" contentType="multipart/form-data; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@ page import="com.oreilly.servlet.MultipartRequest"%> 
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.io.File" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	post.PostDAO post = new post.PostDAO();	
	File f;
	StringTokenizer stk;
	int PO_PIC_COUNT = 0; 
	String PO_PIC = "";// 파일 경로	
	int max = 10*1024*1024;//최대크기
	String dir = application.getRealPath("/post/image");

	try {
		MultipartRequest mr = new MultipartRequest(request, dir, max, "UTF-8");
		
		post.setPO_ID(Integer.parseInt(mr.getParameter("PO_ID")));		
		post = post.getPostPicture(post);
		
		  if (post.getPO_PIC() != null) {
	          stk = new StringTokenizer(post.getPO_PIC(), ",");  	          
	       
	          while(stk.hasMoreTokens()){
	        	  f = new File(dir + "/" + stk.nextToken());
	        	  if (f.exists())
	        		  f.delete();             
	          }
	      }			
		
		post.setPO_ID(Integer.parseInt(mr.getParameter("PO_ID")));		
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
	
	int res = post.updatePost(post);

	JSONObject jobj = new JSONObject();
	
	 if (res == 1) {
		 jobj.put("success", true);	 		
	 } else {
			jobj.put("success", false);	 
	 }
		out.print(jobj.toJSONString()); 
	 response.setContentType("application/json;charset=UTF-8");
	 post.closeConnection();
%>







