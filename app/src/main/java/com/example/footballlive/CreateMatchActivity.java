package com.example.footballlive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.footballlive.data.MatchPost;
import com.example.footballlive.data.ShortPlay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.footballlive.BaseActivity.team;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class CreateMatchActivity extends AppCompatActivity {
    String matchTitle, matchContent, stadium;
    String matchStartTime, matchEndTime, strMatchDay, region;
    String teamKey, createTime;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser mUser;
    TextView createButton, cancelButton, stadiumaddrTextView, startTimeButton, endTimeButton, matchDayButton;
    EditText titleEditText, contentEditText;
    String teamName;
    Spinner regionSpinner;
    int viewId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        getCurrentTime();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        regionSpinner = findViewById(R.id.create_match_region_sp);
        matchDayButton = findViewById(R.id.create_match_matchDay_btn);
        createButton = findViewById(R.id.create_match_create_btn);
        cancelButton = findViewById(R.id.create_match_cancel_btn);
        stadiumaddrTextView = findViewById(R.id.create_match_stadium_tv);
        startTimeButton = findViewById(R.id.create_match_startTime_btn);
        endTimeButton = findViewById(R.id.create_match_endTime_btn);
        titleEditText = findViewById(R.id.create_match_title_et);
        contentEditText = findViewById(R.id.create_match_content_et);


        teamKey = team.getTeam_key();
        stadium = team.getTeam_stadium_address();

        stadiumaddrTextView.setText(stadium);

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

        matchDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMatchDay();
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewId = 1;
                setMatchTime(1);
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewId = 2;
                setMatchTime(2);
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchTitle = titleEditText.getText().toString();
                matchContent = contentEditText.getText().toString();
                teamName = team.getTeam_name();
                stadium = team.getTeam_stadium_address();
                if(matchTitle != null && matchContent != null && region != null && strMatchDay != null && matchStartTime != null && matchEndTime != null) {
                    Log.e("shortPlay", team.getPost() + " ");
                    for(ShortPlay s : team.getPost().values()){
                        Log.e("matchDayCheck", strMatchDay + " ? " + s.getMatchDay());
                        if(s.getMatchDay().equals(strMatchDay)){
                            Log.e("MatchCheck", s.getStartTime() + " / " + s.getEndTime());
                            if(s.getStartTime().equals(matchStartTime) && s.getEndTime().equals(matchEndTime)){
                                showDialog("같은 날짜 같은 시간대 게시글이 이미 존재합니다.", 0);
                                return;
                            }
                        }
                    }
                    showDialog("글을 등록하시겠습니까?", 1);
                }else{
                    showDialog("구장을 제외한 모든 항목을 입력해 주세요.", 0);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("취소하시겠습니까?", 2);
            }
        });
    }

    private void setMatchDay(){
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(dayOfMonth < 10){
                    strMatchDay = year + "-" + (month+1) + "-0" + dayOfMonth;
                }else {
                    strMatchDay = year + "-" + (month + 1) + "-" + dayOfMonth;
                }

                matchDayButton.setText(strMatchDay);
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setMessage("날짜");
        datePickerDialog.show();
    }

    private void setMatchTime(final int viewID){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h, m;
                h = Integer.toString(hourOfDay);
                m = Integer.toString(minute);

                switch (viewID){
                    case 1:
                        matchStartTime = h + "-" + m;
                        startTimeButton.setText(h + ":" + m);
                        break;
                    case 2:
                        matchEndTime = h + "-" + m;
                        endTimeButton.setText(h + ":" + m);
                }

            }
        }, 0, 0, false);

        timePickerDialog.setMessage("시간");
        timePickerDialog.show();

    }

    private void putData(){
        final MatchPost myMatchPost = new MatchPost();
        final ShortPlay shortPlay = new ShortPlay();
        final String matchKey = mRef.child("matchPost").push().getKey();
        myMatchPost.setPlay_key(matchKey);
        myMatchPost.setHome_team_name(teamName);
        myMatchPost.setHome_team(teamKey);
        myMatchPost.setStart_date_time(matchStartTime);
        myMatchPost.setEnd_date_time(matchEndTime);
        myMatchPost.setStadium(stadium);
        myMatchPost.setMatchday(strMatchDay);
        myMatchPost.setTitle(matchTitle);
        myMatchPost.setContent(matchContent);
        myMatchPost.setCreationTime(createTime);
        myMatchPost.setUpdateCnt(0L);
        if(team.getTeam_stadium_latitude() != null && team.getTeam_stadium_longitude() != null){
            myMatchPost.setStadium_latitude(team.getTeam_stadium_latitude());
            myMatchPost.setStadium_longitude(team.getTeam_stadium_longitude());
        }

        shortPlay.setHome_team_key(teamKey);
        shortPlay.setHome_team_name(teamName);
        shortPlay.setStadium(stadium);
        shortPlay.setMatchDay(strMatchDay);
        shortPlay.setPost_title(matchTitle);
        shortPlay.setStartTime(matchStartTime);
        shortPlay.setEndTime(matchEndTime);
        shortPlay.setCreateTime(createTime);




//        Log.e("playkey", myMatchPost.getPlay_key());
        mRef.child("matchPost").child(region).child(matchKey).setValue(myMatchPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LinkedHashMap<String, ShortPlay> newPostList = team.getPost();
                shortPlay.setPostKey(matchKey);
                newPostList.put(matchKey, shortPlay);
                mRef.child("team").child(team.getTeam_key()).child("post").setValue(newPostList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "게시글 등록 완료!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });


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

    private void getCurrentTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        createTime = timeFormat.format(date);
        Log.e("CreateTimeCheck", createTime);
    }
}
