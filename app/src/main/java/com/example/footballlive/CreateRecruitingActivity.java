package com.example.footballlive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.footballlive.BaseActivity.team;

import com.example.footballlive.data.Recruiting;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/* 변수명 수정 완료 */
public class CreateRecruitingActivity extends AppCompatActivity {
    RadioGroup recruitingRadioGroup;
    EditText contentEditText, titleEditText, duesEditText;
    TextView stadiumTextView, createButton, cancelButton;
    Spinner regionSpinner;
    String region, uniform;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recruiting);

        recruitingRadioGroup = findViewById(R.id.create_recruiting_uniform_rd);
        titleEditText = findViewById(R.id.create_recruiting_title_et);
        contentEditText = findViewById(R.id.create_recruiting_content_et);
        stadiumTextView = findViewById(R.id.create_recruiting_stadium_tv);
        duesEditText = findViewById(R.id.create_recruiting_dues_et);
        createButton = findViewById(R.id.create_recruiting_create_btn);
        cancelButton = findViewById(R.id.create_recruiting_cancel_btn);

        regionSpinner = findViewById(R.id.create_recruiting_region_sp);

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        region = "seoul_gangbuk";
                        break;
                    case 2:
                        region = "seoul_gangnam";
                        break;
                    case 3:
                        region = "incheon";
                        break;
                    case 4:
                        region = "ulsan";
                        break;
                    case 5:
                        region = "daegu";
                        break;
                    case 6:
                        region = "busan";
                        break;
                    case 7:
                        region = "daejeon";
                        break;
                    case 8:
                        region = "gwangju";
                        break;
                    case 9:
                        region = "other";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recruitingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.noneUniform:
                        uniform = "없음";
                        break;
                    case R.id.provideUniform:
                        uniform = "제공";
                        break;
                    case R.id.privateExpenseUniform:
                        uniform = "사비 구매";
                        break;
                }
            }
        });
        stadiumTextView.setText(team.getTeam_stadium_address());


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("글을 작성하시겠습니까?", 1);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("취소하시겠습니까?", 2);
            }
        });
    }

    void putData(){
        Recruiting recruiting = new Recruiting();
        String recruitingKey = mRef.child("recruiting").child(region).push().getKey();
        recruiting.setTitle(titleEditText.getText().toString());
        recruiting.setContent(contentEditText.getText().toString());
        recruiting.setStadium(team.getTeam_stadium_address());
        recruiting.setDues(duesEditText.getText().toString());
        recruiting.setTeamKey(team.getTeam_key());
        recruiting.setUniform(uniform);
        recruiting.setTeamName(team.getTeam_name());
        recruiting.setRecruiting_key(recruitingKey);

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String creationTime = simpleDateFormat.format(now);
        recruiting.setCreationTime(creationTime);

        mRef.child("recruiting").child(region).child(recruitingKey).setValue(recruiting).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "성공적으로 저장 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    public void showDialog(String message, final int checkDialog){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(this);
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (checkDialog){
                    case 1:
                        putData();
                        break;
                    case 2:
                        finish();
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog joinDialog = joinDialog_builder.create();
        joinDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mainColor));
                joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mainColor));
            }
        });
        joinDialog.show();
    }
}
