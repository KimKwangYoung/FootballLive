package com.example.footballlive.adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.footballlive.BaseActivity.team;

import com.example.footballlive.dialog.ApplicationDialog;
import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.R;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* 변수명 수정 완료 */
public class ReadyMemberAdapter extends RecyclerView.Adapter<ReadyMemberAdapter.ViewHolder> {

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    LinkedHashMap<User, String> mData;
    ArrayList<User> keyList = new ArrayList<>();
    Context context;

    FootballLiveApplication fla;
    public ReadyMemberAdapter(LinkedHashMap<User, String> mData, Context context, Application app) {
        this.mData = mData;
        this.context = context;
        this.fla = (FootballLiveApplication)app;
        for(Map.Entry<User, String> entry : mData.entrySet()){
            keyList.add(entry.getKey());
            Log.e("ReadyMemberAdapter", entry.getKey().getName());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.ready_member_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = keyList.get(position);
        String greetings = mData.get(keyList.get(position));
        holder.setItem(user, greetings);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView, positionTextView, greetingsTextView;
        Button rejectButton, receiveButton;
        LinearLayout memberLinearLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);


            nameTextView = itemView.findViewById(R.id.ready_member_item_name_tv);
            positionTextView = itemView.findViewById(R.id.ready_member_position_tv);
            greetingsTextView = itemView.findViewById(R.id.ready_member_greetings_tv);
            memberLinearLayout = itemView.findViewById(R.id.ready_member_item_ll);
            rejectButton = itemView.findViewById(R.id.ready_member_item_reject_btn);
            receiveButton = itemView.findViewById(R.id.ready_member_item_receive_btn);
        }

        public void setItem(User user, String greetings){
            nameTextView.setText(user.getName());
            positionTextView.setText(user.getPosition());
            greetingsTextView.setText(greetings);

            receiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTeamId();
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "멤버 가입 신청을 거절하였습니다.", Toast.LENGTH_SHORT).show();
                    rejectUser();
                }
            });

            memberLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationDialog applicationDialog = new ApplicationDialog(context, mData.get(keyList.get(getAdapterPosition())));
                    applicationDialog.callFunction();
                }
            });
        }

        public void joinUser(){
            final int position = getAdapterPosition();
            final User ready_user = keyList.get(getAdapterPosition());
            Log.e("ReadyMemberAdapter", keyList + " / " + getAdapterPosition());

            mRef.child("team").child(team.getTeam_key()).child("ready_member").child(ready_user.getUid()).removeValue();
            final LinkedHashMap<String, User> teamMember = team.getTeam_member();
            teamMember.put(ready_user.getUid(), ready_user);

            mRef.child("team").child(team.getTeam_key()).child("team_member").setValue(teamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mRef.child("users").child(keyList.get(getAdapterPosition()).getUid()).child("teamid").setValue(team.getTeam_key()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "해당 멤버 가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            //recyclerView 최신화, 불러온 team 데이터 변경해주기
                            sendNotifyMessage(keyList.get(position).getUid());
                            mData.remove(keyList.get(getAdapterPosition()));
                            keyList.remove(getAdapterPosition());
                            LinkedHashMap<String, String> teamReadyMember = team.getReady_member();
                            teamReadyMember.remove(ready_user.getUid());
                            team.setReady_member(teamReadyMember);
                            Log.e("ReadyMemberAdapter", keyList + " / " + getAdapterPosition());
                            notifyItemRemoved(position);


                        }
                    });
                }
            });
        }

        public void rejectUser(){
            final User ready_user = keyList.get(getAdapterPosition());
            mRef.child("team").child(team.getTeam_key()).child("ready_member").child(ready_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    mData.remove(keyList.get(getAdapterPosition()));
                    keyList.remove(getAdapterPosition());

                    LinkedHashMap<String, String> teamReadyMember = team.getReady_member();
                    teamReadyMember.remove(ready_user.getUid());
                    team.setReady_member(teamReadyMember);

                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

        public void checkTeamId(){
            Log.e("keyListData", keyList.get(getAdapterPosition()).getUid() + "");
            mRef.child("users").child(keyList.get(getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User cUser = dataSnapshot.getValue(User.class);
                    if(cUser.getTeamid() == null){
                        joinUser();
                    }else{
                        Toast.makeText(context, "이미 팀이 있는 유저 입니다.", Toast.LENGTH_SHORT).show();
                        rejectUser();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void sendNotifyMessage(String userUid){
            mRef.child("users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String title = "회원 가입 알림";
                    String contents = "회원 가입이 수락 되었습니다.";
                    ArrayList<String> tokens = new ArrayList<>();
                    tokens.add(user.getFcmToken());
                    fla.sendMessage(context, title, contents, tokens, FootballLiveApplication.MEMBER_NOTI_CHANNEL_ID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



}
