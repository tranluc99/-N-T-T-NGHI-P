package com.example.iotapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    TextView txtTitleReset ;
    EditText edtEmailReset ;
    Button btnSendMail ;
    CustomLoadingDialog dialog ;
    FirebaseAuth fAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mapping();
        dialog = new CustomLoadingDialog(this);
        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmailReset.getText().toString().trim();
                if(email.length() > 0 ) {
                    fAuth = FirebaseAuth.getInstance() ;
                    fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.startLoadingDialog();
                            if (task.isSuccessful()) {
                                dialog.dismissDialog();
                                Intent intent = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }

    public void mapping() {
        txtTitleReset = findViewById(R.id.txt_title_reset);
        edtEmailReset = findViewById(R.id.edt_email_reset);
        btnSendMail = findViewById(R.id.btn_send_reset);
    }
}