package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.footballlive.adapter.MyTeamPostListAdapter;
import com.example.footballlive.data.ShortPlay;
import com.example.footballlive.data.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.footballlive.BaseActivity.team;
import static com.example.footballlive.BaseActivity.user;

/* 변수명 수정 완료 */
public class MyTeamMatchListActivity extends AppCompatActivity{

    RecyclerView listRecyclerView;

    LinkedHashMap<String, ShortPlay> postMap = new LinkedHashMap<>();
    ArrayList<ShortPlay> postList = new ArrayList<>();
    MyTeamPostListAdapter myTeamPostListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_post_list);

        checkTeam();
        listRecyclerView = findViewById(R.id.post_list_rv);


    }

    public void connetAdapter() {
        Collections.sort(postList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyTeamMatchListActivity.this, RecyclerView.VERTICAL, false);
        listRecyclerView.setLayoutManager(layoutManager);
        myTeamPostListAdapter = new MyTeamPostListAdapter(MyTeamMatchListActivity.this, postList);
        listRecyclerView.setAdapter(myTeamPostListAdapter);
    }

    public void getPost(){
        for(Map.Entry<String, ShortPlay> entry : postMap.entrySet()){
            postList.add(entry.getValue());
        }
        connetAdapter();
    }

    public void checkTeam() {
        if(user.getTeamid() == null){
            return;
        }
        if(team == null){
            return;
        }
        if (!user.getTeamid().equals(team.getTeam_key())) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("team").child(team.getTeam_key()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                postMap = dataSnapshot.getValue(Team.class).getPost();
                getPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
