package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;

import ru.alex9127.app.R;
import ru.alex9127.app.drawing.DrawSurface;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    FrameLayout frameLayout;
    DrawSurface drawSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        String name = getIntent().getStringExtra("Name");
        if (name.isEmpty()) name = "UNKNOWN";
        setContentView(R.layout.activity_game);
        frameLayout = findViewById(R.id.frameLayout);
        drawSurface = new DrawSurface(this, name);
        frameLayout.addView(drawSurface);
        drawSurface.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawSurface.handleClick(x, y);
        }
        return true;
    }
}