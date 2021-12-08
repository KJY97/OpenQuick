package openQuick;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class scrapeData implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("thread start!");
		try {
			ThreadEX threadex = new ThreadEX();
			Thread thread1 = new Thread(threadex, "A");

			thread1.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ThreadEX implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					filter_renewal_Data();
				} catch (ClassNotFoundException | ParseException e1) {
					System.out.println(e1.getMessage() + e1.getCause());
				}
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage() + e.getCause());
				}
			}
		}
	}

	public static void main(String[] args) throws ParseException, ClassNotFoundException {
	}

	public scrapeData() throws ParseException, ClassNotFoundException {
	}

	public static void filter_renewal_Data() throws ClassNotFoundException, ParseException {
		JSONArray xingxing_list = xingxing_list();
		JSONArray kickgoing_list = kickgoing_list();
		JSONArray swing_list = swing_list();
		JSONArray gogossing_list = gogossing_list();

		try {
			JSONArray all_list = kickgoing_list;
			for (int i = 0; i < xingxing_list.size(); i++) {
				all_list.add((JSONObject) xingxing_list.get(i));
			}
			for (int i = 0; i < gogossing_list.size(); i++) {
				all_list.add((JSONObject) gogossing_list.get(i));
			}
			for (int i = 0; i < swing_list.size(); i++) {
				all_list.add((JSONObject) swing_list.get(i));
			}

			kickDAO kdao = new kickDAO();
			JSONArray db_list = kdao.selectKickList();

			JSONArray delete_list = new JSONArray();
			int delete_flag = 0;
			for (int i = 0; i < db_list.size(); i++) {
				delete_flag = 0;
				for (int j = 0; j < all_list.size(); j++) {
					if ((((JSONObject) (db_list.get(i))).get("kickb_imei").toString())
							.equals(((JSONObject) (all_list.get(j))).get("kickb_imei").toString())) {
						delete_flag = 1;
					}
				}
				if (delete_flag == 0)
					delete_list.add(((JSONObject) (db_list.get(i))).get("kickb_imei"));
			}
			kdao.deleteKick_List(delete_list);

			JSONArray update_list = new JSONArray();
			for (int i = 0; i < db_list.size(); i++) {
				for (int j = 0; j < all_list.size(); j++) {
					if ((((JSONObject) (db_list.get(i))).get("kickb_imei").toString())
							.equals((((JSONObject) (all_list.get(j))).get("kickb_imei")).toString())) {
						if (!(((JSONObject) (db_list.get(i))).get("kickb_lng").toString())
								.equals(((JSONObject) (all_list.get(j))).get("kickb_lng").toString())
								|| !(((JSONObject) (db_list.get(i))).get("kickb_lat").toString())
										.equals(((JSONObject) (all_list.get(j))).get("kickb_lat").toString())) {
							update_list.add((JSONObject) (all_list.get(j)));
						}
					}
				}
			}
			kdao.updateKick_List(update_list);

			JSONArray insert_list = new JSONArray();
			int insert_flag = 0;
			for (int i = 0; i < all_list.size(); i++) {
				insert_flag = 0;
				for (int j = 0; j < db_list.size(); j++) {
					if ((((JSONObject) (all_list.get(i))).get("kickb_imei").toString())
							.equals(((JSONObject) (db_list.get(j))).get("kickb_imei").toString())) {
						insert_flag = 1;
					}
				}
				if (insert_flag == 0)
					insert_list.add(((JSONObject) (all_list.get(i))));
			}
			kdao.insertKick_List(insert_list);
		} catch (Exception e) {
			System.out.println("err :" + e.getMessage() + "/" + e.getCause());
		}
	}

	public static JSONArray swing_list() throws ParseException, ClassNotFoundException {
		HttpURLConnection httpURLConnection = null;
		URL url;

		String kick_list = null;
		String userLat = "37.49765";
		String userLng = "127.054108";
		String range = "500000";

		try {
			url = new URL("https://restful.theswing.co.kr/ride/scooter_list");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "Application/json");
			JSONObject jSONObject = new JSONObject();

			jSONObject.put("lat", userLat);
			jSONObject.put("lng", userLng);
			jSONObject.put("range", range);

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
			outputStreamWriter.write(jSONObject.toString());
			outputStreamWriter.flush();
			StringBuilder stringBuilder1 = new StringBuilder();
			if (httpURLConnection.getResponseCode() == 200) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream()));
				while (true) {
					String str2 = bufferedReader.readLine();
					if (str2 != null) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(str2);
						stringBuilder.append("\n");
						stringBuilder1.append(stringBuilder.toString());
						kick_list = str2;
						continue;
					}
					break;
				}
				bufferedReader.close();
			}

		} catch (Exception paramVarArgs) {
			System.out.println(paramVarArgs.getMessage());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}

		return swing_battery(kick_list);

	}

	@SuppressWarnings("unchecked")
	private static JSONArray swing_battery(String kick_list) throws ParseException {
		HttpURLConnection httpURLConnection = null;
		URL url;
		JSONArray unFormat_JA = new JSONArray();
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject JO = (JSONObject) parser.parse(kick_list);
			JSONArray JA = (JSONArray) JO.get("data");
			for (int i = 0; i < JA.size(); i++) {
				url = new URL("https://restful.theswing.co.kr/ride/scooter_info");
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
				httpURLConnection.setRequestProperty("Content-Type", "Application/json");
				JSONObject jSONObject = new JSONObject();

				jSONObject.put("imei", ((JSONObject) (JA.get(i))).get("imei"));
				jSONObject.put("distance", "-1");

				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
				outputStreamWriter.write(jSONObject.toString());
				outputStreamWriter.flush();
				StringBuilder stringBuilder1 = new StringBuilder();
				if (httpURLConnection.getResponseCode() == 200) {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpURLConnection.getInputStream()));
					while (true) {
						String str2 = bufferedReader.readLine();
						if (str2 != null) {
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append(str2);
							stringBuilder.append("\n");
							stringBuilder1.append(stringBuilder.toString());

							unFormat_JA.add(((JSONObject) parser.parse(str2)).get("data"));
							continue;
						}
						break;
					}
					bufferedReader.close();
				}
			}
		} catch (Exception paramVarArgs) {
			System.out.println("paramVarArgs2 : " + paramVarArgs.getMessage());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		// FORMAT
		JSONObject jsonObject = new JSONObject();
		JSONArray result_JA = new JSONArray();
		for (int i = 0; i < unFormat_JA.size(); i++) {
			jsonObject = (JSONObject) unFormat_JA.get(i);

			JSONObject put_JO = new JSONObject();
			put_JO.put("kickb_imei", (jsonObject.get("short_imei")).toString());
			put_JO.put("kickb_com", "swing");
			put_JO.put("kickb_lat", (jsonObject.get("lat")).toString());
			put_JO.put("kickb_lng", (jsonObject.get("lng")).toString());
			put_JO.put("kickb_bat", (jsonObject.get("left_battery")).toString());
			put_JO.put("kickb_distance", (jsonObject.get("possible_movement_distance")).toString());
			put_JO.put("useStatus", "y");
			result_JA.add(put_JO);
			put_JO = null;
		}
		return result_JA;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static JSONArray kickgoing_list() throws ParseException, ClassNotFoundException {
		HttpURLConnection httpURLConnection = null;
		URL url;
		String kick_list = null;
		int range = 1;

		try {
			url = new URL("https://api.kickgoing.io/v2/kickscooters/ready/list?lat=37.49765&lng=127.054108");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "Application/json");

			StringBuilder stringBuilder1 = new StringBuilder();
			if (httpURLConnection.getResponseCode() == 200) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream()));
				while (true) {
					String str2 = bufferedReader.readLine();
					if (str2 != null) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(str2);
						stringBuilder.append("\n");
						stringBuilder1.append(stringBuilder.toString());
						kick_list = str2;
						continue;
					}
					break;
				}
				bufferedReader.close();
			}

		} catch (Exception paramVarArgs) {
			System.out.println("paramVarArgs : " + paramVarArgs.getMessage());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}

		JSONArray format_JA = new JSONArray();
		try {
		JSONParser parser = new JSONParser();
		JSONObject JO_list = (JSONObject) parser.parse(kick_list);

		JSONObject jsonObject = new JSONObject();
		JSONArray result_JA = new JSONArray();

		result_JA = (JSONArray) JO_list.get("kickscooters");
		for (int i = 0; i < result_JA.size(); i++) {
			jsonObject = (JSONObject) result_JA.get(i);
			JSONObject put_JO = new JSONObject();
			put_JO.put("kickb_imei", jsonObject.get("serial_number").toString());
			put_JO.put("kickb_com", "kickgoing");
			put_JO.put("kickb_lat", (jsonObject.get("lat")).toString());
			put_JO.put("kickb_lng", (jsonObject.get("lng")).toString());
			put_JO.put("kickb_bat", (jsonObject.get("battery_rate")).toString());
			put_JO.put("kickb_distance", "-1");
			put_JO.put("useStatus", "y");

			format_JA.add(put_JO);
			put_JO = null;
		}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return format_JA;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray xingxing_list() throws ParseException, ClassNotFoundException {
		HttpURLConnection httpURLConnection = null;
		URL url;
		String kick_list = null;
		JSONArray result_JA = new JSONArray();

		try {
			url = new URL("https://api.honeybees.co.kr/api-xingxing/v1/scootersforapp");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("content-encoding", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImF1dGhJZCI6ImhvbmV5YmVlcyIsInVzZXJJZCI6ImhvbmV5YmVlcyIsInJvbGVzIjpbImFwcHhpbmd4aW5nIl19LCJpYXQiOjE1NTUzMzQ3MjMsImV4cCI6ODY1NTU1MzM0NzIzfQ.br5PT0s_vkFFs24Dxv0acPeQizg_TpXtkdEaWRgkSMw");

			StringBuilder stringBuilder1 = new StringBuilder();
			if (httpURLConnection.getResponseCode() == 200) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream()));
				while (true) {
					String str2 = bufferedReader.readLine();
					if (str2 != null) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(str2);
						stringBuilder.append("\n");
						stringBuilder1.append(stringBuilder.toString());
						kick_list = str2;
						continue;
					}
					break;
				}
				bufferedReader.close();
			}

		} catch (Exception paramVarArgs) {
			System.out.println("paramVarArgs : " + paramVarArgs.getMessage());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		try {
			JSONParser parser = new JSONParser();
			JSONObject JO_list = (JSONObject) parser.parse(kick_list);
			JSONObject jo_temp = new JSONObject();
			JSONObject jsonObject = new JSONObject();

			JSONArray format_list = (JSONArray) JO_list.get("scooters");
			for (int i = 0; i < format_list.size(); i++) {
				jsonObject = (JSONObject) format_list.get(i);
				jo_temp = (JSONObject) jsonObject.get("deviceStatus");

				JSONObject put_JO = new JSONObject();
				put_JO.put("kickb_imei", (jsonObject.get("_id")).toString());
				put_JO.put("kickb_com", "xingxing");
				put_JO.put("kickb_lat",
						(((JSONArray) (((JSONObject) (jo_temp.get("location"))).get("coordinates"))).get(1))
								.toString());
				put_JO.put("kickb_lng",
						(((JSONArray) (((JSONObject) (jo_temp.get("location"))).get("coordinates"))).get(0))
								.toString());
				put_JO.put("kickb_bat", (jo_temp.get("battery")).toString());
				put_JO.put("kickb_distance", "-1");
				if (jsonObject.get("useStatus").equals("AVAILABLE"))
					put_JO.put("useStatus", "y");
				else
					put_JO.put("useStatus", "n");
				result_JA.add(put_JO);
				put_JO = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result_JA;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray gogossing_list() throws ParseException, ClassNotFoundException {
		HttpURLConnection httpURLConnection = null;
		URL url;
		String kick_list = null;
		JSONArray result_JA = new JSONArray();

		try {
			url = new URL("https://api.gogo-ssing.com/ss/api/mob/search.do?type=scooter&latSW=37&lngSW=126&latNE=38&lngNE=128");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("content-encoding", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			StringBuilder stringBuilder1 = new StringBuilder();
			if (httpURLConnection.getResponseCode() == 200) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream()));
				while (true) {
					String str2 = bufferedReader.readLine();
					if (str2 != null) {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(str2);
						stringBuilder.append("\n");
						stringBuilder1.append(stringBuilder.toString());
						kick_list = str2;
						continue;
					}
					break;
				}
				bufferedReader.close();
			}

		} catch (Exception paramVarArgs) {
			System.out.println("paramVarArgs : " + paramVarArgs.getMessage());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		try {
			JSONParser parser = new JSONParser();
			JSONObject JO_list = (JSONObject) parser.parse(kick_list);
			JSONObject jo_temp = new JSONObject();
			JSONArray format_list = new JSONArray();
			JSONObject jsonObject = new JSONObject();

			jo_temp = (JSONObject) JO_list.get("payload");
			format_list = (JSONArray) jo_temp.get("mobList");
			
			for (int i = 0; i < format_list.size(); i++) {
				jsonObject = (JSONObject) format_list.get(i);

				JSONObject put_JO = new JSONObject();
				put_JO.put("kickb_imei", (jsonObject.get("SERIAL_NO")).toString());
				put_JO.put("kickb_com", "gogossing");
				put_JO.put("kickb_lat", (jsonObject.get("LAT")).toString());
				put_JO.put("kickb_lng", (jsonObject.get("LON")).toString());
				put_JO.put("kickb_bat", (jsonObject.get("BATTERY_PER")).toString());
				put_JO.put("kickb_distance", "-1");
				put_JO.put("useStatus", "y");
				
				result_JA.add(put_JO);
				put_JO = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result_JA;
	}
	

}
