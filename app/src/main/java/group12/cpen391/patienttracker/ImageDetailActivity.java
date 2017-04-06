package group12.cpen391.patienttracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Context mContext = getApplicationContext();
        Intent i = getIntent();
        String filename = i.getStringExtra("filename");

        ImageView imageView = (ImageView) findViewById(R.id.image_fullscreen);
        File f = new File(mContext.getFilesDir(), filename);
        Log.v("GALLERY", "Retrieving image from path: " + mContext.getFilesDir() + filename);
        Picasso.with(mContext).load(f).placeholder(R.drawable.ic_error).into(imageView);
    }
}
