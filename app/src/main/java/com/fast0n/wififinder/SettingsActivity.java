package com.fast0n.wififinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    Switch toggle;
    String toggleState;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // set title activity in the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // set row icon in the toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // java addresses
        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        toggle = findViewById(R.id.toggle);
        toggleState = settings.getString("toggleState", null);
        mAdView = findViewById(R.id.adView);


        // banner
        MobileAds.initialize(this, "ca-app-pub-9646303341923759~2289006026");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        try {
            if (toggleState.equals("0"))
                toggle.setChecked(true);
            else
                toggle.setChecked(false);
        } catch (Exception ignored) {
            editor.putString("toggleState", "1");
            editor.apply();
            toggle.setChecked(true);
        }

        toggle.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                editor.putString("toggleState", "0");
                editor.apply();
                String toasty = getString(R.string.toast_notification, String.valueOf(getString(R.string.enabled)));
                Toasty.info(SettingsActivity.this, toasty, Toast.LENGTH_SHORT, true).show();

            } else {
                editor.putString("toggleState", "1");
                editor.apply();
                String toasty = getString(R.string.toast_notification, String.valueOf(getString(R.string.disabled)));
                Toasty.info(SettingsActivity.this, toasty, Toast.LENGTH_SHORT, true).show();
            }

        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));

    }
}