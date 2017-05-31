package com.example.hussein.fly2discover;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class Searching extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        getWindow().setLayout((int)((met.widthPixels)* 0.8), (int)((met.heightPixels) * 0.8));
    }

    public void runview(View v) throws IOException {

        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> addresses = gc.getFromLocationName(searchString, 1);

        if (addresses.size() > 0) {
            Address address = addresses.get(0);
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("lat", address.getLatitude());
                intent.putExtra("long", address.getLongitude());
                startActivity(intent);

        }

    }
}
