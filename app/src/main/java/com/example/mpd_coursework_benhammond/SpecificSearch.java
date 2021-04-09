/*
Ben Hammond | S1709378
*/

package com.example.mpd_coursework_benhammond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.threeten.extra.LocalDateRange;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

public class SpecificSearch extends AppCompatActivity {

    public ArrayList<EarthquakeClass> earthquakeClasses = null;
    public ArrayList<EarthquakeClass> parsedArray = new ArrayList<EarthquakeClass>();

    private Button specificDate_button;
    private Button rangeDate_button;

    public String startDate;
    public String endDate;

    private int largestMag = 0;
    private int north = 0;
    private int south = 0;
    private int east = 0;
    private int west = 0;
    private int depth = 0;
    private int shallow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_search);

        Intent specific_intent = getIntent();
        //Bundle thing = specific_intent.getBundleExtra("earthquakeArray");
        earthquakeClasses = new ArrayList<EarthquakeClass>((ArrayList<EarthquakeClass>) specific_intent.getSerializableExtra("earthquakeArray"));

        specificDate_button = (Button) findViewById(R.id.layout_search_button_date_single);
        rangeDate_button = (Button) findViewById(R.id.layout_search_buttons_date_range);

        MaterialDatePicker.Builder datePicker_builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker.Builder dateRangePicker_builder = MaterialDatePicker.Builder.dateRangePicker();

        datePicker_builder.setTitleText("Select data");
        dateRangePicker_builder.setTitleText("Select date range");

        MaterialDatePicker datePicker = datePicker_builder.build();
        MaterialDatePicker dateRangePicker = dateRangePicker_builder.build();

        specificDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "SPECIFIC_DATE_PICKER");
            }
        });
        rangeDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangePicker.show(getSupportFragmentManager(), "RANGE_DATE_PICKER");
            }
        });

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                String date = datePicker.getHeaderText();


                String theDate = new SimpleDateFormat("dd/MM/yy").format(selection);
                startDate = theDate;
                endDate = theDate;

                ParseDates();
            }
        });

        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                Long date1 = selection.first;
                Long date2 = selection.second;

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy");

                calendar.setTimeInMillis(date1);
                Date start = calendar.getTime();
                calendar.setTimeInMillis(date2);
                Date end = calendar.getTime();

                startDate = outputFormat.format(start);
                endDate = outputFormat.format(end);

                ParseDates();

            }
        });
    }

    public void ParseDates()
    {
        Date oldstart = null;
        try {
            oldstart = new SimpleDateFormat("dd/MM/yy").parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date oldend = null;
        try {
            oldend = new SimpleDateFormat("dd/MM/yy").parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        LocalDate start = oldstart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = oldend.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (int i = 0; i < earthquakeClasses.size(); i++)
        {
            Date oldClassDate = earthquakeClasses.get(i).getDate();
            LocalDate theDate = oldClassDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (LocalDateRange.ofClosed(start, end).contains(theDate))
            {
                parsedArray.add(earthquakeClasses.get(i));
            }

        }

        for (int i = 0; i < parsedArray.size(); i++)
        {
            EarthquakeClass single = parsedArray.get(i);

            float highestMag = Float.valueOf(parsedArray.get(largestMag).getDescriptionMagnitude());
            float currentMag = Float.valueOf(single.getDescriptionMagnitude());

            float currentLat = Float.valueOf(single.getDescriptionLat());
            float currentLong = Float.valueOf(single.getDescriptionLong());
            float highestLat = Float.valueOf(parsedArray.get(north).getDescriptionLat());
            float highestLong = Float.valueOf(parsedArray.get(east).getDescriptionLong());
            float lowestLat = Float.valueOf(parsedArray.get(south).getDescriptionLat());
            float lowestLong = Float.valueOf(parsedArray.get(west).getDescriptionLat());

            String splitDepth = parsedArray.get(depth).getDescriptionDepth().replace(" km", "");
            String splitShallow = parsedArray.get(shallow).getDescriptionDepth().replace(" km", "");
            String splitCurrent = single.getDescriptionDepth().replace(" km", "");

            float deepest = Float.valueOf(splitDepth);
            float shallowest = Float.valueOf(splitShallow);
            float currentDepth = Float.valueOf(splitCurrent);


            if(highestMag < currentMag)
            {
                largestMag = i;
            }

            if(currentLat > highestLat){north = i;}
            else if(currentLat < lowestLat){south = i;}

            if(currentLong > highestLong){east = i;}
            else if(currentLong < lowestLong){west = i;}

            if(currentDepth > deepest){depth = i;}
            else if(currentDepth<shallowest){shallow = i;}

        }

        TextView theDeepest = (TextView)findViewById(R.id.layout_search_field_deepest);
        theDeepest.setText(parsedArray.get(depth).getDescriptionLocation() + " Date: " + parsedArray.get(depth).getDate() + " Lat: "+ parsedArray.get(depth).getDescriptionLat() +" Long: "+ parsedArray.get(depth).getDescriptionLong() + " Mag: " + parsedArray.get(depth).getDescriptionMagnitude() + " Depth: " + parsedArray.get(depth).getDescriptionDepth());
        TextView theShallowest = (TextView)findViewById(R.id.layout_search_field_shallowest);
        theShallowest.setText(parsedArray.get(shallow).getDescriptionLocation() + " Date: " + parsedArray.get(shallow).getDate() + " Lat: "+ parsedArray.get(shallow).getDescriptionLat() +" Long: "+ parsedArray.get(shallow).getDescriptionLong() + " Mag: " + parsedArray.get(shallow).getDescriptionMagnitude() + " Depth: " + parsedArray.get(shallow).getDescriptionDepth());
        TextView theLargest = (TextView)findViewById(R.id.layout_search_field_largest);
        theLargest.setText(parsedArray.get(largestMag).getDescriptionLocation() + " Date: " + parsedArray.get(largestMag).getDate() + " Lat: "+ parsedArray.get(largestMag).getDescriptionLat() +" Long: "+ parsedArray.get(largestMag).getDescriptionLong() + " Mag: " + parsedArray.get(largestMag).getDescriptionMagnitude() + " Depth: " + parsedArray.get(largestMag).getDescriptionDepth());
        TextView northest = (TextView)findViewById(R.id.layout_search_field_north);
        northest.setText(parsedArray.get(north).getDescriptionLocation() + " Date: " + parsedArray.get(north).getDate() + " Lat: "+ parsedArray.get(north).getDescriptionLat() +" Long: "+ parsedArray.get(north).getDescriptionLong() + " Mag: " + parsedArray.get(north).getDescriptionMagnitude() + " Depth: " + parsedArray.get(north).getDescriptionDepth());
        TextView southest = (TextView)findViewById(R.id.layout_search_field_south);
        southest.setText(parsedArray.get(south).getDescriptionLocation() + " Date: " + parsedArray.get(south).getDate() + " Lat: "+ parsedArray.get(south).getDescriptionLat() +" Long: "+ parsedArray.get(south).getDescriptionLong() + " Mag: " + parsedArray.get(south).getDescriptionMagnitude() + " Depth: " + parsedArray.get(south).getDescriptionDepth());
        TextView eastest = (TextView)findViewById(R.id.layout_search_field_east);
        eastest.setText(parsedArray.get(east).getDescriptionLocation() + " Date: " + parsedArray.get(east).getDate() + " Lat: "+ parsedArray.get(east).getDescriptionLat() +" Long: "+ parsedArray.get(east).getDescriptionLong() + " Mag: " + parsedArray.get(east).getDescriptionMagnitude() + " Depth: " + parsedArray.get(east).getDescriptionDepth());
        TextView westest = (TextView)findViewById(R.id.layout_search_field_west);
        westest.setText(parsedArray.get(west).getDescriptionLocation() + " Date: " + parsedArray.get(west).getDate() + " Lat: "+ parsedArray.get(west).getDescriptionLat() +" Long: "+ parsedArray.get(west).getDescriptionLong() + " Mag: " + parsedArray.get(west).getDescriptionMagnitude() + " Depth: " + parsedArray.get(west).getDescriptionDepth());

    }
}




