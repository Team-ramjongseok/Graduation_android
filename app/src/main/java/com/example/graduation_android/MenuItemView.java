package com.example.graduation_android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

public class MenuItemView extends LinearLayout {
    TextView tv1, tv2;

    public MenuItemView(Context context) {
        super(context);
        init(context);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.menu_list_item, this, true);

        tv1 = findViewById(R.id.list_menu_name);
        tv2 = findViewById(R.id.list_menu_price);
    }

    public void setName(String name) {
        tv1.setText(name);
    }

    public void setPrice(int price) {
        tv2.setText(String.valueOf(price));
    }

}
