package com.example.hussein.fly2discover;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

// This is responsible of request and parse JSON requests
public class JsonHandler {

    public JsonHandler() {
    }

    public String ServiceToBeCalled(String URL) {
        String response = null;
        try {
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // read the returned information
            InputStream input = new BufferedInputStream(connection.getInputStream());
            response = convertResultsToString(input);
        } catch (MalformedURLException e) {
        } catch (ProtocolException e) {
        } catch (IOException e) {
        } catch (Exception e) {}
        return response;
    }

    private String convertResultsToString(InputStream inputS) {
        String line;
        BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputS));
        StringBuilder stringB = new StringBuilder();
        try {
            while ((line = bufferedR.readLine()) != null) {
                stringB.append(line).append('\n'); }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try
            {
                inputS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }}
        return stringB.toString();
    }
}
