package com.example.iotapplication.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.example.iotapplication.HomeActivity;
import com.example.iotapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    CircleImageView mCircleView ;
    TextInputLayout txtCurrentPass;
    TextInputLayout txtNewPass;
    TextInputLayout txtConfirmPass;
    EditText edtCurrentPass;
    EditText edtNewPass ;
    EditText edtConfirmPass;
    TextView txtNameUser;
    Button btnUpdate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    CustomLoadingDialog dialog ;
    FirebaseUser user ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null ) {
            txtNameUser.setText(user.getEmail());
        }
        dialog = new CustomLoadingDialog(getActivity());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.startLoadingDialog();
                if( checkInformation() == true ) {
                    String edtCurrentP = edtCurrentPass.getText().toString().trim();
                    String edtNewP = edtNewPass.getText().toString().trim();
                    String edtConfirmP = edtConfirmPass.getText().toString().trim();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user == null ) return;
                    if(edtNewP.equals(edtConfirmP) == true ){
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), edtCurrentP);

                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(edtNewP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toasty.success(getActivity(),"Password is updated",Toasty.LENGTH_SHORT).show();

                                                    } else {
                                                        Toasty.error(getActivity(),"Password is not updated",Toasty.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                        Toasty.error(getActivity(),"Password is not match. Please input password",Toasty.LENGTH_SHORT).show();
                    }
                }else {
                    dialog.dismissDialog();
                }
            }
        });
        mCircleView.setBackgroundResource(R.drawable.user);
        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                clickSelectImage();
            }
        });
        return view ;
    }

    private void clickSelectImage() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(getContext(), "VAO1", Toast.LENGTH_SHORT).show();
            homeActivity.openGallery();
            return ;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "VAO2", Toast.LENGTH_SHORT).show();

            homeActivity.openGallery();
            // return ;
        }else {
            Toast.makeText(getContext(), "VAO3", Toast.LENGTH_SHORT).show();

            String []permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, homeActivity.MY_REQUEST_CODE);
        }
    }

    private void openGallery() {
    }

    public void initUI(View view ) {
        edtCurrentPass = view.findViewById(R.id.editCurrentPassword);
        edtNewPass = view.findViewById(R.id.editNewPassword);
        edtConfirmPass = view.findViewById(R.id.editConfirmPassword);
        txtCurrentPass = view.findViewById(R.id.textCurrentPassword);
        txtNewPass = view.findViewById(R.id.textNewPassword);
        txtConfirmPass = view.findViewById(R.id.textConfirmPassword);
        txtNameUser = view.findViewById(R.id.txt_name_user);
        btnUpdate = view.findViewById(R.id.btn_update);
        mCircleView = view.findViewById(R.id.img_avatar);
    }

    public boolean checkInformation() {
        String edtCurrentP = edtCurrentPass.getText().toString().trim();
        String edtNewP = edtNewPass.getText().toString().trim();
        String edtConfirmP = edtConfirmPass.getText().toString().trim();
        if(!TextUtils.isEmpty(edtCurrentP)
                && !TextUtils.isEmpty(edtNewP)
                && !TextUtils.isEmpty(edtConfirmP)
        )       {

            return true ;
        }else {
            return false ;
        }

    }
    public void setBitMapImageView(Bitmap uri) {
        mCircleView.setImageBitmap(uri);
    }
}