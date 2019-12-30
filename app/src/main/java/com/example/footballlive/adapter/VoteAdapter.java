package com.example.footballlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.R;
import com.example.footballlive.data.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* 변수명 수정 완료 */

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.Viewholder> {

    LinkedHashMap<String, User> mData;
    ArrayList<String> keyList = new ArrayList<>();
    Context context;

    public VoteAdapter(LinkedHashMap<String, User> mData, Context context) {
        this.mData = mData;
        this.context = context;

        for(Map.Entry<String, User> entry : mData.entrySet()){
            keyList.add(entry.getKey());
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.vote_member_item, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        User user = mData.get(keyList.get(position));
        holder.setItem(user);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView nameTextView, positionTextView, backnumberTextView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.vote_item_name_tv);
            positionTextView = itemView.findViewById(R.id.vote_item_position_tv);
            backnumberTextView = itemView.findViewById(R.id.vote_item_backnumber_tv);
        }

        public void setItem(User user){
            nameTextView.setText(user.getName());
            positionTextView.setText(user.getPosition());
            backnumberTextView.setText(user.getBacknumber());
        }
    }
}
