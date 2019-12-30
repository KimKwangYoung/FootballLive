package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.footballlive.adapter.MatchResultAdapter;
import com.example.footballlive.data.MatchResult;
import com.example.footballlive.data.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/* 변수명 수정 완료 */
public class MatchResultActivity extends AppCompatActivity {
    private final String TAG = "MatchResultActivity";

    TextView teamNameTextView, contentTextView;
    RecyclerView resultRecyclerView;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    String teamKey;
    Team mTeam;

    ArrayList<MatchResult> matchResult= new ArrayList<>();
    boolean isTeamMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        teamNameTextView = findViewById(R.id.result_team_name_tv);

        contentTextView = findViewById(R.id.result_content_tv);
        resultRecyclerView = findViewById(R.id.result_rv);


        Intent intent = getIntent();
        teamKey = intent.getStringExtra("teamKey");
        getTeamData();
    }

    private void getTeamData() {
        mRef.child("team").child(teamKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTeam = dataSnapshot.getValue(Team.class);
                Log.e(TAG, "userTeamID : " + BaseActivity.user.getTeamid() + " teamKey : " + mTeam.getTeam_key());
                if(BaseActivity.user.getTeamid().equals(mTeam.getTeam_key())){
                    isTeamMember = true;
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {
        for(MatchResult m : mTeam.getMy_team_match_result().values()){
            matchResult.add(m);
        }
        teamNameTextView.setText(mTeam.getTeam_name() + "의 경기 결과");
        contentTextView.setText("총 " + matchResult.size() + "경기");
        connectAdapter();
    }

    private void connectAdapter() {

        Log.e(TAG, "isTeamMember : " + isTeamMember + " TeamKey : " + mTeam.getTeam_key());

        LinearLayoutManager layoutManager = new LinearLayoutManager(MatchResultActivity.this, RecyclerView.VERTICAL, false);
        resultRecyclerView.setLayoutManager(layoutManager);
        MatchResultAdapter matchResultAdapter = new MatchResultAdapter(MatchResultActivity.this, mTeam.getMy_team_match_result(), isTeamMember, mTeam.getTeam_key());
        resultRecyclerView.setAdapter(matchResultAdapter);
    }



}
