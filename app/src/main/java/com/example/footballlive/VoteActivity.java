package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.adapter.VoteAdapter;
import com.example.footballlive.data.ReadyMatch;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class VoteActivity extends AppCompatActivity implements View.OnClickListener {

    TextView attendCntTextView, notAttendCntTextView, undefinedCntTextView;
    Button attentButton, notAttendButton, cancelVoteButton;
    LinearLayout voteLinearLayout;
    RecyclerView attendRecyclerView, notAttendRecyclerView, undefinedRecyclerView;

    LinkedHashMap<String, User> attendHashMap = new LinkedHashMap<>();
    LinkedHashMap<String, User> notAttendHashMap = new LinkedHashMap<>();
    LinkedHashMap<String, User> undefinedHashMap = new LinkedHashMap<>();

    ReadyMatch match;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    String team_key;
    String match_key;
    String user_key;

    VoteAdapter voteAdapter;

    Context context = VoteActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Intent intent = getIntent();
        match_key = intent.getStringExtra("matchKey");

        attendCntTextView = findViewById(R.id.vote_attend_cnt_tv);
        notAttendCntTextView = findViewById(R.id.vote_not_attend_cnt_tv);
        undefinedCntTextView = findViewById(R.id.vote_undefined_cnt_tv);

        attentButton = findViewById(R.id.vote_attend_btn);
        notAttendButton = findViewById(R.id.vote_not_attend_btn);
        cancelVoteButton = findViewById(R.id.vote_cancel_btn);


        voteLinearLayout = findViewById(R.id.vote_ll);
        attendRecyclerView = findViewById(R.id.vote_attend_rv);
        notAttendRecyclerView = findViewById(R.id.vote_not_attend_rv);
        undefinedRecyclerView = findViewById(R.id.vote_undefined_rv);

        attentButton.setOnClickListener(this);
        notAttendButton.setOnClickListener(this);
        cancelVoteButton.setOnClickListener(this);

        getVoteData();
    }

    void getVoteData(){
        user_key = BaseActivity.user.getUid();
        team_key = BaseActivity.user.getTeamid();
        mRef.child("team").child(team_key).child("ready_play").child(match_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                match = dataSnapshot.getValue(ReadyMatch.class);
                attendHashMap = match.getCham();
                notAttendHashMap = match.getBulcham();
                undefinedHashMap = match.getAllMember();
                updateUI();
                Log.e("hashMapCheck", attendHashMap + " \n" + notAttendHashMap + "\n" + undefinedHashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void updateUI(){
        if(attendHashMap.containsKey(user_key)){
            voteLinearLayout.setVisibility(View.INVISIBLE);
            cancelVoteButton.setVisibility(View.VISIBLE);

        }
        if(notAttendHashMap.containsKey(user_key)){
            voteLinearLayout.setVisibility(View.INVISIBLE);
            cancelVoteButton.setVisibility(View.VISIBLE);
        }
        if(undefinedHashMap.containsKey(user_key)){
            voteLinearLayout.setVisibility(View.VISIBLE);
            cancelVoteButton.setVisibility(View.INVISIBLE);
        }

        attendCntTextView.setText(attendHashMap.size() + "명");
        notAttendCntTextView.setText(notAttendHashMap.size() + "명");
        undefinedCntTextView.setText(undefinedHashMap.size()+ "명");

        LinearLayoutManager chamLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        attendRecyclerView.setLayoutManager(chamLayoutManager);
        LinearLayoutManager bulchamLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        notAttendRecyclerView.setLayoutManager(bulchamLayoutManager);
        LinearLayoutManager mijungLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        undefinedRecyclerView.setLayoutManager(mijungLayoutManager);

        setVoteAdapter();
    }

    void setVoteAdapter(){
        voteAdapter = new VoteAdapter(attendHashMap, context);
        attendRecyclerView.setAdapter(voteAdapter);

        voteAdapter = new VoteAdapter(notAttendHashMap, context);
        notAttendRecyclerView.setAdapter(voteAdapter);

        voteAdapter = new VoteAdapter(undefinedHashMap, context);
        undefinedRecyclerView.setAdapter(voteAdapter);
    }

    void setVoteResult(){
        mRef.child("team").child(team_key).child("ready_play").child(match_key).setValue(match).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "저장 성공!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == attentButton){
            User user = undefinedHashMap.get(user_key);
            attendHashMap.put(user_key, user);
            undefinedHashMap.remove(user_key);
        }
        if(v == notAttendButton){
            User user = undefinedHashMap.get(user_key);
            notAttendHashMap.put(user_key, user);
            undefinedHashMap.remove(user_key);
        }
        if(v == cancelVoteButton && attendHashMap.containsKey(user_key)){
            User user = attendHashMap.get(user_key);
            undefinedHashMap.put(user_key, user);
            attendHashMap.remove(user_key);
        }
        if(v == cancelVoteButton && notAttendHashMap.containsKey(user_key)){
            User user = notAttendHashMap.get(user_key);
            undefinedHashMap.put(user_key, user);
            notAttendHashMap.remove(user_key);
        }

        match.setAllMember(undefinedHashMap);
        match.setCham(attendHashMap);
        match.setBulcham(notAttendHashMap);

        updateUI();
        setVoteResult();
    }
}
