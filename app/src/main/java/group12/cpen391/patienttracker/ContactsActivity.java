package group12.cpen391.patienttracker;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

public class ContactsActivity extends AppCompatActivity implements AdapterView.OnClickListener {

    static final int PICK_CONTACT_REQUEST = 1;
    static final int PICK_CONTACT_REQUEST_MAX = 4;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ImageButton mC1Button;
    private EditText mC1Name;
    private EditText mC1Number;
    private EditText mC1Relation;
    private String currentC1Name;
    private String currentC1Number;
    private String currentC1Relation;

    private ImageButton mC2Button;
    private EditText mC2Name;
    private EditText mC2Number;
    private EditText mC2Relation;
    private String currentC2Name;
    private String currentC2Number;
    private String currentC2Relation;

    private ImageButton mC3Button;
    private EditText mC3Name;
    private EditText mC3Number;
    private EditText mC3Relation;
    private String currentC3Name;
    private String currentC3Number;
    private String currentC3Relation;

    private ImageButton mC4Button;
    private EditText mC4Name;
    private EditText mC4Number;
    private EditText mC4Relation;
    private String currentC4Name;
    private String currentC4Number;
    private String currentC4Relation;

    private FloatingActionButton mConfirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mC1Name = (EditText) findViewById(R.id.input_c1_name);
        mC1Number = (EditText) findViewById(R.id.input_c1_number);
        mC1Relation = (EditText) findViewById(R.id.input_c1_relation);
        mC1Button = (ImageButton) findViewById(R.id.edit_c1_but);
        mC1Button.setOnClickListener(this);

        mC2Name = (EditText) findViewById(R.id.input_c2_name);
        mC2Number = (EditText) findViewById(R.id.input_c2_number);
        mC2Relation = (EditText) findViewById(R.id.input_c2_relation);
        mC2Button = (ImageButton) findViewById(R.id.edit_c2_but);
        mC2Button.setOnClickListener(this);

        mC3Name = (EditText) findViewById(R.id.input_c3_name);
        mC3Number = (EditText) findViewById(R.id.input_c3_number);
        mC3Relation = (EditText) findViewById(R.id.input_c3_relation);
        mC3Button = (ImageButton) findViewById(R.id.edit_c3_but);
        mC3Button.setOnClickListener(this);

        mC4Name = (EditText) findViewById(R.id.input_c4_name);
        mC4Number = (EditText) findViewById(R.id.input_c4_number);
        mC4Relation = (EditText) findViewById(R.id.input_c4_relation);
        mC4Button = (ImageButton) findViewById(R.id.edit_c4_but);
        mC4Button.setOnClickListener(this);

        mConfirmButton = (FloatingActionButton) findViewById(R.id.confirm_contact_fab);
        mConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.edit_c1_but):
                pickContact(0);
                break;
            case (R.id.edit_c2_but):
                pickContact(1);
                break;
            case (R.id.edit_c3_but):
                pickContact(2);
                break;
            case (R.id.edit_c4_but):
                pickContact(3);
                break;
            case (R.id.confirm_contact_fab):
                copyToSend();
                this.finish();
                break;
        }
    }

    private void pickContact(int c_number) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(i, PICK_CONTACT_REQUEST + c_number);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void copyToSend() {
        currentC1Name = mC1Name.getText().toString();
        currentC1Number = mC1Number.getText().toString().replaceAll("[^0-9+]", "");
        currentC1Relation = mC1Relation.getText().toString();

        currentC2Name = mC2Name.getText().toString();
        currentC2Number = mC2Number.getText().toString().replaceAll("[^0-9+]", "");
        currentC2Relation = mC2Relation.getText().toString();

        currentC3Name = mC3Name.getText().toString();
        currentC3Number = mC3Number.getText().toString().replaceAll("[^0-9+]", "");
        currentC3Relation = mC3Relation.getText().toString();

        currentC4Name = mC4Name.getText().toString();
        currentC4Number = mC4Number.getText().toString().replaceAll("[^0-9+]", "");
        currentC4Relation = mC4Relation.getText().toString();

        JSONObject o = new JSONObject();
        try {
            o.put("C1name", currentC1Name);
            o.put("C1phone", currentC1Number);
            o.put("C1relation", currentC1Relation);

            o.put("C2name", currentC2Name);
            o.put("C2phone", currentC2Number);
            o.put("C2relation", currentC2Relation);

            o.put("C3name", currentC3Name);
            o.put("C3phone", currentC3Number);
            o.put("C3relation", currentC3Relation);

            o.put("C4name", currentC4Name);
            o.put("C4phone", currentC4Number);
            o.put("C4relation", currentC4Relation);

        } catch (JSONException e){ }

        // Write json to DE1.
        BluetoothService bt = BluetoothService.getService();
        bt.write(o.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode <= PICK_CONTACT_REQUEST_MAX && requestCode >= PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                Log.i(TAG, "return success");

                if (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    phones.moveToFirst();

                    String phoneNo = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //TODO normalize numbers (maybe not needed)

                    int c_num = requestCode - PICK_CONTACT_REQUEST;

                    switch (c_num) {
                        case (0):
                            mC1Number.setText(phoneNo);
                            mC1Name.setText(name);
                            break;
                        case (1):
                            mC2Number.setText(phoneNo);
                            mC2Name.setText(name);
                            break;
                        case (2):
                            mC3Number.setText(phoneNo);
                            mC3Name.setText(name);
                            break;
                        case (3):
                            mC4Number.setText(phoneNo);
                            mC4Name.setText(name);
                            break;
                    }
                    phones.close();
                }
                cursor.close();
            }
        }
    }
}
