package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;



/* 변수명 수정 완료 */
public class PasswordResetActivity extends AppCompatActivity {
    String userEmail;
    EditText emailTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        emailTextView = findViewById(R.id.password_reset_email_et);

        Button sendButton = findViewById(R.id.password_reset_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailTextView.getText().toString();
                if(!userEmail.equals("")){
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "이메일 전송 완료", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    showDialog("이메일을 입력해주세요");
                }
            }
        });

    }

    public void showDialog(String message){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(this);
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog joinDialog = joinDialog_builder.create();
        joinDialog.show();
    }



}
