package com.example.iotapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.example.iotapplication.Fragment.ControlFragment;
import com.example.iotapplication.Fragment.HistoryFragment;
import com.example.iotapplication.Fragment.HomeFragment;
import com.example.iotapplication.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CODE = 1;

    public ProfileFragment profileFragment = new ProfileFragment();
    BottomNavigationView mBottom;
    CustomLoadingDialog dialog ;
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) return;
                        Log.d("onActivityResult: ", intent.getData().toString());
                        Uri uri = intent.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileFragment.setBitMapImageView(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
    FirebaseAuth fAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottom = findViewById(R.id.bottom_nav);
        fAuth = FirebaseAuth.getInstance();
        dialog = new CustomLoadingDialog(this);
        FirebaseUser user = fAuth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            finish();
        }

        ReplaceFragment(new HomeFragment());
        mBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_action:
                        ReplaceFragment(new HomeFragment());
                        break;
                    case R.id.control_action:
                        ReplaceFragment(new ControlFragment());
                        break;
                    case R.id.profile_action:
                        ReplaceFragment(profileFragment);
                        break;
                    case R.id.history_action:
                        ReplaceFragment(new HistoryFragment());
                        break;
                    case R.id.logout_action:
                        dialog.startLoadingDialog();
                        AlertDialog.Builder buid = new AlertDialog.Builder(HomeActivity.this);
                        buid.setTitle("Xác nhận");
                        buid.setMessage("Bạn có muốn đăng xuất hệ thống không ?");

                        buid.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                FirebaseAuth.getInstance().signOut();
                                dialog.dismissDialog();
                                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        buid.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialog.dismissDialog();

                            }
                        });

                        AlertDialog dialog = buid.create();
                        dialog.show();
                        break;
                    default:
                        ReplaceFragment(new HomeFragment());
                        break;
                }
                return true;
            }
        });
    }
    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }
}