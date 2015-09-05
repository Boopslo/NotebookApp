package practice.oslo.com.notebookapp;

import java.util.Date;
import java.util.Locale;


/**
 * This class is for every item to function different capabilities
 *
 * Created by Oslo on 6/11/15.
 */
public class Item implements java.io.Serializable{

    private long id;
    private long dateTime;
    private Colors color;
    private String title;
    private String content;
    private String fileName;
    private double latitude;
    private double longitude;
    private long lastModified;
    private boolean selected;
    // new variable: the alarm date and time
    private long alarmDateTime;

    public Item(){
        title = "";
        content = "";
        color = Colors.LIGHTGRAY;
    }

    public Item(long id, long dateTime, Colors color, String title, String content, String fileName,
                double latitude, double longitude, long lastModified){

        this.id = id;
        this.dateTime = dateTime;
        this.color = color;
        this.title = title;
        this.content = content;
        this.fileName = fileName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastModified = lastModified;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setDateTime(long dateTime){
        this.dateTime = dateTime;
    }

    public String getLocaleDateTime(){
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(dateTime));
    }

    public String getLocaleDate(){
        return String.format(Locale.getDefault(), "%tF", new Date(dateTime));
    }

    public String getLocaleTime(){
        return String.format(Locale.getDefault(), "%tR", new Date(dateTime));
    }

    public long getDateTime(){
        return dateTime;
    }

    public void setColor(Colors color){
        this.color = color;
    }

    public Colors getColor(){
        return color;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLastModified(long lastModified){
        this.lastModified = lastModified;
    }

    public long getLastModified(){
        return lastModified;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean getSelected(){
        return selected;
    }

    public void setAlarmDateTime(long alarm){
        this.alarmDateTime = alarm;
    }

    public long getAlarmDateTime(){
        return alarmDateTime;
    }


}
