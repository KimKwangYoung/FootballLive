package com.example.footballlive.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.dialog.ApplicationDialog;
import com.example.footballlive.ChangeListSize;
import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.MatchResultActivity;
import com.example.footballlive.R;
import com.example.footballlive.data.ReadyMatch;
import com.example.footballlive.data.ShortPlay;
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

import static com.example.footballlive.BaseActivity.team;

/* 변수명 수정 완료 */
public class ApplicationTeamAdapter extends RecyclerView.Adapter<ApplicationTeamAdapter.Viewholder> {

    Context context;
    ChangeListSize mCallback;
    ArrayList<Team> teamList;
    ArrayList<String> greetings;
    String postKey;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    ShortPlay post;
    int adapterPosition;
    FootballLiveApplication fla;

    public ApplicationTeamAdapter(Context context
            , ArrayList<Team> teamList
            , ArrayList<String> greetings
            , String postKey
            , ChangeListSize listener
            , Application app) {
        this.context = context;
        this.teamList = teamList;
        this.greetings = greetings;
        this.postKey = postKey;
        this.mCallback = listener;
        this.fla = (FootballLiveApplication)app;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.application_team_item, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Team team = teamList.get(position);
        String greetings = this.greetings.get(position);

        holder.setItem(team, greetings);
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView teamNameTextView, greetingsTextView, rejectButton, acceptButton, oppTeamMatchResultButton;
        LinearLayout itemLinearLayout;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            teamNameTextView = itemView.findViewById(R.id.application_team_item_team_name_tv);
            greetingsTextView = itemView.findViewById(R.id.application_team_item_greetings_tv);
            rejectButton = itemView.findViewById(R.id.application_team_item_reject_btn);
            acceptButton = itemView.findViewById(R.id.application_team_item_accept_btn);
            oppTeamMatchResultButton = itemView.findViewById(R.id.application_team_match_result_btn);

            itemLinearLayout = itemView.findViewById(R.id.application_team_item_ll);

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectMatch(teamList.get(getAdapterPosition()));
                    teamList.remove(getAdapterPosition());
                    greetings.remove(getAdapterPosition());

                    notifyItemRemoved(getAdapterPosition());
                }
            });

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterPosition = getAdapterPosition();
                    getPost(teamList.get(getAdapterPosition()));
                }
            });
            itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationDialog applicationDialog = new ApplicationDialog(context, greetings.get(getAdapterPosition()));
                    applicationDialog.callFunction();
                }
            });

            oppTeamMatchResultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MatchResultActivity.class);
                    intent.putExtra("teamKey", teamList.get(getAdapterPosition()).getTeam_key());
                    context.startActivity(intent);
                }
            });
        }

        public void setItem(Team team, String greetings){
            teamNameTextView.setText(team.getTeam_name());
            greetingsTextView.setText(greetings);
        }
    }

    public void rejectMatch(final Team t){
        mRef.child("team")
                .child(team.getTeam_key())
                .child("post")
                .child(postKey)
                .child("applicationTeam")
                .child(t.getTeam_key())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 거절당한 팀 신청목록에서 지워주기
                removeApplicationMatch(team.getTeam_key());
            }
        });

    }

    public void removeApplicationMatch(String teamKey){
        mRef.child("team").child(teamKey).child("my_team_application_list").child(postKey)
                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mCallback.changeSize(teamList.size());
                Toast.makeText(context, "매치 신청이 거절되었습니다.", Toast.LENGTH_SHORT);
            }
        });
    }


    public void getPost(final Team t){
        mRef.child("team").child(team.getTeam_key()).child("post").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(ShortPlay.class);
                addHomeTeamReadyMatch(post, t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addHomeTeamReadyMatch(ShortPlay post, final Team t){
        String matchDay = post.getMatchDay();
        String startTime = post.getStartTime();
        String endTime = post.getEndTime();
        String stadium = post.getStadium();

        final LinkedHashMap<String, ReadyMatch> myTeamReadyPlay;
        myTeamReadyPlay = t.getReady_play();

        if(myTeamReadyPlay.containsKey(postKey)){
            Toast.makeText(context, "이미 매칭이 완료된 게시글로 기존 매칭을 취소하고 진행하여 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ReadyMatch readyMatch = new ReadyMatch();
        readyMatch.setAllMember(t.getTeam_member());
        readyMatch.setMatchDay(matchDay);
        readyMatch.setStartTime(startTime);
        readyMatch.setEndTime(endTime);
        readyMatch.setOppentTeamKey(t.getTeam_key());
        readyMatch.setOppentTeamName(t.getTeam_name());
        readyMatch.setStadium(stadium);
        readyMatch.setMatchKey(postKey);

        myTeamReadyPlay.put(postKey, readyMatch);
        mRef.child("team").child(team.getTeam_key()).child("ready_play").setValue(myTeamReadyPlay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                String title = "매치 성사 알림";
                String contents = "매치가 성사 되었습니다. 예정된 매치 목록을 확인해 보세요!";
                t.setReady_play(myTeamReadyPlay);
                sendFCM(title, contents, getTeamFCMTokens(team));
                addAwayTeamReadyMatch(t, readyMatch);
            }
        });

    }

    public void addAwayTeamReadyMatch(final Team t, ReadyMatch readyMatch){
        readyMatch.setOppentTeamName(team.getTeam_name());
        readyMatch.setOppentTeamKey(team.getTeam_key());
        readyMatch.setAllMember(t.getTeam_member());

        LinkedHashMap<String, ReadyMatch> oppentTeamReadyPlay;
        oppentTeamReadyPlay = t.getReady_play();
        oppentTeamReadyPlay.put(post.getPostKey(), readyMatch);
        mRef.child("team").child(t.getTeam_key()).child("ready_play").setValue(oppentTeamReadyPlay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRef.child("team").child(team.getTeam_key()).child("post").child(postKey).child("applicationTeam").child(t.getTeam_key()).setValue(null);
                teamList.remove(adapterPosition);
                greetings.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                mCallback.changeSize(teamList.size());
                String title = "매치 성사 알림";
                String contents = "매치가 성사 되었습니다. 예정된 매치 목록을 확인해 보세요!";
                sendFCM(title, contents, getTeamFCMTokens(t));
                Toast.makeText(context, "매칭이 완료되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private ArrayList<String> getTeamFCMTokens(Team t){
        ArrayList<String> tokens = new ArrayList<>();
        for(User u : t.getTeam_member().values()){
            tokens.add(u.getFcmToken());
        }

        return tokens;
    }

    private void sendFCM(String title, String contents, ArrayList<String> tokens) {
        fla.sendMessage(context, title, contents, tokens, FootballLiveApplication.MATCH_NOTI_CHANNEL_ID);
    }


}
