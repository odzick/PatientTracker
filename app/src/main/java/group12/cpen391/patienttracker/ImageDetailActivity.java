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

        Intent i = getIntent();
        Context context = getApplicationContext();

        String filename = i.getStringExtra("filename");
        File f = new File(context.getFilesDir(), filename);
        Log.v("GALLERY", "Retrieving image from path: " + context.getFilesDir() + filename);

        // Load image from filesystem into ImageView
        ImageView imageView = (ImageView) findViewById(R.id.image_fullscreen);
        Picasso.with(context).load(f).placeholder(R.drawable.ic_error).into(imageView);
    }
}
