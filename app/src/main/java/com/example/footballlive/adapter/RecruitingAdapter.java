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

        TextView recruitingTitleTextView, recruitingStadiumTextView, recruitingTeamNameTextView, recruitingUniformTextView;
        CardView recruitingCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recruitingCardView = itemView.findViewById(R.id.recruiting_card_cv);
            recruitingTitleTextView = itemView.findViewById(R.id.recruiting_card_title_tv);
            recruitingTeamNameTextView = itemView.findViewById(R.id.recruiting_card_team_name_tv);
            recruitingUniformTextView = itemView.findViewById(R.id.recruiting_card_uniform_tv);
            recruitingStadiumTextView = itemView.findViewById(R.id.recruiting_card_stadium_tv);

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

            recruitingTitleTextView.setText(recruiting.getTitle());
            recruitingTeamNameTextView.setText(recruiting.getTeamName());
            recruitingStadiumTextView.setText(recruiting.getStadium());
            recruitingUniformTextView.setText("유니폼 " + recruiting.getUniform());
        }

    }
}

