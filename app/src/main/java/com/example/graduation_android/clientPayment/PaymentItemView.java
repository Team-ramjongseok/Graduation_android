package com.example.graduation_android.clientPayment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.graduation_android.R;

public class PaymentItemView extends LinearLayout {

    TextView id, order_time, amount, name, location, order_status;

    public PaymentItemView(Context context) {
        super(context);
        init(context);
    }

    public PaymentItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.payment_item, this, true);

        id = findViewById(R.id.pay_id);
        order_time = findViewById(R.id.pay_order_time);
        amount = findViewById(R.id.pay_amount);
        name = findViewById(R.id.pay_name);
        location = findViewById(R.id.pay_location);
        order_status = findViewById(R.id.pay_order_status);
    }

    public void setId(int id_val) {
        id.setText(String.valueOf(id_val));
    }

    public void setAmount(int amount_val) {
        amount.setText(String.valueOf(amount_val));
    }

    public void setOrder_time(String order_time_val) {
        order_time.setText(order_time_val);
    }

    public void setName(String name_val) {
        name.setText(name_val);
    }

    public void setLocation(String location_val) {
        location.setText(location_val);
    }

    public void setOrder_status(String order_status_val) {
        order_status.setText(order_status_val);
    }
}
