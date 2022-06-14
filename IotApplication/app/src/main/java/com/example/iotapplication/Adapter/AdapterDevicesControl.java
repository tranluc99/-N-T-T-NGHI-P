package com.example.iotapplication.Adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iotapplication.Model.DeviceControl;
import com.example.iotapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDevicesControl extends RecyclerView.Adapter<AdapterDevicesControl.DeviceViewHolder> {

    List<DeviceControl> mListDevices ;
    Context mContext;
    FirebaseDatabase firebaseDatabase ;
    FirebaseAuth firebaseAuth ;
    public AdapterDevicesControl(List<DeviceControl> mListDevices, Context mContext) {
        this.mListDevices = mListDevices;
        this.mContext = mContext;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device_control,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceControl devices = mListDevices.get(position);
        if(devices == null ) return ;
        holder.mTtitle.setText(devices.getNameDevice());
        Glide.with(mContext).load(devices.getImage()).into(holder.mImageIcon);
        holder.btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                holder.imgViewStatus.setBackgroundResource(R.drawable.bg_status_online);

                Toast.makeText(mContext, "position " + position + " On", Toast.LENGTH_SHORT).show();
                writeData(1,devices.getNameDevice(),devices);
                writeHistory(devices.getNameDevice(),devices);
            }
        });
        holder.btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                holder.imgViewStatus.setBackgroundResource(R.drawable.bg_status);
                writeData(0,devices.getNameDevice(),devices);
                Toast.makeText(mContext, "position " + position + " Off", Toast.LENGTH_SHORT).show();
                writeHistory(devices.getNameDevice(),devices);
            }
        });
        if (devices.getStatusDevice() == 0) {
            holder.imgViewStatus.setBackgroundResource(R.drawable.bg_status);
        }else {
            holder.imgViewStatus.setBackgroundResource(R.drawable.bg_status_online);
        }

        holder.btnRemoveControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRemove(mContext,holder,devices);
            }
        });
    }
    public void removeFirebase(DeviceControl device) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db ;
        FirebaseUser user = fAuth.getCurrentUser();
        String devices = "list-devices-control/"+user.getUid()+"/devices-control/"+device.getNameDevice();
        db = firebaseDatabase.getReference(devices);
        db.removeValue();
    }
    public void showDialogRemove(Context mContext, DeviceViewHolder holder, DeviceControl devices ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Warming");
        String question = "Bạn có muốn xóa device "+devices.getNameDevice()+" không ?";
        builder.setMessage(question);
        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext, "YES", Toast.LENGTH_SHORT).show();
                int position = holder.getAdapterPosition();
                mListDevices.remove(position);
                removeHistory(devices.getNameDevice());
                removeFirebase(devices);

                notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext, "NO", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return mListDevices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        public TextView mTtitle , btnRemoveControl ;
        public Button btnOn ;
        public Button btnOff;
        public ImageView imgViewStatus ;
        public CircleImageView mImageIcon ;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageIcon = itemView.findViewById(R.id.img_icon);
            btnOn = itemView.findViewById(R.id.btn_on);
            mTtitle = itemView.findViewById(R.id.tv_title);
            btnOff = itemView.findViewById(R.id.btn_off);
            imgViewStatus = itemView.findViewById(R.id.img_status);
            btnRemoveControl = itemView.findViewById(R.id.btn_remove_control);
        }
    }
    public void writeData(int status , String device , DeviceControl deviceControl) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String path = "list-devices-control/"+user.getUid()+"/devices-control/"+ device;
        DatabaseReference db = firebaseDatabase.getReference(path);
        deviceControl.setStatusDevice(status);
        db.setValue(deviceControl);
    }
    private void writeHistory(String deviceName,DeviceControl dev){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String path= "history/"+user.getUid()+"/"+"list-device-control/"+deviceName;
        DatabaseReference db = firebaseDatabase.getReference(path) ;
        db.push().setValue(dev);
    }
    public void removeHistory(String deviceName) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String path= "history/"+user.getUid()+"/"+"list-device-control/"+deviceName;
        DatabaseReference db = firebaseDatabase.getReference(path) ;
        db.removeValue();
    }
}
