package com.example.footballlive.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.NoticeContentActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.Notice;

import java.util.ArrayList;

/* 변수명 수정 완료 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    ArrayList<Notice> mData;
    Context context;

    public NoticeAdapter(ArrayList<Notice> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice item = mData.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.notice_item, parent, false);

        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, creationTimeTextView;
        LinearLayout noticeItemLinearLayout;

        public ViewHolder(View itemView){
            super(itemView);

            titleTextView = itemView.findViewById(R.id.notice_item_title_tv);
            creationTimeTextView = itemView.findViewById(R.id.notice_item_creation_time_tv);
            noticeItemLinearLayout = itemView.findViewById(R.id.notice_item_ll);

            noticeItemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, NoticeContentActivity.class);
                    intent.putExtra("titleTextView", mData.get(position).getTitle());
                    intent.putExtra("content", mData.get(position).getContent());
                    intent.putExtra("creationDate", mData.get(position).getCreationDate());
                    context.startActivity(intent);
                }
            });

        }

        public void setItem(Notice item){
            titleTextView.setText(item.getTitle());
            creationTimeTextView.setText(item.getCreationDate());
        }
    }
}
