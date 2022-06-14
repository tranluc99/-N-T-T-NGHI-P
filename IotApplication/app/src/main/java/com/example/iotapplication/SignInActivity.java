package com.example.iotapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapplication.Adapter.CustomLoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class SignInActivity extends AppCompatActivity {

    TextInputLayout txtLayoutEmail ;
    TextInputLayout txtLayoutPassword;
    EditText edtEmail;
    EditText edtPassword ;

    Button btnLogin ;
    TextView btnSignUp ;
    TextView btnReset ;
    CustomLoadingDialog progress ;
    FirebaseAuth fAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Mapping();
        progress = new CustomLoadingDialog(this);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null) {
//            if(user.isEmailVerified()){
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
//            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(email.length() > 3 && email.length() > 3) {

                    clickLogin(email, password);
                }else {
                    Toasty.error(SignInActivity.this, "Please input email or password", Toasty.LENGTH_SHORT, true).show();

                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void clickLogin(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.startLoadingDialog();
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (user.isEmailVerified()){
                            progress.dismissDialog();
                            Toasty.success(SignInActivity.this, "Login in successfully!", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            startActivity(intent);

                          /*  }else{
//                                Toast.makeText(SignInActivity.this, "", Toast.LENGTH_SHORT).show();
                                Toasty.error(SignInActivity.this, "Please vertify email to log in", Toasty.LENGTH_SHORT, true).show();
                                progress.dismissDialog();
                            }
                            */


//                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });

                        }else {
//                            Toast.makeText(SignInActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
                            Toasty.error(SignInActivity.this, "Please check password!", Toasty.LENGTH_SHORT, true).show();
                            progress.dismissDialog();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SignInActivity.this, "Failed Connected ", Toast.LENGTH_SHORT).show();
                        Toasty.error(SignInActivity.this, "Please check connect internet", Toasty.LENGTH_SHORT, true).show();

                        progress.dismissDialog();
                    }
                });
    }

    private void Mapping() {
        txtLayoutEmail = findViewById(R.id.textInputEmail);
        txtLayoutPassword = findViewById(R.id.textInputPassword);
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.cirLoginButton);
        btnSignUp = findViewById(R.id.btn_signup);
        btnReset = findViewById(R.id.btn_forgot_password);

    }
}