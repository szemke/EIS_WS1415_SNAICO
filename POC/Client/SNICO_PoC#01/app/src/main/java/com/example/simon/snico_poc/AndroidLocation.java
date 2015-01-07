package com.example.simon.snico_poc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidLocation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final GPSService mGPSService = new GPSService(AndroidLocation.this);
        mGPSService.getLocation();

        TextView lat = (TextView)findViewById(R.id.lat);
        TextView lon = (TextView)findViewById(R.id.lon);
        final TextView finalAddress = (TextView)findViewById(R.id.address);
        final TextView finalLat = lat;
        final TextView finalLon = lon;

        Button getLocationBtn = (Button) findViewById(R.id.btnLocation);
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address;
                finalLat.setText(R.string.lat);
                finalLon.setText(R.string.lon);
                finalAddress.setText(R.string.address);

                if (!mGPSService.isLocationAvailable) {

                    Toast.makeText(getApplicationContext(), "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();

                } else {
                    double latitude = mGPSService.getLatitude();
                    double longitude = mGPSService.getLongitude();
                    finalLat.append(String.valueOf(latitude));
                    finalLon.append(String.valueOf(longitude));

                    address = mGPSService.getLocationAddress();
                    finalAddress.append(address);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.android_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_poc1) {
            startActivity(new Intent(getApplicationContext(), AndroidHttpPost.class));
        }
        if (id == R.id.action_poc2) {
            startActivity(new Intent(getApplicationContext(), AndroidLocation.class));
        }
        if (id == R.id.action_poc3) {
            startActivity(new Intent(getApplicationContext(), AndroidGCM.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
