package com.example.p1uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Journey extends AppCompatActivity {

    DriverDB drivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_journey);

        if (savedInstanceState == null) {
            Bundle extra = getIntent().getExtras();
            String userInfo = extra.getString("travel_info");
            TextView header = (TextView) findViewById(R.id.textMain);
            header.setText(userInfo);
        }
        else {
            String userInfo = (String) savedInstanceState.getSerializable("travel_info");
            TextView header = (TextView) findViewById(R.id.textMain);
            header.setText(userInfo);
        }

        drivers = new DriverDB(this);
        driverResponses();
    }

    public void driverResponses() {
        ArrayList<TextView> nameVs = new ArrayList<TextView>();
        nameVs.add((TextView) findViewById(R.id.name1));
        nameVs.add((TextView) findViewById(R.id.name2));
        nameVs.add((TextView) findViewById(R.id.name3));
        nameVs.add((TextView) findViewById(R.id.name4));

        ArrayList<TextView> rateVs = new ArrayList<TextView>();
        rateVs.add((TextView) findViewById(R.id.rating1));
        rateVs.add((TextView) findViewById(R.id.rating2));
        rateVs.add((TextView) findViewById(R.id.rating3));
        rateVs.add((TextView) findViewById(R.id.rating4));

        ArrayList<TextView> carVs = new ArrayList<TextView>();
        carVs.add((TextView) findViewById(R.id.car1));
        carVs.add((TextView) findViewById(R.id.car2));
        carVs.add((TextView) findViewById(R.id.car3));
        carVs.add((TextView) findViewById(R.id.car4));

        ArrayList<String> driverNames = new ArrayList<String>();
        driverNames.addAll(drivers.getDrivers());

        for (int i = 0; i < nameVs.size(); i++) {
            Log.d("name", "Name of driver is " + driverNames.get(i));
            nameVs.get(i).setText(driverNames.get(i));
            String[] driver = drivers.getDriver(driverNames.get(i));
            rateVs.get(i).setText(driver[0] + "/5");
            carVs.get(i).setText(driver[1]);
        }
    }

    public void confirmation(final String name, final String rating, final String car, final String pickUp) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmed!");
        alert.setMessage("You have confirmed for " + name + " (Rating " + rating + ") in a " + car + " to take you " + pickUp);
        alert.setPositiveButton("Cool", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                String res = name + " in a " + car + " will take you " + pickUp + ", At: ";
                intent.putExtra("response", res);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        alert.setCancelable(true);
        alert.create().show();
    }

    public void confirm(View view) {
        TextView rating = null;
        TextView car = null;
        TextView userData = (TextView) findViewById(R.id.textMain);
        switch(view.getId()) {
            case R.id.select1:
                TextView name1 = (TextView) this.findViewById(R.id.name1);
                rating = this.findViewById(R.id.rating1);
                car = this.findViewById(R.id.car1);
                confirmation(name1.getText().toString(), rating.getText().toString(), car.getText().toString(), userData.getText().toString());
                break;
            case R.id.select2:
                TextView name2 = (TextView) this.findViewById(R.id.name2);
                rating = this.findViewById(R.id.rating2);
                car = this.findViewById(R.id.car2);
                confirmation(name2.getText().toString(), rating.getText().toString(), car.getText().toString(), userData.getText().toString());
                break;
            case R.id.select3:
                TextView name3 = (TextView) this.findViewById(R.id.name3);
                rating = this.findViewById(R.id.rating3);
                car = this.findViewById(R.id.car3);
                confirmation(name3.getText().toString(), rating.getText().toString(), car.getText().toString(), userData.getText().toString());
                break;
            case R.id.select4:
                TextView name4 = (TextView) this.findViewById(R.id.name4);
                rating = this.findViewById(R.id.rating4);
                car = this.findViewById(R.id.car4);
                confirmation(name4.getText().toString(), rating.getText().toString(), car.getText().toString(), userData.getText().toString());
                break;
            default:
                break;
        }
    }
}
