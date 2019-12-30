package com.example.footballlive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static com.example.footballlive.BaseActivity.user;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/* 변수명 수정 완료 */
public class ProfileupdateActivity extends AppCompatActivity {
    private static final String TAG = "ProfileupdateActivity";

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    String name, position;
    EditText nameEditText;
    RadioGroup positionRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileupdate);
        positionRadioGroup = findViewById(R.id.profile_position_rd);
        nameEditText = findViewById(R.id.profile_name_et);


        nameEditText.setText(user.getName());
        position = user.getPosition();
        switch (position){
            case "FW":
                positionRadioGroup.check(R.id.profile_FW_rb);
                break;
            case "MF":
                positionRadioGroup.check(R.id.profile_MF_rb);
                break;
            case "DF":
                positionRadioGroup.check(R.id.profile_DF_rb);
                break;
            case "GK":
                positionRadioGroup.check(R.id.profile_GK_rb);
                break;
        }


        Button updateButton = findViewById(R.id.profile_update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserData();
            }
        });

    }

    public void changeUserData(){
        name = nameEditText.getText().toString();
        RadioButton rb = findViewById(positionRadioGroup.getCheckedRadioButtonId());
        position = rb.getText().toString();
        user.setPosition(position);
        user.setName(name);
        mRef.child("users").child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(user.getTeamid() != null){
                    //팀안의 있는 유저의 정보를 변경하기 위해 팀 데이터를 얻어옴
                    changeUserDataOfTeam();
                }else {
                    Toast.makeText(getApplicationContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    public void changeUserDataOfTeam(){
        mRef.child("team").child(user.getTeamid()).child("team_member").child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
