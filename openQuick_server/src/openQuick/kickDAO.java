package openQuick;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class kickDAO {
	@SuppressWarnings("unchecked")
	public JSONArray selectKickList() throws ClassNotFoundException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();

		try {
			String sql = "select * from kickboard";

			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("kickb_imei", rs.getString("kickb_imei"));
				jsonObject.put("kickb_com", rs.getString("kickboard_com__kcom_name"));
				jsonObject.put("kickb_lat", rs.getString("kickb_lat"));
				jsonObject.put("kickb_lng", rs.getString("kickb_lng"));
				jsonObject.put("kickb_bat", rs.getString("kickb_bat"));
				jsonObject.put("kickb_distance", rs.getString("kickb_distance"));
				jsonArray.add(jsonObject);
				jsonObject = null;

			}
		} catch (SQLException sqle) {
			System.out.println("sql err : " + sqle.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return jsonArray;
	}

	public String insertKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		String rst = "success";

		try {
			conn = DBConnection.getConnection();

			for (int i = 0; i < kick_list.size(); i++) {
				jsonObject = (JSONObject) kick_list.get(i);
				if (jsonObject.get("useStatus").equals("y")) {
					String sql = "insert INTO kickboard(kickb_imei, kickboard_com__kcom_name, kickb_lat, kickb_lng, kickb_bat, kickb_distance)"
							+ "values (?, ?, ?, ?, ?, ?)";

					pstmt = conn.prepareStatement(sql);

					pstmt.setString(1, (jsonObject.get("kickb_imei")).toString());
					pstmt.setString(2, (jsonObject.get("kickb_com")).toString());
					pstmt.setString(3, (jsonObject.get("kickb_lat")).toString());
					pstmt.setString(4, (jsonObject.get("kickb_lng")).toString());
					pstmt.setString(5, (jsonObject.get("kickb_bat")).toString());
					pstmt.setString(6, (jsonObject.get("kickb_distance")).toString());

					pstmt.executeUpdate();
				}
			}

		} catch (SQLException sqle) {
			System.out.println("sql err : " + sqle.getMessage());
			rst = "fail";
		} finally {
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
	public String updateKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		String rst = "success";

		try {
			conn = DBConnection.getConnection();

			for (int i = 0; i < kick_list.size(); i++) {
				jsonObject = (JSONObject) kick_list.get(i);
				if (jsonObject.get("useStatus").equals("y")) {
					String sql = "UPDATE kickboard SET kickb_lng = ?, kickb_lat = ?, kickb_bat = ?, kickb_distance = ? WHERE kickb_imei = ?";

					pstmt = conn.prepareStatement(sql);

					pstmt.setString(1, (jsonObject.get("kickb_lng")).toString()); 
					pstmt.setString(2, (jsonObject.get("kickb_lat")).toString()); 
					pstmt.setString(3, (jsonObject.get("kickb_bat")).toString()); 
					pstmt.setString(4, (jsonObject.get("kickb_distance")).toString());
					pstmt.setString(5, (jsonObject.get("kickb_imei")).toString());

					pstmt.executeUpdate();
				}
			}

		} catch (SQLException sqle) {
			System.out.println("update sql err : " + sqle.getMessage());
			rst = "fail";
		} finally {
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

	public String deleteKick_List(JSONArray kick_list) throws ClassNotFoundException, ParseException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		String rst = "success";

		try {
			conn = DBConnection.getConnection();
			for (int i = 0; i < kick_list.size(); i++) {
				String sql = "DELETE FROM kickboard WHERE kickb_imei=?";

				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, (kick_list.get(i)).toString());
				pstmt.executeUpdate();
			}

		} catch (SQLException sqle) {
			System.out.println("sql err : " + sqle.getMessage());
			rst = "fail";
		} finally {
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
