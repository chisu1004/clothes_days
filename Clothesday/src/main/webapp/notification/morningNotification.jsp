<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="notification.*"%>
<%@ page import="javax.servlet.*" %>
<%@ page import="user.UserDAO" %> 
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java.net.HttpURLConnection"%>
<%@ page import="java.net.URL"%>
<%@ page import="com.google.firebase.messaging.MulticastMessage" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ page import="com.google.android.gcm.server.*"%>

<%
	UserDAO user = new UserDAO();
	ArrayList<String> token = new ArrayList<String>(); 
	String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);
	 boolean SHOW_ON_IDLE = false; //옙 활성화 상태일때 보여줄것인지
	 int LIVE_TIME = 1; //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간입니다.
	 int RETRY = 2; //메시지 전송에 실패할 시 재시도 횟수입니다.
	 String simpleApiKey = "AIzaSyDZFoOME55TbQPyPVIDVAqVwENhLWavlR4";
	 String gcmURL = "https://android.googleapis.com/fcm/send";
	 token = user.getTokenStringArray();
	 request.setCharacterEncoding("utf-8");
	 String title = new String("오늘의 날씨와 추천 옷차림을 확인해보세요!".getBytes("UTF-8"),"UTF-8");
	 String msg = new String("".getBytes("UTF-8"), "UTF-8");
	 out.print(msg);
	 Sender sender = new Sender(simpleApiKey);

	 MulticastMessage message = MulticastMessage.builder()
			    .putData("score", "850")
			    .putData("time", "2:45")
			    .addAllTokens(token)
			    .build();


%>
