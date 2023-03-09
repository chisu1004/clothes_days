<%@ page language="java" contentType="text/html charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="post.PostDAO" %> 
<%@ page import="postScrap.PostScrapDAO" %> 
<%@ page import="like.LikeTableDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%

	post.PostDAO post = new post.PostDAO();	
	PostScrapDAO scrap = new PostScrapDAO();
	LikeTableDAO like = new LikeTableDAO();
	try {
		post.setPO_ID(Integer.parseInt(request.getParameter("PO_ID")));
		scrap.setPS_PO_ID(Integer.parseInt(request.getParameter("PO_ID")));
		scrap.setPS_ME_ID(request.getParameter("ME_ID"));
		like.setLIKE_ME_ID(request.getParameter("ME_ID"));
		like.setLIKE_PO_ID(Integer.parseInt(request.getParameter("PO_ID")));
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	JSONArray jarray = new JSONArray();
	JSONObject jobj = new JSONObject();
	int scrapCheck = 0;
	scrapCheck = scrap.checkScrap(scrap);
	int likeCheck = 0;
	likeCheck = like.checkPostLike(like);
	
	
	jarray = post.getPost(post);
	
	
	if (jarray != null) {
		jobj.put("post", jarray);	
		jobj.put("scrap", scrapCheck);
		jobj.put("like", likeCheck);
	} else {
		jobj.put("success", false);
		
	}
	
	post.closeConnection();
	scrap.closeConnection();
	like.closeConnection();
	out.print(jobj.toJSONString()); 
	response.setContentType("application/json;charset=UTF-8");
%>


