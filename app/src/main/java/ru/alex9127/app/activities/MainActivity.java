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
        startTime = System.currentTimeMillis();
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("Time", getDataResult);
        intent.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        startActivityForResult(intent, 0);
    }

    public void quit(View v) {
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new SaveData().execute();
        Log.v("LOG", String.valueOf(databaseConnector.sum()));
    }

    public void continueExisting(View view) {
        String contents = "";
        try {
            FileInputStream fis = this.openFileInput("saveData.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error occurred reading data from saveData.json",
                        Toast.LENGTH_SHORT).show();
            } finally {
                contents = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File saveData.json not found",
                    Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        i.putExtra("Save", contents);
        new GetData().execute();
        i.putExtra("Time", getDataResult);
        startTime = System.currentTimeMillis();
        startActivityForResult(i, 0);
    }

    class GetData extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... args) {
            return databaseConnector.sum();
        }

        @Override
        protected void onPostExecute(Integer i) {
            getDataResult = i;
        }
    }

    public static class SaveData extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... args) {
            databaseConnector.insert((int) (System.currentTimeMillis() - startTime));
            return null;
        }
    }
}