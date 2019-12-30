package com.example.footballlive.adapter;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.dialog.BacknumberDialog;
import com.example.footballlive.BaseActivity;
import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.R;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* 변수명 수정 완료 */
public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.Viewholder> {
    LinkedHashMap<String, User> mData;
    ArrayList<String> keyList = new ArrayList<>();
    Context context;
    User user;
    FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    int position;
    FootballLiveApplication fla;

    public MemberListAdapter(LinkedHashMap<String, User> mData, Context context, Application app){
        this.context = context;
        this.mData = mData;
        this.fla = (FootballLiveApplication)app;
        for(Map.Entry<String, User> entry : mData.entrySet()){
            keyList.add(entry.getKey());
        }
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.team_member_list_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        User user = mData.get(keyList.get(position));
        holder.setItem(user);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView nameTextView, positionTextView, backnumberTextView, inputBacknumberButton, releaseButton;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.member_list_item_name_tv);
            positionTextView = itemView.findViewById(R.id.member_list_item_position_tv);
            backnumberTextView = itemView.findViewById(R.id.member_list_item_backnumber_tv);
            releaseButton = itemView.findViewById(R.id.member_list_item_release_btn);
            inputBacknumberButton = itemView.findViewById(R.id.member_list_item_input_backnumber_btn);

            inputBacknumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!cUser.getUid().equals(BaseActivity.team.getTeam_leader())){
                        Toast.makeText(context, "등번호 입력은 임원진 권한 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    position = getAdapterPosition();
                    BacknumberDialog backnumberDialog = new BacknumberDialog(context, mData.get(keyList.get(position)).getUid());
                    backnumberDialog.setDialogListner(new BacknumberDialog.BackNumberDialogListner() {
                        @Override
                        public void onButtonClicked(String backnumber) {
                            User user = mData.get(keyList.get(getAdapterPosition()));
                            user.setBacknumber(backnumber);
                            mData.put(keyList.get(getAdapterPosition()), user);
                            BaseActivity.team.setTeam_member(mData);
                            notifyItemChanged(getAdapterPosition());
                        }
                    });
                    backnumberDialog.show();
                }
            });

            releaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!cUser.getUid().equals(BaseActivity.team.getTeam_leader())){
                        Toast.makeText(context, "방출은 임원진 권한 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    position = getAdapterPosition();
                    showDialog("팀원을 방출 시키겠습니까? \n방출 후에는 복구할 수 없습니다.");
                }
            });

        }

        public void setItem(User user){
            nameTextView.setText(user.getName());
            positionTextView.setText(user.getPosition());
            backnumberTextView.setText(user.getBacknumber());
        }

        void showDialog(String message){
            AlertDialog.Builder joinDialog_builder;
            joinDialog_builder = new AlertDialog.Builder(context);
            joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeMember(position);
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

        void removeMember(int position){
            user = mData.get(keyList.get(position));
            mData.remove(user.getUid());
            Log.e("MemberListAdapter", user.getUid() + " " + user.getTeamid());
            mRef.child("team").child(BaseActivity.team.getTeam_key()).child("team_member").setValue(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user.setTeamid(null);
                    user.setBacknumber(null);
                    changeUserData();
                }
            });
            notifyItemRemoved(position);
        }

        void changeUserData(){
            mRef.child("users").child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "방출 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    sendFCM();
                    BaseActivity.team.setTeam_member(mData);
                }
            });
        }

        private void sendFCM() {
            String title = "방출 알림";
            String contents = "팀에서 방출 되었습니다.";
            ArrayList<String> fcmTokens = new ArrayList<>();

            fcmTokens.add(user.getFcmToken());

            fla.sendMessage(context, title, contents, fcmTokens, FootballLiveApplication.MEMBER_NOTI_CHANNEL_ID);
        }
    }
}
