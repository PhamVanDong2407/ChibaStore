package com.example.a175.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a175.Helper.User;
import com.example.a175.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText signUpNameTxt, signUpUsernameTxt, signUpEmailTxt, signUpPasswordTxt;
    Button signUpBtn;
    TextView signUpTxt;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signUpNameTxt = findViewById(R.id.signUpNameTxt);
        signUpUsernameTxt = findViewById(R.id.signUpUsernameTxt);
        signUpEmailTxt = findViewById(R.id.signUpEmailTxt);
        signUpPasswordTxt = findViewById(R.id.signUpPasswordTxt);
        signUpBtn = findViewById(R.id.signUpBtn);
        signUpTxt = findViewById(R.id.signUpTxt);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signUpNameTxt.getText().toString();
                String username = signUpUsernameTxt.getText().toString();
                String email = signUpEmailTxt.getText().toString();
                String password = signUpPasswordTxt.getText().toString();

                if (name.isEmpty()) {
                    signUpNameTxt.setError("Tên người dùng không được để trống!");
                    signUpNameTxt.requestFocus();
                    return;
                }
                if (username.isEmpty()) {
                    signUpUsernameTxt.setError("Tên tài khoản không được để trống!");
                    signUpUsernameTxt.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    signUpEmailTxt.setError("Email không được để trống!");
                    signUpEmailTxt.requestFocus();
                    return;
                }
                if (!email.contains("@")) {
                    signUpEmailTxt.setError("Email không đúng định dạng!");
                    signUpEmailTxt.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    signUpPasswordTxt.setError("Mật khẩu không được để trống!");
                    signUpPasswordTxt.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    signUpPasswordTxt.setError("Mật khẩu phải có ít nhất 6 ký tự!");
                    signUpPasswordTxt.requestFocus();
                    return;
                }
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");
                User user = new User(name, username, email, password);
                reference.child(username).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
