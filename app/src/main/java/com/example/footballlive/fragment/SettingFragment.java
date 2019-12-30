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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.BaseActivity;
import com.example.footballlive.CreateTeamActivity;
import com.example.footballlive.LoginActivity;
import com.example.footballlive.MainActivity;
import com.example.footballlive.NoticeActivity;
import com.example.footballlive.ProfileupdateActivity;
import com.example.footballlive.R;
import com.example.footballlive.UserCardView;
import com.example.footballlive.data.Team;

import static com.example.footballlive.BaseActivity.user;
import static com.example.footballlive.BaseActivity.team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




/* 변수명 수정 완료 */
public class SettingFragment extends Fragment {

    FirebaseUser mUser;

    TextView loginAndJoinButton, profileUpdateButton, noticeButton, createTeamButton;
    LinearLayout settingLinearLayout;
    UserCardView profileCardView; // 유저 프로필 카드뷰
    FrameLayout frame;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting, container, false);


        frame = rootView.findViewById(R.id.frame);

        loginAndJoinButton = rootView.findViewById(R.id.setting_login_btn);
        profileCardView = rootView.findViewById(R.id.setting_user_profile_cv);
        profileUpdateButton = rootView.findViewById(R.id.setting_profile_update_btn);
        noticeButton = rootView.findViewById(R.id.setting_notice_btn);
        createTeamButton = rootView.findViewById(R.id.setting_create_team_btn);

        settingLinearLayout = rootView.findViewById(R.id.setting_logout_ll);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("로그아웃 하시겠습니까?", 1);
            }
        });

        loginAndJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        profileUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null){
                    Intent intent = new Intent(getActivity(), ProfileupdateActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{

                }
            }
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null){
                Intent intent = new Intent(getActivity(), CreateTeamActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "로그인 먼저 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && user.getTeamid() != null) {
            checkTeam();
        }
        if(mUser != null){
            settingLinearLayout.setVisibility(View.VISIBLE);
            changeFrame(true);
        }else {
            settingLinearLayout.setVisibility(View.INVISIBLE);
            changeFrame(false);
        }
    }

    public void getUserProfile(){
        try {
            profileCardView.setName(user.getName());
            if(team != null) {
                profileCardView.setTeamName(team.getTeam_name());
                if(user.getBacknumber() != null) {
                    profileCardView.setPosition(user.getPosition() + "  " + user.getBacknumber());
                }else{
                    profileCardView.setPosition(user.getPosition());
                }
            }else{
                profileCardView.setPosition(user.getPosition());
                profileCardView.setTeamName("소속 팀 없음");
            }
        }catch (NullPointerException e){
            showDialog("예기치 못한 오류가 발생하였습니다.\n 앱을 다시 실행하여 주세요", 3);
            getActivity().finish();
        }


    }
    // 프로필 카드 or 로그인 Framelayout관련 메서드
    public void changeFrame(boolean userLogin){
        if(userLogin){
            Log.e("User", user.getUid() + " " + user.getName() + " ");
            getUserProfile();
            profileCardView.setVisibility(View.VISIBLE);
            loginAndJoinButton.setVisibility(View.INVISIBLE);
        }else{
            loginAndJoinButton.setVisibility(View.VISIBLE);
            profileCardView.setVisibility(View.INVISIBLE);
        }
    }

    public void showDialog(String message, final int checkDialog){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(getActivity());
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkDialog == 1){
                    if (mUser != null) {
                        FirebaseAuth.getInstance().signOut();
                        mUser = null;
                        BaseActivity.user = null;
                        BaseActivity.team = null;
                        loginAndJoinButton.setText("로그인 및 회원가입");
                        changeFrame(false);
                        settingLinearLayout.setVisibility(View.INVISIBLE);
                    }
                }else if(checkDialog == 2){
                    getActivity().finish();
                }
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
                joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
                joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
            }
        });
        joinDialog.show();
    }

    public void checkTeam(){
        try {

            if(user == null){
                return;
            }
            if(user.getTeamid() == null){
                return;
            }
            if(team == null){
                return;
            }

            if(!user.getTeamid().equals(team.getTeam_key())) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("team").child(user.getTeamid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        team = dataSnapshot.getValue(Team.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }catch (NullPointerException e){
            Toast.makeText(getActivity(), "오류로 인해 메인화면을 다시 실행합니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }catch (Exception e){
            Toast.makeText(getActivity(), "예기치 못한 오류가 발생하였습니다. 앱을 다시 시작해주세요", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            }

        }
    }
