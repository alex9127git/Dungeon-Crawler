package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;

import ru.alex9127.app.R;
import ru.alex9127.app.drawing.DrawSurface;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    DrawSurface drawSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        String name = getIntent().getStringExtra("Name");
        if (name.isEmpty()) name = "UNKNOWN";
        drawSurface = new DrawSurface(this, name);
        setContentView(drawSurface);
        drawSurface.setOnTouchListener(this);
        /*
        setContentView(R.layout.activity_game);
        drawSurface = findViewById(R.id.drawSurface);
        drawSurface.setOnTouchListener(this);
        */
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