package com.myplacelocator.Functions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MyPlace implements Parcelable {



    private String name;
    private String address;
    private double lat;
    private double lng;
    private String imageUrl;
    private String id;
    private double rate;
    private String tel;
    private String website;

    public MyPlace(){}

    public MyPlace(String name, String address, String id, String imageUrl, double lat, double lng,double rate, String tel, String website) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.rate = rate;
        this.tel = tel;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public double getRate() {
        return rate;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTel() {
        return tel;
    }


    public String getWebsite() {
        return website;
    }

    protected MyPlace(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        imageUrl = in.readString();
        id = in.readString();
        rate = in.readDouble();
        tel  =in.readString();
        website = in.readString();
    }

    public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
        @Override
        public MyPlace createFromParcel(Parcel in) {
            return new MyPlace(in);
        }

        @Override
        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(imageUrl);
        dest.writeString(id);
        dest.writeDouble(rate);
        dest.writeString(tel);
        dest.writeString(website);
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2, String unit) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        if (unit.equals("Km"))
            return d;
        else
            return d/1.61;
    }
}
