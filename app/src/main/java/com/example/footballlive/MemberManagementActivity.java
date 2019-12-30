package com.example.footballlive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.footballlive.adapter.MemberListAdapter;
import com.example.footballlive.data.Team;

/* 변수명 수정 완료 */
public class MemberManagementActivity extends AppCompatActivity {
    RecyclerView memberRecyclerView;
    TextView memberCntTextView;
    Team team = BaseActivity.team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_management);

        memberRecyclerView = findViewById(R.id.member_rv);
        memberCntTextView = findViewById(R.id.member_cnt_tv);

        updateUI();

    }

    void connectAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        memberRecyclerView.setLayoutManager(layoutManager);
        MemberListAdapter memberListAdapter = new MemberListAdapter(team.getTeam_member(), MemberManagementActivity.this, getApplication());
        memberRecyclerView.setAdapter(memberListAdapter);
    }

    void updateUI(){
        memberCntTextView.setText(team.getTeam_member().size() + "명");
        connectAdapter();
    }
}


