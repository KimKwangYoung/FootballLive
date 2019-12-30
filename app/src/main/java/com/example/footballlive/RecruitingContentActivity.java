package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.footballlive.BaseActivity.user;
import com.example.footballlive.data.Recruiting;
import com.example.footballlive.dialog.CustomDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/* 변수명 수정 완료 */
public class RecruitingContentActivity extends AppCompatActivity {
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    TextView titleTextView, teamNameTextView, contentTextView, stadiumTextView, infoTextView;
    String recKey, region;
    Recruiting recruiting;
    private FootballLiveApplication fla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiting_content);

        Intent intent = getIntent();
        recKey = intent.getStringExtra("recruitingKey");
        region = intent.getStringExtra("region");

        fla = (FootballLiveApplication)getApplication();
        contentTextView = findViewById(R.id.recruiting_content_content_tv);
        stadiumTextView = findViewById(R.id.recruiting_content_stadium_tv);
        titleTextView = findViewById(R.id.recruiting_content_title_tv);
        teamNameTextView = findViewById(R.id.recruiting_content_team_name_tv);
        infoTextView = findViewById(R.id.recruiting_content_info_tv);

        getRecruitingData();

        Button applicationButton = findViewById(R.id.recruiting_content_application_btn);
        applicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getTeamid() == null){
                    joinApplication();
                }else{
                    Toast.makeText(getApplicationContext(), "이미 팀이 가입된 상태입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getRecruitingData(){
        mRef.child("recruiting").child(region).child(recKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recruiting = dataSnapshot.getValue(Recruiting.class);
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUI(){
        teamNameTextView.setText("< " + recruiting.getTeamName() + " >");
        titleTextView.setText(recruiting.getTitle());
        stadiumTextView.setText(recruiting.getStadium());
        infoTextView.setText("유니폼 : " + recruiting.getUniform() + "\n회비 : " + recruiting.getDues() + "원");


    }

    public void joinApplication(){
        CustomDialog customDialog = new CustomDialog(RecruitingContentActivity.this, region, user.getUid(), recruiting.getTeamKey(),"recruiting", getApplication());
        customDialog.callFunction();
    }

}
