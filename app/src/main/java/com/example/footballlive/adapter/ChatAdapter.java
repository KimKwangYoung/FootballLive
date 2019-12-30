package com.example.footballlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.R;
import com.example.footballlive.data.Chat;
import com.example.footballlive.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    DatabaseReference mDatabase;
    Context context;
    ArrayList<Chat> mDatas;
    User user;

    public ChatAdapter(Context context, ArrayList<Chat> mDatas, User mUser) {
        this.context = context;
        this.mDatas = mDatas;
        this.user = mUser;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        try {
            final Chat data = mDatas.get(position);
            if (data.getWriterId().equals(user.getUid())) {
                holder.my_root.setVisibility(View.VISIBLE);
                holder.other_root.setVisibility(View.GONE);

                holder.my_content.setText(data.getContent());
                holder.my_date.setText(dateTimeFormat(data.getDatetime()));
            } else {
                holder.my_root.setVisibility(View.GONE);
                holder.other_root.setVisibility(View.VISIBLE);

                holder.other_name.setText(data.getName());
                holder.other_content.setText(data.getContent());
                holder.other_date.setText(dateTimeFormat(data.getDatetime()));
            }
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout other_root;
        TextView other_name;
        TextView other_content;
        TextView other_date;

        LinearLayout my_root;
        TextView my_content;
        TextView my_date;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            other_root = itemView.findViewById(R.id.ll_otherchat);
            other_name = itemView.findViewById(R.id.tv_other_name);
            other_content = itemView.findViewById(R.id.tv_other_content);
            other_date = itemView.findViewById(R.id.tv_other_date);

            my_root = itemView.findViewById(R.id.ll_mychat);
            my_content = itemView.findViewById(R.id.tv_my_content);
            my_date = itemView.findViewById(R.id.tv_my_date);

            my_root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Chat chat = mDatas.get(getAdapterPosition());
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("chats").child(user.getTeamid()).child(chat.getKey()).removeValue();

                    mDatas.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    return false;
                }
            });
        }

        public void setItem(int position, Chat play) {
            mDatas.set(position, play);
        }

    }

    public String dateTimeFormat(String date) {
        //yyyyMMddHHmmss
        //오늘 오후 9시 10분
        // 9월 13일 오전 4시 3분
        //2019년 9월 13일 오전 11시 32분

        String prefix = "";
        String postfix = "";

        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputSdfYMD = new SimpleDateFormat("yyyy년 M월 d일");
        SimpleDateFormat outputSdfMD = new SimpleDateFormat("M월 d일");
        SimpleDateFormat outputSdfTIME = new SimpleDateFormat("ah시 m분");
        String stoday = inputSdf.format(today);

        if(stoday.substring(0,7).equals(date.substring(0, 7))) {
            prefix = "오늘";
        }else if(stoday.substring(0, 4).equals(date.substring(0,4))){
            try {
                prefix = outputSdfMD.format(inputSdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            try {
                postfix = outputSdfYMD.format(inputSdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        try {
            postfix = outputSdfTIME.format(inputSdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return prefix + " " + postfix;

    }
}
