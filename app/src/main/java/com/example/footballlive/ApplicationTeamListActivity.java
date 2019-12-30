package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.footballlive.adapter.ApplicationTeamAdapter;
import com.example.footballlive.data.ShortPlay;
import com.example.footballlive.data.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.footballlive.BaseActivity.user;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/* 변수명 수정 완료 */
public class ApplicationTeamListActivity extends AppCompatActivity {
    String postKey;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    TextView applicationTeamCntTextView;
    RecyclerView teamListRecyclerView;

    LinkedHashMap<String, String> applicationTeam = new LinkedHashMap<>();
    LinkedHashMap<String, ShortPlay> postList = new LinkedHashMap<>();
    ArrayList<Team> teamList = new ArrayList<>();
    ArrayList<String> greetingList = new ArrayList<>();
    ShortPlay post;

    ApplicationTeamAdapter applicationTeamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_team_list);

        Intent intent = getIntent();
        postKey = intent.getStringExtra("postKey");

        teamListRecyclerView = findViewById(R.id.application_team_rv);
        applicationTeamCntTextView = findViewById(R.id.application_team_cnt_tv);
        getPost();



    }

    public void getPost(){
        mRef.child("team").child(user.getTeamid()).child("post").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applicationTeam = dataSnapshot.getValue(ShortPlay.class).getApplicationTeam();
                Log.e("applicationTeam", applicationTeam + " ");
                getApplicationTeam();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for(Map.Entry<String,ShortPlay> entry : postList.entrySet()){
            if(postKey.equals(entry.getKey())){
                post = entry.getValue();
            }
        }
    }

    // teamNameList, greetingsList 데이터 채워넣기
    public void getApplicationTeam(){
        final ArrayList<String> teamKeyList = new ArrayList<>();


        for(Map.Entry<String, String> entry : applicationTeam.entrySet()){
            teamKeyList.add(entry.getKey());
            greetingList.add(entry.getValue());
        }

        Log.e("teamKeyList", teamKeyList + " / " + greetingList);

        mRef.child("team").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String key : teamKeyList){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(key.equals(ds.getValue(Team.class).getTeam_key())){
                            teamList.add(ds.getValue(Team.class));
                        }
                    }
                }

                connectAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void connectAdapter(){

        applicationTeamCntTextView.setText(teamList.size() + "팀");
        Log.e("teamList", teamList + " " + postKey);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        teamListRecyclerView.setLayoutManager(layoutManager);

        applicationTeamAdapter = new ApplicationTeamAdapter(ApplicationTeamListActivity.this, teamList, greetingList, postKey, getInstance(), getApplication());
        teamListRecyclerView.setAdapter(applicationTeamAdapter);
    }

    private ChangeListSize getInstance(){
        return new ChangeListSize() {
            @Override
            public void changeSize(int size) {
                applicationTeamCntTextView.setText(size + "경기");
            }
        };
    }


}

