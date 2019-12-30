package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.footballlive.adapter.ReadyMatchAdapter;
import com.example.footballlive.data.ReadyMatch;
import com.example.footballlive.data.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import static com.example.footballlive.BaseActivity.user;

/* 변수명 수정 완료 */
public class ReadyMatchListActivity extends AppCompatActivity implements ChangeListSize {

    TextView matchCntTextView;
    RecyclerView matchRecyclerView;

    Team team;
    LinkedHashMap<String, ReadyMatch> matchHashMap = new LinkedHashMap<>();
    ArrayList<ReadyMatch> matchList = new ArrayList<>();

    ReadyMatchAdapter readyMatchAdapter;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_match_list);

        matchCntTextView = findViewById(R.id.ready_match_cnt_tv);
        matchRecyclerView = findViewById(R.id.ready_match_rv);

        getTeamData();
    }

    void getTeamData(){
        mRef.child("team").child(user.getTeamid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
                setMatchData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setMatchData(){
        matchHashMap = team.getReady_play();

        for(ReadyMatch match : matchHashMap.values()){
            matchList.add(match);
        }

        sortMatchList();
    }

    void connectAdapter(){


        matchCntTextView.setText(matchList.size() + "경기");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        if(layoutManager != null) {
            matchRecyclerView.setLayoutManager(layoutManager);
        }
        readyMatchAdapter = new ReadyMatchAdapter(ReadyMatchListActivity.this, matchList, getApplication(), this);
        matchRecyclerView.setAdapter(readyMatchAdapter);
    }

    void sortMatchList(){

        Collections.sort(matchList);

        connectAdapter();
    }

    @Override
    public void changeSize(int size) {
        matchCntTextView.setText(size + "경기");
    }
}
