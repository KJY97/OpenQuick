package com.example.openquick.Login;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpConnection extends Thread {

    String param;
    String _url;
    private String result;

    public HttpConnection(String param, String _url) {
        this.param = param;
        this._url = _url;
    }

    public void run() {
        try {
            URL url = new URL(_url);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            OutputStream os = urlConn.getOutputStream();
            param = "json=" + param;
            os.write(param.getBytes("UTF-8"));
            os.flush();
            os.close();

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {

            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                String line;
                String page = "";

                while ((line = reader.readLine()) != null) {
                    page += line;
                }

                result = page;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}