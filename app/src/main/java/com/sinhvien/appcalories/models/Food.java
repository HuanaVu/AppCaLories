package com.sinhvien.appcalories.models;

public class Food {
    private String id;
    private String tenMon;
    private int calories;
    private String category;
    private String imageUrl;

    public Food() {
        // Constructor mặc định để Firebase có thể đọc dữ liệu
    }

    public Food(String id, String tenMon, int calories, String category, String imageUrl) {
        this.id = id;
        this.tenMon = tenMon;
        this.calories = calories;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getTenMon() { return tenMon; }
    public int getCalories() { return calories; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }

    public void setId(String id) { this.id = id; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
