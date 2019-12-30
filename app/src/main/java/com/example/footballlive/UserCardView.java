package com.example.footballlive;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/* 변수명 수정 완료 */
public class UserCardView extends LinearLayout {
    ImageView imageView;
    TextView nameTextView, teamTextView, positionTextView;
    public UserCardView(Context context) {
        super(context);
        init(context);
    }

    public UserCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.user_cardview, this, true);

        nameTextView = findViewById(R.id.profile_card_name_tv);
        teamTextView = findViewById(R.id.profile_card_team_tv);
        positionTextView = findViewById(R.id.profile_card_position_tv);
    }

    public void setName(String name){
        nameTextView.setText(name);
    }

    public void setTeamName(String teamName){
        teamTextView.setText(teamName);
    }

    public void setPosition(String position){
        positionTextView.setText(position);
    }
}
