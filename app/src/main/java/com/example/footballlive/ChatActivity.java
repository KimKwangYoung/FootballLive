package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.footballlive.adapter.ChatAdapter;
import com.example.footballlive.data.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends BaseActivity {
    String teamKey;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    ChatAdapter chatAdapter;

    DatabaseReference mDatabase;
    ImageView back;
    RecyclerView chatRecyclerView;
    EditText editText;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        teamKey = user.getTeamid();

        back = findViewById(R.id.chat_back_btn);
        send = findViewById(R.id.chat_sent_btn);
        editText = findViewById(R.id.chat_et);
        chatRecyclerView = findViewById(R.id.chat_rv);

        setRv();
        setData();

        setClickEventListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setClickEventListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();

                chat.setWriterId(user.getUid());
                chat.setName(user.getName());
                chat.setContent(editText.getText().toString());

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

                chat.setDatetime(sdf.format(date));

                String writerId;
                String name;
                String content;
                String datetime;

                updateChat(chat);
            }
        });
    }

    private void updateChat(Chat chat) {
        String key = mDatabase.child("chats").child(teamKey).push().getKey();
        chat.setKey(key);
        mDatabase.child("chats").child(teamKey).child(key).setValue(chat);

    }

    private void setRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(this, chatArrayList, user);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    private void setData() {
        mDatabase.child("chats").child(teamKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatArrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    chatArrayList.add(ds.getValue(Chat.class));
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
