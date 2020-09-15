package com.example.p1uber;

import android.app.Application;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class App extends Application {

    UserDB users;
    DriverDB drivers;

    @Override
    public void onCreate() {
        super.onCreate();
        makeDBs();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
        drivers.addDriver("Toyo Ta", 56.3, -2.8, 3, "Citreon 67 Green");
    }

    public SQLiteOpenHelper getUsers() {
        return users;
    }

    public SQLiteOpenHelper getDrivers() {
        return drivers;
    }
}
