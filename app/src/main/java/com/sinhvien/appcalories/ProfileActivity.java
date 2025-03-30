package com.sinhvien.appcalories;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinhvien.appcalories.models.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtWeight, edtHeight;
    private Spinner spnGender, spnGoal;
    private Button btnSaveProfile;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        edtWeight = findViewById(R.id.edt_weight);
        edtHeight = findViewById(R.id.edt_height);
        spnGender = findViewById(R.id.spn_gender);
        spnGoal = findViewById(R.id.spn_goal);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Gán danh sách cho Spinner Giới tính
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(genderAdapter);

        // Gán danh sách cho Spinner Mục tiêu tập luyện
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(
                this, R.array.goal_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGoal.setAdapter(goalAdapter);

        // Khởi tạo Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String weightStr = edtWeight.getText().toString().trim();
        String heightStr = edtHeight.getText().toString().trim();

        if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr);

        // Lấy giới tính từ Spinner
        String gender = spnGender.getSelectedItem().toString();
        // Lấy mục tiêu từ Spinner
        String goal = spnGoal.getSelectedItem().toString();

        // Tạo đối tượng UserProfile
        UserProfile userProfile = new UserProfile(userId, weight, height, gender, goal);

        // Lưu vào Firebase
        databaseReference.child(userId).setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    // Quay lại HomeActivity
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}