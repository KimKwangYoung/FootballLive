package com.example.footballlive.fragment;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.footballlive.CreateRecruitingActivity;
import com.example.footballlive.R;
import com.example.footballlive.adapter.RecruitingAdapter;
import com.example.footballlive.data.Recruiting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.footballlive.BaseActivity.user;

import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class RecruitingFragment extends Fragment {

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    Spinner recruitingSpinner;
    ImageView createButton;
    RecyclerView recruitingRecyclerView;
    LinearLayout recruitingLinearLayout;

    String region;

    LinkedHashMap<String, Recruiting> recruitingLinkedHashMap = new LinkedHashMap<>();
    RecruitingAdapter recruitingAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_recruiting, container, false);

        recruitingSpinner = rootView.findViewById(R.id.recruiting_region_sp);
        recruitingRecyclerView = rootView.findViewById(R.id.recruiting_rv);
        createButton = rootView.findViewById(R.id.recruiting_create_btn);
        recruitingLinearLayout = rootView.findViewById(R.id.recruiting_board_ll);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recruitingRecyclerView.setLayoutManager(layoutManager);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getTeamid() != null){
                Intent intent = new Intent(getActivity(), CreateRecruitingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                }else{
                    showDialog("소속된 팀이 없습니다.");
                }
            }
        });



        recruitingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    recruitingRecyclerView.setVisibility(View.VISIBLE);
                    recruitingLinearLayout.setVisibility(View.INVISIBLE);
                    region = regionCheck(position);
                    getRecruitingList();
                }else if(position == 0){
                    recruitingLinearLayout.setVisibility(View.VISIBLE);
                    recruitingRecyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    public void getRecruitingList(){
        recruitingLinkedHashMap.clear();
        mRef.child("recruiting").child(region).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    recruitingLinkedHashMap.put(ds.getKey(), ds.getValue(Recruiting.class));
                    Log.e("LinkedHashMapData", recruitingLinkedHashMap.toString());
                }
                Log.e("LinkedHashMapCheck", recruitingLinkedHashMap + " ");
                connectAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void connectAdapter(){
        Log.e("connectAdapter", "실행확인");
        recruitingAdapter = new RecruitingAdapter(recruitingLinkedHashMap, getActivity(), region);
        recruitingRecyclerView.setAdapter(recruitingAdapter);
    }

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
    public void showDialog(String message){
        AlertDialog.Builder joinDialog_builder;
        joinDialog_builder = new AlertDialog.Builder(getActivity());
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
    }
}
