package com.example.alvaroramirez.programamovilemprendedores;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class News {

    private int id;
    private String title;
    private String description;
    private Date date;
    private String url_photo;
    private Bitmap photo;
    private boolean approved;
    private int idCommunity;

    public News(int id, String title, String description, String date, String url_photo, Bitmap photo, boolean approved) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url_photo = url_photo;
        this.photo = photo;
        this.approved = approved;

        String dateIn = date.split("T")[0];
//        String timeIn = date.split("T")[1];//.split(".")[0];
//        timeIn = timeIn.split("\\.")[0];
//        String fechaHora = dateIn + " " + timeIn;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date newDate = new Date();
        try{
            this.date = format.parse(date);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public News(int id, String title, String description, String date, String url_photo, Bitmap photo, boolean approved, int idCommunity)  {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url_photo = url_photo;
        this.photo = photo;
        this.approved = approved;
        this.idCommunity = idCommunity;
        String dateIn = date.split("T")[0];
        String timeIn = date.split("T")[1];//.split(".")[0];
        timeIn = timeIn.split("\\.")[0];
        String fechaHora = dateIn + " " + timeIn;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date newDate = new Date();
        try{
            newDate = format.parse(fechaHora);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.date = newDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getIdCommunity() {
        return idCommunity;
    }

    public void setIdCommunity(int idCommunity) {
        this.idCommunity = idCommunity;
    }
}
