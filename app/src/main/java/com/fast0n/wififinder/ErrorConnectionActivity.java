package com.fast0n.wififinder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ErrorConnectionActivity extends AppCompatActivity {

    TextView title, desc;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_connection);
        Button button = findViewById(R.id.button);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);

        button.setOnClickListener(v -> {
            if (isOnline()) {
                Intent mStartActivity = new Intent(ErrorConnectionActivity.this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(ErrorConnectionActivity.this,
                        mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) ErrorConnectionActivity.this
                        .getSystemService(Context.ALARM_SERVICE);
                assert mgr != null;
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            } else
                Toasty.info(ErrorConnectionActivity.this, desc.getText(), Toast.LENGTH_SHORT, true)
                        .show();


        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}