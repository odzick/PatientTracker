package group12.cpen391.patienttracker;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageItem {
    public int id;
    public String date;
    public String filename;

    public ImageItem(int id, String date, String filename){
        this.id = id;
        this.date = formatDate(date);
        this.filename = filename;
    }

    private String formatDate(String date){
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            return new SimpleDateFormat("MMMM d, yyyy h:mm a").format(d);
        } catch(ParseException e){
            Log.e("ImageItem", e.toString());
            return date;
        }
    }
}