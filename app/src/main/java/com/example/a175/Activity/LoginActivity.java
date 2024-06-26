package com.example.a175.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a175.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText loginUsernameTxt, loginPasswordTxt;
    Button loginBtn;
    TextView loginTxt;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginUsernameTxt = findViewById(R.id.loginUsernameTxt);
        loginPasswordTxt = findViewById(R.id.loginPasswordTxt);
        loginBtn = findViewById(R.id.loginBtn);
        loginTxt = findViewById(R.id.loginTxt);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUsername() {
        String val = loginUsernameTxt.getText().toString().trim();
        if (val.isEmpty()) {
            loginUsernameTxt.setError("Tên người dùng không được để trống!");
            loginUsernameTxt.requestFocus();
            return false;
        } else {
            loginUsernameTxt.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPasswordTxt.getText().toString().trim();
        if (val.isEmpty()) {
            loginPasswordTxt.setError("Mật khẩu không được để trống!");
            loginPasswordTxt.requestFocus();
            return false;
        } else {
            loginPasswordTxt.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginUsernameTxt.getText().toString();
        String userPassword = loginPasswordTxt.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsernameTxt.setError(null);
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordDB, userPassword)) {
                            loginPasswordTxt.setError(null);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // Lưu tên người dùng và mật khẩu vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", userUsername);
                            editor.putString("password", userPassword); // Lưu mật khẩu vào SharedPreferences
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loginPasswordTxt.setError("Mật khẩu không đúng!");
                            loginPasswordTxt.requestFocus();
                        }
                    }
                } else {
                    loginUsernameTxt.setError("Tên người dùng không tồn tại!");
                    loginUsernameTxt.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi bị huỷ bỏ
                Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

