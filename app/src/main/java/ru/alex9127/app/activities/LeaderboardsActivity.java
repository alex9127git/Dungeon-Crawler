package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ru.alex9127.app.R;

public class LeaderboardsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        ((TextView) findViewById(R.id.text)).setText(getIntent().getStringExtra("Text"));
    }
}