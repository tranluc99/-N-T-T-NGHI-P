package com.example.iotapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.example.iotapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout txtDisplayName;
    TextInputLayout txtLayoutEmail ;
    TextInputLayout txtLayoutPassword;
    TextInputLayout txtLayoutPasswordConfirm;
    EditText edtEmail;
    EditText edtPassword ;
    EditText edtPasswordConfirm;
    EditText edtDisplayName ;
    CustomLoadingDialog dialog ;
    Button btnSignUp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Mapping();
        dialog = new CustomLoadingDialog(this);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String strPassword = edtPassword.getText().toString().trim();
        String strPasswordConfirm = edtPasswordConfirm.getText().toString().trim();
        String strName = edtDisplayName.getText().toString().trim()     ;
        String strEmail = edtEmail.getText().toString().trim();

        if(!TextUtils.isEmpty(strPassword)
                && !TextUtils.isEmpty(strPasswordConfirm)
                && !TextUtils.isEmpty(strName)
                && !TextUtils.isEmpty(strEmail)
        )       {
            if(strPassword.length() >= 6 && strPasswordConfirm.length() >= 6 ) {
                if(strPassword.equals(strPasswordConfirm)) {
                    processSignUp(strEmail, strPassword);
                }else {
                    Toast.makeText(this, "not equal password", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "not length password", Toast.LENGTH_SHORT).show();

            }
        }


    }
    public void writeInformationUser(FirebaseUser user, String pass) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
        User user1 = new User(user.getDisplayName(),user.getEmail(),user.getUid());
        user1.setPawssword(pass);
        String path = "list-users/"+ user.getUid();
        DatabaseReference db = firebaseDatabase.getReference(path);
        db.push().setValue(user1);
    }

    private void processSignUp(String email , String password ) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.startLoadingDialog();
                        if (task.isSuccessful())
                        {
                            writeInformationUser(mAuth.getCurrentUser(),password);
                            dialog.dismissDialog();
                            Toasty.success(SignUpActivity.this,"Registered successfully",Toasty.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            Toasty.error(SignUpActivity.this,"Please check internet error",Toasty.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SignUpActivity.this, "Failed Connected ", Toast.LENGTH_SHORT).show();
                        Toasty.error(SignUpActivity.this,"Please check internet error",Toasty.LENGTH_SHORT).show();

                        dialog.dismissDialog();
                    }
                });
    }

    private void Mapping() {
        txtLayoutEmail = findViewById(R.id.textInputEmail);
        txtLayoutPassword = findViewById(R.id.textInputPassword);
        txtDisplayName = findViewById(R.id.textInputName) ;
        txtLayoutPasswordConfirm = findViewById(R.id.textInputPasswordConfirm);
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        edtDisplayName = findViewById(R.id.editTextName);
        edtPasswordConfirm = findViewById(R.id.editTextPasswordCònirm);

        btnSignUp = findViewById(R.id.cirSignUpButton);
    }
}