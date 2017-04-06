package group12.cpen391.patienttracker;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

public class PatientInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnClickListener {

    private EditText mEditName;
    private EditText mEditPHN;
    private EditText mEditAddress;
    private EditText mEditDate;
    private EditText mEditCity;
    private EditText mEditPostalCode;
    private FloatingActionButton confirmFAB;
    private Spinner mSpinner;

    //TODO put in data structure convenient for bluetooth
    private String currentName;
    private String currentPHN;
    private String currentAddress;
    private String currentCity;
    private String currentPostalCode;
    private String currentProvince;
    private int selectedYear = 1948;
    private int selectedMonth = 9;
    private int selectedDay = 13;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        mEditName = (EditText) findViewById(R.id.input_name);
        mEditPHN = (EditText) findViewById(R.id.input_phn);
        mEditAddress = (EditText) findViewById(R.id.input_address);
        mEditCity = (EditText) findViewById(R.id.input_city);
        mEditPostalCode = (EditText) findViewById(R.id.input_postal_code);

        mEditDate = (EditText) findViewById(R.id.input_date);
        mEditDate.setOnClickListener(this);

        confirmFAB = (FloatingActionButton) findViewById(R.id.confirm_fab);
        confirmFAB.setOnClickListener(this);

        mSpinner = (Spinner) findViewById(R.id.province_spinner);
        mSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.province_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spin
        mSpinner.setAdapter(adapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String type_selected = parent.getItemAtPosition(pos).toString();

        currentProvince = type_selected;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.confirm_fab):
                // (limited input types and size in xml already)
                currentName = mEditName.getText().toString();
                currentPHN = mEditPHN.getText().toString();
                currentAddress = mEditAddress.getText().toString();
                currentCity = mEditCity.getText().toString();
                currentPostalCode = mEditPostalCode.getText().toString();
                //TODO error toasters for blank entries

                //TODO: move to helper function.
                // Create
                JSONObject o = new JSONObject();
                try {
                    o.put("name", currentName);
                    o.put("phn", currentPHN);
                    o.put("street", currentAddress);
                    o.put("city", currentCity + ", " + currentProvince + ", " + currentPostalCode);
                    o.put("year", selectedYear);
                    o.put("month", selectedMonth);
                    o.put("day", selectedDay);
                } catch (JSONException e) {
                }

                // Write json to DE1.
                BluetoothService bt = BluetoothService.getService();
                bt.write(o.toString());

                //TODO: feedback on successful update
                this.finish();
                break;
            case (R.id.input_date):
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(PatientInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth = selectedmonth + 1;
                        mEditDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        selectedDay = selectedday;
                        selectedMonth = selectedmonth;
                        selectedYear = selectedyear;
                    }
                }, 1970, 1, 1);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("PatientInfo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
