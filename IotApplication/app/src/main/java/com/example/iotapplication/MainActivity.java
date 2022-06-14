package com.example.iotapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapplication.Model.Device;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView listDevices ;
    FloatingActionButton fbtAddDevice;

    List<Device> mListDevices = new ArrayList<>();
    FirebaseDatabase firebaseDatabase ;

    DatabaseReference db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        listDevices = findViewById(R.id.list_device);
        fbtAddDevice = findViewById(R.id.btn_add);

        fbtAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDialog(Gravity.CENTER);
            }
        });

    }

    private void notifyDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_dialog);

        Window window = dialog.getWindow();
        if (window == null ) return ;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity ;
        window.setAttributes(windowAttribute);

        if (Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        }else{
            dialog.setCancelable(false);
        }

        EditText edtNameDevice = dialog.findViewById(R.id.edt_name);
        Button btYes = dialog.findViewById(R.id.btn_ok);
        Button btNo = dialog.findViewById(R.id.btn_no);

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String name = edtNameDevice.getText().toString().trim();
                Device device = new Device(name,"");
                mListDevices.add(device);
                String devices = "list-devices/"+1+"/sensor/"+edtNameDevice.getText().toString().trim();
                db = firebaseDatabase.getReference(devices);
                db.setValue(device);
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void readData(){
        db = firebaseDatabase.getReference();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}