package com.example.iotapplication.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iotapplication.Adapter.AdapterDevices;
import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.example.iotapplication.DetailsDeviceActivity;
import com.example.iotapplication.Model.Device;
import com.example.iotapplication.Model.Status;
import com.example.iotapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements AdapterDevices.ItemOnCLickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static String hold = "";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    private RecyclerView mRecyclerview ;
    FirebaseDatabase firebaseDatabase ;
    FirebaseAuth fAuth;
    DatabaseReference db ;
    AdapterDevices adapterDevices;
    FloatingActionButton fbtn ;
    CustomLoadingDialog dialog ;
    List<Device> mListDevices = new ArrayList<>();
    List<Status> mListStatus = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);
        dialog = new CustomLoadingDialog(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        readData();

        adapterDevices = new AdapterDevices(mListStatus,getActivity(),this);
        mRecyclerview.setAdapter(adapterDevices);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.hasFixedSize();

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDialog(Gravity.CENTER);
            }
        });

        return view ;
    }

    private void writeHistory(String deviceName,Device dev){
        FirebaseUser user = fAuth.getCurrentUser();
        String path= "history/"+user.getUid()+"/"+"list-device/"+deviceName;
        db = firebaseDatabase.getReference(path) ;
        db.push().setValue(dev);
    }

    private void notifyDialog(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_dialog);
        Log.d( "notifyDialog: ", "Notification");

        Window window = dialog.getWindow();
        if (window == null ) return ;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Log.d( "notifyDialog: ", "Notification1");

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
                Device device = new Device(name,"");
                Log.d("onClick: ", name);
                if (name.equals("humidity") || name.equals("độ ẩm")) {
                    device.setNameDevice(device.getNameDevice().substring(0,1).toUpperCase() + device.getNameDevice().substring(1));
                    device.setImage(R.drawable.humidity);
                    device.setUnit("%");
                }
                else if (name.equals("temperature") || name.equals("nhiệt độ")) {
                    device.setNameDevice(device.getNameDevice().substring(0,1).toUpperCase() + device.getNameDevice().substring(1));
                    device.setImage(R.drawable.hot);
                    device.setUnit("\u2103");
                }
                else if (name.equals("moisture") || name.equals("độ ẩm đất")) {
                    device.setNameDevice(device.getNameDevice().substring(0,1).toUpperCase() + device.getNameDevice().substring(1));
                    device.setUnit("%");
                    device.setImage(R.drawable.moisturizing);
                }else {
                    device.setNameDevice(device.getNameDevice().substring(0,1).toUpperCase() + device.getNameDevice().substring(1));
                    device.setImage(R.drawable.icons8_device_64);
                }
                device.setValues("0");
                device.setThreshold(0);
                mListDevices.add(device);
                adapterDevices.notifyDataSetChanged();
                FirebaseUser user = fAuth.getCurrentUser();
                String devices = "list-devices/"+user.getUid()+"/sensor/";
                db = firebaseDatabase.getReference(devices);
                HashMap<String,Object> keyMap = new HashMap<>();
                keyMap.put(device.getNameDevice(),device);
                db.updateChildren(keyMap);
                if(device.getValues().length() > 1 ) writeHistory(device.getNameDevice(),device);
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                Status status = new Status();
                status.setStatus(1);
                status.setTime(year+"-"+month+"-"+day+"-"+hour+"-"+minute);
                status.setNameDevice(device.getNameDevice());
                mListStatus.add(status);
                String statusDevice = "list-user-devices/"+user.getUid();
                db = firebaseDatabase.getReference(statusDevice);
                HashMap<String,Object> keyMapStatus = new HashMap<>();
                keyMapStatus.put(device.getNameDevice(),status);
                db.updateChildren(keyMapStatus);
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
    private void readData() {
        FirebaseUser user = fAuth.getCurrentUser();
        String path= "list-user-devices/"+user.getUid();
        db = firebaseDatabase.getReference(path) ;


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.startLoadingDialog();
                mListStatus.clear();
                for ( DataSnapshot snap: snapshot.getChildren()
                ) {
//                    Log.d("onChildAdded: ", snap.getKey() + " ");
                    //Device device = snap.getValue(Device.class);
                    Status status = snap.getValue(Status.class);

                    if (status == null) continue;
//                    Log.d("onChildAdded Sensor: ", device.toString()+" ");
                    if (status.getNameDevice().equals("Temperature") ||
                            status.getNameDevice().equals("temperature")) {
                        status.setImage(R.drawable.hot);
                    }
                    else if(status.getNameDevice().equals("Humidity")
                            ||status.getNameDevice().equals("humidity")) {
                        status.setImage(R.drawable.humidity);
                    }else if(status.getNameDevice().equals("Moisture") ||
                            status.getNameDevice().equals("moisture")) {
                        status.setImage(R.drawable.moisturizing);
                    }else {
                        status.setImage(R.drawable.icons8_device_64);
                    }
                    mListStatus.add(status);
                }
                adapterDevices.notifyDataSetChanged();
                dialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismissDialog();
                Toasty.error(getActivity(),"Please check internet !",Toasty.LENGTH_SHORT).show();
            }
        });
    }
    private void initUI(View view) {
        fbtn = view.findViewById(R.id.btn_add);
        mRecyclerview = view.findViewById(R.id.list_device);
    }


    @Override
    public void getPosition(int position) {
//        Toast.makeText(getContext(), "Position", Toast.LENGTH_SHORT).show();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("list-devices/"+user+"/sensor/"
                        +mListStatus.get(position).getNameDevice());


        Toasty.info(getContext(),"Position "+ position,Toasty.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),
                DetailsDeviceActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("nameDevice", mListStatus.get(position).getNameDevice());
        intent.putExtras(bundle);
        startActivity(intent);
    }



}