package practice.oslo.com.notebookapp;import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, OnMapReadyCallback{
    /*
        need to google api connection and the location listener to update the locations
    */
    /*
        use google's location API to read the location of the current device
    */
    // client side api
    private GoogleApiClient googleApiClient;
    // the object to request location
    private LocationRequest locationRequest;
    // location object to store the current location
    private Location currentLocation;
    // Markers for the current location and the stored locations
    private Marker currentMarker, itemMarker;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private SupportMapFragment supportMapFragment;
    public static final LatLng PHILLY = new LatLng(39.9516, -75.1567);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        setGoogleApiClient();
        setLocationRequest();
        Intent startIntent = getIntent();
        double lat = startIntent.getDoubleExtra("lat", 0.0);
        double lng = startIntent.getDoubleExtra("lng", 0.0);
        setUp(startIntent, lat, lng);
    }

    private void setUp(Intent intent, double lat, double lng){
        if( (lat != 0.0) && (lng != 0.0) ){
            LatLng newLocation = new LatLng(lat, lng);
            addOwnMarker(newLocation, intent.getStringExtra("title"), intent.getStringExtra("datetime"));
            moveMap(newLocation);
        } else {
            // check if the api client is connected
            if(googleApiClient.isConnected() == false) {
                googleApiClient.connect();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        //googleMap.setMyLocationEnabled(true);
        moveMap(PHILLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // every time when resuming the activity, reconnect to the google API client
        if(!googleApiClient.isConnected() && currentMarker != null){
            googleApiClient.connect();
        }

    }

    @Override
    protected void onPause(){
        super.onPause();

        // when pause the activity, remove the service so to save battery
        if( googleApiClient.isConnected() ) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            //googleApiClient.disconnect();
        }

    }

    @Override
    protected void onStop(){
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
            //finish();
        }

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
            mMap = supportMapFragment.getMap();
            //supportMapFragment.getMapAsync(this);
            if( mMap != null){
                //setUpMap();
                setListeners();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // create the latitude and longitude object for the place
        LatLng place = new LatLng(25, 121);
        // move the place
        //moveMap(place);
        addOwnMarker(place, "You r here", "google maps");
    }

    // move the map to the designated place
    private void moveMap(LatLng place){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(16).build();
        addOwnMarker(place, "Your are here", "Place Info");
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    // add marker to the current position
    private void addOwnMarker(LatLng place, String title, String snippet){
        // the Marker object allows users to set a marker to a position
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place).title(title).snippet(snippet).icon(icon);
        // when we are now adding marker, assign it to itemMarker
        itemMarker = mMap.addMarker(markerOptions);
    }

    /*
        connection call backs
     */
    @Override
    public void onConnected(Bundle bundle) {
        // when connected to Google services, update the location
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MapsActivity.this);
    }
    /*
        calls when connection suspended, the integer means the code of suspending
     */
    @Override
    public void onConnectionSuspended(int cause) {
        if( (cause == CAUSE_NETWORK_LOST) || (cause == CAUSE_SERVICE_DISCONNECTED) ) {
            googleApiClient.connect();
        }
    }

    /*
        the information will be passed in when connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        // if the error is because uninstallation of google service
        if(errorCode == ConnectionResult.SERVICE_MISSING){
            Toast.makeText(this, R.string.google_play_service_missing, Toast.LENGTH_LONG).show();
        }

    }

    /*
        when location changed, has to keep track of it
     */
    private synchronized void setGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /*
        if needs to receive the latest location information, has to create
        the location request service
     */
    private void setLocationRequest(){
        locationRequest = new LocationRequest();
        // receive new information every 1000 ms
        locationRequest.setInterval(10 * 1000);
        // set the fastest interval to read the location info as 1000 ms
        locationRequest.setFastestInterval(1000);
        // read the GPS location as top priority
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        // if location changed
        currentLocation = location;
        // set new latitude and longitude
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // if current marker not set yet
        if(currentMarker == null){
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            currentMarker.setPosition(latLng);
        }
        // after setting the markers, move to the current place
        moveMap(latLng);
    }

    private void setListeners(){
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(googleApiClient == null)
                            googleApiClient.connect();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        // return the intent back to ItemActivity
                        Intent returnback = new Intent();
                        returnback.putExtra("lat", 0.0);
                        returnback.putExtra("lng", 0.0);
                        setResult(Activity.RESULT_OK, returnback);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.equals(currentMarker)){
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertBuilder.setTitle("You are here").setMessage("Location Info").setCancelable(true);
                    alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("lat", currentLocation.getLatitude());
                            intent.putExtra("lng", currentLocation.getLongitude());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                    alertBuilder.setNegativeButton(android.R.string.cancel, null);
                    alertBuilder.show();
                    return true;
                }
                return false;
            }
        });

    }


}

