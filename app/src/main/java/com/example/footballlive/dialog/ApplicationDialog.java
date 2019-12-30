package com.example.footballlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.footballlive.R;
import com.example.footballlive.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationDialog {
    private Context context;

    String greetings;
    User ready_user;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    TextView grettingsTextView;

    Dialog dlg;

    public ApplicationDialog(Context context, String greetings) {
        this.context = context;
        this.greetings = greetings;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public boolean callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.ready_member_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        grettingsTextView = dlg.findViewById(R.id.recruiting_application_dialog_tv);
        grettingsTextView.setText(greetings);

        Button okButton = dlg.findViewById(R.id.recruiting_application_dialog_btn);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        return true;

    }





}
