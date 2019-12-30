package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.footballlive.adapter.NoticeAdapter;
import com.example.footballlive.data.Notice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/* 변수명 수정 */
public class NoticeActivity extends AppCompatActivity {

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    ArrayList<Notice> notices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        getAppNoticeData();

    }

    private void connectAdapter(){
        RecyclerView noticeRecyclerView = findViewById(R.id.notice_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        noticeRecyclerView.setLayoutManager(layoutManager);
        NoticeAdapter adapter = new NoticeAdapter(notices, NoticeActivity.this);
        noticeRecyclerView.setAdapter(adapter);

    }

    private void getAppNoticeData(){
        mRef.child("app-noticeButton").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    notices.add(d.getValue(Notice.class));
                }
                connectAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
