package com.example.footballlive.dialog;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.R;
import com.example.footballlive.data.MatchPost;
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
import java.util.Map;

import static com.example.footballlive.BaseActivity.team;
import static com.example.footballlive.BaseActivity.user;

/* 변수명 수정 완료 */
public class CustomDialog {
    private Context context;

    String region, key, checkFunction, userUid, teamKey;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    EditText contentEditText;
    MatchPost matchPost;
    Dialog dlg;
    FootballLiveApplication fla = null;
    public CustomDialog(Context context, String region, MatchPost matchPost, String checkFunction, Application app) {
        this.context = context;
        this.region = region;
        this.checkFunction = checkFunction;
        this.matchPost = matchPost;
        this.fla = (FootballLiveApplication)app;
    }

    public CustomDialog(Context context, String region, String userUid, String teamKey, String checkFunction, Application app){
        this.context = context;
        this.region = region;
        this.userUid = userUid;
        this.teamKey = teamKey;
        this.checkFunction = checkFunction;
        this.fla = (FootballLiveApplication)app;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        contentEditText = dlg.findViewById(R.id.custom_dialog_content_et);
        final Button okButton = dlg.findViewById(R.id.custom_dialog_btn);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFunction.equals("match")) {
                    applyMatch();
                }else if(checkFunction.equals("recruiting") ){
                    applyTeam();
                }
            }
        });


    }

    // 게시글 작성 팀의 해당 매치 신청팀으로 추가
    public void applyMatch(){
        mRef.child("team").child(matchPost.getHome_team()).child("post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds : dataSnapshot.getChildren()){
                    if(matchPost.getPlay_key().equals(ds.getValue(ShortPlay.class).getPostKey())){

                        LinkedHashMap<String, String> applicationTeamList = ds.getValue(ShortPlay.class).getApplicationTeam();

                        if(applicationTeamList.containsKey(team.getTeam_key())){/* 이미 신청한 내역이 있는지 검사 */
                            Toast.makeText(context, "이미 신청한 경기입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        applicationTeamList.put(team.getTeam_key(), contentEditText.getText().toString());

                        mRef.child("team")
                                .child(matchPost.getHome_team())
                                .child("post")
                                .child(ds.getKey())
                                .child("applicationTeam")
                                .setValue(applicationTeamList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        addMyTeamApplication(ds.getValue(ShortPlay.class));
                                    }
                                });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 우리 팀 신청 리스트에 추가
    public void addMyTeamApplication(ShortPlay shortPlay){
        LinkedHashMap<String, String> emptyMap = new LinkedHashMap<>();
        shortPlay.setApplicationTeam(emptyMap); /* 해당 게시글 매치 신청 리스트는 비워서 넣어주기 */
        LinkedHashMap<String, ShortPlay> teamAppList = team.getMy_team_application_list();
        teamAppList.put(shortPlay.getPostKey(), shortPlay);
        mRef.child("team").child(user.getTeamid()).child("my_team_application_list")
                .setValue(teamAppList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getFCMTokens();
                Toast.makeText(context, "신청 완료 되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getFCMTokens() {
        mRef.child("team").child(matchPost.getHome_team()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Team team = dataSnapshot.getValue(Team.class);
                LinkedHashMap<String, User> members = team.getTeam_member();
                ArrayList<String> tokens = new ArrayList<>();
                for(User u : members.values()){
                    tokens.add(u.getFcmToken());
                    Log.e("CustomDialog", u.getFcmToken());
                }
                sendFCM(tokens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFCM(ArrayList<String> tokens) {
        String title = "매치 신청 알림";
        String contents = "새로운 매치 신청이 있습니다.";

        fla.sendMessage(context, title, contents, tokens, FootballLiveApplication.MATCH_NOTI_CHANNEL_ID);
        dlg.dismiss();
    }

    private void applyTeam(){
        if(user.getTeamid() == null) {
            mRef.child("team").child(teamKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean checkUser = false;
                    final Team team = dataSnapshot.getValue(Team.class);
                    LinkedHashMap<String, String> ready_member = team.getReady_member();
                    ArrayList<String> readyMemberKey = new ArrayList<>();

                    for(Map.Entry<String, String> entry : ready_member.entrySet()){
                        readyMemberKey.add(entry.getKey());
                    }
                    for(String key : readyMemberKey){
                        if(key.equals(user.getUid())){
                            Toast.makeText(context, "이미 신청된 상태입니다.", Toast.LENGTH_LONG).show();
                            checkUser = true;
                            break;
                        }
                    }
                    if(!checkUser) {
                        ready_member.put(user.getUid(), contentEditText.getText().toString());
                        team.setReady_member(ready_member);
                        mRef.child("team").child(teamKey).child("ready_member").setValue(ready_member).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "성공적으로 신청되었습니다.", Toast.LENGTH_SHORT).show();
                                LinkedHashMap<String, User> usersMap = team.getTeam_member();
                                ArrayList<String> fcmTokens = new ArrayList<>();

                                for(User u : usersMap.values()){
                                    fcmTokens.add(u.getFcmToken());
                                }
                                String title = "회원 가입 신청";
                                String contents = "새로운 회원 가입 신청이 있습니다.";
                                fla.sendMessage(context, title, contents, fcmTokens, FootballLiveApplication.MEMBER_NOTI_CHANNEL_ID);
                                dlg.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(context, "이미 팀 가입이 되어 있어 신청할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

}
