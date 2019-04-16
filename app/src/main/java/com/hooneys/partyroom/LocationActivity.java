package com.hooneys.partyroom;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationActivity extends AppCompatActivity {
    private EditText locationEdit;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationEdit = (EditText) findViewById(R.id.location_edit_text);
        send = (Button) findViewById(R.id.send_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = getNameToLatLng(locationEdit.getText().toString());
                if(latLng!= null){
                    Log.d("Location", "location : " + latLng.latitude + " / " + latLng.longitude);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("location_lat", (float) latLng.latitude);
                    intent.putExtra("location_lon", (float) latLng.longitude);
                    intent.putExtra("location_name", locationEdit.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    //Null
                    Toast.makeText(getApplicationContext(), "주소를 다시 확인해주세요.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private LatLng getNameToLatLng(String spot){
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        Address address = null;
        try{
            list = geocoder.getFromLocationName(spot, 1);
            address = list.get(0);
            if(address != null){
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
