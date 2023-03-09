<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO" %> 
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="org.json.simple.JSONObject"%>


<%
	UserDAO user = new UserDAO();	
	String ME_ID = request.getParameter("ME_ID");
	String ME_PW = request.getParameter("ME_PW");
	
	int res;
	int rec_res; // 로그인 기록 결과

	res = user.login(ME_ID,ME_PW);

	JSONObject jobj = new JSONObject();
	  
	switch(res) {
	case 1: // 성공		
		UserDAO namedao = user.getUser(ME_ID);
		
		String nick = namedao.getME_NICK();
		String id = namedao.getME_ID();
		String pw = namedao.getME_PW();
		 
			rec_res = user.loginRecord(namedao);	
		
		    session.setAttribute("ME_ID", id);		 
			jobj.put("success", true);
			jobj.put("ME_NICK",nick);
			jobj.put("ME_ID",id);
			jobj.put("ME_PW",pw);
		break;
	case 0: // 비밀번호 없음			
		jobj.put("success", false);
		break;
	case -1: //서버 오류
		jobj.put("success", false);		

		break;
	default: 
		jobj.put("success", false);
	}
	
	user.closeConnection();
	out.print(jobj.toJSONString());
	response.setContentType("application/json;charset=UTF-8");
%>
