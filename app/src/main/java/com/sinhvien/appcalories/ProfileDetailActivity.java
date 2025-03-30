package com.sinhvien.appcalories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinhvien.appcalories.models.UserProfile;
public class ProfileDetailActivity extends AppCompatActivity {

    private TextView txtUserId, txtWeight, txtHeight, txtGender, txtGoal;
    private Button btnEditProfile;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // Ánh xạ View
        txtWeight = findViewById(R.id.txt_weight);
        txtHeight = findViewById(R.id.txt_height);
        txtGender = findViewById(R.id.txt_gender);
        txtGoal = findViewById(R.id.txt_goal);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        Button btnBackHome = findViewById(R.id.btn_back_home);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileDetailActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Khởi tạo Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        // Load dữ liệu từ Firebase
        loadUserProfile();

        // Chuyển sang trang chỉnh sửa hồ sơ
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileDetailActivity.this, ProfileActivity.class));
            }
        });
    }

    private void loadUserProfile() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        txtWeight.setText("Cân nặng: " + userProfile.getCanNang() + " kg");
                        txtHeight.setText("Chiều cao: " + userProfile.getChieuCao() + " cm");
                        txtGender.setText("Giới tính: " + userProfile.getGioiTinh());
                        txtGoal.setText("Mục tiêu: " + userProfile.getMucTieu());
                    }
                } else {
                    Toast.makeText(ProfileDetailActivity.this, "Không tìm thấy hồ sơ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileDetailActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
