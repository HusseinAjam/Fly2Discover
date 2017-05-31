package com.example.hussein.fly2discover;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.abs;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private ImageView mPointer;
    private Button button;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean buttonchecker = false;
    private String TAG = MapsActivity.class.getSimpleName();
    private LatLng startingPoin;
    private MapStyleOptions styleOptions;
    private String [] SearchCtegories;
    private String username =  "";
    private SeekBar tiltAndZoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Fix screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mPointer = (ImageView) findViewById(R.id.plain);

        // Accelerate Button on touch listener, the button will work as long as you are pressing it
        button = (Button) findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonchecker = true;
                        return true;
                    case MotionEvent.ACTION_UP:
                        buttonchecker = false;
                        spinINfinitly();
                        return true;
                }
                return false;
            }
        });
        spinINfinitly();

        // Assign default search categorie
        SearchCtegories = new String[10];
        SearchCtegories[0] = new String("museum");
        SearchCtegories[1] = new String("church");
        SearchCtegories[2] = new String("university");
        SearchCtegories[3] = new String("library");
        SearchCtegories[4] = new String("park");
        SearchCtegories[5] = new String("cinema");
        SearchCtegories[6] = new String("zoo");
        SearchCtegories[7] = new String("stadium");
        SearchCtegories[8] = new String("embassy");
        SearchCtegories[9] = new String("airport");


        // Set up default starting point "London"
        startingPoin = new LatLng(51.510675, -0.119199);

        // Check Intent received parameters from the Searching activity
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("lat"))
            {
                double lat, lang;
                lat = getIntent().getDoubleExtra("lat",51.510675 ); //this value is just initial value
                lang = getIntent().getDoubleExtra("long",-0.119199 );  //this value is just initial value
                startingPoin = new LatLng(lat, lang);
            }
        }

        // Check Intent received parameters from the categories selection, places activity
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("categories"))
            {
                // Set up the starting point location
                ArrayList<String> selectedCategories = new ArrayList<>(getIntent().getStringArrayListExtra("categories"));
                SearchCtegories = new String[selectedCategories.size()];
                SearchCtegories = selectedCategories.toArray(SearchCtegories);
            }
        }

        // Check Intent received parameters from the Login Activity
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("email"))
            {
                 username = getIntent().getStringExtra("email");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Assign the customised night view map style as a default style
        styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(styleOptions);

        // Delay for 1 second until the maps get ready loaded
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraPosition newCamPos = new CameraPosition(startingPoin,
                        15.5f,
                        mMap.getCameraPosition().tilt, //use the default tilt level
                        mMap.getCameraPosition().bearing); //use the default bearing level
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null); // animate view to London
                new GetContacts(startingPoin, SearchCtegories).execute();            }
        }, 1000);

        // Add listeners to markers
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Update and add new  markers if the user moved away enough
                CalculateDistance object = new CalculateDistance();
                double distanceToCheck = 0;
                distanceToCheck = object.distFrom((float)startingPoin.latitude,(float)startingPoin.longitude,
                        (float)marker.getPosition().latitude,(float) marker.getPosition().longitude);
                // Check if the user traveled 3 km from the last marker generator point
                // Every 3 Km point a way from the last starting point is a trigger to greate more marker and update the center point
                // I use this technique to illuminate the limitation of the places API which only return maximum of 20 result per request
                if(abs(distanceToCheck) > 3000)
                {
                    // Update the starting point
                    startingPoin = marker.getPosition();
                    // Generate more markers over the new radius, this AsyncTask is responsible of requesting JSON value from the
                    // Google places web api and parse it using the JSOON handler class and add them to the as markers to the map
                    new GetContacts(startingPoin, SearchCtegories).execute();
                }
                // Create object to read and write from the internal json configuration file
                InternalStorage internalObject = new InternalStorage();
                // Go through the next activity, the OnlineReferences activity to see more information and pictures
                Intent intent = new Intent(getApplicationContext(), OnlineRefrences.class);
                intent.putExtra("keyWord", marker.getTitle());

                // If this place not visited before, then add points. Otherwise will not add any points because I will leave the
                // detail of the user missing so will not find any Id to go the the Database and uddate.
                if(!internalObject.readFromFile(getApplicationContext(),marker.getTitle())) {
                    // Update location state to known by adding it to the configuration file
                    internalObject.writeToFile(marker.getTitle(), getApplicationContext());
                    //
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
               else // else the please is visited before and known so just open the activity with uploading ID.
                {
                    startActivity(intent);
                }
            }
        });

        // Listener to the seekbar for both zoom and tilt level
        tiltAndZoom = (SeekBar) findViewById(R.id.customSeekBar);
        tiltAndZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int barValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                barValue = progresValue;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               int zoomLevel = barValue + 14; // These values assumed by myself to not let the user go far backward or inward
               int tiltLevel =  barValue * 6; // These values assumed by myself to not let the user go far backward or inward
                CameraPosition newCamPos = new CameraPosition(startingPoin,
                        zoomLevel,
                        tiltLevel,
                        mMap.getCameraPosition().bearing); //use same old bearing level
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 1000, null);
            }
        });
    }

    /////////////////////////////////////////// Menu////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Select map type or Style
        switch (id) {
            case R.id.mapTypeNormal:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                styleOptions = MapStyleOptions.loadRawResourceStyle(this,
                        R.raw.map_no_style);
                mMap.setMapStyle(styleOptions);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default: break;
        }
        // Select plane logo
        switch (id) {
            case R.id.i1:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mPointer.setImageResource(R.mipmap.i1);
                break;
            case R.id.i2:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mPointer.setImageResource(R.mipmap.i2);
                break;
            case R.id.i3:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mPointer.setImageResource(R.mipmap.i3);
                break;
            case R.id.i4:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mPointer.setImageResource(R.mipmap.i4);
                break;
            case R.id.i5:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mPointer.setImageResource(R.mipmap.i5
                );
                break;
            case R.id.place:
                // click on places categories selection option
                Intent i = new Intent(getApplicationContext(), Places.class);
                startActivity(i);
                break;
            case R.id.search:
                // click on Search place option
                Intent j = new Intent(getApplicationContext(), Searching.class);
                startActivity(j);
                break;
            case R.id.score:
                // click on score and ranking details option
                Intent k = new Intent(getApplicationContext(), Score.class);
                k.putExtra("username", username);
                startActivity(k);
                break;

            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
//////////////////////////////////////End of Menus///////////////////////////////////////////////////
//////////////////////////////// Sensors Job/////////////////////////////////////////////////////////
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Round the values of accelerometers reading
        if (event.sensor == mAccelerometer) {
            float X = (float)Math.round(event.values[0]);
            float Y= (float)Math.round(event.values[1]);
            //float Z= event.values[2];
            float angle = (float) (Math.atan2(X, Y)/(Math.PI/180));

            if(!flatOrNot(event) && buttonchecker == true) //If phone state not flat and the accelerate button is pressed
            {
                movePlane(angle); // start moving the plane by angle
                moveMap (X , Y);  // start moving the map by same angle syncronisingly
            }
        }
    }

    public void movePlane (float angle)
    {
        RotateAnimation rotateAnim = new RotateAnimation(-angle, 0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(10000);
        rotateAnim.setFillAfter(true);
        mPointer.startAnimation(rotateAnim);
    }
    // this method to be called when the user is not pressing the accelerate button
    public void spinINfinitly()
    {
        RotateAnimation spiner = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        spiner.setInterpolator(new LinearInterpolator());
        spiner.setDuration(10000);
        spiner.setRepeatCount(Animation.INFINITE);
        mPointer.startAnimation(spiner);
    }
    public void moveMap (float x , float y)
    {
        mMap.animateCamera(CameraUpdateFactory.scrollBy(y, -x), 1, null);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    // To check the state of the phone if it is flat or tilit by angle
    public boolean flatOrNot(SensorEvent event)
    {
        float[] g = new float[3];
        g = event.values.clone();
        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);
        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        if (inclination < 18 || inclination > 180)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /////////////////////// End of Sensors Methods /////////////////////////////////////////////////
    /////////////////////////////////For Places and Markers generator class ////////////////////////

    private class GetContacts extends AsyncTask<LatLng, Void, Void> {
        private LatLng distination;
        private String Categories [];
        private ArrayList<LatLng> contactList;
        private ArrayList<String> names;
        private ArrayList<String> types;

        public GetContacts(LatLng target, String[] caterories) {
            distination = target;
            Categories = caterories;
            contactList = new ArrayList<>();
            names = new ArrayList<>();
            types = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(LatLng... arg0) {
            JsonHandler sh = new JsonHandler();
            String [] jsonStr = new String[Categories.length];

            for(int j = 0 ;j < Categories.length; j++)
            {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+distination.latitude+","+distination.longitude+"&radius=5000&types="+Categories[j]+"&key=AIzaSyBXvTFH5qq65lujqJJD7mS45JXlzM0xZoY";
                jsonStr[j] = sh.ServiceToBeCalled(url);
            }

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    for (int j = 0; j < jsonStr.length; j++)
                    {
                        JSONObject jsonObj = new JSONObject(jsonStr[j]);
                        // Getting JSON Array node
                        JSONArray results = jsonObj.getJSONArray("results");
                        // looping through All places
                        for (int i = 0; i < results.length(); i++)
                        {
                            JSONObject jobject = results.getJSONObject(i);
                            String name = jobject.getString("name");
                            JSONObject geometry = jobject.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            LatLng address = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            contactList.add(address);
                            names.add(name);
                            types.add(Categories[j]);
                        }
                    }
                }

                catch (final JSONException e) {
                    // Thread to display toast message
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Error while parsing JSON: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {
                // Thread to display toast message
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Can't find JSON file",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            InternalStorage internalObject = new InternalStorage();
            for (int i = 0; i < contactList.size(); i++) {
                // Check if place been visited before
                if( internalObject.readFromFile(getApplicationContext(),names.get(i)))
                {
                    // add customise marker icon indicates that you have visited this place before
                    mMap.addMarker(new MarkerOptions()
                            .position(contactList.get(i))
                            .title(names.get(i))
                            .snippet("type:"+ types.get(i))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.knownmarker)));
                }
                else
                {
                    // place not been visited before so just add the default marker
                    mMap.addMarker(new MarkerOptions()
                            .position(contactList.get(i))
                            .title(names.get(i))
                            .snippet("type:"+ types.get(i)));
                }
            }
        }
    }
}
