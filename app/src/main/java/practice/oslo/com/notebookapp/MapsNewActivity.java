package practice.oslo.com.notebookapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsNewActivity extends FragmentActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng place;
    private SupportMapFragment supportMapFragment;
    private GoogleApiClient apiClient;
    private MarkerOptions marker;
    private Marker currentMarker, itemMarker;
    private String placeTitle;
    private LocationManager locationManager;
    private Location currentPlace;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if(!apiClient.isConnected() && currentMarker != null){
            apiClient.connect();
        }
    }

    private void setUpBasics(){
        place = new LatLng(39.980271, -75.157053);
        placeTitle = "Temple University";
        marker = new MarkerOptions();
        marker.title(placeTitle).snippet("We the T").position(place);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Criteria criteria = new Criteria();

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
        // Do a null check to confirm that we have not already instantiated the map.
        if (supportMapFragment == null) {
            // Try to obtain the map from the SupportMapFragment.
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //mMap = supportMapFragment.getMap();
            supportMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            mMap = supportMapFragment.getMap();
            if ( supportMapFragment != null) {
                //supportMapFragment.getMapAsync(this);
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    protected void loadMap(LatLng place){
        //mMap = supportMapFragment.getMap();

        CameraPosition position = new CameraPosition.Builder().target(place).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), new GoogleMap.CancelableCallback(){

            @Override
            public void onFinish() {
                if(itemMarker !=null)
                    itemMarker.showInfoWindow();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //loadMap();
        setUpBasics();
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(marker);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        configureApiClient();
        configureLocation();
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lng = intent.getDoubleExtra("lng", 0.0);
        if(lat != 0.0 && lng !=0.0){
            LatLng itemplace = new LatLng(lat, lng);
            addOwnMarker(itemplace, intent.getStringExtra("title"), intent.getStringExtra("datetime"));
            loadMap(itemplace);
        } else {
            if(!apiClient.isConnected()){
                apiClient.connect();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPlace = location;
        LatLng newplace = new LatLng(location.getLatitude(), location.getLongitude());

        if(currentMarker == null){
            currentMarker = mMap.addMarker(marker.position(newplace));
        } else {
            currentMarker.setPosition(newplace);
        }
        loadMap(newplace);
    }


    private void configureLocation(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    private synchronized void configureApiClient(){
        apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, MapsNewActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    private void addOwnMarker(LatLng place, String title, String content){
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        marker.position(place).title(title).snippet(content).icon(icon);
        itemMarker = mMap.addMarker(marker);
    }

    protected void setListeners(){
        final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(!apiClient.isConnected()){
                            apiClient.connect();
                        }
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (marker.equals(itemMarker)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(MapsNewActivity.this);

                    ab.setTitle(R.string.title_update_location)
                            .setMessage(R.string.message_update_location)
                            .setCancelable(true);

                    ab.setPositiveButton("update", onClickListener);
                    ab.setNeutralButton("clear", onClickListener);
                    ab.setNegativeButton(android.R.string.cancel, onClickListener);

                    ab.show();
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentMarker)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(MapsNewActivity.this);

                    ab.setTitle(R.string.title_current_location)
                            .setMessage(R.string.message_current_location)
                            .setCancelable(true);

                    ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent result = new Intent();
                            result.putExtra("lat", currentPlace.getLatitude());
                            result.putExtra("lng", currentPlace.getLongitude());
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        }
                    });
                    ab.setNegativeButton(android.R.string.cancel, null);

                    ab.show();

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int error_code = connectionResult.getErrorCode();
        if(error_code == ConnectionResult.SERVICE_MISSING){
            Toast.makeText(this, "google play service missing", Toast.LENGTH_LONG).show();
        }
    }

}
