package com.example.footballlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.footballlive.BaseActivity;
import com.example.footballlive.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/* 변수명 수정 완료 */
public class BacknumberDialog extends Dialog implements View.OnClickListener {
    Context context;
    public BackNumberDialogListner backNumberDialogListner;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    EditText backnumberEditText;
    Button backnumberButton;
    String userUid, backnumber;

    public BacknumberDialog(Context context, String uid){
        super(context);
        this.context = context;
        this.userUid = uid;
    }

    public interface BackNumberDialogListner{
        void onButtonClicked(String backnumber);
    }

    public void setDialogListner(BackNumberDialogListner backnumberDialogListner){
        this.backNumberDialogListner = backnumberDialogListner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_backnumber_dialog);

        backnumberEditText = findViewById(R.id.input_backnumber_et);
        backnumberButton = findViewById(R.id.input_backnumber_btn);
        backnumberButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.input_backnumber_btn:
                Toast.makeText(context, "변경중...", Toast.LENGTH_SHORT).show();
                backnumber = backnumberEditText.getText().toString();
                checkInteger(backnumber);
        }
    }

    public void checkInteger(String backnumber){
        try {
            Integer.parseInt(backnumber);
            inputBacknumber(backnumber);
        }catch (NumberFormatException e){
            Toast.makeText(context, "숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void inputBacknumber(String number){
        mRef.child("users").child(userUid).child("backnumber").setValue(number).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                modifyTeamMemberData();
            }
        });
    }

    private void modifyTeamMemberData(){
        mRef.child("team").child(BaseActivity.team.getTeam_key()).child("team_member").child(userUid).child("backnumber").setValue(backnumber).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "등번호가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                backNumberDialogListner.onButtonClicked(backnumber);
                dismiss();
            }
        });
    }
}
