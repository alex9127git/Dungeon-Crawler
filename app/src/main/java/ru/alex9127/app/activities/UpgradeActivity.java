package ru.alex9127.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.alex9127.app.R;

public class UpgradeActivity extends AppCompatActivity {
    private TextView coinsTv, atkEffTv, atkCostTv, defEffTv, defCostTv, hpEffTv, hpCostTv, manaEffTv, manaCostTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        coinsTv = findViewById(R.id.coinsTv);
        atkEffTv = findViewById(R.id.atkEffect);
        atkCostTv = findViewById(R.id.atkCost);
        defEffTv = findViewById(R.id.defEffect);
        defCostTv = findViewById(R.id.defCost);
        hpEffTv = findViewById(R.id.hpEffect);
        hpCostTv = findViewById(R.id.hpCost);
        manaEffTv = findViewById(R.id.manaEffect);
        manaCostTv = findViewById(R.id.manaCost);
        updateUI();
    }

    public void updateUI() {
        SharedPreferences p = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        String text = getString(R.string.coins, p.getInt("Coins", 0));
        coinsTv.setText(text);
        text = getString(R.string.percentageEffect, p.getInt("Attack power level", 0), "ATK");
        atkEffTv.setText(text);
        text = getString(R.string.cost, p.getInt("Attack power cost", 1));
        atkCostTv.setText(text);
        text = getString(R.string.percentageEffect, p.getInt("Defense power level", 0), "DEF");
        defEffTv.setText(text);
        text = getString(R.string.cost, p.getInt("Defense power cost", 1));
        defCostTv.setText(text);
        text = getString(R.string.flatEffect, p.getInt("Start HP level", 0) * 10, "HP");
        hpEffTv.setText(text);
        if (p.getInt("Start HP level", 0) < 10) {
            text = getString(R.string.cost, p.getInt("Start HP cost", 2));
        } else {
            text = "MAX.";
        }
        hpCostTv.setText(text);
        text = getString(R.string.flatEffect, p.getInt("Start mana level", 0), "MANA");
        manaEffTv.setText(text);
        if (p.getInt("Start mana level", 0) < 5) {
            text = getString(R.string.cost, p.getInt("Start mana cost", 5));
        } else {
            text = "MAX.";
        }
        manaCostTv.setText(text);
    }

    public void goBack(View v) {
        finish();
    }

    public void upgrade(String upgrade) {
        SharedPreferences p = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        int lvl = p.getInt(upgrade + " level", 0) + 1;
        int coins = p.getInt("Coins", 0);
        int defValue = 0;
        switch (upgrade) {
            case "Attack power":
            case "Defense power":
                defValue = 1; break;
            case "Start HP": defValue = 2; break;
            case "Start mana": defValue = 5; break;
        }
        if (coins >= p.getInt(upgrade + " cost", defValue) &&
                ((upgrade.equals("Start HP") && lvl <= 10) ||
                        ((upgrade.equals("Start mana") && lvl <= 5))
                        || (upgrade.equals("Attack power") || upgrade.equals("Defense power")))) {
            e.putInt(upgrade + " level", lvl);
            e.putInt("Coins", p.getInt("Coins", 0) -
                    p.getInt(upgrade + " cost", 0));
            int cost = p.getInt(upgrade + " level", 0) + 1;
            switch (upgrade) {
                case "Attack power":
                case "Defense power":
                    cost++;
                    break;
                case "Start HP": cost = (int) Math.pow(2, cost + 1); break;
                case "Start mana": cost = (int) Math.pow(5, cost + 1); break;
            }
            e.putInt(upgrade + " cost", cost);
            e.apply();
        }
        updateUI();
    }

    public void attackUpgrade(View v) {
        upgrade("Attack power");
    }

    public void defenseUpgrade(View v) {
        upgrade("Defense power");
    }

    public void hpUpgrade(View v) {
        upgrade("Start HP");
    }

    public void manaUpgrade(View v) {
        upgrade("Start mana");
    }
}