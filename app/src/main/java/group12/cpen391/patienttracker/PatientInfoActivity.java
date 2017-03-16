package group12.cpen391.patienttracker;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

public class PatientInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnClickListener {

    private EditText mEditName;
    private EditText mEditPHN;
    private EditText mEditAddress;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        mEditName = (EditText) findViewById(R.id.input_name);
        mEditPHN = (EditText) findViewById(R.id.input_phn);
        mEditAddress = (EditText) findViewById(R.id.input_address);
        mEditCity = (EditText) findViewById(R.id.input_city);
        mEditPostalCode = (EditText) findViewById(R.id.input_postal_code);

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
                    o.put("address", currentAddress);
                    o.put("city", currentCity + ", " + currentProvince + ", " + currentPostalCode);
                } catch (JSONException e){ }

                // Write json to DE1.
                BluetoothService bt = BluetoothService.getService();
                bt.write(o.toString());

                //TODO: feedback on successful update
                this.finish();
                break;
        }
    }
}
