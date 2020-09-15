package com.example.p1uber;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.VideoView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class UberMaps extends FragmentActivity implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener {

    boolean locPerm;
    private LatLng current = null;
    private GoogleMap mMap;
    private Marker userMark;
    private Button driversButton;
    private FusedLocationProviderClient fusedLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_uber_maps);

        Intent intent = getIntent();
        locPerm = intent.getBooleanExtra("locPerm", true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        Button timeButton = (Button) findViewById(R.id.time_chooser);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeChooser = new TimeSelection();
                timeChooser.show(getSupportFragmentManager(), "time chooser");
            }
        });

        driversButton = (Button) findViewById((R.id.drivers));
        driversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UberMaps.this, Journey.class);
                EditText text = (EditText) findViewById(R.id.source);
                String info = "From: " + text.getText().toString();
                text = (EditText) findViewById(R.id.dest);
                info = info + ", To: " + text.getText().toString();
                TextView text2 = (TextView) findViewById(R.id.time_chooser);
//                info = info + ", At: " + text2.getText().toString();
                intent.putExtra("travel_info", info);
                startActivityForResult(intent, 1);
            }
        });
        driversButton.setVisibility(View.GONE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView timeText = (TextView) findViewById(R.id.time);
        timeText.setText(hourOfDay + ":" + minute);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(18);

        try {
            // GET THE CURRENT LOCATION OF THE DEVICE
            fusedLocationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        current = new LatLng(location.getLatitude(), location.getLongitude());
                        userMark = mMap.addMarker(new MarkerOptions().position(current).title("You"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                    }
                }
            });
        }
        catch (SecurityException e) {
            Log.d("error", "Error Message: " + e.getMessage());
            // IF FAILURE THEN SET TO ST ANDREWS
            current = new LatLng(56.3403902, -2.7955844);
        }

        placeDriver("Jack Ford", 56.3403902, -3);
        placeDriver("Rolland Royce", 56.3404, -2.79);
        placeDriver("Dino Ferrari", 56.33, -2.77);
        placeDriver("Totoro", 56.3, -2.8);

        mMap.setMinZoomPreference(6);
    }

    public void placeDriver(String driver, double lat, double longitude) {
        LatLng drvPos = new LatLng(lat, longitude);
        mMap.addMarker(new MarkerOptions().position(drvPos).title(driver).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    // PLACES START AND END MARKERS ON MAP
    public void search(View view) {
        EditText source = (EditText) findViewById(R.id.source);
        String src = source.getText().toString();

        EditText destination = (EditText) findViewById(R.id.dest);
        String dest = destination.getText().toString();
        List<Address> addresses;
        List<Address> addresses2;

        if(src != null || !src.equals("") || dest != null || !dest.equals("")) {
            // PLACES MARKER AT START POSITION
            Geocoder geo = new Geocoder(this);
            try {
                addresses = geo.getFromLocationName(src, 1);
            }
            catch (IOException e) {
                Log.d("error", "IOError!!!" + e.getMessage());
                addresses = null;
                e.printStackTrace();
            }
            Address add = addresses.get(0);
            LatLng startcoords = new LatLng(add.getLatitude(), add.getLongitude());
            mMap.addMarker(new MarkerOptions().position(startcoords).title("Start Point"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(startcoords));

            // PLACES MARKER AT END POSITION
            try {
                addresses2 = geo.getFromLocationName(dest, 1);
            }
            catch (IOException e) {
                addresses2 = null;
                Log.d("error", "IOError" + e.getMessage());
                e.printStackTrace();
            }
            add = addresses2.get(0);
            LatLng endcoords = new LatLng(add.getLatitude(), add.getLongitude());
            mMap.addMarker(new MarkerOptions().position(endcoords).title("End Point"));

            userMark.remove();
            driversButton.setVisibility(View.VISIBLE);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String res = data.getStringExtra("response");

            EditText source = (EditText) this.findViewById(R.id.source);
            source.setVisibility(View.GONE);
            EditText dest = (EditText) this.findViewById(R.id.dest);
            dest.setVisibility(View.GONE);
            TextView time = (TextView) this.findViewById(R.id.time_chooser);
            time.setVisibility(View.GONE);

            Button timeButton = (Button) this.findViewById(R.id.time_chooser);
            timeButton.setVisibility(View.GONE);
            Button search = (Button) this.findViewById(R.id.search_button);
            search.setVisibility(View.GONE);
            driversButton.setVisibility(View.GONE);

            LinearLayout layout = (LinearLayout) this.findViewById(R.id.places);
            TextView journey = new TextView(this);
            journey.setText(res);
            layout.addView(journey);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    driverHere();
                }
            }, 5000);
        }
    }

    //
    public void driverHere() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Find your driver!");
        alert.setMessage("Your driver has arrived!");
        alert.setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startJourney();
            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }

    public void startJourney() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                arrived();
            }
        }, 5000);
    }

    public void arrived() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Arrived!");
        alert.setMessage("You have arrived at your destination!");
        alert.setPositiveButton("Brilliant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }
}
