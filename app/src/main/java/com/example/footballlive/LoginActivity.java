package com.example.footballlive;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.example.footballlive.dialog.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {

    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    TextView loginButton, passwordResetButton, joinButton;
    EditText emailEditText, passwordEditText;

    String loginEmail, loginPassword;

    LoadingDialog loadingDialog;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email_et);
        passwordEditText = findViewById(R.id.login_password_et);
        passwordResetButton = findViewById(R.id.login_passwordReset_btn);
        loginButton = findViewById(R.id.login_login_btn);
        joinButton = findViewById(R.id.login_join_btn);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startThread();
                loadingDialog = new LoadingDialog();
                loadingDialog.progressON(LoginActivity.this);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginEmail = emailEditText.getText().toString().trim();
                loginPassword = passwordEditText.getText().toString();

                signInEmailandPassword(loginEmail, loginPassword);
            }
        }).start();
    }
    private void signInEmailandPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("LoginSuccess", "로그인 성공");
//                    mUser = FirebaseAuth.getInstance().getCurrentUser();
//                    getUserData(mUser.getUid());
                    getUserData(mAuth.getUid());
                }else{

                    Toast.makeText(getApplicationContext(), "로그인 실패!\n이메일과 비밀번호를 다시해 한번 확인해 주세요", Toast.LENGTH_SHORT).show();
                    Log.d("LoginError", "로그인 실패");
                    finishDialog();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            finish();
        }
        else {
            Log.d(TAG, "유저 정보 존재하지 않음.");
        }
    }

    public void getUserData(String uid){
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(user.getTeamid() != null){
                    getTeamData();
                }else{
                    finishDialog();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTeamData() {
        mRef.child("team").child(user.getTeamid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
                Log.e("LoginActivity", team.getTeam_key() + " ");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finishDialog();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void finishDialog(){
        loadingDialog.progressOFF();
    }
}
