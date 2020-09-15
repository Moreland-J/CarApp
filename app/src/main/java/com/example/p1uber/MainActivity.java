package com.example.p1uber;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    UserDB users;
    DriverDB drivers;
    private boolean locPerm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        makeDBs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!locPerm) {
            getPerm();
        }
    }

    // FILL DBs
    public void makeDBs() {
        users = new UserDB(this);
        drivers = new DriverDB(this);

        users.addUser("Ricky", "secretword");
        users.addUser("needarid3plz", "password");

        drivers.addDriver("lifttosomewhere", 56.3403902, 3, 2, "Fiat");
        drivers.addDriver("Rolland Royce", 56.3404, -2.7, 5, "Rolls Royce A5 Silver");
        drivers.addDriver("Dino Ferrari", 56.33, -2.77, 5, "Ferrari 458 Red");
        drivers.addDriver("Totoro", 56.3, -2.8, 3, "Citreon 67 Green");
    }

    // CHECK WHETHER APP HAS PERMISSION TO ACCESS LOCATION
    public boolean permitted() {
        final LocationManager man = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!man.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            permitError();
            return false;
        }
        return true;
    }

    // REQUESTS ACCESS TO LOCATION
    public void permitError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To use this app you must allow access to your location:");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent yesGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(yesGPS, 9002);
            }
        });
        AlertDialog al = builder.create();
        al.show();
    }

    public void getPerm() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locPerm = true;
        }
        else {
            // THIS IS ALL YOU NEED
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 9003);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9002) {
            if (!locPerm) {
                getPerm();
            }
        }
    }

    public boolean checkSignIn(View view) {
        EditText user = (EditText) findViewById(R.id.user);
        String username = user.getText().toString();

        if (users.recordExists(username)) {
            signIn();
            return true;
        }
        else {
            TextView textView = findViewById(R.id.textView);
            textView.setText("Login information was incorrect");
            return false;
        }
    }

    public boolean register(View view) {
        EditText user = (EditText) findViewById(R.id.user);
        String username = user.getText().toString();

        if (users.recordExists(username)) {
            TextView textView = findViewById(R.id.textView);
            textView.setText("Username already exists");
            return false;
        }
        else {
            EditText pass = (EditText) findViewById(R.id.pass);
            String password = user.getText().toString();
            users.addUser(username, password);
            signIn();
            return true;
        }
    }

    public void signIn() {
        Intent intent = new Intent(this, UberMaps.class);
//        intent.putExtra("locPerm", locPerm);
        startActivity(intent);
    }
}

// Code was taken from:
// https://gist.github.com/mitchtabian/2b9a3dffbfdc565b81f8d26b25d059bf
// for
