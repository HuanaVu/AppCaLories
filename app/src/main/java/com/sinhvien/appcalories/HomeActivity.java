package com.sinhvien.appcalories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.sinhvien.appcalories.models.Food;
import java.util.ArrayList;
import java.util.List;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import android.widget.ProgressBar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    // UI Components
    private TextView txtBMI, txtGoalToday, txtCalTotal;
    private Button btnCreateProfile;
    private CardView cardBMIInfo;
    private DrawerLayout drawerLayout;
    private TextView[] txtCalMeals;
    private Button[] btnChonMeals;
    private ProgressBar progressCalories;
    private TextView txtCaloriesConsumed, txtCaloriesLeft, txtPercentage;
    private int totalCaloriesGoal = 2000; // Có thể lấy từ Firebase

    // Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    // Data
    private int totalCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        setupNavigation();
        setupMealSelection();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            checkUserProfile(user.getUid());
        }
    }
    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtBMI = findViewById(R.id.txt_bmi);
        txtGoalToday = findViewById(R.id.txt_goal_today);
        txtCalTotal = findViewById(R.id.txtCalTotal);
        cardBMIInfo = findViewById(R.id.card_bmi_info);
        btnCreateProfile = findViewById(R.id.btn_create_profile);
        btnCreateProfile.setVisibility(View.GONE);
        // Initialize meal views
        int[] txtCalIds = {R.id.txtCalSang, R.id.txtCalTrua, R.id.txtCalChieu, R.id.txtCalToi};
        int[] btnChonIds = {R.id.btnChonSang, R.id.btnChonTrua, R.id.btnChonChieu, R.id.btnChonToi};
        txtCalMeals = new TextView[4];
        btnChonMeals = new Button[4];
        for (int i = 0; i < 4; i++) {
            txtCalMeals[i] = findViewById(txtCalIds[i]);
            btnChonMeals[i] = findViewById(btnChonIds[i]);
            txtCalMeals[i].setText("0% kcal"); // Initialize with 0 calories
        }
        progressCalories = findViewById(R.id.progress_calories);
        txtCaloriesConsumed = findViewById(R.id.txt_calories_consumed);
        txtCaloriesLeft = findViewById(R.id.txt_calories_left);
        txtPercentage = findViewById(R.id.txt_percentage);
    }
    private void updateCaloriesProgress(int consumedCalories) {
        // Tính toán giá trị
        int remaining = totalCaloriesGoal - consumedCalories;
        int progress = (int) (((float)consumedCalories / totalCaloriesGoal) * 100);

        // Cập nhật ProgressBar
        progressCalories.setProgress(progress);

        // Đổi màu nếu vượt quá 100%
        if (progress > 100) {
            progressCalories.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(this, R.color.warning_red),
                    PorterDuff.Mode.SRC_IN
            );
        } else {
            progressCalories.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(this, R.color.progress_foreground),
                    PorterDuff.Mode.SRC_IN
            );
        }

        // Cập nhật text
        txtCaloriesConsumed.setText("Đã ăn: " + consumedCalories + " kcal");
        txtCaloriesLeft.setText("Còn lại: " + remaining + " kcal");
        txtPercentage.setText(progress + "%");
        txtCalTotal.setText(consumedCalories + "/" + totalCaloriesGoal + " kcal");
    }

    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupMealSelection() {
        btnCreateProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        final String[] categories = {"Bữa sáng", "Bữa trưa", "Bữa chiều", "Bữa tối"};

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            btnChonMeals[i].setOnClickListener(v ->
                    showFoodSelectionDialog(categories[finalI], txtCalMeals[finalI]));
        }
    }

    private void showFoodSelectionDialog(String category, TextView targetTextView) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        Query query = foodRef.orderByChild("category").equalTo(category);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foodList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food != null) {
                            // Ensure ID is set (for manual entries)
                            if (food.getId() == null) {
                                food.setId(dataSnapshot.getKey());
                            }
                            foodList.add(food);
                        }
                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error parsing food data", e);
                    }
                }

                if (foodList.isEmpty()) {
                    Toast.makeText(HomeActivity.this,
                            "Không có món ăn nào trong danh mục " + category,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                showFoodDialog(foodList, targetTextView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Lỗi khi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void showFoodDialog(List<Food> foodList, TextView targetTextView) {
        String[] foodNames = new String[foodList.size()];
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            foodNames[i] = String.format("%s (%d cal)", food.getTenMon(), food.getCalories());
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn món ăn")
                .setItems(foodNames, (dialog, which) -> {
                    int selectedCalories = foodList.get(which).getCalories();
                    targetTextView.setText(String.valueOf(selectedCalories));
                    calculateTotalCalories();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void calculateTotalCalories() {
        totalCalories = 0;
        for (TextView txtCal : txtCalMeals) {
            try {
                String calText = txtCal.getText().toString();
                if (!calText.isEmpty()) {
                    totalCalories += Integer.parseInt(calText);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing calorie value", e);
            }
        }
        updateCaloriesProgress(totalCalories);
    }

    private void checkUserProfile(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    findViewById(R.id.card_setup_profile).setVisibility(View.VISIBLE);
                    btnCreateProfile.setVisibility(View.VISIBLE);
                    return;
                }

                try {
                    Float canNang = snapshot.child("canNang").getValue(Float.class);
                    Float chieuCao = snapshot.child("chieuCao").getValue(Float.class);
                    String mucTieu = snapshot.child("mucTieu").getValue(String.class);

                    if (canNang == null || chieuCao == null || mucTieu == null) {
                        throw new NullPointerException("Thiếu dữ liệu hồ sơ");
                    }

                    float bmi = calculateBMI(canNang, chieuCao);
                    int caloriesTarget = calculateCalories(canNang, chieuCao, mucTieu);

                    // Các dòng mới thêm
                    totalCaloriesGoal = caloriesTarget;
                    updateCaloriesProgress(totalCalories);

                    txtBMI.setText(String.format("BMI: %.1f", bmi));
                    txtGoalToday.setText(String.format("Mục tiêu hôm nay: %d kcal", caloriesTarget));
                    cardBMIInfo.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing profile data", e);
                    Toast.makeText(HomeActivity.this,
                            "Lỗi xử lý dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Lỗi tải dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private float calculateBMI(float canNang, float chieuCao) {
        return canNang / ((chieuCao / 100) * (chieuCao / 100));
    }

    private int calculateCalories(float canNang, float chieuCao, String mucTieu) {
        int bmr = (int) (10 * canNang + 6.25 * chieuCao - 5 * 25 + 5);
        int maintenanceCalories = (int) (bmr * 1.55);

        switch (mucTieu) {
            case "Tăng cân": return maintenanceCalories + 500;
            case "Giảm cân": return maintenanceCalories - 500;
            default: return maintenanceCalories;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already in home
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileDetailActivity.class));
        } else if (id == R.id.nav_food) {
            startActivity(new Intent(this, FoodActivity.class));
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}