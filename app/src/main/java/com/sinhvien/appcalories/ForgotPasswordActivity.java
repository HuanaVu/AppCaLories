package com.sinhvien.appcalories;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edtEmail;
    private Button btnSendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_forgot);

        addControls();
        handleEvents();
    }

    private void addControls() {
        auth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtForgotEmail); // Đã sửa đúng ID
        btnSendCode = findViewById(R.id.btnForgotSendCode); // Đã sửa đúng ID
    }

    private void handleEvents() {
        btnSendCode.setOnClickListener(v -> sendResetCode());
    }

    private void sendResetCode() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Mã xác nhận đã được gửi đến email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
