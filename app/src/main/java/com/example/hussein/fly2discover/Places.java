package com.example.hussein.fly2discover;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

public class Places extends Activity {
    private ArrayList<String> checkedCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeslayout);

        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        getWindow().setLayout((int)((met.widthPixels)* 0.8), (int)((met.heightPixels) * 0.8));

        checkedCategories = new ArrayList<>();
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("categories", checkedCategories);
                startActivity(intent);

            }
        });
    }

    public void onClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.museumcheck:
                if (checked)
                    checkedCategories.add("museum");
                break;
            case R.id.librarycheck:
                if (checked)
                    checkedCategories.add("library");
                break;
            case R.id.parkcheck:
                if (checked)
                    checkedCategories.add("park");
                break;
            case R.id.cinemacheck:
                if (checked)
                    checkedCategories.add("cinema");
                break;
            case R.id.zoocheck:
                if (checked)
                    checkedCategories.add("zoo");
                break;
            case R.id.universitycheck:
                if (checked)
                    checkedCategories.add("university");
                break;
            case R.id.stadiumcheck:
                if (checked)
                    checkedCategories.add("stadium");
                break;
            case R.id.embassycheck:
                if (checked)
                    checkedCategories.add("embassy");
                break;
            case R.id.airportcheck:
                if (checked)
                    checkedCategories.add("airport");
                break;
            case R.id.churchcheck:
                if (checked)
                    checkedCategories.add("church");
                break;

        }
    }

}
