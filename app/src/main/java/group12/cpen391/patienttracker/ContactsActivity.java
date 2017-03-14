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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

public class ContactsActivity extends AppCompatActivity implements AdapterView.OnClickListener {

    static final int PICK_CONTACT_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private Button mC1Button;
    private EditText mC1Name;
    private EditText mC1Number;
    private EditText mC1Relation;
    private String currentC1Name;
    private String currentC1Number;
    private String currentC1Relation;
    private FloatingActionButton mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mC1Name = (EditText) findViewById(R.id.input_c1_name);
        mC1Number = (EditText) findViewById(R.id.input_c1_number);
        mC1Relation = (EditText) findViewById(R.id.input_c1_relation);
        mC1Button = (Button) findViewById(R.id.edit_c1_but);

        mConfirmButton = (FloatingActionButton) findViewById(R.id.confirm_contact_fab);

        mC1Button.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case(R.id.edit_c1_but):
                pickContact();
                break;
            case(R.id.confirm_contact_fab):
                currentC1Name = mC1Name.getText().toString();
                currentC1Number = mC1Number.getText().toString();
                currentC1Relation = mC1Relation.getText().toString();
                //TODO send via bluetooth
                break;
        }
    }

    private void pickContact() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            Intent i= new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(i, PICK_CONTACT_REQUEST);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                Log.i(TAG, "return success");

                if (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+contactId,null, null);
                    phones.moveToFirst();

                    String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    mC1Number.setText(phoneNo);
                    mC1Name.setText(name);
                    phones.close();
                }
                cursor.close();
            }
        }
    }
}
