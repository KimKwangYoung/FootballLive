package com.example.footballlive.adapter;

import android.app.AlertDialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballlive.R;
import com.example.footballlive.data.MatchResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/* 변수명 수정 완료 */
public class MatchResultAdapter extends RecyclerView.Adapter<MatchResultAdapter.Viewholder> {

    final private String TAG = "MatchResultAdapter";

    Context context;
    LinkedHashMap<String, MatchResult> mData;
    ArrayList<MatchResult> results = new ArrayList<>();
    String teamKey;
    boolean isTeamMember;


    public MatchResultAdapter(Context context, LinkedHashMap<String, MatchResult> mData, boolean isTeamMember, String teamKey) {
        this.context = context;
        this.mData = mData;
        this.isTeamMember = isTeamMember;
        this.teamKey = teamKey;

        for(MatchResult r : mData.values()){
            results.add(r);
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.match_result_item, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        MatchResult m = results.get(position);
        holder.setItem(m);

        Log.e(TAG, "onBindViewHolder - myTeamScore : " + m.getMyTeam_score() + " / oppTeamScore : " + m.getOppTeam_score());
    }



    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        TextView myTeamNameTextView, oppTeamNameTextView, myTeamScoreTextView, oppTeamScoreTextView, inputResultButton, resultHideTextView
                , matchDayTextView;
        LinearLayout scoreLinearLayout;
        AlertDialog dlg;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            myTeamNameTextView = itemView.findViewById(R.id.match_result_item_my_team_name_tv);
            oppTeamNameTextView = itemView.findViewById(R.id.match_result_item_opp_team_name_tv);
            myTeamScoreTextView = itemView.findViewById(R.id.match_result_item_my_team_score_tv);
            oppTeamScoreTextView = itemView.findViewById(R.id.match_result_item_opp_team_score_tv);
            inputResultButton = itemView.findViewById(R.id.match_result_item_input_result_btn);
            scoreLinearLayout = itemView.findViewById(R.id.match_result_item_score_ll);
            resultHideTextView = itemView.findViewById(R.id.match_result_item_hide_ll);
            matchDayTextView = itemView.findViewById(R.id.match_result_item_match_day_tv);

            inputResultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /* Dialog 설정 */
                    int position = getAdapterPosition();
                    Log.e(TAG, "position : " + position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.match_result_dialog,null, false);
                    builder.setView(view);

                    final EditText myTeam_score_et = view.findViewById(R.id.myTeam_score_et);
                    final EditText oppTeam_score_et = view.findViewById(R.id.oppTeam_score_et);
                    Button match_result_input_btn = view.findViewById(R.id.matchresult_input_btn);
                    Button match_result_cancel_btn = view.findViewById(R.id.matchresult_cancel_btn);
                    TextView myTeam_name_tv = view.findViewById(R.id.myTeam_name_tv);
                    TextView oppTeam_name_tv = view.findViewById(R.id.oppTeam_name_tv);
                    final MatchResult matchResult = results.get(position);
                    myTeam_name_tv.setText(matchResult.getMyTeam());
                    oppTeam_name_tv.setText(matchResult.getOppTeam());

                    dlg = builder.create();

                    /* Dialog 확인 버튼 클릭리스너 구현 */
                    match_result_input_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String my_team_score = myTeam_score_et.getText().toString();
                            String opp_team_score = oppTeam_score_et.getText().toString();

                            matchResult.setMyTeam_score(my_team_score);
                            matchResult.setOppTeam_score(opp_team_score);


                            results.set(getAdapterPosition(), matchResult);
                            for(MatchResult m : results){
                                Log.e(TAG, "my_team_score" + m.getMyTeam_score() + "opp_team_score" + m.getOppTeam_score());

                            }

                            Toast.makeText(context, "저장중...", Toast.LENGTH_SHORT).show();
                            updateMatchResult(matchResult);

                        }
                    });

                    dlg.show();
                }

            });

        }


        public void setItem(MatchResult m) {

            myTeamNameTextView.setText(m.getMyTeam());
            oppTeamNameTextView.setText(m.getOppTeam());
            matchDayTextView.setText(m.getMatchDay());
            Log.e(TAG, "setItem - myTeamScore : " + m.getMyTeam_score() + " / oppTeamScore : " + m.getOppTeam_score());
            if(m.getMyTeam_score() != null && m.getOppTeam_score() != null){
                myTeamScoreTextView.setText(m.getMyTeam_score());
                oppTeamScoreTextView.setText(m.getOppTeam_score());
                inputResultButton.setVisibility(View.INVISIBLE);
                scoreLinearLayout.setVisibility(View.VISIBLE);
                resultHideTextView.setVisibility(View.INVISIBLE);
            }else{
                if (isTeamMember) {
                    inputResultButton.setVisibility(View.VISIBLE);
                    scoreLinearLayout.setVisibility(View.INVISIBLE);
                    resultHideTextView.setVisibility(View.VISIBLE);
                }else{
                    inputResultButton.setVisibility(View.INVISIBLE);
                    scoreLinearLayout.setVisibility(View.INVISIBLE);
                    resultHideTextView.setVisibility(View.VISIBLE);
                }
            }
        }

        private void updateMatchResult(MatchResult m){
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            Log.e(TAG, "matchKey : " + m.getMatchKey() + " teamKey : " + teamKey);
            mRef.child("team").child(teamKey).child("my_team_match_result").child(m.getMatchKey()).setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "저장 성공!", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(getAdapterPosition());
                    dlg.dismiss();
                }
            });
        }
    }
}

