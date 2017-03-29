package group12.cpen391.patienttracker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    static final int REQUEST_IMAGE_CROP = 3;

    private View rootView;
    private Button mChangeInfoButton;
    private Button mChangePhotoButton;
    private Button mChangeContactButton;
    private Button mPairDeviceButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            mChangeInfoButton = (Button) rootView.findViewById(R.id.patient_info_button);
            mPairDeviceButton = (Button) rootView.findViewById(R.id.bluetooth_button);
            mChangeContactButton = (Button) rootView.findViewById(R.id.contacts_button);
            mChangePhotoButton = (Button) rootView.findViewById(R.id.change_photo_button);

            mChangeInfoButton.setOnClickListener(this);
            mPairDeviceButton.setOnClickListener(this);
            mChangeContactButton.setOnClickListener(this);
            mChangePhotoButton.setOnClickListener(this);

        } catch (InflateException e){
        Log.e("settingsview", "Inflate exception");
        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case(R.id.patient_info_button):
                Intent infoIntent = new Intent(this.getContext(), PatientInfoActivity.class);
                startActivity(infoIntent);
                break;
            case(R.id.contacts_button):
                Intent contactsIntent = new Intent(this.getContext(), ContactsActivity.class);
                startActivity(contactsIntent);
                break;
            case(R.id.change_photo_button):
                selectImage();
                break;
            case(R.id.bluetooth_button):
                //TODO: instructions for pairing for DE1 device
//                Intent bluetoothIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
//                startActivity(bluetoothIntent);
               // BluetoothService.getService().connect();
                Intent bluetoothIntent = new Intent(this.getContext(), BluetoothActivity.class);
                startActivity(bluetoothIntent);
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                else if (items[item].equals("Choose from Library")) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
                }
                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_GALLERY ) {
                Uri imageUri = data.getData();
                doCrop(imageUri);
            }
            if(requestCode == REQUEST_IMAGE_CROP){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                sendBitmap(imageBitmap);
            }
        }
    }
    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this.getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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
