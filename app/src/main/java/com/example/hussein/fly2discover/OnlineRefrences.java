package com.example.hussein.fly2discover;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.util.ArrayList;
import java.util.Random;

import static android.R.id.list;

public class OnlineRefrences extends AppCompatActivity {

    private WebView myWebView;
    private String keyWord;
    private String points = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_online_refrences);


        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("keyWord"))
            {
                keyWord =  getIntent().getStringExtra("keyWord");
            }
        }
        myWebView.loadUrl("https://en.wikipedia.org/wiki/"+keyWord);

        // Get the username, If this activity doesn't recive username, it means this place been visited before and
        // no new points to add, and no need to execute the android service.
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("username"))
            {
                String username = getIntent().getStringExtra("username");
                // Set some random  points to the users score (between 0 to 10)
                Random rand = new Random();
                points = (rand.nextInt(11))+"";
                OnlineRefrences.updateUserScore obj = new OnlineRefrences.updateUserScore(username);
                obj.execute((Void) null);
                Toast.makeText(this, points + " points added!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.onlinemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Select map type or Style
        switch (id) {
            case R.id.instagram:
                if (keyWord.equals("The British Museum"))
                myWebView.loadUrl("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/InstegramAPI.html");
                else
                    myWebView.loadUrl("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/noImagesAvailable.html");
                break;
            case R.id.wikipedia:
                myWebView.loadUrl("https://en.wikipedia.org/wiki/"+keyWord);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
   // Update User's Score in the Database
    public class updateUserScore extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        updateUserScore(String email) {
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

            if(updateYourScore(mEmail))
            {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                // do somethin
            }
        }

        @Override
        protected void onCancelled() {
        }

        public boolean updateYourScore(String user_name) {
            try {
                URL url = new URL("http://194.81.104.22/~13432608/MobileDevelopmentAssignment/updateYourScore.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                //Send data
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("points","UTF-8")+"="+URLEncoder.encode(points,"UTF-8");
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
                if(result.equals("OK"))
                {
                    return true;
                }
                else
                {
                    return false;
                }

            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        }

}
