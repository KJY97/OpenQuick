<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
   <%@ page import="java.sql.*" %>
   <%@ page import="java.time.LocalDateTime" %>
   <%@ page import="java.time.format.DateTimeFormatter" %>
   <%@ page import="org.json.simple.JSONObject" %>
   <%@ page import="org.json.simple.parser.JSONParser" %>
   <%@ page import="org.json.simple.parser.ParseException" %>
<%
   request.setCharacterEncoding("UTF-8");
   String json = request.getParameter("json");
   
   JSONParser parser = new JSONParser();
   Object obj = parser.parse(json);
   JSONObject jsonObj = (JSONObject)obj;
   
   String id = jsonObj.get("id").toString();
   String nickname = jsonObj.get("nickname").toString();
   String email = "";
   
   if (jsonObj.get("email") != null)
      email = jsonObj.get("email").toString();

   LocalDateTime date = LocalDateTime.now();
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   String loginState = "false";
   String scom_name = null;
   
   Connection conn = null;
   PreparedStatement pstmt = null;
   scom_name = "kakao";
   
   // DB 
   try{
         String user = "root";
         String pw = "0000";
         String url = "jdbc:mysql://localhost/test";

         try {
            Class.forName("com.mysql.jdbc.Driver");
         } catch (ClassNotFoundException e) {
         }

      conn = DriverManager.getConnection(url, user, pw);
      
      String sql = "select user_sign_up_date from user where user_id = ?";
      
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();
      
      int cnt = 0;
      while(rs.next()) {
         String sign_up_date = rs.getString("user_sign_up_date");
         cnt++;
      }
      
      if (cnt > 0) {
         sql = "UPDATE user SET user_loginstate = ?, user_login_date = ? WHERE user_id = ?";
         
         loginState = "true";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, loginState);    
         pstmt.setString(2, formatter.format(date));
         pstmt.setString(3, id);
         pstmt.executeUpdate();
         
         out.print("Success");
      }
      else { 
         sql = "INSERT INTO user(user_id, user_nickname, user_email, user_sign_up_date, user_loginstate, user_login_date, user_logout_date, social_com__scom_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
         
         loginState = "true";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);            
         pstmt.setString(2, nickname);    
         pstmt.setString(3, email);
         pstmt.setString(4, formatter.format(date));
         pstmt.setString(5, loginState); 
         pstmt.setString(6, formatter.format(date));
         pstmt.setString(7, formatter.format(date));
         pstmt.setString(8, scom_name);
         pstmt.executeUpdate();
         
         out.print("Success");
      }
    } catch(SQLException sqle){
       out.print("Fail");
    } finally{
       try{
          if( pstmt != null) pstmt.close();
          if( conn != null) conn.close();
       } catch(Exception e){
       }
    }
%>
