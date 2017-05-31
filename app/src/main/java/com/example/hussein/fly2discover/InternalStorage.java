package com.example.hussein.fly2discover;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Hussein on 4/1/2017.
 */

public class InternalStorage {

    public void writeToFile(String value,Context context) {
        try {
            value =value + "\n";
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_APPEND));
            writer.append(value);
            writer.close();
        }
        catch (IOException e) {
        }
    }

    public boolean readFromFile(Context context, String title) {
        boolean checker = false;
        try {
            InputStream input = context.openFileInput("config.txt");

            if ( input != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                BufferedReader BReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = BReader.readLine()) != null ) {
                    if(receiveString.equals(title)) {
                        checker = true;
                        break;
                    }
                }
                input.close();
            }
        }
        catch (FileNotFoundException e) {
         } catch (IOException e) {
         }
        return checker;
    }
}
