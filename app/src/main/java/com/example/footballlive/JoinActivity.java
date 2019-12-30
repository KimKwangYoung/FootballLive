package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.footballlive.data.User;

import static com.example.footballlive.BaseActivity.myToken;
import static com.example.footballlive.BaseActivity.user;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 변수명 수정 완료 */
public class JoinActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, passwordCheckEditText, nameEditText;
    RadioGroup positionRadioGroup;

    private String email, password, name, position;

    FirebaseAuth mAuth;
    private static final String TAG = "JoinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);



        emailEditText = findViewById(R.id.join_email_et);
        passwordEditText = findViewById(R.id.join_password_et);
        passwordCheckEditText = findViewById(R.id.join_passwordCheck_et);
        nameEditText = findViewById(R.id.join_name_et);

        positionRadioGroup = findViewById(R.id.join_position_rg);
        mAuth = FirebaseAuth.getInstance();

        Button join = findViewById(R.id.join_button);
        positionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.FW_radio:
                        position = "FW";
                        break;
                    case R.id.MF_radio:
                        position = "MF";
                        break;
                    case R.id.DF_radio:
                        position = "DF";
                        break;
                    case R.id.GK_radio:
                        position = "GK";
                        break;
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                name = nameEditText.getText().toString();
                boolean password_match = password.equals(passwordCheckEditText.getText().toString());
                if (email != null && password != null && name != null && position != null) {
                    if(password_match) {
                        createAccount(email, password, name, position);
                    }else
                        showDialog("비밀번호 확인 불일치");
                }else{
                    showDialog("모든항목을 모두 입력해야 합니다.");
                }
            }
        });


    }

    /* 이메일 유효성 검사 */
    public boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    private void putDataToDatabase(String nameData, String positionData){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String uid = mAuth.getUid();

        final User cUser = new User();

        cUser.setUid(uid);
        cUser.setEmail(email);
        cUser.setName(nameData);
        cUser.setPosition(positionData);
        cUser.setFcmToken(myToken);


        mDatabase.child("users").child(uid).setValue(cUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user = cUser;
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "가입 실패 : " + e.toString());
            }
        });
    }

    private void createAccount(String email, String password, final String name, final String position) {

        boolean email_check;
        email_check = isValidEmail(email);

        if (email_check) {
            if (password.length() >= 6) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        putDataToDatabase(name, position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException){
                            showDialog("이메일이 이미 사용중입니다.");
                            return;
                        }
                    }
                });
                }else {showDialog("비밀번호는 6자 이상이여야 합니다.");}
        }else{
                showDialog("이메일 형식을 다시 확인해주세요");
            }
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

