package com.sinhvien.appcalories.models;

public class UserProfile {
    public String userId;
    public float canNang;
    public float chieuCao;
    public String gioiTinh;
    public String mucTieu; // "Giảm cân", "Tăng cân", "Duy trì cân nặng"

    public UserProfile() {
        // Bắt buộc có constructor mặc định cho Firebase
    }

    public UserProfile(String userId, float canNang, float chieuCao, String gioiTinh, String mucTieu) {
        this.userId = userId;
        this.canNang = canNang;
        this.chieuCao = chieuCao;
        this.gioiTinh = gioiTinh;
        this.mucTieu = mucTieu;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getCanNang() {
        return canNang;
    }

    public void setCanNang(float canNang) {
        this.canNang = canNang;
    }

    public float getChieuCao() {
        return chieuCao;
    }

    public void setChieuCao(float chieuCao) {
        this.chieuCao = chieuCao;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getMucTieu() {
        return mucTieu;
    }

    public void setMucTieu(String mucTieu) {
        this.mucTieu = mucTieu;
    }
}
