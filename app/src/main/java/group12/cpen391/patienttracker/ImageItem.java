package group12.cpen391.patienttracker;

import android.graphics.Bitmap;

public class ImageItem {
    public int id;
    public String date;
    public Bitmap image;

    public ImageItem(int id, String date, Bitmap image){
        this.id = id;
        this.date = date;
        this.image = image;
    }
}