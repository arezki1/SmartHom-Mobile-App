package com.arezki.myapplication;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    private SeekBar sBar;
    private TextView tView;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getting the Temperature and Humidity reading from database

        DatabaseReference database1 = FirebaseDatabase.getInstance().getReference();

        database1.child("temp1").addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Object values = snapshot.getValue(Object.class);

                                        //get the temperature values here to instantly be displayed in the mobile app
                                        try{
                                            JSONObject obj = new JSONObject(values.toString());
                                            JSONObject snippet = obj.getJSONObject("data");

                                            String Temperature= snippet.getString("Temperature");
                                            double temperature=Double.parseDouble(Temperature);
                                            System.out.println("The temperature is "+temperature);


                                            TextView tv = (TextView) findViewById(R.id.textView2);
                                            if (tv != null)
                                            {tv.setText(temperature +" °C");}
                                        } catch (JSONException e) {
                                            Log.e("MYAPP", "unexpected JSON exception", e);
                                            // Do something to recover ... or kill the app.
                                            throw new RuntimeException(e);
                                        }

                                        //get the humidity values here to instantly be displayed in the mobile app
                                        try{
                                            JSONObject obj = new JSONObject(values.toString());
                                            JSONObject snippet = obj.getJSONObject("data");

                                            String Humidity=snippet.getString("Humidity");
                                            double humidity=Double.parseDouble(Humidity);
                                            System.out.println("The humidity is "+humidity);
                                            TextView tv = (TextView) findViewById(R.id.textView);
                                            if (tv != null)
                                            {tv.setText(humidity + " %");}
                                        } catch (JSONException e) {
                                            Log.e("MYAPP", "unexpected JSON exception", e);
                                            // Do something to recover ... or kill the app.
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                                    throw new RuntimeException();
                                }
                            });



        //Display the message
        final Button orderBtn4 = findViewById(R.id.button4);
        orderBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read from the database

                EditText edit = (EditText)findViewById(R.id.editText);

                String result = edit.getText().toString();
                final DatabaseReference message = database.getReference("message");
                message.setValue(result);
                Toast.makeText(getApplicationContext(), "sent", Toast.LENGTH_LONG).show();

            }
        });

// from here we are working on the seekbar to get the values change

        sBar =  findViewById(R.id.seekBar);

        //tView.setText(sBar.getProgress() + "/" + sBar.getMax());
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;

                final DatabaseReference light = database.getReference("light");
                light.setValue(pval);
                System.out.println(pval);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
                //tView.setText(pval + "/" + seekBar.getMax());
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //tView.setText(pval + "/" + seekBar.getMax());

            }
        });


        // Buzzer on/off

        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    // get the buzzer reference from data base
                    final DatabaseReference buzzer = database.getReference("buzzer");

                    //set its value to "on"
                    buzzer.setValue("on");
                } else {
                    // The toggle is disabled
                    final DatabaseReference buzzer = database.getReference("buzzer");

                    //set the value to "off"
                    buzzer.setValue("off");
                }
            }
        });


        // from here we are setting the threshold of the the light sensor
        sBar =  findViewById(R.id.seekBar2);
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;

                //Here we are alimiting the user to use only numbers 10,20,30,40 .... to 100
                // no need of other values
                if (progress % 10 == 0) {

                    //we are getting the thresh reference from firebase database
                    final DatabaseReference light = database.getReference("thresh");

                    //we are setting it up to the value chosen by the user
                    light.setValue(pval);

                    //we are setting the textView to the value chosen so that the user can get informed visually
                    TextView tv1 = findViewById(R.id.textView3);

                    tv1.setText(" Set to: "+pval);
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

}
