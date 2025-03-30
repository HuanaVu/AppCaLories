package com.sinhvien.appcalories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appcalories.models.Food;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    private Context context;
    private int resource;
    private List<Food> foodList;
    private FoodListener listener;

    public FoodAdapter(Context context, int resource, List<Food> foodList, FoodListener listener) {
        super(context, resource, foodList);
        this.context = context;
        this.resource = resource;
        this.foodList = foodList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Food food = foodList.get(position);

        TextView txtName = convertView.findViewById(R.id.txtFoodName);
        TextView txtCalories = convertView.findViewById(R.id.txtFoodCalories);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        txtName.setText(food.getTenMon());
        txtCalories.setText(food.getCalories() + " kcal");

        btnEdit.setOnClickListener(v -> listener.onEdit(food));
        btnDelete.setOnClickListener(v -> listener.onDelete(food));

        return convertView;
    }

    public interface FoodListener {
        void onEdit(Food food);
        void onDelete(Food food);
    }
}
