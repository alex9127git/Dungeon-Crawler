package ru.alex9127.app.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ru.alex9127.app.R;
import ru.alex9127.app.database.TerrainDataBase;
import ru.alex9127.app.drawing.DrawSurface;
import ru.alex9127.app.saving.Save;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    FrameLayout frameLayout;
    DrawSurface drawSurface;
    public static TerrainDataBase databaseConnector;
    public final Gson gson = new Gson();

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
        setContentView(R.layout.activity_game);
        frameLayout = findViewById(R.id.frameLayout);
        drawSurface = new DrawSurface(this, name, save);
        frameLayout.addView(drawSurface);
        drawSurface.setOnTouchListener(this);

        databaseConnector = new TerrainDataBase(this);
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
    }

    class GetData extends AsyncTask<Void, Integer, ArrayList<TerrainDataBase.Item>> {
        protected ArrayList<TerrainDataBase.Item> doInBackground(Void... args) {
            return databaseConnector.selectAll();
        }

        @Override
        protected void onPostExecute(ArrayList<TerrainDataBase.Item> items) {
            StringBuilder s = new StringBuilder();
            for (TerrainDataBase.Item item:items) {
                s.append(item.toString());
            }
            /*
            Intent i = new Intent(MainActivity.this, LeaderboardsActivity.class);
            i.putExtra("Text", s.toString());
            startActivityForResult(i, 1);
            */
        }
    }

    class SaveData extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... args) {
            //databaseConnector.insert();
            return null;
        }
    }
}

