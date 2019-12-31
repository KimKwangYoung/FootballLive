package com.example.footballlive;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoadingActivity extends BaseActivity {
    final static private String TAG = "LoadingActivity";
    DatabaseReference mDatabase;
    FirebaseUser fUser;
    TextView loadingTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadingTextView = findViewById(R.id.loading_tv);

        startThread();

    }

    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFCMServerKey();
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        myToken = instanceIdResult.getToken();
                        Log.e(TAG, myToken + " is token");
                        getUserData();
                    }
                });
            }
        }).start();
    }

    private void getFCMServerKey(){
        mDatabase.child("ServerKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    if(d.getKey().equals("fcmKey")){
                        fcmSeverKey = d.getValue(String.class);
                    }else if(d.getKey().equals("naverApiSecretKey")){
                        naverApiSecretKey = d.getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData() {
        if(fUser == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    try {
                        if (!myToken.equals(user.getFcmToken())) {
                            updateToken();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(user.getTeamid() != null){
                        getTeamData();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateToken() {
        mDatabase.child("users").child(fUser.getUid()).child("fcmToken").setValue(myToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Token update successfully!");
            }
        });
    }

    private void getTeamData() {
            mDatabase.child("team").child(user.getTeamid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    team = dataSnapshot.getValue(Team.class);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


}
