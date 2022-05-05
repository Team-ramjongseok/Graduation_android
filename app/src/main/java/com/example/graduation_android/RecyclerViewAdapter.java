package com.example.graduation_android;

import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cafeImgItem;
        TextView cafeNameItem, cafeEmptySeatItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cafeImgItem = (ImageView) itemView.findViewById(R.id.img_item);
            cafeImgItem.setClipToOutline(true); //둥근 테두리
            cafeNameItem = (TextView) itemView.findViewById(R.id.cafe_name_item);
            cafeEmptySeatItem = (TextView) itemView.findViewById(R.id.cafe_empty_seat);
        }
    }

    private ArrayList<RecyclerViewItem> mList = null;
    public RecyclerViewAdapter(ArrayList<RecyclerViewItem> mList) {
        this.mList = mList;
    }
    
    //viewholder 객체 생성 및 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.main_cafe_list_item, parent, false);
        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        RecyclerViewItem item = mList.get(position);

        holder.cafeImgItem.setImageResource(R.drawable.cafe_sample);
        holder.cafeNameItem.setText(item.getCafeName());
        holder.cafeEmptySeatItem.setText(item.getEmptyseat());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}
