package group12.cpen391.patienttracker;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;


public class BluetoothActivity extends AppCompatActivity {
    public static final int UPDATE_UI = 0;
    public static final int SHOW_TOAST = 1;
    public static final int OPEN_BT_SETTINGS = 2;

    private Switch mPairSwitch;
    private static Handler uiHandler;
    private BluetoothService bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_UI: {
                        boolean devicePaired = (Boolean) msg.obj;
                        SettingsFragment.updateBluetoothButtons(devicePaired);
                        mPairSwitch.setChecked(devicePaired);
                        updateDeviceInfoText();
                        break;
                    }
                    case SHOW_TOAST: {
                        String responseMsg = (String) msg.obj;
                        Toast.makeText(getApplicationContext(), responseMsg, Toast.LENGTH_LONG).show();
                        break;
                    }
                    case OPEN_BT_SETTINGS: {
                        Intent pairIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(pairIntent);
                        break;
                    }
                }
            }
        };

        mPairSwitch = (Switch) findViewById(R.id.pair_switch);
        bt = BluetoothService.getService();

        updateDeviceInfoText();

        mPairSwitch.setChecked(bt.devicePaired());

        mPairSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // De-register listener to avoid calling itself
//                mPairSwitch.setOnCheckedChangeListener(null);
                mPairSwitch.setChecked(false); // Default to off and let connection logic update switch state
//                mPairSwitch.setOnCheckedChangeListener(this);
                boolean devicePaired = bt.devicePaired();
                Log.v("WHY", "bChecked: " + devicePaired);
                if (!devicePaired && !bt.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                } else if (!devicePaired) {
                    bt.connect();
                } else {
                    bt.close();
                }
            }
        });
    }

    public static Handler getHandler() {
        return uiHandler;
    }

    private void updateDeviceInfoText() {
        TextView mDeviceName = (TextView) findViewById(R.id.device_name);
        TextView mDeviceMac = (TextView) findViewById(R.id.device_mac);
        mDeviceName.setText(String.format(getString(R.string.bt_device_name), bt.getDeviceName()));
        mDeviceMac.setText(String.format(getString(R.string.bt_device_mac), bt.getDeviceMac()));
    }

}
