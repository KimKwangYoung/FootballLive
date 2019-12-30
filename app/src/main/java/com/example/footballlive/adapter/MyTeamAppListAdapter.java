package com.example.footballlive.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.ApplicationActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.ShortPlay;
import java.util.ArrayList;

/* 변수명 수정 완료 */
public class MyTeamAppListAdapter extends RecyclerView.Adapter<MyTeamAppListAdapter.Viewholder> {

    ArrayList<ShortPlay> mData;
    Context context;

    public MyTeamAppListAdapter(ArrayList<ShortPlay> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.my_team_application_list_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ShortPlay shortPlay = mData.get(position);
        holder.setItem(shortPlay);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public String transformPlayData(ShortPlay play) {
        String day, time, sum;
        String[] dayArry = play.getMatchDay().split("-");
        String[] stratTimeArry = play.getStartTime().split("-");
        String[] endTimeArry = play.getEndTime().split("-");
        day = dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일 ";
        if (stratTimeArry[1].equals("0")) {
            if (endTimeArry[1].equals("0")) {
                time = stratTimeArry[0] + "시~" + endTimeArry[0] + "시";
            } else {
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분~" + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        } else {
            if (endTimeArry[1].equals("0")) {
                time = stratTimeArry[0] + "시~" + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            } else {
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분~" + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        }


        sum = day + time;
        return sum;
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        TextView titleTextView, matchDayTextView, stadiumTextView;
        LinearLayout itemLinearLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            itemLinearLayout = itemView.findViewById(R.id.team_application_item_ll);
            titleTextView = itemView.findViewById(R.id.team_application_item_title_tv);
            matchDayTextView = itemView.findViewById(R.id.team_application_item_match_day_time_tv);
            stadiumTextView = itemView.findViewById(R.id.team_application_item_stadium_tv);


            itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ApplicationActivity.class);
                    intent.putExtra("playKey", mData.get(getAdapterPosition()).getPostKey());
                    context.startActivity(intent);
                }
            });
        }

        public void setItem(ShortPlay shortPlay){

            titleTextView.setText(shortPlay.getPost_title());
            matchDayTextView.setText(transformPlayData(shortPlay));
            Log.e("tranformCheck", transformPlayData(shortPlay));
            stadiumTextView.setText(shortPlay.getStadium());
        }
    }
}
