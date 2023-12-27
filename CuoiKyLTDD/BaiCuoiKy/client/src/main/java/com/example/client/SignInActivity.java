package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText signinUsername, signinPassword;
    Button signinButton;
    TextView noAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signinUsername = findViewById(R.id.userName);
        signinPassword = findViewById(R.id.userPass);
        signinButton = findViewById(R.id.btnSignIn);
        noAccount = findViewById(R.id.noAccount);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                } else {
                    checkUser();
                }
            }
        });
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    public Boolean validateUsername() {
        String val = signinUsername.getText().toString();
        if (val.isEmpty()) {
            signinUsername.setError("Không được để trống Tên đăng nhập");
            return false;
        } else {
            signinUsername.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = signinPassword.getText().toString();
        if (val.isEmpty()) {
            signinPassword.setError("Không được để trống Mật khẩu");
            return false;
        } else {
            signinPassword.setError(null);
            return true;
        }
    }
    public void checkUser(){
        String userUsername = signinUsername.getText().toString().trim();
        String userPassword = signinPassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    signinUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        signinUsername.setError(null);
                        Toast.makeText(SignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        signinUsername.setError("Sai mật khẩu!");
                        signinUsername.requestFocus();
                    }
                } else {
                    signinUsername.setError("Người dùng không tồn tại");
                    signinUsername.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}