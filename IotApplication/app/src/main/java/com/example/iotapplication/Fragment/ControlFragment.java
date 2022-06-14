package com.example.iotapplication.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapplication.Adapter.AdapterDevicesControl;
import com.example.iotapplication.Model.DeviceControl;
import com.example.iotapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String param1, String param2) {
        ControlFragment fragment = new ControlFragment();
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
    static int check = 0;
    ImageView imgPump ;
    FirebaseAuth fAuth ;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference db ;
    List<DeviceControl> mListDevices = new ArrayList<>();
    RecyclerView mListDeviceControl ;
    AdapterDevicesControl adapterDevices;
    FloatingActionButton fbtn ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        initUI(view);
        adapterDevices = new AdapterDevicesControl(mListDevices,getActivity());
        mListDeviceControl.setAdapter(adapterDevices);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        mListDeviceControl.setLayoutManager(linearLayoutManager);
        mListDeviceControl.hasFixedSize();
        readStatus();
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDialog(Gravity.CENTER);
            }
        });
        return view ;
    }
    private void notifyDialog(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_dialog);
        //Log.d( "notifyDialog: ", "Notification");

        Window window = dialog.getWindow();
        if (window == null ) return ;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

       // Log.d( "notifyDialog: ", "Notification1");

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

                name = name.toLowerCase();

                DeviceControl deviceControl = new DeviceControl();
                if (name.equals("lamp") || name.equals("đèn") || name.equals("đen") || name.equals("den")) {
                    deviceControl.setImage(R.drawable.lightbulb);
                }
                else if (name.equals("air-conditioner") || name.equals("air conditioner") || name.equals("điều hòa")) {
                    deviceControl.setImage(R.drawable.air_conditioner);
                }else if (name.equals("pump") || name.equals("bơm") || name.equals("bom")) {
                    deviceControl.setImage(R.drawable.pump);
                }else {
                    deviceControl.setImage(R.drawable.icons8_device_64);
                }


                deviceControl.setNameDevice(name.substring(0,1).toUpperCase()+name.substring(1));
                deviceControl.setStatusDevice(0); // 1 hoạt động , 0 là không hoạt động
                mListDevices.add(deviceControl);
                FirebaseUser user = fAuth.getCurrentUser();
                String devices = "list-devices-control/"+user.getUid()+"/devices-control/"+deviceControl.getNameDevice();
                db = firebaseDatabase.getReference(devices);
                db.setValue(deviceControl);
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void initUI(View view) {
        mListDeviceControl = view.findViewById(R.id.list_device_control);
        fbtn = view.findViewById(R.id.btn_add_control);

    }
    private void readStatus() {
        FirebaseUser user = fAuth.getCurrentUser();
        String devices = "list-devices-control/"+user.getUid()+"/devices-control/";
        db = firebaseDatabase.getReference(devices);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListDevices.clear();
                for (DataSnapshot snap : snapshot.getChildren()
                     ) {
                    DeviceControl deviceControl = snap.getValue(DeviceControl.class);
                    if (deviceControl.getNameDevice().equals("Light") || deviceControl.getNameDevice().equals("light")) {
                        deviceControl.setImage(R.drawable.lightbulb);
                    }
                    else if(deviceControl.getNameDevice().equals("Pump") ||
                            deviceControl.getNameDevice().equals("pump")) {
                        deviceControl.setImage(R.drawable.pump_on);
                    }else if(deviceControl.getNameDevice().equals("Televisition") ||
                            deviceControl.getNameDevice().equals("televisition") ) {
                        deviceControl.setImage(R.drawable.television);
                    }else if (deviceControl.getNameDevice().equals("Air Conditioner") ||
                            deviceControl.getNameDevice().equals("Air_Conditioner") ||
                            deviceControl.getNameDevice().equals("Air-Conditioner")||
                            deviceControl.getNameDevice().equals("air-conditioner")){
                        deviceControl.setImage(R.drawable.air_conditioner);
                    }else if (deviceControl.getNameDevice().equals("Fan ") ||
                            deviceControl.getNameDevice().equals("fan")){
                        deviceControl.setImage(R.drawable.fan);
                    }else {
                        deviceControl.setImage(R.drawable.icons8_device_64);
                    }
                    mListDevices.add(deviceControl);
                }
                adapterDevices.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void writeData(int status) {
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
//        DatabaseReference ref = firebaseDatabase.getReference("History");
//        Operation operation = new Operation();
//        operation.setStatus(status);
//        operation.setTime(String.valueOf(System.currentTimeMillis()));
//        ref.child(String.valueOf(System.currentTimeMillis())).setValue(operation);
    }
}