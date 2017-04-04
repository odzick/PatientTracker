package group12.cpen391.patienttracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent i = getIntent();
        String image = i.getStringExtra("image");

        ImageView imageView = (ImageView) findViewById(R.id.image_fullscreen);
        Picasso.with(this).load("file:///android_asset/testimage.jpg").placeholder(R.drawable.ic_error).into(imageView);
    }
}
