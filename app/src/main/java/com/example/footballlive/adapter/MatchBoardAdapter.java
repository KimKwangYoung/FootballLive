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

import com.example.footballlive.ApplicationActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.MatchPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class MatchBoardAdapter extends RecyclerView.Adapter<MatchBoardAdapter.ViewHolder> {
    Context context;
    public LinkedHashMap<String, MatchPost> mData;
    ArrayList<MatchPost> mMatchPostList = new ArrayList<>();

    public MatchBoardAdapter(LinkedHashMap<String, MatchPost> mData, Context context) {
        this.mData = mData;
        this.context = context;

        for(MatchPost p : mData.values()){
            mMatchPostList.add(p);
        }

        Collections.sort(mMatchPostList);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView teamNameTextView, stadiumTextView, matchDayTextView, titleTextView, creationTimeTextView;
        LinearLayout matchItemLinearLayout;

        ViewHolder(View itemView){
            super(itemView);

            titleTextView = itemView.findViewById(R.id.match_board_item_title_tv);
            teamNameTextView = itemView.findViewById(R.id.match_board_item_team_name_tv);
            stadiumTextView = itemView.findViewById(R.id.match_board_item_stadium_tv);
            matchDayTextView = itemView.findViewById(R.id.match_board_item_match_day_tv);
            creationTimeTextView = itemView.findViewById(R.id.match_board_item_creation_time_tv);
            matchItemLinearLayout = itemView.findViewById(R.id.match_board_item_ll);

            matchItemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String playKey;
                    playKey = mMatchPostList.get(getAdapterPosition()).getPlay_key();
                    Intent intent = new Intent(context, ApplicationActivity.class);
                    intent.putExtra("playKey", playKey);
                    context.startActivity(intent);
                }
            });

        }

        public void setItem(MatchPost matchPost){
            teamNameTextView.setText(matchPost.getHome_team_name());
            titleTextView.setText(matchPost.getTitle());
            stadiumTextView.setText(matchPost.getStadium());
            matchDayTextView.setText(transformPlayData(matchPost));

           //TODO : 작성시간이 없는 다른 게시글 지워주기
            try {
                creationTimeTextView.setText(matchPost.getCreationTime());
            }catch (Exception e){

            }

        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.match_board_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchPost matchPost = mMatchPostList.get(position);
        holder.setItem(matchPost);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public String transformPlayData(MatchPost matchPost){
        String day, time, sum;
        String[] dayArry = matchPost.getMatchday().split("-");
        String[] stratTimeArry = matchPost.getStart_date_time().split("-");
        String[] endTimeArry = matchPost.getEnd_date_time().split("-");
        day = dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일\n";
        if(stratTimeArry[1].equals("0")){
            if(endTimeArry[1].equals("0")){
                time = stratTimeArry[0] + "시 ~ " + endTimeArry[0] + "시";
            }else{
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분 ~ " + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        }else{
            if(endTimeArry[1].equals("0")){
                time = stratTimeArry[0] + "시 ~" + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }else {
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분 ~ " + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        }


        sum = day + time;
        return sum;
    }
}
