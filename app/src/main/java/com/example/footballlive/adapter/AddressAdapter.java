package com.example.footballlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.AddressActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.AddressData;

import java.util.ArrayList;
import java.util.List;

/* 변수명 수정 완료 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Viewholder> {
    ArrayList<AddressData.AddData> mData;
    Context context;
    AddressActivity.JusoListner listner;

    public AddressAdapter(List<AddressData.AddData> mData, Context context, AddressActivity.JusoListner listner) {
        this.mData = (ArrayList<AddressData.AddData>) mData;
        this.context = context;
        this.listner = listner;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.address_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        AddressData.AddData data = mData.get(position);
        holder.setItem(data);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView numberAddressTextView, roadAddressTextView;
        LinearLayout addressLinearLayout;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            numberAddressTextView = itemView.findViewById(R.id.address_item_number_tv);
            roadAddressTextView = itemView.findViewById(R.id.address_item_road_tv);
            addressLinearLayout = itemView.findViewById(R.id.address_item_ll);
        }

        public void setItem(final AddressData.AddData data){

            numberAddressTextView.setText(data.getJibunAddress());
            roadAddressTextView.setText(data.getRoadAddress());

            addressLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.pushData(data);
                }
            });
        }
    }
}
