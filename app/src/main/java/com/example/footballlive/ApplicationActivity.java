package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.data.MatchPost;
import com.example.footballlive.dialog.CustomDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.util.LinkedHashMap;

import static com.example.footballlive.BaseActivity.team;
import static com.example.footballlive.BaseActivity.user;

public class ApplicationActivity extends AppCompatActivity {
    TextView titleTextView, stadiumTextView, dayAndTimeTextView, contentTextView;
    DatabaseReference mRef;
    MatchPost matchPost;
    LinkedHashMap<String, String> applicationTeam = new LinkedHashMap<>();
    String playKey, region;
    Boolean applicationCheck = false;
    Button button;
    NaverMap mMap;
    LatLng myLatLng;
    LinearLayout map_hide_ll, map_show_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        titleTextView = findViewById(R.id.app_title);
        stadiumTextView = findViewById(R.id.playapp_staduim);
        dayAndTimeTextView = findViewById(R.id.playapp_DayTime);
        contentTextView = findViewById(R.id.playapp_Content);
        map_hide_ll = findViewById(R.id.map_hide_ll);
        map_show_ll = findViewById(R.id.map_show_ll);

        Intent intent = getIntent();
        playKey = intent.getStringExtra("playKey");
        mRef = FirebaseDatabase.getInstance().getReference();


        getPlayData();


        button = findViewById(R.id.playapp_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getTeamid() == null){
                    Toast.makeText(getApplicationContext(), "소속된 팀이 없어 신청이 불가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(applicationCheck == true){
                    showCancelDialog("취소 하시겠습니까?");
                }else{
                    matchApplication();
                }
            }
        });
    }

    /* DB에서 key를 이용해 match data 가져오기 */
    private void getPlayData(){
        mRef.child("matchPost").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot rds : dataSnapshot.getChildren()) {
                    for (DataSnapshot pds : rds.getChildren()) {
                        if (pds.getKey().equals(playKey)) {
                            matchPost = pds.getValue(MatchPost.class);
                            if(matchPost.getStadium_latitude() !=null && matchPost.getStadium_longitude() != null){
                                String latitude = matchPost.getStadium_latitude();
                                String longitude = matchPost.getStadium_longitude();
                                myLatLng = new LatLng(
                                        Double.parseDouble(latitude),
                                        Double.parseDouble(longitude)
                                );
                                map_hide_ll.setVisibility(View.INVISIBLE);
                                setMap();
                            }else{
                                map_show_ll.setVisibility(View.INVISIBLE);
                                updateUI();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(){
        titleTextView.setText(matchPost.getTitle());
        stadiumTextView.setText(matchPost.getStadium());
        dayAndTimeTextView.setText(transformPlayData());
        contentTextView.setText(matchPost.getContent());
        checkApplication();

    } // 불러온 Data로 UI구성

    private void setMap(){
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_stadium);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_stadium, mapFragment).commit();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                mMap = naverMap;

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(myLatLng).animate(CameraAnimation.None);
                mMap.moveCamera(cameraUpdate);
                Marker marker = new Marker();
                marker.setPosition(myLatLng);
                marker.setMap(mMap);
                checkApplication();
                updateUI();
            }
        });
    }

    public String transformPlayData(){
        String day, time, sum;
        String[] dayArry = matchPost.getMatchday().split("-");
        String[] stratTimeArry = matchPost.getStart_date_time().split("-");
        String[] endTimeArry = matchPost.getEnd_date_time().split("-");
        day = dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일";
        time = stratTimeArry[0] + "시" + stratTimeArry[1] + "분 ~ " + endTimeArry[0] + "시" + endTimeArry[1] + "분";

        sum = day + " " + time;
        return sum;
    } /* 매치 날짜, 시간 년월일/시분 형식으로 변형 */

    private void matchApplication(){
        CustomDialog customDialog = new CustomDialog(ApplicationActivity.this, region, matchPost, "match", getApplication());
        customDialog.callFunction();
    }

    private void checkApplication(){
        if(team.getMy_team_application_list().containsKey(playKey)){
            button.setText("신청 취소");
            applicationCheck = true;
        }
    }

    private void showCancelDialog(String message){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(ApplicationActivity.this);
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelMatch();
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
                joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ApplicationActivity.this, R.color.mainColor));
                joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ApplicationActivity.this, R.color.mainColor));
            }
        });
        joinDialog.show();
    }

    /* 홈팀 신청내역 지워주기 */
    private void cancelMatch(){
        mRef.child("team").child(matchPost.getHome_team()).child("post").child(matchPost.getPlay_key()).child("applicationTeam").child(team.getTeam_key())
                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                removeMyTeamApplication();
            }
        });
    }

    /* 우리팀 신청내역 지워주기 */
    private void removeMyTeamApplication(){
        mRef.child("team").child(team.getTeam_key()).child("my_team_application_list").child(matchPost.getPlay_key()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ApplicationActivity.this, "성공적으로 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                applicationCheck = false;
                button.setText("신청");
            }
        });
    }

}
