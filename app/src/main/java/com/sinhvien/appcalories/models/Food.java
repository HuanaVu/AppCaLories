package com.sinhvien.appcalories.models;

public class Food {
    private String id;
    private String tenMon;
    private int calories; // Calories per 100g
    private String category;
    private String imageUrl;
    private int defaultQuantity = 100; // Default quantity in grams

    public Food() {
        // Default constructor for Firebase
    }

    public Food(String id, String tenMon, int calories, String category, String imageUrl) {
        this.id = id;
        this.tenMon = tenMon;
        this.calories = calories;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTenMon() { return tenMon; }
    public int getCalories() { return calories; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public int getDefaultQuantity() { return defaultQuantity; }

    public void setId(String id) { this.id = id; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDefaultQuantity(int defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    // Helper method to calculate calories based on quantity
    public int calculateCalories(int quantity) {
        return (calories * quantity) / 100;
    }
}