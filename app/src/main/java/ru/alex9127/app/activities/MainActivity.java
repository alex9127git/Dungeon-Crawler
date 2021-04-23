package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.io.*;

import ru.alex9127.app.R;
import ru.alex9127.app.database.HistoryDataBase;

public class MainActivity extends AppCompatActivity {
    public int getDataResult;
    public static long startTime;
    public static HistoryDataBase databaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseConnector = new HistoryDataBase(this);
    }

    public void startNew(View v) {
        new GetData().execute();
    }

    public void quit(View v) {
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.hasExtra("Result")) {
            switch (data.getStringExtra("Result")) {
                case "Won":
                    Toast.makeText(this, "You completed the game!",
                            Toast.LENGTH_LONG).show();
                    break;
                case "Lost":
                    Toast.makeText(this, "Game over",
                            Toast.LENGTH_LONG).show();
            }
        } else new SaveData().execute();
    }

    public void continueExisting(View view) {
        new GetDataWithSave().execute();
    }

    class GetData extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... args) {
            return databaseConnector.sum();
        }

        @Override
        protected void onPostExecute(Integer i) {
            getDataResult = i;
            startTime = System.currentTimeMillis();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("Time", getDataResult);
            intent.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
            startActivityForResult(intent, 0);
        }
    }

    class GetDataWithSave extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... args) {
            return databaseConnector.sum();
        }

        @Override
        protected void onPostExecute(Integer i) {
            String contents = "";
            try {
                FileInputStream fis = MainActivity.this.openFileInput("saveData.json");
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line = reader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append('\n');
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error occurred reading data from saveData.json",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    contents = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "File saveData.json not found",
                        Toast.LENGTH_SHORT).show();
            }
            getDataResult = i;
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
            intent.putExtra("Save", contents);
            intent.putExtra("Time", getDataResult);
            startTime = System.currentTimeMillis();
            startActivityForResult(intent, 0);
        }
    }

    public static class SaveData extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... args) {
            databaseConnector.insert((int) (System.currentTimeMillis() - startTime));
            return null;
        }
    }
}