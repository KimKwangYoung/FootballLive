package com.example.footballlive.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.ApplicationTeamListActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.MatchPost;
import com.example.footballlive.data.ShortPlay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/* 변수명 수정 완료 */
public class MyTeamPostListAdapter extends RecyclerView.Adapter<MyTeamPostListAdapter.Viewholder> {

    Context context;

    ArrayList<ShortPlay> mData;

    public MyTeamPostListAdapter(Context context, ArrayList<ShortPlay> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.post_list_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        holder.setItem(mData.get(position));

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

        TextView titleTextView, matchDayTextView, applicationListButton, updateButton;
        LinearLayout postItemLinearLayout;

        String updateTime;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        long updateCnt;
        String postKey;
        String region;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.post_item_title_tv);
            matchDayTextView = itemView.findViewById(R.id.post_item_match_day_tv);
            postItemLinearLayout = itemView.findViewById(R.id.post_item_ll);
            applicationListButton = itemView.findViewById(R.id.post_item_application_list_btn);
            updateButton = itemView.findViewById(R.id.post_item_update_btn);

            applicationListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ApplicationTeamListActivity.class);
                    intent.putExtra("postKey", mData.get(getAdapterPosition()).getPostKey());
                    context.startActivity(intent);
                }
            });
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Date now = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    updateTime = simpleDateFormat.format(now);

                    checkUpdateCnt();

                }
            });

        }

        public void setItem(ShortPlay play){
            titleTextView.setText(play.getPost_title());
            matchDayTextView.setText(transformPlayData(play));
        }

        // 끌올 전에 가능 횟수 체크
        public void checkUpdateCnt(){
            postKey = mData.get(getAdapterPosition()).getPostKey();
            Log.e("postKey", postKey);
            mRef.child("matchPost").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot rds : dataSnapshot.getChildren()) {
                        for (DataSnapshot pds : rds.getChildren()) {
                            if (pds.getKey().equals(postKey)) {
                                region = rds.getKey();
                                MatchPost matchPost = pds.getValue(MatchPost.class);
//                                matchPost.setUpdateCnt(2L);

                                Log.e("longCheck", matchPost.getPlay_key() + " " + region);
                                updateCnt = matchPost.getUpdateCnt();

                                if(updateCnt < 2 && updateCnt >= 0){
                                    updateCnt += 1;
                                    updatePost(matchPost);
                                }else{
                                    Toast.makeText(context, "끌올 횟수를 다 사용 하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void updatePost(MatchPost p) {
            p.setCreationTime(updateTime);
            p.setUpdateCnt(updateCnt);
            Log.e("updateCntCheck", region + " " + postKey + " " + p.getUpdateCnt());
            mRef.child("matchPost").child(region).child(postKey).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "끌올 완료! (횟수 " + (2-updateCnt) + "번 남음)", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }








}

