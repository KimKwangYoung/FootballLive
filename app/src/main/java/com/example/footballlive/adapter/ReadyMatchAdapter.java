package com.example.footballlive.adapter;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.footballlive.BaseActivity.team;

import com.example.footballlive.BaseActivity;
import com.example.footballlive.ChangeListSize;
import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.MatchResultActivity;
import com.example.footballlive.R;
import com.example.footballlive.VoteActivity;
import com.example.footballlive.data.ReadyMatch;
import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class ReadyMatchAdapter extends RecyclerView.Adapter<ReadyMatchAdapter.Viewholder> {

    Context context;

    ArrayList<ReadyMatch> mData;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();



    FootballLiveApplication fla;

    ChangeListSize mCallBack;

    public ReadyMatchAdapter(Context context, ArrayList<ReadyMatch> mData, Application app, ChangeListSize changeListSize) {
        this.context = context;
        this.mData = mData;
        this.mCallBack = changeListSize;
        this.fla = (FootballLiveApplication)app;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.ready_match_list_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ReadyMatch readyMatch = mData.get(position);
        holder.setItem(readyMatch);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView teamNameTextView, stadiumTextView, voteButton, matchDayTextView, oppTeamMatchResultButton;
        LinearLayout readyMatchItemLinearLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            teamNameTextView = itemView.findViewById(R.id.ready_match_item_teamName);
            stadiumTextView = itemView.findViewById(R.id.ready_match_item_stadium);
            matchDayTextView = itemView.findViewById(R.id.ready_match_item_matchday);
            readyMatchItemLinearLayout = itemView.findViewById(R.id.ready_match_item_ll);
            voteButton = itemView.findViewById(R.id.ready_match_item_vote_tv);
            oppTeamMatchResultButton = itemView.findViewById(R.id.tv_opp_team_match_result);

            readyMatchItemLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog("취소하시겠습니까?", getAdapterPosition());
                    return false;
                }
            });

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VoteActivity.class);
                    intent.putExtra("matchKey", mData.get(getAdapterPosition()).getMatchKey());
                    context.startActivity(intent);
                }
            });

            oppTeamMatchResultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MatchResultActivity.class);
                    intent.putExtra("teamKey", mData.get(getAdapterPosition()).getOppentTeamKey());
                    context.startActivity(intent);
                }
            });

        }

        public void setItem(ReadyMatch match) {
            teamNameTextView.setText(match.getOppentTeamName());
            stadiumTextView.setText(match.getStadium());
            matchDayTextView.setText(transformPlayData(match));
        }

        public void showDialog(String message, final int position){
            AlertDialog.Builder joinDialog_builder;
            joinDialog_builder = new AlertDialog.Builder(context);
            joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelMatchOfMyTeam(position);
                }
            }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            final AlertDialog joinDialog = joinDialog_builder.create();
            joinDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.mainColor));
                    joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.mainColor));
                }
            });
            joinDialog.show();
        }

        public void cancelMatchOfMyTeam(final int position){
            final LinkedHashMap<String, ReadyMatch> homeReadyMatchList = team.getReady_play();
            final String awayTeamKey = mData.get(position).getOppentTeamKey();
            final String matchKey = mData.get(position).getMatchKey();
            homeReadyMatchList.remove(matchKey);
            Log.e("homeReadyMatchList", homeReadyMatchList + " ");

            mRef.child("team").child(team.getTeam_key()).child("ready_play").setValue(homeReadyMatchList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String oppTeamName = mData.get(getAdapterPosition()).getOppentTeamName();
                    String title = "매치 취소 알림";
                    String contents = oppTeamName + "팀과의 매치가 취소되었습니다.";
                    cancelMatchOfOpponentTeam(awayTeamKey, matchKey, position);
                    team.setReady_play(homeReadyMatchList);

                    sendFCM(title, contents, getTeamFCMTokens(team));

                }
            });
        }

        //TODO : 수정필요
        public void cancelMatchOfOpponentTeam(final String teamKey, final String matchKey, final int position){
            mRef.child("team").child(teamKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LinkedHashMap<String, ReadyMatch> tempMap;
                    final Team team = dataSnapshot.getValue(Team.class);
                    tempMap = team.getReady_play();
                    tempMap.remove(matchKey);
                    mRef.child("team").child(teamKey).child("ready_play").setValue(tempMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mData.remove(position);
                            notifyItemRemoved(position);
                            Log.e("ReadyMatchAdapter", mData.size() + " ");
                            if(mData.size() == 0){
                                mCallBack.changeSize(0);
                            }
                            mCallBack.changeSize(mData.size());
                            Toast.makeText(context, "취소 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            String title = "매치 취소 알림";
                            String contents = BaseActivity.team.getTeam_name() + "팀과의 매치가 취소되었습니다.";
                            sendFCM(title, contents, getTeamFCMTokens(team));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private ArrayList<String> getTeamFCMTokens(Team team){
        ArrayList<String> tokens = new ArrayList<>();
        for(User u : team.getTeam_member().values()){
            tokens.add(u.getFcmToken());
        }

        return tokens;
    }

    private void sendFCM(String title, String contents, ArrayList<String> tokens) {
        fla.sendMessage(context, title, contents, tokens, FootballLiveApplication.MATCH_NOTI_CHANNEL_ID);
    }

    public String transformPlayData(ReadyMatch play) {
        String day, time, sum;
        String[] dayArry = play.getMatchDay().split("-");
        String[] stratTimeArry = play.getStartTime().split("-");
        String[] endTimeArry = play.getEndTime().split("-");
        day = dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일 ";
        if (stratTimeArry[1].equals("0")) {
            if (endTimeArry[1].equals("0")) {
                time = stratTimeArry[0] + "시 ~ " + endTimeArry[0] + "시";
            } else {
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분 ~ " + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        } else {
            if (endTimeArry[1].equals("0")) {
                time = stratTimeArry[0] + "시 ~" + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            } else {
                time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분 ~ " + endTimeArry[0] + "시" + endTimeArry[1] + "분";
            }
        }

        sum = day + time;
        return sum;
    }


}
