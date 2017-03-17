package group12.cpen391.patienttracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class BluetoothActivity extends AppCompatActivity {

    Switch mConnectDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mConnectDevice = (Switch) findViewById(R.id.pair_switch);

        SharedPreferences sharedPrefs = this.getSharedPreferences("com.g12.patientTracker.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        mConnectDevice.setChecked(sharedPrefs.getBoolean("devicePaired", false));

        mConnectDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.g12.patientTracker.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit();
                    editor.putBoolean("devicePaired", true);
                    editor.commit();
                    BluetoothService.getService().connect();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.g12.patientTracker.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit();
                    editor.putBoolean("devicePaired", false);
                    editor.commit();
                    BluetoothService.getService().close();
                }
            }
        });
    }
}
