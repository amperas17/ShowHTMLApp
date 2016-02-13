package com.amperas17.showhtmlapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetHttpContent {

    public static String getContent(String path)  {

        String error="Check your url path!";
        BufferedReader reader=null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();

            if (connection.getResponseCode()==200) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }

                return (buf.toString());
            } else {
                return error;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return error;
        } catch (IOException e) {
            e.printStackTrace();
            return error;
        } catch (Exception e){
            e.printStackTrace();
            return error;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
