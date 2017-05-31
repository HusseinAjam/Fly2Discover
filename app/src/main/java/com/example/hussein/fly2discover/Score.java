package com.example.hussein.fly2discover;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Score extends Activity {
private String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        getWindow().setLayout((int)((met.widthPixels)* 0.8), (int)((met.heightPixels) * 0.8));

        // Get the username
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("username"))
            {
                username = getIntent().getStringExtra("username");
                UserScoreCheck obj = new Score.UserScoreCheck(username);
                obj.execute((Void) null);

            }
        }
    }


    public class UserScoreCheck extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private String YourScore = "";
        private String YourRanking = "";
        private String BestScore = "";

        UserScoreCheck(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            if(checkYourScore(mEmail))
            {
                if(checkYourRanking(YourScore))
                {
                    if(checkBestScore(mEmail))
                    return true;
                }
                return false;
            }
            // Or create a new account

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                TextView textView = (TextView) findViewById(R.id.score);
                textView.setText(YourScore+"");

                TextView textView2 = (TextView) findViewById(R.id.ranking);
                textView2.setText(YourRanking+"");

                TextView textView3 = (TextView) findViewById(R.id.bestscore);
                textView3.setText(BestScore+"");

                TextView textView4 = (TextView) findViewById(R.id.username);
                textView4.setText(mEmail);
            }
        }

        @Override
        protected void onCancelled() {
        }

        public boolean checkYourScore(String user_name) {
            try {
                URL url = new URL("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/getYourScore.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                //Send data
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Read feedback
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                YourScore = result;
                return true;

            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }

        public boolean checkYourRanking(String score) {
            try {
                URL url = new URL("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/getYourRanking.php");
                HttpURLConnection httpURLConnection1 = (HttpURLConnection) url.openConnection();
                httpURLConnection1.setRequestMethod("POST");
                httpURLConnection1.setDoOutput(true);
                httpURLConnection1.setDoInput(true);
                //Send data
                OutputStream outputStream1 = httpURLConnection1.getOutputStream();
                BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(outputStream1, "UTF-8"));
                String post_data = URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
                bufferedWriter1.write(post_data);
                bufferedWriter1.flush();
                bufferedWriter1.close();
                outputStream1.close();
                //Read feedback
                InputStream inputStream1 = httpURLConnection1.getInputStream();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1, "iso-8859-1"));
                String result1 = "";
                String line1 = "";
                while ((line1 = bufferedReader1.readLine()) != null) {
                    result1 += line1;
                }
                bufferedReader1.close();
                inputStream1.close();
                httpURLConnection1.disconnect();

                YourRanking = result1;
                return true;

            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }

        public boolean checkBestScore(String user_name) {
            try {
                URL url = new URL("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/getBestScore.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                //Send data
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Recive feedback
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                BestScore = result;
                return true;

            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }

    }
}
