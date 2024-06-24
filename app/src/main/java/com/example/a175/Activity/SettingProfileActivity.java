package com.example.a175.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SettingProfileActivity extends AppCompatActivity {

    EditText AccountSetNameTxt, AccountSetUsernameTxt, AccountSetEmailTxt, AccountSetPasswordTxt;
    Button updateBtn;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AccountSetNameTxt = findViewById(R.id.AccountSetNameTxt);
        AccountSetUsernameTxt = findViewById(R.id.AccountSetUsernameTxt);
        AccountSetEmailTxt = findViewById(R.id.AccountSetEmailTxt);
        AccountSetPasswordTxt = findViewById(R.id.AccountSetPasswordTxt);
        updateBtn = findViewById(R.id.updateBtn);

        // Lấy thông tin người dùng từ SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Lấy thông tin từ Firebase và hiển thị
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(username);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    AccountSetNameTxt.setText(user.getName());
                    AccountSetUsernameTxt.setText(user.getUsername());
                    AccountSetEmailTxt.setText(user.getEmail());
                    AccountSetPasswordTxt.setText(user.getPassword());
                }
            } else {
                Toast.makeText(SettingProfileActivity.this, "Không thể tải thông tin người dùng!", Toast.LENGTH_SHORT).show();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = AccountSetNameTxt.getText().toString();
                String newUsername = AccountSetUsernameTxt.getText().toString();
                String email = AccountSetEmailTxt.getText().toString();
                String password = AccountSetPasswordTxt.getText().toString();

                if (name.isEmpty()) {
                    AccountSetNameTxt.setError("Tên người dùng không được để trống!");
                    AccountSetNameTxt.requestFocus();
                    return;
                }
                if (newUsername.isEmpty()) {
                    AccountSetUsernameTxt.setError("Tên tài khoản không được để trống!");
                    AccountSetUsernameTxt.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    AccountSetEmailTxt.setError("Email không được để trống!");
                    AccountSetEmailTxt.requestFocus();
                    return;
                }
                if (!email.contains("@")) {
                    AccountSetEmailTxt.setError("Email không đúng định dạng!");
                    AccountSetEmailTxt.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    AccountSetPasswordTxt.setError("Mật khẩu không được để trống!");
                    AccountSetPasswordTxt.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    AccountSetPasswordTxt.setError("Mật khẩu phải có ít nhất 6 ký tự!");
                    AccountSetPasswordTxt.requestFocus();
                    return;
                }

                // Cập nhật thông tin người dùng lên Firebase
                User user = new User(name, newUsername, email, password);
                reference.setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật lại SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", newUsername);
                        editor.apply();

                        Toast.makeText(SettingProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SettingProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SettingProfileActivity.this, "Cập nhật thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
