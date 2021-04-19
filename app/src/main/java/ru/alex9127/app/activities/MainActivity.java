package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ru.alex9127.app.R;
import ru.alex9127.app.database.TerrainDataBase;
import ru.alex9127.app.saving.Save;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startNew(View v) {
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        startActivityForResult(i, 0);
    }

    public void quit(View v) {
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        Gson gson = new Gson();
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("Name", ((EditText) findViewById(R.id.enterName)).getText().toString());
        i.putExtra("Save", contents);
        startActivityForResult(i, 0);
    }
}