package com.gitpro.discoverqa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Place implements Serializable {
    public List<ActivityClass> activities;
    public int airQuality;
    public String categoryAR;
    public String categoryEN;
    public String descAR;
    public String descEN;
    public int estimatedTime;
    public String imageURL;
    public int internet;
    public double latitude;
    public double longitude;
    public String nameAR;
    public String nameEN;
   public String recommendedAge;
    public int recommendedSeason;
    public Date recommendedTime;
    public String keywords;



    public Place(List<ActivityClass> activities, int airQuality, String categoryAR, String categoryEN,  String descAR, String descEN, int estimatedTime, String imageURL,int internet,double latitude,double longitude,String nameAR, String nameEN, String recommendedAge,int recommendedSeason, Date recommendedTime,String keywords) {
        this.airQuality = airQuality;
        this.categoryAR = categoryAR;
        this.categoryEN = categoryEN;
        this.descAR = descAR;
        this.descEN = descEN;
        this.estimatedTime = estimatedTime;
        this.imageURL = imageURL;
        this.internet = internet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nameAR = nameAR;
        this.nameEN = nameEN;
        this.recommendedAge = recommendedAge;
        this.recommendedSeason = recommendedSeason;
        this.recommendedTime = recommendedTime;
        this.keywords = keywords;
        this.activities = activities;
    }

   public Place(){

   }
}
