package ru.alex9127.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.*;

import ru.alex9127.app.R;
import ru.alex9127.app.database.HistoryDataBase;
import ru.alex9127.app.drawing.DrawSurface;
import ru.alex9127.app.saving.Save;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    FrameLayout frameLayout;
    DrawSurface drawSurface;
    public final Gson gson = new Gson();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        String name = getIntent().getStringExtra("Name");
        if (name.isEmpty()) name = "UNKNOWN";
        Save save = null;
        if (getIntent().hasExtra("Save")) {
            String s = getIntent().getStringExtra("Save");
            save = gson.fromJson(s, Save.class);
        }
        int time = getIntent().getIntExtra("Time", 0);
        setContentView(R.layout.activity_game);
        frameLayout = findViewById(R.id.frameLayout);
        drawSurface = new DrawSurface(this, name, save, time);
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

    @Override
    protected void onPause() {
        super.onPause();
        Save save = drawSurface.save();
        try (FileOutputStream fos = this.openFileOutput("saveData.json",
                Context.MODE_PRIVATE)) {
            fos.write(gson.toJson(save).getBytes());
        } catch (IOException e) {
            Toast.makeText(this, "Error occurred saving data to saveData.json",
                    Toast.LENGTH_SHORT).show();
        }
        new MainActivity.SaveData().execute();
        Log.v("LOG", String.valueOf(MainActivity.databaseConnector.sum()));
    }
}

