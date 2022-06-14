package com.example.iotapplication.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iotapplication.Model.Device;
import com.example.iotapplication.Model.Status;
import com.example.iotapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AdapterDevices extends RecyclerView.Adapter<AdapterDevices.DeviceViewHolder> {

    List<Status> mListDevices ;
    Context mContext;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth ;

    public interface ItemOnCLickListener{
        void getPosition(int position);
    }

    public ItemOnCLickListener item ;

    public AdapterDevices(List<Status> mListDevices, Context mContext , ItemOnCLickListener item) {
        this.mListDevices = mListDevices;
        this.mContext = mContext;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        this.item = item ;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Status devices = mListDevices.get(position);
        if(devices == null ) return ;
        holder.mTtitle.setText(devices.getNameDevice());
        Glide.with(mContext).load(devices.getImage()).into(holder.mImageIcon);
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRemove(mContext,holder,devices);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Oke", Toast.LENGTH_SHORT).show();
                item.getPosition(holder.getAbsoluteAdapterPosition());
//                Toasty.info(mContext,"Position ",);
            }
        });
    }
    public void removeFirebase(Status device) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db ;
        FirebaseUser user = fAuth.getCurrentUser();
        String devices = "list-user-devices/"+user.getUid()+"/"+device.getNameDevice();
        db = firebaseDatabase.getReference(devices);
        db.removeValue();
    }
    @Override
    public int getItemCount() {
        return mListDevices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        public TextView mValue , mTtitle, mUnit , btnRemove ;
        public TextView txtThresHold ;
        public CircleImageView mImageIcon ;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageIcon = itemView.findViewById(R.id.img_icon);
            mTtitle = itemView.findViewById(R.id.tv_title);

            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }

    public void showDialogRemove(Context mContext, DeviceViewHolder holder, Status devices ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Warming");
        String question = "Bạn có muốn xóa device "+devices.getNameDevice()+" không ?";
        builder.setMessage(question);
        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toasty.info(mContext, "Removing devices", Toast.LENGTH_SHORT).show();
                int position = holder.getAdapterPosition();
                mListDevices.remove(position);
                removeHistory(devices.getNameDevice());
                removeFirebase(devices);

                notifyDataSetChanged();
                Toasty.info(mContext, "Removed devices", Toast.LENGTH_SHORT).show();

                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toasty.info(mContext, "No remove device", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }
    public void removeHistory(String deviceName) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String path= "history/"+user.getUid()+"/"+"list-device/"+deviceName;
        DatabaseReference db = firebaseDatabase.getReference(path) ;
        db.removeValue();
    }

}
