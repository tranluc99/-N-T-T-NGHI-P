package com.example.iotapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.example.iotapplication.Model.Device;
import com.example.iotapplication.Model.DeviceControl;
import com.example.iotapplication.Model.History;
import com.example.iotapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

    public RecyclerView mRecyclerView ;
    FirebaseDatabase fb ;
    DatabaseReference db ;
    MaterialSpinner spinner ;
    MaterialSpinner spinnerControl ;
    List<Object> listDevice = new ArrayList<>();
    List<String> listDeviceControl = new ArrayList<>();
    String[][] mTable;
    TableLayout tableLayout ;
    LinkedList<Object>linkedList = new LinkedList();
    LinkedList<Object>linkedListControl = new LinkedList();
    DataTable dataTable;
    CustomLoadingDialog dialog ;
    // AdapterOperation adapterOperation ;
    //List<Operation> mList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initUI(view);
        dialog = new CustomLoadingDialog(getActivity());
        getListDevices();
        getListDeviceControl();

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Toast.makeText(getContext(), "Get "+ item, Toast.LENGTH_SHORT).show();
                if (String.valueOf(item).contains("List") == false) {
                    spinnerControl.setSelectedIndex(0);
//                    dialog.startLoadingDialog();
                    getDetailSensor((String) item);
//                    getDataListDeviceSensorFromFirebase((String) item, time );

                }
            }
        });

        spinnerControl.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Toast.makeText(getContext(), "Get "+ item, Toast.LENGTH_SHORT).show();
                if (String.valueOf(item).contains("List") == false) {
                    spinner.setSelectedIndex(0);
//                    getDataListDeviceControlFromFirebase((String) item,);
                    getDetailControl((String)item);
                }
            }
        });
        readHistory();

        return view ;
    }

    private void getDetailSensor(String item) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path = "list-user-devices/"+user.getUid()+"/"+item + "/"+ "time";
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    String time = snapshot.getValue(String.class);
                    Log.d("Time = " , time);
                    getDataListDeviceSensorFromFirebase(item, time);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getDetailControl(String item) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path = "list-devices-control/"+user.getUid()+"/"+"devices-control/"+item + "/"+ "time";
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    String time = snapshot.getValue(String.class);
                    Log.d("Time = " , time);
                    getDataListDeviceControlFromFirebase(item, time);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initUI(View view ){
        spinner = view.findViewById(R.id.list_device_sensor);
        spinnerControl = view.findViewById(R.id.list_device_control_1);
        dataTable = view.findViewById(R.id.data_table);
    //    tableLayout = view.findViewById(R.id.table_list);
    }

    public void initTableSensor(Context context, LinkedList<Object> linkedList , String name ) {
        DataTableHeader header = new DataTableHeader.Builder()
                .item("ID", 100)
                .item("Name", 100)
                .item("Value", 100)
                .item("Time", 100)
    .build();
        ArrayList<DataTableRow> rows = new ArrayList<>();
        // define 200 fake rows for table
        for(int i=0;i<linkedList.size();i++) {
            History device = (History) linkedList.get(i);
            DataTableRow row = new DataTableRow.Builder()
                    .value((i+1)+"")
                    .value(name)
                    .value(device.getValues())
                    .value(device.getTime()).build();
            rows.add(row);
        }
        dataTable.setHeader(header);
        dataTable.setRows(rows);
        dataTable.inflate(context);

    }
    public void initTableControl(Context context, LinkedList<Object> linkedList,String name ) {
        DataTableHeader header = new DataTableHeader.Builder()
                .item("ID", 50)
                .item("Name", 150)
                .item("Status", 50)
                .item("Time", 150)
                .build();
        ArrayList<DataTableRow> rows = new ArrayList<>();
        // define 200 fake rows for table
        for(int i=0;i<linkedList.size();i++) {
            History device = (History) linkedList.get(i);
            DataTableRow row = new DataTableRow.Builder()
                    .value((i+1)+"")
                    .value(name)
                    .value(( Integer.valueOf( device.getValues()) == 1) ? "ON" : "OFF" )
                    .value(device.getTime()).build();
            rows.add(row);

        }

        dataTable.setHeader(header);
        dataTable.setRows(rows);
        dataTable.inflate(context);

    }
    public void getDataListDeviceControlFromFirebase(String deviceName,String time ) {

        String []timeTemp = time.split("-");
        List<Integer> baseTime = getListTime(timeTemp);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path = "history/"+user.getUid()+"/"+"list-device-control/"+deviceName;
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linkedListControl.clear();
                dialog.startLoadingDialog();

                for (DataSnapshot snap : snapshot.getChildren()
                ) {
                    History history = snap.getValue(History.class);
                    if(history.getTime() == null || history.getValues() == null) continue ;
//                    Log.d("History ", history.getTime());
                    String[]item = history.getTime().split("-");
                    List<Integer> coreTime = getListTime(item);
//                    Log.d("list - core : ", coreTime.toString());
//                    Log.d("list - base  : ", baseTime.toString());

                    int coreTime0 = coreTime.get(0);
                    int coreTime1 = coreTime.get(1);
                    int coreTime2 = coreTime.get(2);
                    int coreTime3 = coreTime.get(3);
                    int coreTime4 = coreTime.get(4);

                    int baseTime0 = coreTime.get(0);
                    int baseTime1 = coreTime.get(1);
                    int baseTime2 = coreTime.get(2);
                    int baseTime3 = coreTime.get(3);
                    int baseTime4 = coreTime.get(4);


                    if(baseTime0 == coreTime0) {
                        //  Log.d("BUG", baseTime.get(0)+" | "+  coreTime.get(0)    );

                        if(baseTime1 == coreTime1) {
                            //  Log.d("BUG", baseTime.get(1)+" | "+  coreTime.get(1)    );

                            if(baseTime2 == coreTime2){
                                if(baseTime3 == coreTime3){
                                    if(baseTime4 <= coreTime4){
                                        linkedListControl.add(history);
                                    }
                                }else {
                                    if(baseTime3 < coreTime3){
                                        linkedListControl.add(history);
                                    }
                                }
                            }else {
                                // Log.d("BUG", baseTime.get(2)+" | "+
                                //   coreTime.get(2)    );
                                if(baseTime2 < coreTime2){
                                    linkedListControl.add(history);
                                }
                            }
                        }else {
                            if(baseTime1 < coreTime1){
                                linkedListControl.add(history);
                            }
                        }
                    }else {
                        //Log.d("BUG", baseTime.get(0)+" | "+  coreTime.get(0)    );

                        if(baseTime0 < coreTime0){
                            linkedListControl.add(history);
                        }
                    }
                }

                Log.d( "onDataChange control: ",linkedListControl.size()+" ");
                initTableControl(getContext(),linkedListControl,deviceName);
               // getData();
                dialog.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismissDialog();

            }
        });
    }
    public List<Integer> getListTime(String time[] ) {
        List<Integer> lTime = new ArrayList<>();
        for(int i = 0 ; i < time.length ; i++){
            lTime.add(Integer.valueOf(time[i]));
        }
        return lTime;
    }
    public void getDataListDeviceSensorFromFirebase(String deviceName, String time ) {
        String []timeTemp = time.split("-");
        List<Integer> baseTime = getListTime(timeTemp);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path = "history/"+user.getUid()+"/"+"list-device/"+deviceName;
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linkedList.clear();
                for (DataSnapshot snap : snapshot.getChildren()
                     ) {
                    History history = snap.getValue(History.class);
                    if(history.getTime() == null || history.getValues() == null) continue ;
//                    Log.d("History ", history.getTime());
                    String[]item = history.getTime().split("-");
                    List<Integer> coreTime = getListTime(item);
//                    Log.d("list - core : ", coreTime.toString());
//                    Log.d("list - base  : ", baseTime.toString());

                    int coreTime0 = coreTime.get(0);
                    int coreTime1 = coreTime.get(1);
                    int coreTime2 = coreTime.get(2);
                    int coreTime3 = coreTime.get(3);
                    int coreTime4 = coreTime.get(4);

                    int baseTime0 = coreTime.get(0);
                    int baseTime1 = coreTime.get(1);
                    int baseTime2 = coreTime.get(2);
                    int baseTime3 = coreTime.get(3);
                    int baseTime4 = coreTime.get(4);


                    if(baseTime0 == coreTime0) {
                      //  Log.d("BUG", baseTime.get(0)+" | "+  coreTime.get(0)    );

                        if(baseTime1 == coreTime1) {
                          //  Log.d("BUG", baseTime.get(1)+" | "+  coreTime.get(1)    );

                            if(baseTime2 == coreTime2){
                                 if(baseTime3 == coreTime3){
                                     if(baseTime4 <= coreTime4){
                                        linkedList.add(history);
                                    }
                                }else {
                                    if(baseTime3 < coreTime3){
                                        linkedList.add(history);
                                    }
                                }
                            }else {
                                // Log.d("BUG", baseTime.get(2)+" | "+
                                      //   coreTime.get(2)    );
                                if(baseTime2 < coreTime2){
                                    linkedList.add(history);
                                }
                            }
                        }else {
                            if(baseTime1 < coreTime1){
                                linkedList.add(history);
                            }
                        }
                    }else {
                        //Log.d("BUG", baseTime.get(0)+" | "+  coreTime.get(0)    );

                        if(baseTime0 < coreTime0){
                            linkedList.add(history);
                        }
                    }
                }
//               Log.d( "onDataChange Sensor : " , linkedList.size() + " ");
//                dialog.dismissDialog();g

                initTableSensor(getContext(),linkedList,deviceName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getListDevices () {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path = "list-user-devices/"+user.getUid();
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDevice.clear();
                listDevice.add("List devices sensor");
                for (DataSnapshot snap : snapshot.getChildren()
                     ) {
                    listDevice.add(snap.getKey());
                }
                spinner.setItems(listDevice);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void  getListDeviceControl() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        String path ="list-devices-control/"+user.getUid()+"/devices-control/";
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDeviceControl.clear();
                listDeviceControl.add("List devices control");

                for (DataSnapshot snap : snapshot.getChildren()
                ) {
                    listDeviceControl.add(snap.getKey());
                }
                spinnerControl.setItems(listDeviceControl);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readHistory(){

    }
}