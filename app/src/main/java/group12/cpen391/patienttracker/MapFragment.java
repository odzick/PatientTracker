package group12.cpen391.patienttracker;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static group12.cpen391.patienttracker.serverMessageParsing.Translator.parseGPS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, AdapterView.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private GoogleMap mMap;
    private Marker patientMarker;
    private MapView mMapView;
    private Spinner mSpinner;
    private ImageButton mRefreshLocationButton;
    private LatLng mCurrentLatLng;
    private Marker myMarker;
    private Polyline polyline;

    private ArrayList<LatLng> pathPoints = new ArrayList<LatLng>();


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        try{
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map_view);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            mSpinner = (Spinner) rootView.findViewById(R.id.map_type_spinner);
            mSpinner.setOnItemSelectedListener(this);

            mRefreshLocationButton = (ImageButton) rootView.findViewById(R.id.location_button);
            mRefreshLocationButton.setOnClickListener(this);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.map_type_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spin
            mSpinner.setAdapter(adapter);

            mCurrentLatLng = ((MainActivity) getActivity()).getLatLng();

        }catch (InflateException e){
            Log.e("mapview", "Inflate exception");
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
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentLatLng = ((MainActivity) getActivity()).getLatLng();
        if(mCurrentLatLng != null){
            myMarker.setPosition(mCurrentLatLng);
            myMarker.setVisible(true);
        }
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ubc = new LatLng(49.2606, -123.2460);

        if(mCurrentLatLng != null) {
            myMarker = mMap.addMarker(new MarkerOptions().position(mCurrentLatLng).title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .visible(false));
        }

        patientMarker = mMap.addMarker(new MarkerOptions().position(ubc).title("Patient"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubc, 15));
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(10)
                .color(Color.parseColor("#125688"))
                .geodesic(true);

        //populatePath();
        //addPathtoPolyline(polylineOptions);
        polyline = mMap.addPolyline(polylineOptions);
        polyline.setPoints(pathPoints);
        if(!pathPoints.isEmpty()) {
            patientMarker.setPosition(pathPoints.get(pathPoints.size() - 1));
        }

        patientMarker.showInfoWindow();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case(R.id.location_button):
                new ServerConnector().execute();
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String type_selected = parent.getItemAtPosition(pos).toString();

        switch (type_selected){
            case "Normal":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Hybrid":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void populatePath(){
        //TODO populate with real server data
        pathPoints.add(new LatLng(49.2606, -123.2460));
        pathPoints.add(new LatLng(59.2606, -123.2460));
    }

    public void addPathtoPolyline(PolylineOptions p){
        for(int i = 0; i < pathPoints.size(); i++){
            p.add(pathPoints.get(i));
        }
    }




    public class ServerConnector extends AsyncTask<Void, Void, Void> {

        ArrayList<LatLng> path = new ArrayList<LatLng>();


        protected void imageReceiveing(){

            String s  = "";
            String hostName = "g12host.ddns.net";
            int portNumber = 3307;

            byte[] b = new byte[256];

            try {

                Socket imageSocket = new Socket(hostName, portNumber);

                OutputStream outStream = imageSocket.getOutputStream();
                InputStream inStream = imageSocket.getInputStream();

                outStream.write(("Hello\nDevice:Android \n Id:1").getBytes("US-ASCII"));
                inStream.read(b, 0, 256);

                s = new String(b);
                if(!s.contains("OK")){
                    outStream.close();
                    inStream.close();
                    imageSocket.close();
                    return;
                }
                outStream.write(("REQ:MAN \n DEVICE:1").getBytes("US-ASCII"));


                //byte array, offset, length

                do {

                    inStream.read(b, 0, 256);
                    s += new String(b, "US-ASCII");
                } while (inStream.available()!=0);


                //get manifest
                String [] parts = s.split(",");


                //request for a single image
                outStream.write(parts[0].getBytes("US-ASCII"));

                //path = parseGPS(s);
                outStream.close();
                inStream.close();
                imageSocket.close();
            }

            catch  (UnknownHostException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }






        }

        protected void connect() {
            String s  = "";
            String hostName = "g12host.ddns.net";
            int portNumber = 3307;


            byte[] b = new byte[256];

            try {

                Socket echoSocket = new Socket(hostName, portNumber);

                OutputStream outStream = echoSocket.getOutputStream();
                InputStream inStream = echoSocket.getInputStream();

                outStream.write(("Hello\nDevice:Android \n Id:1").getBytes("US-ASCII"));
                inStream.read(b, 0, 256);

                s = new String(b);
                if(!s.contains("OK")){
                    outStream.close();
                    inStream.close();
                    echoSocket.close();
                    return;
                }
                outStream.write(("REG:GPS \n DEVICE:1").getBytes("US-ASCII"));


                //byte array, offset, length

                do {
                    inStream.read(b, 0, 256);
                    s += new String(b, "US-ASCII");
                } while (inStream.available()!=0);

                path = parseGPS(s);
                outStream.close();
                inStream.close();
                echoSocket.close();
            }

            catch  (UnknownHostException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void... params) {
            connect();
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Void result) {
            pathPoints = path;
            polyline.setPoints(pathPoints);
            patientMarker.setPosition(pathPoints.get(pathPoints.size() - 1));
        }
    }
}
