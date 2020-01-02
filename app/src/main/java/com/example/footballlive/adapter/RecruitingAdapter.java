package com.example.footballlive.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.R;
import com.example.footballlive.RecruitingContentActivity;
import com.example.footballlive.data.Recruiting;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class RecruitingAdapter extends RecyclerView.Adapter<RecruitingAdapter.ViewHolder> {

    public LinkedHashMap<String, Recruiting> mData;
    Context context;

    ArrayList<Recruiting> recruitings = new ArrayList<>();
    String region;

    public RecruitingAdapter(LinkedHashMap<String, Recruiting> mData, Context context, String region){
        this.mData = mData;
        this.context = context;
        this.region = region;
        for(Recruiting r : mData.values()){
            recruitings.add(r);
        }

        Collections.sort(recruitings);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recruitinjg_card_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recruiting recruiting = recruitings.get(position);
        holder.setItem(recruiting);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, stadiumTextView, teamNameTextView, uniformTextView, creationTimeTextView;
        CardView recruitingCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recruitingCardView = itemView.findViewById(R.id.recruiting_card_cv);
            titleTextView = itemView.findViewById(R.id.recruiting_card_title_tv);
            teamNameTextView = itemView.findViewById(R.id.recruiting_card_team_name_tv);
            uniformTextView = itemView.findViewById(R.id.recruiting_card_uniform_tv);
            stadiumTextView = itemView.findViewById(R.id.recruiting_card_stadium_tv);
            creationTimeTextView = itemView.findViewById(R.id.recruiting_card_creation_time_tv);

            recruitingCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(context, RecruitingContentActivity.class);
                intent.putExtra("recruitingKey", recruitings.get(getAdapterPosition()).getRecruiting_key());
                intent.putExtra("region", region);
                context.startActivity(intent);

                }
            });

        }

        public void setItem(Recruiting recruiting) {

            titleTextView.setText(recruiting.getTitle());
            teamNameTextView.setText(recruiting.getTeamName());
            stadiumTextView.setText(recruiting.getStadium());
            uniformTextView.setText("유니폼 " + recruiting.getUniform());
            creationTimeTextView.setText(recruiting.getCreationTime());
        }

    }
}

