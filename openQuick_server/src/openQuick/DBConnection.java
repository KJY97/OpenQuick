package openQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");

		Connection conn = null;
		try {
			String user = "root";
			String pw = "0000";
			String url = "jdbc:mysql://localhost:3306/openquick";

			conn = DriverManager.getConnection(url, user, pw);
		} catch (SQLException sqle) {
			System.out.println("DB 접속실패 : " + sqle.toString());
		} catch (Exception e) {
			System.out.println("Unkonwn error");
		}
		return conn;
	}
}

//public static Connection getConnection() throws ClassNotFoundException 
//
//public JSONArray selectKickList() throws ClassNotFoundException 	
//
//public String insertKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException
//	
//public String updateKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException
//	
//public String deleteKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException
//	
//public String insertLog(String ip,String user_id) throws ClassNotFoundException, ParseException
//	
//public void contextInitialized(ServletContextEvent arg0)
//
//public class ThreadEX implements Runnable
//
//public static void main(String[] args) throws ParseException, ClassNotFoundException
//
//public scrapeData() throws ParseException, ClassNotFoundException
//
//public static void filter_renewal_Data() throws ClassNotFoundException, ParseException
//
//public static JSONArray swing_list() throws ParseException, ClassNotFoundException
//
//private static JSONArray swing_battery(String kick_list) throws ParseException
//
//public static JSONArray kickgoing_list() throws ParseException, ClassNotFoundException
//
//public static JSONArray xingxing_list() throws ParseException, ClassNotFoundException 
//
//public static JSONArray gogossing_list() throws ParseException, ClassNotFoundException 
