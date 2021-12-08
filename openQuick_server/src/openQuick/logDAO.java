package openQuick;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
public class logDAO {

	public String insertLog(String ip,String user_id) throws ClassNotFoundException, ParseException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String rst = "success";

		try {
			conn = DBConnection.getConnection();

			String sql = "insert INTO log(log_request_date, log_user_id, log_local_ip, log_request_first_line, log_response_code)"
					+ "values (NOW(), ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);
			// System.out.println("jsonObject" + jsonObject);
			pstmt.setString(1, user_id); // kickb_com
			pstmt.setString(2, ip); // kickb_lat
			pstmt.setString(3, "test"); // kickb_lng
			pstmt.setString(4, "test"); // kickb_bat
			
			pstmt.executeUpdate();
		} catch(SQLException sqle) {
			System.out.println("sql err : " + sqle.getMessage());
			rst = "fail";
		}finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				rst = "fail";
			}
		}
		return rst;
	}
}
	
