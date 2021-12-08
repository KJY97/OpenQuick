<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="openQuick.kickDAO"%>
<%@page import="openQuick.logDAO"%>
<%@page import="org.json.simple.JSONArray"%>
<%
String ip = request.getHeader("X-Forwarded-For");
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getHeader("Proxy-Client-IP"); 
	} 
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getHeader("WL-Proxy-Client-IP"); 
	} 
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getHeader("HTTP_CLIENT_IP"); 
	} 
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
	}
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getHeader("X-Real-IP"); 
	} 
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	ip = request.getHeader("X-RealIP"); 
	}
if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	ip = request.getRemoteAddr(); 
	}
logDAO logdao = new logDAO();
logdao.insertLog(ip, "test");
try{
	Class.forName("com.mysql.jdbc.Driver");
	kickDAO kd = new kickDAO();
	JSONArray list = kd.selectKickList();
	out.println(list.toString());
} catch(Exception e) {
}
%>