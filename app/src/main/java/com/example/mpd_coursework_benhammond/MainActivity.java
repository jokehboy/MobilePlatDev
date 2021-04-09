/*
Ben Hammond | S1709378
*/
package com.example.mpd_coursework_benhammond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

//import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;


//import org.me.gcu.equakestartercode.R;

public class MainActivity extends AppCompatActivity implements OnClickListener
{

    private Button startButton;
    private Button specifiedSearchButton;
    private String result = "";
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    ListView displayListView;
    public LinkedList<EarthquakeClass> daList = null;
    public MySimpleArrayAdapter theArrayAdapter = null;
    ListView overviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        daList = new LinkedList<EarthquakeClass>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MyTag","in onCreate");
        // Set up the raw links to the graphical components

        theArrayAdapter = new MySimpleArrayAdapter( this, 1, daList);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        specifiedSearchButton = (Button)findViewById(R.id.specific_search);
        specifiedSearchButton.setOnClickListener(this::specificSearch);
        Log.e("MyTag","after startButton");
        startProgress();




        // More Code goes here
    }

    public void specificSearch(View aview)
    {
        Intent specific_intent = new Intent(getApplicationContext(), SpecificSearch.class);
        Bundle thing = new Bundle();
        thing.putSerializable("LINKEDLIST",(Serializable)daList);
        specific_intent.putExtra("earthquakeArray", daList);
        startActivity(specific_intent);
    }
    public void onClick(View aview)
    {
        Log.e("MyTag","in onClick");
        displayListView = (ListView)findViewById(R.id.listOverview);
        displayListView.setAdapter(theArrayAdapter);
        //startProgress();

        displayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String funnyString = Integer.toString(position);
                Log.e("Click Pos",funnyString);
                EarthquakeClass earth_single = theArrayAdapter.getItem(position);
                Intent expanded_intent = new Intent(getApplicationContext(), ExpandedView.class);
                expanded_intent.putExtra("single", earth_single);
                startActivity(expanded_intent);
            }
        });
        Log.e("MyTag","after startProgress");
    }




    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //



    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            //Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //Log.e("MyTag","after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    //Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //
            EarthquakeClass widget = null;
            LinkedList <EarthquakeClass> alist = null;
            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( result ) );
                int eventType = xpp.getEventType();
                boolean insideItem = false;
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    String theTag = xpp.getName();
                    // Found a start tag
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        // Check which Tag we have
                        if (theTag.equalsIgnoreCase("channel"))
                        {
                            alist  = new LinkedList<EarthquakeClass>();
                        }
                        else
                        if (theTag.equalsIgnoreCase("item"))
                        {
                            //Log.e("MyTag","Item Start Tag found");
                            widget = new EarthquakeClass();
                            insideItem = true;
                        }

                        if(insideItem)
                        {
                            if (theTag.equalsIgnoreCase("title"))
                            {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                                //Log.e("MyTag","Title is " + temp);
                                widget.setTitle(temp);
                            }
                            else// Check which Tag we have
                                if (theTag.equalsIgnoreCase("description"))
                                {
                                    // Now just get the associated text
                                    String temp = xpp.nextText();
                                    // Do something with text
                                    //Log.e("MyTag","Description is " + temp);
                                    String[] words = temp.split(";");

                                    for(int i = 0; i < words.length; i++)
                                    {
                                        String fullString = words[i];
                                        String seperatedString;
                                        if(i==0)
                                        {
                                            seperatedString = fullString.replace("Origin date/time: ","");
                                            widget.setDescriptionOriginDate(seperatedString);
                                            //Log.e("MyTag","origin is " + seperatedString);
                                        }
                                        if(i==1)
                                        {
                                            seperatedString = fullString.replace("Location:","");
                                            widget.setDescriptionLocation(seperatedString);
                                            //Log.e("MyTag","location is " + seperatedString);
                                        }
                                        if(i==2)
                                        {
                                            seperatedString = fullString.replace("Lat/long:","");
                                            String[] latLong = seperatedString.split(",");

                                            widget.setDescriptionLat(latLong[0]);
                                            widget.setDescriptionLong(latLong[1]);
                                            // Log.e("MyTag","lat is " + latLong[0]);
                                            //Log.e("MyTag","long is " + latLong[1]);
                                        }
                                        if(i==3)
                                        {
                                            seperatedString = fullString.replace("Depth:","");
                                            widget.setDescriptionDepth(seperatedString);
                                            //Log.e("MyTag","depth is " + seperatedString);
                                        }
                                        if(i==4)
                                        {
                                            seperatedString = fullString.replace("Magnitude:","");
                                            seperatedString = seperatedString.replace(" ", "");
                                            widget.setDescriptionMagnitude(seperatedString);

                                            //Log.e("MyTag","magnitude is " + seperatedString);
                                        }




                                    }
                                    widget.setDescription(temp);
                                }
                                else// Check which Tag we have
                                    if (theTag.equalsIgnoreCase("link"))
                                    {
                                        // Now just get the associated text
                                        String temp = xpp.nextText();
                                        // Do something with text
                                        //Log.e("MyTag","Link is " + temp);
                                        widget.setLink(temp);
                                    }
                                    else// Check which Tag we have
                                        if (theTag.equalsIgnoreCase("pubDate"))
                                        {
                                            // Now just get the associated text
                                            String temp = xpp.nextText();
                                            // Do something with text
                                            //Log.e("MyTag","Publish date is " + temp);
                                            //widget.setDate(temp);
                                            SimpleDateFormat currentFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);

                                            try {
                                                Date date = currentFormat.parse(temp);
                                                widget.setDate(date);
                                                String stringDate = new SimpleDateFormat("dd/MM/yy").format(date);
                                                //Date newDate = new SimpleDateFormat("dd/MM/yy").parse(stringDate);
                                                Log.e("MyTag","Publish date is " + date);
                                                widget.setDateFormatted(stringDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else// Check which Tag we have
                                            if (theTag.equalsIgnoreCase("category"))
                                            {
                                                // Now just get the associated text
                                                String temp = xpp.nextText();
                                                // Do something with text
                                                //Log.e("MyTag","Category is " + temp);
                                                widget.setCategory(temp);
                                            }
                                            else// Check which Tag we have
                                                if (theTag.equalsIgnoreCase("lat"))
                                                {
                                                    // Now just get the associated text
                                                    String temp = xpp.nextText();
                                                    // Do something with text
                                                    //Log.e("MyTag","Latitude is " + temp);
                                                    widget.setLatitude(temp);
                                                }
                                                else// Check which Tag we have
                                                    if (theTag.equalsIgnoreCase("long"))
                                                    {
                                                        // Now just get the associated text
                                                        String temp = xpp.nextText();
                                                        // Do something with text
                                                        //Log.e("MyTag","Longitude is " + temp);
                                                        widget.setLongitude(temp);
                                                    }
                        }

                    }
                    else
                    if(eventType == XmlPullParser.END_TAG)
                    {
                        if (theTag.equalsIgnoreCase("item"))
                        {
                            insideItem = false;
                            //Log.e("MyTag","Item is " + widget.toString());
                            alist.add(widget);


                        }
                        else
                        if (theTag.equalsIgnoreCase("channel"))
                        {
                            int size;
                            size = alist.size();
                            //Log.e("MyTag","Number of items " + size);


                        }
                    }


                    // Get the next event
                    eventType = xpp.next();

                } // End of while

                daList = alist;
                //return alist;

                for(int i = 0; i < daList.size(); i++)
                {
                    theArrayAdapter.add(daList.get(i));
                    Log.e("index", ""+i);
                }
            }
            catch (XmlPullParserException ae1)
            {
                //Log.e("MyTag","Parsing error" + ae1.toString());
            }
            catch (IOException ae1)
            {
                //Log.e("MyTag","IO error during parsing");
            }

            //Log.e("MyTag","End document");


            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !





            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                }
            });
        }

    }


}

 class MySimpleArrayAdapter extends ArrayAdapter<EarthquakeClass> {
    private final Context context;
    final LinkedList<EarthquakeClass> mainArrayList;

    public MySimpleArrayAdapter(Context context, int resource, LinkedList<EarthquakeClass> values) {
        super(context, -1, values);
        this.context = context;
        this.mainArrayList = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_layout, parent, false);

        TextView txt = (TextView)rowView.findViewById(R.id.layout_title);
        txt.setText("UK Earthquake:\n"+ mainArrayList.get(position).getDescriptionLocation() );

        TextView date = (TextView)rowView.findViewById(R.id.layout_date);
        date.setText(mainArrayList.get(position).getDescriptionOriginDate());

        TextView latlong = (TextView)rowView.findViewById(R.id.layout_latlong);
        latlong.setText(mainArrayList.get(position).getDescriptionLat() + " " + mainArrayList.get(position).getDescriptionLong());

        TextView mag = (TextView)rowView.findViewById(R.id.layout_magnitude);
        mag.setText(mainArrayList.get(position).getDescriptionMagnitude());

        float m = Float.valueOf(mainArrayList.get(position).getDescriptionMagnitude());




        if(m < 0.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_0));}
        if(m >= 0.5 && m < 1) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_0_5));}
        if(m >= 1 && m < 1.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_1));}
        if(m >= 1.5 && m < 2) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_1_5));}
        if(m >= 2 && m < 2.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_2));}
        if(m >= 2.5 && m < 3) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_2_5));}
        if(m >= 3 && m < 3.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_3));}
        if(m >= 3.5 && m < 4) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_3_5));}
        if(m >= 4 && m < 4.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_4));}
        if(m >= 4.5 && m < 5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_4_5));}
        if(m >= 5 && m < 5.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_5));}
        if(m >= 5.5 && m < 6) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_5_5));}
        if(m >= 6 && m < 6.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_6));}
        if(m >= 6.5 && m < 7) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_6_5));}
        if(m >= 7 && m < 7.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_7));}
        if(m >= 7.5 && m < 8) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_7_5));}
        if(m >= 8 && m < 8.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_8));}
        if(m >= 8.5 && m < 9) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_8_5));}
        if(m >= 9 && m < 9.5) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_9));}
        if(m >= 9.5 && m < 10) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_9_5));}
        if(m >= 10) {mag.setBackgroundColor(context.getResources().getColor(R.color.lvl_10));}

        return rowView;
    }

    @Override
    public void add(EarthquakeClass object) {
        //super.add(object);

        this.mainArrayList.add(object);
    }

}