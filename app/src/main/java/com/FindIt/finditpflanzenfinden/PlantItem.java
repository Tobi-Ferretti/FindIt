package com.FindIt.finditpflanzenfinden;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.widget.ImageView;

import androidx.core.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;

public class PlantItem implements Serializable {
    private transient Drawable imageView;
    private int id=java.lang.System.identityHashCode(this);
    private String imgBase64String;
    private String name;
    private String category;
    private Date dateRangeFirst;
    private Date getDateRangeSecond;
    private double latitude;
    private double longitude;
    private String notice;
    private String describtion;
    private String gName;
    private String gImageUrl;
    private boolean deleted=false;

    public PlantItem(Drawable imageView, String name, String category, Pair<Date, Date> dateRange, double latitude, double longitude, String notice, String describtion, String gName, String gImageUrl) {
        this.imageView=imageView;
        encodeImage();
        this.name = name;
        this.category = category;
        this.dateRangeFirst=dateRange.first;
        this.getDateRangeSecond=dateRange.second;
        this.latitude = latitude;
        this.longitude=longitude;
        this.notice = notice;
        this.describtion=describtion;
        this.gName=gName;
        this.gImageUrl=gImageUrl;
    }


    public void encodeImage(){
        Bitmap bitmap = ((BitmapDrawable)imageView).getBitmap();
        bitmap=Bitmap.createScaledBitmap(bitmap,1000,1250, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        bitmap.recycle();

        this.imgBase64String= Base64.getEncoder().encodeToString(byteArray);
        imageView=null;
    }

    /*public Bitmap decodeImage(){
        byte[] decodedString = Base64.getDecoder().decode(imgBase64String);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }*/

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Drawable getImageView() {
        return imageView;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Date getDateRangeFirst() {
        return dateRangeFirst;
    }

    public Date getGetDateRangeSecond() {
        return getDateRangeSecond;
    }

    public String getImgBase64String() {
        return imgBase64String;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNotice() {
        return notice;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescribtion() {
        return describtion;
    }

    public String getgName() {
        return gName;
    }

    public String getgImageUrl() {
        return gImageUrl;
    }

    public String objectToString() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(
                    new Base64OutputStream(baos, 1
                            | 2));
            oos.writeObject(this);
            oos.close();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "PlantItem{" +
                "imageView=" + imageView +
                ", id=" + id +
                ", imgBase64String='" + imgBase64String + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", dateRangeFirst=" + dateRangeFirst +
                ", getDateRangeSecond=" + getDateRangeSecond +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", notice='" + notice + '\'' +
                ", describtion='" + describtion + '\'' +
                ", gName='" + gName + '\'' +
                ", gImageUrl='" + gImageUrl + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
