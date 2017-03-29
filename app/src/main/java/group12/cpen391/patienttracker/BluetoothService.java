package group12.cpen391.patienttracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.icu.util.Output;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static java.security.AccessController.getContext;

public class BluetoothService {
    private static BluetoothService bt = new BluetoothService();
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private boolean isRunning;

    public BluetoothService() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevice = null;
        mConnectThread = null;
        mConnectedThread = null;
        isRunning = false;
    }

    public static BluetoothService getService() {
        return bt;
    }

    public synchronized void connect() {
        // TODO: Check if bluetooth enabled and redirect to bluetooth intent page.
        // if (!mBluetoothAdapter.isEnabled()) {
        //   Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        // }
        isRunning = false;
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        mDevice = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.v("Bluetooth", "pairedDevices.size() = " + pairedDevices.size());
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

        }
    }

    public synchronized void close() {
        Log.v("Bluetooth", "stopping BT threads");
        isRunning = false;
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
    }

    public synchronized void write(String data){
        if (!isRunning){
            // TODO: replace with error toast
            Log.v("Bluetooth", "Failed write: Not connected to Bluetooth device");
        }
        data = "|" + data + "|";
        Log.v("Bluetooth", "Writing to DE1: " + data);
        mConnectedThread.write(data.getBytes());
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.e("Bluetooth", "Error connecting to bluetooth socket", connectException);
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
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
            }
            mmOutStream = tmpOut;
        }

        public void run() {
            isRunning = true;
            Log.v("Bluetooth", "ConnectedThread created. isRunning = " + isRunning);
            while (!Thread.currentThread().isInterrupted()) {
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
            interrupt();
        }
    }
}
