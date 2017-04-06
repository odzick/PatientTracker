package group12.cpen391.patienttracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static BluetoothService mBluetoothService;
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private boolean devicePaired;

    private final String TAG = "Bluetooth";
    private final String BT_ERR_NO_DEVICES = "No Bluetooth devices to pair with.";
    private final String BT_ERR_ON_CONNECT = "Error connecting to Bluetooth socket.";
    private final String BT_SUCCESS = "Successfully connected to Bluetooth device";


    public BluetoothService() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevice = null;
        mConnectThread = null;
        mConnectedThread = null;
        devicePaired = false;
    }

    public static BluetoothService getService() {
        if (mBluetoothService == null) {mBluetoothService = new BluetoothService();}
        return mBluetoothService;
    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean devicePaired() {
        return devicePaired;
    }

    public String getDeviceName(){
        if (mDevice == null) return "N/A";
        return mDevice.getName();
    }

    public String getDeviceMac() {
        if (mDevice == null) return "N/A";
        return mDevice.getAddress();
    }

    public synchronized void connect() {
        devicePaired = false;
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.v(TAG, "connect(): pairedDevices.size() = " + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                // TODO: choose device based on name/MAC
                mDevice = device;
            }
        }

        if (mDevice != null) {
            mConnectThread = new ConnectThread(mDevice);
            mConnectThread.start();
        } else {
            showToast(BT_ERR_NO_DEVICES);
            openBluetoothSettings();
        }
    }

    public synchronized void close() {
        Log.v(TAG, "close(): stopping BT threads");
        mDevice = null;
        devicePaired = false;
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        updateUI(devicePaired);
    }

    public synchronized void write(String data){
        if (!devicePaired){
            Log.v(TAG, "Failed write: Not connected to Bluetooth device");
            return;
        }
        data = "|" + data + "|";
        Log.v(TAG, "Writing to DE1: " + data);
        mConnectedThread.write(data.getBytes());
    }

    private void showToast(String message) {
        Handler h = BluetoothActivity.getHandler();
        Message msg = h.obtainMessage(BluetoothActivity.SHOW_TOAST);
        msg.obj = message;
        h.sendMessage(msg);
    }

    private void updateUI(boolean devicePaired) {
        Handler h = BluetoothActivity.getHandler();
        Message msg = h.obtainMessage(BluetoothActivity.UPDATE_UI);
        msg.obj = devicePaired;
        h.sendMessage(msg);
    }

    private void openBluetoothSettings() {
        Handler h = BluetoothActivity.getHandler();
        Message msg = h.obtainMessage(BluetoothActivity.OPEN_BT_SETTINGS);
        h.sendMessage(msg);
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Exception creating BT socket", e);
            }
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.e(TAG, "Exception connecting to bluetooth socket", connectException);
                showToast(BT_ERR_ON_CONNECT);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Exception closing BT socket", closeException);
                }
                return;
            }
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception closing BT socket", e);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Exception opening OutputStream on BT socket", e);
            }
            mmOutStream = tmpOut;
        }

        public void run() {
            showToast(BT_SUCCESS);
            devicePaired = true;
            updateUI(devicePaired);
            while (!Thread.currentThread().isInterrupted()) { }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Exception writing to OutputStream", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception closing BT socket", e);
            }
            interrupt();
        }
    }
}
