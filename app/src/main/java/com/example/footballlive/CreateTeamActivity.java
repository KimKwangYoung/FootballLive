package com.example.footballlive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.data.Team;
import com.example.footballlive.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class CreateTeamActivity extends BaseActivity {

    private final int ADDRESS_REQUEST_CODE = 1001;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    TextView cancelButton, createButton, stadiumAddressTextView1;
    EditText teamNameEditText, stadiumAddressEditText, phoneNumberEditText;
    RadioGroup stadiumRadioGroup;
    LinearLayout stadiumLinearLayout;
    RadioButton radioButton;

    String address1, address2, latitude, longitude;
    String teamName;
    String phoneNumber;
    Boolean hasStadium = null;
    Boolean isDuplicationTeamName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        cancelButton = findViewById(R.id.create_team_cancel_btn);
        createButton = findViewById(R.id.create_team_create_btn);
        teamNameEditText = findViewById(R.id.create_team_teamName_et);
        stadiumAddressTextView1 = findViewById(R.id.create_team_searchAddress_btn);
        stadiumAddressEditText = findViewById(R.id.create_team_detailedAddress_et);
        stadiumRadioGroup = findViewById(R.id.create_team_stadium_rg);
        stadiumLinearLayout = findViewById(R.id.create_team_stadium_ll);
        radioButton = findViewById(R.id.create_team_stadium_no_possesion_rb);
        phoneNumberEditText = findViewById(R.id.create_team_team_phone_number_et);

        stadiumLinearLayout.setVisibility(View.INVISIBLE/* 최초 구장 입력 부분 가리기 */);


        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());/* '-' 자동 추가 */

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("취소하시겠습니까?", 2);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTeam();
            }
        });

        stadiumAddressTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTeamActivity.this, AddressActivity.class);
                startActivityForResult(intent, ADDRESS_REQUEST_CODE);
            }
        });
        stadiumRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.create_team_stadium_no_possesion_rb:
                        hasStadium = false;
                        stadiumLinearLayout.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.create_team_stadium_possesion_rb:
                        hasStadium = true;
                        stadiumLinearLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADDRESS_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                Log.e("실행 확인", "RESULT_OK");
                address1 = data.getStringExtra("jibun_address");
                stadiumAddressTextView1.setText(address1);
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
            }
        }
    }

    /* 팀 정보 유효성 검사 */
    private void setTeam() {
        teamName = teamNameEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();

        if(teamName.length() <= 2){
            showDialog("팀 이름은 두글자 이상이어야 합니다.", 0);
            return;
        }

        if(phoneNumber.length() != 13){
            showDialog("팀 대표번호는 11자 이어야 합니다.", 0);
            return;
        }

        if(hasStadium == null){
            showDialog("구장 보유 여부를 체크해야 합니다.", 0);
            return;
        }

        /* 팀 이름 중복여부 체크 */
        mRef.child("team").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isDuplicationTeamName = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getValue(Team.class).getTeam_name().trim().equals(teamName)){
                        showDialog("이미 등록된 이름입니다.", 0);
                        isDuplicationTeamName = true;
                        break;
                    }
                }
                if(!isDuplicationTeamName) {
                    showDialog("작성하신 내용대로 팀을 생성하시겠습니까?", 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /* Team Data 객체 생성 후 데이터베이스 저장 */
    private void putDatabase() {

        final Team myTeam = new Team();
        String key = mRef.child("team").push().getKey();

        myTeam.setTeam_key(key);
        myTeam.setTeam_name(teamNameEditText.getText().toString());
        myTeam.setTeam_leader(user.getUid());

        address2 = stadiumAddressEditText.getText().toString();

        if(hasStadium) {
            if(address1.equals("")){
                showDialog("구장 주소를 입력하셔야 합니다.", 0);
                return;
            }
            myTeam.setTeam_stadium_latitude(latitude);
            myTeam.setTeam_stadium_longitude(longitude);

            if (address2 != null) {
                myTeam.setTeam_stadium_address(address1 + " " + address2);
            } else {
                myTeam.setTeam_stadium_address(address1);
            }
        }

        LinkedHashMap<String, User> myTeamMember = myTeam.getTeam_member();
        myTeamMember.put(user.getUid(), user);
        myTeam.setTeam_member(myTeamMember);

        mRef.child("team").child(key).setValue(myTeam);
        User currentUser = new User();
        currentUser.setTeamid(key);
        if(team == null) {
            mRef.child("users").child(user.getUid()).child("teamid").setValue(currentUser.getTeamid())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateTeamActivity.this, "팀 생성이 완료 되었습니다!", Toast.LENGTH_SHORT).show();
                            team = myTeam;
                            user.setTeamid(myTeam.getTeam_key());
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                        }
                    });
        }



    }

    public void showDialog(String message, final int checkDialog){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(this);
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (checkDialog){
                    case 1:
                        putDatabase();
                        break;
                    case 2:
                        finish();
                        break;
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
