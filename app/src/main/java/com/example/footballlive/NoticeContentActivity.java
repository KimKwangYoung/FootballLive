package com.example.footballlive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/* 변수명 수정 완료 */
public class NoticeContentActivity extends AppCompatActivity {
    TextView titleTextView, contentTextView, creationDateTextView;
    String title, content, creationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        titleTextView = findViewById(R.id.notice_content_title_tv);
        contentTextView = findViewById(R.id.notice_content_content_tv);
        creationDateTextView = findViewById(R.id.notice_content_creation_date_tv);
        Intent intent = getIntent();

        title = intent.getStringExtra("titleTextView");
        content = intent.getStringExtra("content");
        creationTime = intent.getStringExtra("creationDate");

        titleTextView.setText(title);
        creationDateTextView.setText(creationTime);
        contentTextView.setText(content);
    }
}
