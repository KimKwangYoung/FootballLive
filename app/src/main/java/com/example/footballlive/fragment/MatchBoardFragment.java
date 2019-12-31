package com.example.footballlive.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footballlive.CreateMatchActivity;
import com.example.footballlive.R;
import com.example.footballlive.adapter.MatchBoardAdapter;
import com.example.footballlive.data.MatchPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.footballlive.BaseActivity.team;
import static com.example.footballlive.BaseActivity.user;

import java.util.Calendar;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class MatchBoardFragment extends Fragment {
    RecyclerView matchRecyclerView;
    ImageView matchCreateButton;
    Spinner regionSpinner;
    LinearLayout matchBoardLinearLayout;
    TextView dateButton, infoTextView;
    ImageView infoImageView;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    String teamID, region, matchDay;
    LinkedHashMap<String, MatchPost> matchList = new LinkedHashMap<>();
    MatchBoardAdapter matchAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_match_board, container, false);

        matchRecyclerView = rootView.findViewById(R.id.match_board_rv);
        matchCreateButton = rootView.findViewById(R.id.match_board_create_btn);
        regionSpinner = rootView.findViewById(R.id.match_board_region_sp);
        matchBoardLinearLayout = rootView.findViewById(R.id.match_board_ll);
        dateButton = rootView.findViewById(R.id.match_board_date_btn);
        infoTextView = rootView.findViewById(R.id.match_board_info_tv);
        infoImageView = rootView.findViewById(R.id.match_board_info_iv);

        //recyclerView 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        matchRecyclerView.setLayoutManager(layoutManager);

        matchRecyclerView.setVisibility(View.INVISIBLE);

        //유저가 로그인 상태인지 확인하는 부분
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        if(cUser != null){
            getTeamID();
        }


        matchCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(team == null){
                    showDialog("팀 정보가 존재하지 않습니다.", 0);
                    return;
                }

                String stadium = team.getTeam_stadium_address();
                if(stadium == null){
                    showDialog("보유한 구장이 없습니다.", 0);
                    return;
                }

                Intent intent = new Intent(getActivity(), CreateMatchActivity.class);
                startActivity(intent);
            }
        });

        // 지역 값 받아오기
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    if(matchDay != null){
                        region = regionCheck(position);
                        getMatchList();
                    }else{
                        region = regionCheck(position);
                    }
                }else if(position == 0){
                    matchBoardLinearLayout.setVisibility(View.VISIBLE);
                    matchRecyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //날짜 설정
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(region != null) {
                    setMatchDay();
                }else{
                    Toast.makeText(getActivity(), "지역을 먼저 설정해주세요!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(region != null){
            if(matchDay != null) {
                String[] dayArry = matchDay.split("-");
                dateButton.setText(dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일");
                showPlayList();
            }
        }
    }

    public void showDialog(String message, final int checkDialog){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(getContext());
        joinDialog_builder.setMessage(message).setTitle("알림").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                joinDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
                joinDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.mainColor));
            }
        });
        joinDialog.show();
    } // 알림 다이얼로그 창 띄워주기

    public void getMatchList(){
        matchList.clear();
        mRef.child("matchPost").child(region).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (matchDay.equals(ds.getValue(MatchPost.class).getMatchday())) {
                        matchList.put(ds.getKey(), ds.getValue(MatchPost.class));
                    }
                }
                if (!matchList.isEmpty()) {
                    showPlayList();
                    Log.e("MatchBoardFragment", matchList + " ");
                } else {
                    matchBoardLinearLayout.setVisibility(View.VISIBLE);
                    matchRecyclerView.setVisibility(View.INVISIBLE);
                    infoImageView.setImageResource(R.drawable.ic_report_problem_grey_600_36dp);
                    infoTextView.setText("검색하신 정보로 경기가 존재하지 않습니다.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    } // matchPost list 받아오기

    public void showPlayList(){
//        Collections.sort(matchList, playComparator); // ArrayList로 받아올때 정렬해주기

        matchAdapter = new MatchBoardAdapter(matchList, getActivity());
        matchRecyclerView.setAdapter(matchAdapter);
        matchBoardLinearLayout.setVisibility(View.INVISIBLE);
        matchRecyclerView.setVisibility(View.VISIBLE);
    } // 불러온 matchPost list 리싸이클러뷰에 보여주기

    public void getTeamID(){
        try {
            teamID = user.getTeamid();
        }catch (Exception e){
            e.printStackTrace();
        }

    } //로그인된 유저 teamid 받아오기

    public String regionCheck(int position){
        String regionChecked = null;

        switch (position){
            case 1:
                regionChecked = "seoul_gangbuk";
                break;
            case 2:
                regionChecked = "seoul_gangnam";
                break;
            case 3:
                regionChecked = "incheon";
                break;
            case 4:
                regionChecked = "ulsan";
                break;
            case 5:
                regionChecked = "daegu";
                break;
            case 6:
                regionChecked = "busan";
                break;
            case 7:
                regionChecked = "daejeon";
                break;
            case 8:
                regionChecked = "gwangju";
                break;
            case 9:
                regionChecked = "other";
                break;
        }

        return regionChecked;
    }

    public void setMatchDay(){
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(dayOfMonth < 10) {
                    matchDay = year + "-" + (month + 1) + "-0" + dayOfMonth;
                }else{
                    matchDay = year + "-" + (month + 1) + "-" + dayOfMonth;
                }
                String[] dayArry = matchDay.split("-");
                dateButton.setText(dayArry[0] + "년 " + dayArry[1] + "월 " + dayArry[2] + "일");
                getMatchList();
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setMessage("날짜");
        datePickerDialog.show();
    }


}
