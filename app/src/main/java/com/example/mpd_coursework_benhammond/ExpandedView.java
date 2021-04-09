/*
Ben Hammond | S1709378
*/

package com.example.mpd_coursework_benhammond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExpandedView extends AppCompatActivity {

    private GoogleMap map;
    private float _lat;
    private float _long;
    private String _title;
    private float _mag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expanded_view);

        Intent expanded_intent = getIntent();
        EarthquakeClass earth_single = (EarthquakeClass)expanded_intent.getSerializableExtra("single");

        TextView location = (TextView)findViewById(R.id.expanded_title);
        location.setText(earth_single.getDescriptionLocation() + "\n Lat/Long: " + earth_single.getDescriptionLat() + ", " + earth_single.getDescriptionLong());

        TextView mag = (TextView)findViewById(R.id.expanded_mag);
        mag.setText("Magnitude: " + earth_single.getDescriptionMagnitude());

        TextView depth = (TextView)findViewById(R.id.expanded_depth);
        depth.setText("Depth: " + earth_single.getDescriptionDepth());

        TextView date = (TextView)findViewById(R.id.expanded_date);
        date.setText("Date: " + earth_single.getDateFormatted());

        TextView time = (TextView)findViewById(R.id.expanded_time);
        Date theDate = earth_single.getDate();
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        String theString = currentFormat.format(theDate);
        time.setText("Time: "+theString);

        _lat = Float.valueOf(earth_single.getDescriptionLat());
        _long = Float.valueOf(earth_single.getDescriptionLong());
        _title = earth_single.getDescriptionLocation();
        _mag = Float.valueOf(earth_single.getDescriptionMagnitude());
        SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this::onMapReady);
    }

    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);



        LatLng latLong = new LatLng(_lat,_long);
       //map.addMarker(new MarkerOptions().position(latLong).title(_title), );
        MarkerOptions options = new MarkerOptions().position(latLong).title(_title);
        if(_mag >= 0 && _mag <2){options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));}
        if(_mag >= 2 && _mag < 6){options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));}
        if(_mag >= 6){options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));}

        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLong));
    }
}