package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

import ru.alex9127.app.R;
import ru.alex9127.app.database.TerrainDataBase;

public class MainActivity extends AppCompatActivity {
    public static TerrainDataBase databaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseConnector = new TerrainDataBase(this);
    }

    public void start(View v) {
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
                Intent i = new Intent(MainActivity.this, LeaderboardsActivity.class);
                i.putExtra("Text", s.toString());
                startActivityForResult(i, 1);
            }
        }
        //new GetData().execute();
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        startActivityForResult(i, 0);
    }

    public void quit(View v) {
        class SaveData extends AsyncTask<Void, Integer, Void> {
            protected Void doInBackground(Void... args) {
                //databaseConnector.insert();
                return null;
            }
        }
        //new SaveData().execute();
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}