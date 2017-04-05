package group12.cpen391.patienttracker;

import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.Arrays;

public class SendImageActivity extends AppCompatActivity implements AdapterView.OnClickListener{

    ImageView mToSendView;
    FloatingActionButton mConfirmFab;
    Bitmap toSendBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        mToSendView = (ImageView) findViewById(R.id.to_send_image_view);
        mConfirmFab = (FloatingActionButton) findViewById(R.id.confirm_image_fab);

        toSendBitmap = (Bitmap) getIntent().getParcelableExtra("IMG_BITMAP");

        mToSendView.setImageBitmap(toSendBitmap);
        mConfirmFab.setOnClickListener( this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.confirm_image_fab):
                sendBitmap(toSendBitmap);
                this.finish();
                break;
        }
    }

    void sendBitmap(Bitmap imageBitmap){
        int pixels[] = new int[250  * 250];
        byte bytePixels[] = new byte[250 * 250];

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, 250, 250, false);
        scaledBitmap.getPixels(pixels, 0 , 250, 0, 0, 250, 250);
        Log.i ("IMAGE", String.format("pixel 0,0 " + String.format( "0x%08X", imageBitmap.getPixel(0, 0))));
        Log.i("IMAGE", "pixel buffer " + Arrays.toString(pixels));
        Log.i("IMAGE", "image size " + pixels.length);

        ARGBto6bitRGB(pixels, bytePixels);
        String toSend = new String(bytePixels);
        Log.i("IMAGE", "pixel buffer " + toSend);

        BluetoothService bt = BluetoothService.getService();
        bt.write("~" + toSend);
    }

    void ARGBto6bitRGB(int[] ARGB, byte[] RGB){
        int pixelIn;
        int pixel;

        for (int i = 0; i <  ARGB.length; i++){
            pixelIn = ARGB[i];
            pixel = ((pixelIn & 0xc00000) >> 18) | ((pixelIn & 0xc000) >> 12) | ((pixelIn & 0xc0) >> 6);
            RGB[i] = (byte) pixel;
        }
    }
}
