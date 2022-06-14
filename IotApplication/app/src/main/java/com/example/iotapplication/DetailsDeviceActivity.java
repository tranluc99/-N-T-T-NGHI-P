package com.example.iotapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.iotapplication.Model.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class DetailsDeviceActivity extends AppCompatActivity {
    static String tempDevice ="";
    ArcGauge arcGauge ;
    TextView nameDevice;
    EditText edtThreshHold ;
    Button btnSettings, btnBack;
    FirebaseAuth fAuth ;
    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_device);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        arcGauge = findViewById(R.id.arcGauge);
        arcGauge.setGaugeBackGroundColor( Color.parseColor("#00b20b"));
        arcGauge.setDisplayValuePoint(true);
        arcGauge.setValueColor( Color.parseColor("#ce0000"));
        Range r = new Range();
        r.setColor(Color.parseColor("#ce0000"));
        arcGauge.addRange(r);
        nameDevice = findViewById(R.id.device) ;
        fAuth = FirebaseAuth.getInstance();
        edtThreshHold = findViewById(R.id.min);

        if (bundle != null){
            tempDevice = bundle.getString("nameDevice");
        }
        updateStatusValue();
        edtThreshHold.setEnabled(false);
        btnSettings = findViewById(R.id.btn_setting);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailsDeviceActivity.this, HomeActivity.class));
                finish();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check == 0){
                    edtThreshHold.setEnabled(true);
                    check = 1 ;
                }else {
                        int Max = Integer.valueOf(edtThreshHold.getText().toString().trim());

                        FirebaseUser user = fAuth.getCurrentUser();
                        String path = "list-devices/" + user.getUid() + "/sensor/" + tempDevice;
                        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
                        DatabaseReference db = firebaseDB.getReference(path);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("threshold", Max);
                        db.updateChildren(hashMap);
                        edtThreshHold.setEnabled(false);
                        check = 0;
                        Toasty.success(DetailsDeviceActivity.this,"Successful Settings", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void updateStatusValue() {
        FirebaseUser user = fAuth.getCurrentUser();
        String path= "list-devices/"+user.getUid()+"/sensor/"+tempDevice;
        Log.d( "updateStatusValue: ", path);
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDB.getReference(path);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot == null) return;
                Device device = snapshot.getValue(Device.class);
                if(device != null)
                {
                    arcGauge.setValue(Double.valueOf(String.valueOf(device.getValues())));
                    edtThreshHold.setText(String.valueOf(device.getThreshold()));
                    nameDevice.setText(device.getNameDevice() + "( "+((device.getUnit() != null && device.getUnit().length() > 0) ? device.getUnit() : "UnKown Unit") +" )");

                }

//                Log.d( "onDataChange: ", device+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}