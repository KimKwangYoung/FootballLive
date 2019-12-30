package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import static com.example.footballlive.BaseActivity.user;

import com.example.footballlive.adapter.MyTeamAppListAdapter;
import com.example.footballlive.data.ShortPlay;
import com.example.footballlive.data.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class MyTeamAppListActivity extends AppCompatActivity {
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    LinkedHashMap<String, ShortPlay> myTeamAppList = new LinkedHashMap<>();
    ArrayList<ShortPlay> mData = new ArrayList<>();

    TextView listCntTextView;
    RecyclerView listRecyclerView;

    Team team;
    MyTeamAppListAdapter myTeamAppListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team_app_list);

        listCntTextView = findViewById(R.id.app_list_cnt_tv);
        listRecyclerView = findViewById(R.id.app_list_rv);

        getAppList();
    }

    private void getAppList() {
        mRef.child("team").child(user.getTeamid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
                try {
                    myTeamAppList = team.getMy_team_application_list();
                    Log.e("myTeamAppList", myTeamAppList + " ");
                    setAdapterData();
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapterData() {
        for(ShortPlay sp : myTeamAppList.values()){
            mData.add(sp);
        }

        updateUI();
    }

    private void updateUI() {

        try {
            listCntTextView.setText(mData.size() + "경기");
        }catch (NullPointerException e){
            listCntTextView.setText(0 + "경기");
        }

        Log.e("mData", mData + "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        listRecyclerView.setLayoutManager(layoutManager);
        myTeamAppListAdapter = new MyTeamAppListAdapter(mData, MyTeamAppListActivity.this);
        listRecyclerView.setAdapter(myTeamAppListAdapter);
    }


}
