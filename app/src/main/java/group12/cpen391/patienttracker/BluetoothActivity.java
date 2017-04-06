package group12.cpen391.patienttracker;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class BluetoothActivity extends AppCompatActivity {
    public static final int UPDATE_UI = 0;
    public static final int SHOW_TOAST = 1;

    private Switch mPairSwitch;
    private static Handler uiHandler;
    private BluetoothService bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        uiHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
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
                }
            }
        };

        mPairSwitch = (Switch) findViewById(R.id.pair_switch);
        bt = BluetoothService.getService();

        updateDeviceInfoText();

        mPairSwitch.setChecked(bt.devicePaired());

        mPairSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                mPairSwitch.setChecked(false); // Default to off and let connection logic update switch state
                if (bChecked && !bt.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                } else if (bChecked) {
                    bt.connect();
                } else {
                    bt.close();
                }
            }
        });
    }

    public static Handler getHandler(){
        return uiHandler;
    }

    private void updateDeviceInfoText() {
        TextView mDeviceName = (TextView) findViewById(R.id.device_name);
        TextView mDeviceMac = (TextView) findViewById(R.id.device_mac);
        mDeviceName.setText(String.format(getString(R.string.bt_device_name), bt.getDeviceName()));
        mDeviceMac.setText(String.format(getString(R.string.bt_device_mac), bt.getDeviceMac()));
    }
}
