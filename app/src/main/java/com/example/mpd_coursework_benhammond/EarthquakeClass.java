/*
Ben Hammond | S1709378
*/
package com.example.mpd_coursework_benhammond;

import java.util.Date;
import java.io.Serializable;

public class EarthquakeClass  implements  Serializable{

    String Title;
    String Description;
    String description_OriginDate;
    String description_Location;
    String description_Lat;
    String description_Long;
    String description_Depth;
    String description_Magnitude;

    String dateFormatted;

    String Link;
    Date Date;
    String Category;
    String Latitude;
    String Longitude;

    String mag_color;

    public EarthquakeClass()
    {
        Title = "";
        Description = "";
        Link = "";
        Date = new Date();
        Category = "";
        Latitude = "";
        Longitude = "";
        description_OriginDate = "";
        description_Location = "";
        description_Lat = "";
        description_Long = "";
        description_Depth = "";
        description_Magnitude = "";
        dateFormatted = "";
        mag_color = "";
    }

    public EarthquakeClass(String aTitle, String aDescription,
                           String adescription_OriginDate, String adescription_Location, String adescription_Lat, String adescription_Long, String adescription_Depth, String adescription_Magnitude ,
                           String aLink, Date aDate, String aCategory, String aLatitude, String aLongitude, String aDateFormatted, String aMagColor)
    {
        Title = aTitle;
        Description = aDescription;
        Link = aLink;
        Date = aDate;
        Category = aCategory;
        Latitude = aLatitude;
        Longitude = aLongitude;

        description_OriginDate = adescription_OriginDate;
        description_Location = adescription_Location;
        description_Lat = adescription_Lat;
        description_Long = adescription_Long;
        description_Depth = adescription_Depth;
        description_Magnitude = adescription_Magnitude;
        dateFormatted = aDateFormatted;
        mag_color = aMagColor;
    }

    public String getTitle(){return Title;}
    public String getDescription(){return Description;}
    public String getLink(){return Link;}
    public Date getDate(){return Date;}
    public String getCategory(){return Category;}
    public String getLatitude(){return Latitude;}
    public String getLongitude(){return Longitude;}

    public String getDateFormatted(){return dateFormatted;}

    public String getDescriptionOriginDate(){return description_OriginDate;}
    public String getDescriptionLocation(){return description_Location;}
    public String getDescriptionLat(){return description_Lat;}
    public String getDescriptionLong(){return description_Long;}
    public String getDescriptionDepth(){return description_Depth;}
    public String getDescriptionMagnitude(){return description_Magnitude;}
    public String getMagColor(){return mag_color;}



    public void setTitle(String aTitle){Title = aTitle;}
    public void setDescription(String aDescription){Description = aDescription;}
    public void setLink(String aLink){Link = aLink;}
    public void setDate(Date aDate){Date = aDate;}
    public void setCategory(String aCategory){Category = aCategory;}
    public void setLatitude(String aLateral){Latitude = aLateral;}
    public void setLongitude(String aLongitude){Longitude = aLongitude;}

    public void setDateFormatted(String aDateFormatted){dateFormatted = aDateFormatted;}

    public void setDescriptionOriginDate(String adescription_OriginDate){description_OriginDate = adescription_OriginDate;}
    public void setDescriptionLocation(String adescription_Location){description_Location = adescription_Location;}
    public void setDescriptionLat(String adescription_Lat){description_Lat = adescription_Lat;}
    public void setDescriptionLong(String adescription_Long){description_Long = adescription_Long;}
    public void setDescriptionDepth(String adescription_Depth){description_Depth = adescription_Depth;}
    public void setDescriptionMagnitude(String adescription_Magnitude){description_Magnitude = adescription_Magnitude;}

    public void setMagColor(String aMagColor){mag_color = aMagColor;}




}
