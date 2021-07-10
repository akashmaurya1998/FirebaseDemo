        package com.aakash.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText edtUserName, edtEmail, edtPassword;
    Button btnSignUp, btnSignIn;
    TextView tvHeading;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    Shader shader;
    ProgressBar progress;
    TextInputLayout ilEmail, ilPassword, ilUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.editTextTextEmailAddress);
        edtPassword = findViewById(R.id.editTextTextPassword);
        edtUserName = findViewById(R.id.editTextTextPersonName);
        tvHeading = findViewById(R.id.tv_heading);
        shader = new LinearGradient(0,0,0, tvHeading.getLineHeight(),
                new int[] {
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                }, null, Shader.TileMode.CLAMP);

        tvHeading.getPaint().setShader(shader);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progress = findViewById(R.id.pbSignIn);

        ilPassword = findViewById(R.id.ilPassword);
        ilEmail = findViewById(R.id.ilEmail);
        ilUsername = findViewById(R.id.ilUsername);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtEmail.getText().toString()) && !TextUtils.isEmpty(edtPassword.getText().toString()) && !TextUtils.isEmpty(edtUserName.getText().toString())) {
                    signUp();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill the form!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtEmail.getText().toString()) && !TextUtils.isEmpty(edtPassword.getText().toString())) {
                    signIn();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill the form!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                        if (TextUtils.isEmpty(edtEmail.getText().toString())){
                            ilEmail.setError("Required");
                            ilEmail.setErrorEnabled(true);
                        } else {
                            ilEmail.setError(null);
                        }
                }
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (TextUtils.isEmpty(edtPassword.getText().toString())){
                        ilPassword.setError("Required");
                        ilPassword.setErrorEnabled(true);
                    } else {
                        ilPassword.setError(null);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            transitionToSocialMedia();
            finish();
        }
    }

    private void signUp(){
        btnSignIn.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Signed Up", Toast.LENGTH_LONG).show();
                        database.getReference().child("users")
                                .child(task.getResult().getUser().getUid())
                                .child("username")
                                .setValue(edtUserName.getText().toString());

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(edtUserName.getText().toString())
                                .build();

                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this, "Profile Updated!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        transitionToSocialMedia();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    progress.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void signIn(){
        btnSignIn.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                    transitionToSocialMedia();
                    progress.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void transitionToSocialMedia(){
        Intent intent = new Intent(MainActivity.this, SocialMediaActivity.class);
        startActivity(intent);
        Intent intent1 = new Intent(MainActivity.this, MessageService.class);
        startService(intent1);
    }
}