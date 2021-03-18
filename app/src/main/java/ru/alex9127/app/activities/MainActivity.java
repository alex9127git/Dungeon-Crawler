package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.alex9127.app.R;
import ru.alex9127.app.database.LeaderBoardDataBase;

public class MainActivity extends AppCompatActivity {
    public static LeaderBoardDataBase databaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseConnector = new LeaderBoardDataBase(this);
    }

    public void start(View v) {
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        startActivityForResult(i, 0);
    }

    public void leaderBoards(View v) {
        class GetData extends AsyncTask<Void, Integer, ArrayList<LeaderBoardDataBase.Item>> {
            protected ArrayList<LeaderBoardDataBase.Item> doInBackground(Void... args) {
                return databaseConnector.selectAll();
            }

            @Override
            protected void onPostExecute(ArrayList<LeaderBoardDataBase.Item> items) {
                StringBuilder s = new StringBuilder();
                for (LeaderBoardDataBase.Item item:items) {
                    s.append(item.toString());
                }
                Intent i = new Intent(MainActivity.this, LeaderboardsActivity.class);
                i.putExtra("Text", s.toString());
                startActivityForResult(i, 1);
            }
        }
        new GetData().execute();

    }

    public void quit(View v) {
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}