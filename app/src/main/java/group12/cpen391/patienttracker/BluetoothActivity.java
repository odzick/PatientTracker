package group12.cpen391.patienttracker;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends AppCompatActivity {

    static Switch mPairSwitch;
    static SharedPreferences sharedPrefs;
    private static Context mContext;
    BluetoothService bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mPairSwitch = (Switch) findViewById(R.id.pair_switch);
        mContext = getApplicationContext();
        bt = BluetoothService.getService();

        updateDeviceInfoText();

        sharedPrefs = this.getSharedPreferences("com.g12.patientTracker.BLUETOOTH_PREF", Context.MODE_PRIVATE);
        mPairSwitch.setChecked(sharedPrefs.getBoolean("devicePaired", false));

        mPairSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                updateSwitchState(false); // Default to off and let connection logic update switch state
                if (bChecked && !bt.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                } else if (bChecked) {
                    bt.connect();
                    updateDeviceInfoText();
                } else {
                    bt.close();
                    updateDeviceInfoText();
                }
            }
        });
    }

    public static synchronized void updateSwitchState(boolean devicePaired) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("devicePaired", devicePaired);
        editor.apply();
        mPairSwitch.setChecked(devicePaired);
    }

    public static void showReponseToast(String responseMsg) {
        Toast.makeText(mContext, responseMsg, Toast.LENGTH_LONG).show();
    }

    private void updateDeviceInfoText() {
        TextView mDeviceName = (TextView) findViewById(R.id.device_name);
        TextView mDeviceMac = (TextView) findViewById(R.id.device_mac);
        mDeviceName.setText(String.format(getString(R.string.bt_device_name), bt.getDeviceName()));
        mDeviceMac.setText(String.format(getString(R.string.bt_device_mac), bt.getDeviceMac()));
    }
}
