package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import static com.example.footballlive.BaseActivity.team;

import com.example.footballlive.adapter.ReadyMemberAdapter;
import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/* 변수명 수정 완료 */
public class ReadyMemberActivity extends AppCompatActivity {

    RecyclerView readyMemberRecyclerView;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    LinkedHashMap<User, String> readyMemberData = new LinkedHashMap<>();
    ArrayList<User> keyList = new ArrayList<>();
    ArrayList<String> greetingsList = new ArrayList<>();

    Team mTeam;
    ReadyMemberAdapter rmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_member);

        readyMemberRecyclerView = findViewById(R.id.ready_member_rv);



        getTeamData();
    }

    public void getUserData(){


        LinkedHashMap<String, String> mData = mTeam.getReady_member();
        Log.e("mDataKey", mData + "");
        final ArrayList<String> userKeyList = new ArrayList<>(); // userUid 리스트로 만들어주기

        for(Map.Entry<String, String> entry : mData.entrySet()){
            userKeyList.add(entry.getKey());
        }

        Log.e("userKeyList", userKeyList + "");


        // 인사말 리스트로 만들어주기
        for(String key : userKeyList){
            greetingsList.add(mData.get(key));
        }



        mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String userKey : userKeyList) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        Log.e("userKeyList2", userKeyList.size() + " " + userKeyList.get(i));
                        if (userKey.equals(ds.getValue(User.class).getUid())) {
                            keyList.add(ds.getValue(User.class));
                            Log.e("keyList", keyList + "");
                        }
                    }
                    connectAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void connectAdapter(){
        for(int i = 0; i < keyList.size(); i++){
            readyMemberData.put(keyList.get(i), greetingsList.get(i));
        }

        Log.e("LogMember", readyMemberData + " ");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        readyMemberRecyclerView.setLayoutManager(layoutManager);
        rmAdapter = new ReadyMemberAdapter(readyMemberData, ReadyMemberActivity.this, getApplication());
        readyMemberRecyclerView.setAdapter(rmAdapter);
    }
    private void getTeamData(){
        mRef.child("team").child(team.getTeam_key()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTeam = dataSnapshot.getValue(Team.class);
                getUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        rmAdapter = new ReadyMemberAdapter(readyMemberData, ReadyMemberActivity.this, getApplication());
        readyMemberRecyclerView.setAdapter(rmAdapter);
    }


}
