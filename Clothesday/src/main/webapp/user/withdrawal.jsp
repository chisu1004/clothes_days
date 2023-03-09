<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page import="post.PostDAO" %>
<%@ page import="like.LikeTableDAO" %> 
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.File" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%@page import="org.json.simple.JSONObject"%>

<%	
	UserDAO user = new UserDAO();
	PostDAO post = new PostDAO();	
	LikeTableDAO like = new LikeTableDAO();
	ArrayList<PostDAO> mList = new ArrayList<PostDAO>();
	
	like.setLIKE_ME_ID(request.getParameter("ME_ID"));
	user.setME_ID(request.getParameter("ME_ID"));
	post.setPO_ME_ID(request.getParameter("ME_ID"));

	
	File f;
	StringTokenizer stk;
	mList = post.getUserPO_PIC(post);
	
	String dir = application.getRealPath("/post/image");
	String dir2 = application.getRealPath("/profile/image");
	//게시글 사진 지우기
	try {
		for (int i = 0; i < mList.size(); i++ ) {
			PostDAO post2 = mList.get(i);
						
			  if (post2.getPO_PIC() != null) {
		          stk = new StringTokenizer(post2.getPO_PIC(), ",");           
		       
		          while(stk.hasMoreTokens()){
		        	  f = new File(dir + "/" + stk.nextToken());
		        	  if (f.exists())
		        		  f.delete();            
		          }
		      }				
				
		}
		  
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	
	user = user.getProfile(user);
	// 프로필 사진 지우기
	try {	
						
		 if (user.getME_PIC() != null) {	          
			 f = new File(dir2 + "/" + user.getME_PIC());
			  if (f.exists())
		   		  f.delete(); 		
		 }
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	like.deleteAllUserPO_LIKE(like);
	
	int res = user.withdrawal(user);
	


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
