package com.example.footballlive.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.ChatActivity;
import com.example.footballlive.FootballLiveApplication;
import com.example.footballlive.MatchResultActivity;
import com.example.footballlive.MemberManagementActivity;
import com.example.footballlive.MyTeamAppListActivity;
import com.example.footballlive.MyTeamMatchListActivity;
import com.example.footballlive.R;
import com.example.footballlive.ReadyMatchListActivity;
import com.example.footballlive.ReadyMemberActivity;
import com.example.footballlive.data.MatchResult;
import com.example.footballlive.data.ReadyMatch;
import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.example.footballlive.BaseActivity.team;
import static com.example.footballlive.BaseActivity.user;


/* 변수명 수정 완료 */
public class TeamFragment extends Fragment {

    public final static String TAG = "TeamFragment";

    ImageView chat;
    TextView postListButton, readyMemberButton, readyMatchButton, teamApplicationButton, memberManagementButton, leaveTeamButton, matchResultButton;
    TextView matchStadiumTextView, matchDayTextView, matchOppTeamTextView;
    LinearLayout blindLinearLayout, teamLinearLayout, matchLinearLayout, matchHideLinearLayout;
    FrameLayout teamFrameLayout;

    Team mTeam = team;
    ArrayList<ReadyMatch> readyMatches = new ArrayList<>();
    ReadyMatch readyMatch;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_team, container, false);

        chat = rootView.findViewById(R.id.iv_chat);
        postListButton = rootView.findViewById(R.id.team_post_list_btn);
        readyMemberButton = rootView.findViewById(R.id.team_ready_member_btn);
        readyMatchButton = rootView.findViewById(R.id.team_ready_match_btn);
        teamApplicationButton = rootView.findViewById(R.id.team_app_list_btn);

        blindLinearLayout = rootView.findViewById(R.id.team_hide_ll);
        teamLinearLayout = rootView.findViewById(R.id.team_team_ll);

        memberManagementButton = rootView.findViewById(R.id.team_member_management_btn);
        leaveTeamButton = rootView.findViewById(R.id.team_leave_team_btn);
        matchStadiumTextView = rootView.findViewById(R.id.team_match_stadium_tv);
        matchDayTextView = rootView.findViewById(R.id.team_match_day_tv);
        matchOppTeamTextView = rootView.findViewById(R.id.team_match_opp_team_name_tv);

        matchLinearLayout = rootView.findViewById(R.id.team_match_ll);
        matchHideLinearLayout = rootView.findViewById(R.id.team_match_hide_ll);
        teamFrameLayout = rootView.findViewById(R.id.team_fl);

        matchResultButton = rootView.findViewById(R.id.team_match_result_btn);


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team != null) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "소속된 팀이 없어 채팅방에 입장 불가 합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        postListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyTeamMatchListActivity.class);
                startActivity(intent);
            }
        });


        readyMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team != null) {
                    Intent intent = new Intent(getActivity(), ReadyMemberActivity.class);
                    startActivity(intent);
                }
            }
        });

        readyMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReadyMatchListActivity.class);
                startActivity(intent);
            }
        });

        teamApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyTeamAppListActivity.class);
                startActivity(intent);
            }
        });

        memberManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemberManagementActivity.class);
                startActivity(intent);
            }
        });

        matchResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MatchResultActivity.class);
                intent.putExtra("teamKey", team.getTeam_key());
                startActivity(intent);
            }
        });

        leaveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team.getTeam_leader().equals(user.getUid())) {
                    Toast.makeText(getActivity(), "팀의 회장은 탈퇴할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog("팀을 탈퇴하시겠습니까?\n팀을 탈퇴 후에는 복구 할 수 없습니다.", 0);
            }
        });

        return rootView;
    }

    // fragment 띄울 때마다 현재시각과 예정된 경기 종료 시간이 끝났으면 결과 입력 dialog 띄워주기
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
}

    private void updateUI() {

        if(user == null){
            blindLinearLayout.setVisibility(View.VISIBLE);
            teamLinearLayout.setVisibility(View.INVISIBLE);
            return;
        }

        if(team == null){
            blindLinearLayout.setVisibility(View.VISIBLE);
            teamLinearLayout.setVisibility(View.INVISIBLE);
            return;
        }

        teamLinearLayout.setVisibility(View.VISIBLE);
        blindLinearLayout.setVisibility(View.INVISIBLE);

        Log.e(TAG, "blindLinearLayout Check : " + blindLinearLayout.getVisibility());
        checkReadyMatch();
    }

    private void checkReadyMatch(){
        Log.e(TAG, "checkReadyMatch() 실행");
        try {
            if (mTeam.getReady_play() != null && !mTeam.getReady_play().isEmpty()) {
                compareDate();
                if(mTeam.getReady_play().isEmpty()){
                    matchLinearLayout.setVisibility(View.VISIBLE);
                    matchHideLinearLayout.setVisibility(View.INVISIBLE);
                    return;
                }
                matchLinearLayout.setVisibility(View.VISIBLE);
                matchHideLinearLayout.setVisibility(View.INVISIBLE);
                sortMatch();
            }else{
                matchLinearLayout.setVisibility(View.INVISIBLE);
                matchHideLinearLayout.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){
            matchLinearLayout.setVisibility(View.INVISIBLE);
            matchHideLinearLayout.setVisibility(View.VISIBLE);
            Log.e(TAG, "checkReadyMatch() - catch문 실행");
        }
    }

    private void getTeamData() {
        mRef.child("team").child(user.getTeamid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTeam = dataSnapshot.getValue(Team.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 변경된 유저 데이터 집어넣어주기
    public void removeTeamId(){
        mRef.child("users").child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                removeUserData();
            }
        });
    }

    // team 데이터에서 해당 유저 지우기
    public void removeUserData(){
        mRef.child("team").child(team.getTeam_key()).child("team_member").child(user.getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                sendFCM();

                team = null;
                Toast.makeText(getActivity(), "성공적으로 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), getActivity().getClass());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void sendFCM() {
        String name = user.getName();
        String title = "회원 탈퇴";
        String contents = name + "님이 팀을 탈퇴 하였습니다.";
        FootballLiveApplication fla = (FootballLiveApplication)getActivity().getApplication();
        LinkedHashMap<String, User> members = team.getTeam_member();
        members.remove(user.getUid());
        ArrayList<String> fcmTokens = new ArrayList<>();
        for(User u : members.values()){
            fcmTokens.add(u.getFcmToken());
        }
        fla.sendMessage(getActivity(), title, contents, fcmTokens, FootballLiveApplication.MEMBER_NOTI_CHANNEL_ID);
    }

    public void showDialog(String message, final int dialogCode){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(getActivity());
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (dialogCode) {
                    case 0:
                        user.setTeamid(null);
                        user.setBacknumber(null);
                        removeTeamId();
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), MatchResultActivity.class);
                        intent.putExtra("teamKey", team.getTeam_key());
                        startActivity(intent);
                        break;
               }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    switch (dialogCode){
                        case 1:
                            updateUI();
                    }
            }
        });
        final AlertDialog joinDialog = joinDialog_builder.create();
        joinDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
                joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
            }
        });
        joinDialog.show();
    }

    private void sortMatch(){
        readyMatches.clear();
        LinkedHashMap<String, ReadyMatch> readyMatchLinkedHashMap;

        readyMatchLinkedHashMap = mTeam.getReady_play();
        Log.e("TeamDataCheck", mTeam.getTeam_key() + " " + mTeam.getReady_play());
        for(ReadyMatch rm : readyMatchLinkedHashMap.values()){
            readyMatches.add(rm);
        }

        Collections.sort(readyMatches);

        ReadyMatch nextMatch;
        nextMatch = readyMatches.get(0);

        matchOppTeamTextView.setText("vs " + nextMatch.getOppentTeamName());
        matchStadiumTextView.setText(nextMatch.getStadium());
        matchDayTextView.setText(transformPlayData(nextMatch));
    }

    public String transformPlayData(ReadyMatch play){
        String day, time, sum;
        String[] dayArry = play.getMatchDay().split("-");
        String[] stratTimeArry = play.getStartTime().split("-");
        String[] endTimeArry = play.getEndTime().split("-");
        day = dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일 ";
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

    private void compareDate(){
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        Date currentTime = null;
        try {
            currentTime = simpleDateFormat.parse(simpleDateFormat.format(now));
        }catch (Exception e){

        }
        LinkedHashMap<String, ReadyMatch> team_ready_match = team.getReady_play();
        LinkedHashMap<String, MatchResult> team_match_result = team.getMy_team_match_result();

        /*예정된 경기 목록 중 끝난 경기가 있는 지 확인*/
        for(ReadyMatch r : team_ready_match.values()){
            String matchTime = r.getMatchDay() + " " + r.getEndTime();
            Date matchDate = null;
            Log.e("matchTimeCheck", matchTime);

            try {
                matchDate = simpleDateFormat.parse(matchTime);
            }catch (Exception e){

            }

            int compare = currentTime.compareTo(matchDate);

            if(compare > 0){
                readyMatch = r;

                /* 앱 내 team data 변경 */
                team_ready_match.remove(r.getMatchKey());
                team.setReady_play(team_ready_match);
                MatchResult m = new MatchResult();
                m.setMyTeam(team.getTeam_name());
                m.setOppTeam(r.getOppentTeamName());
                m.setMatchKey(r.getMatchKey());
                m.setMatchDay(r.getMatchDay());
                m.setStadium(r.getStadium());
                team_match_result.put(r.getMatchKey(), m);

                transferReadyMatch(team_ready_match, team_match_result);
                return;
            }
        } //end for
    }

    private void transferReadyMatch(LinkedHashMap<String, ReadyMatch> rm, final LinkedHashMap<String, MatchResult> mr){
        mRef.child("team").child(team.getTeam_key()).child("ready_play").setValue(rm).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRef.child("team").child(team.getTeam_key()).child("my_team_match_result").setValue(mr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "transfer success!");
                        showDialog("끝난 경기가 있습니다.\n경기 결과를 입력 하시겠습니까?", 1);

                    }
                });
            }
        });
    }
}
