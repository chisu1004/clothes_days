<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.io.File" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	File f;
	StringTokenizer stk;
	PostDAO post = new PostDAO();	
	String dir = application.getRealPath("/post/image");
	int res = 0;
		
	try {
		post.setPO_ID(Integer.parseInt(request.getParameter("PO_ID")));		
		post = post.getPostPicture(post);
		
		  if (post.getPO_PIC() != null) {
	          stk = new StringTokenizer(post.getPO_PIC(), ",");            
	       
	          while(stk.hasMoreTokens()){
	        	  f = new File(dir + "/" + stk.nextToken());
	        	  if (f.exists())
	        		  f.delete();             
	          }
	      }
			res = post.deletePost(post);
		  
	} catch (Exception ex) {
		ex.printStackTrace();
	}

	
	
	JSONObject jobj = new JSONObject();
	jobj.put("id", post.getPO_ID());
	
	if (res == 1) {		
		jobj.put("result", true);		
	} else {
		jobj.put("result", false);
	}
	 post.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
%>


