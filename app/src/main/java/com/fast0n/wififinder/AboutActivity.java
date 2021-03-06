package com.fast0n.wififinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // set title activity in the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Element paypalElement = new Element();
        paypalElement.setTitle(getString(R.string.donate));
        paypalElement.setIconDrawable(R.drawable.ic_donate);
        paypalElement.setIconTint(R.color.colorPrimary);
        paypalElement.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/Fast0n/1.0"))));

        Element accountElement = new Element();
        accountElement.setTitle(getString(R.string.author) + "\nMassimiliano Montaleone (Fast0n)");
        accountElement.setIconDrawable(R.drawable.ic_user);
        accountElement.setIconTint(R.color.colorPrimary);
        accountElement.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/fast0n"))));

        Element soucecodeElement = new Element();
        soucecodeElement.setTitle(getString(R.string.source_code));
        soucecodeElement.setIconDrawable(R.drawable.ic_github);
        soucecodeElement.setIconTint(R.color.colorPrimary);
        soucecodeElement.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fast0n/WiFiFinder"))));

        Element instagramElement = new Element();
        instagramElement.setTitle(getString(R.string.instagram));
        instagramElement.setIconDrawable(R.drawable.ic_instagram);
        instagramElement.setIconTint(R.color.colorPrimary);
        instagramElement.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/fast0n"))));

        Element playstoreElement = new Element();
        playstoreElement.setTitle(getString(R.string.play_store));
        playstoreElement.setIconDrawable(R.drawable.about_icon_google_play);
        playstoreElement.setIconTint(R.color.colorPrimary);
        playstoreElement.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.fast0n.wififinder"))));

        Element translateElement = new Element();
        translateElement.setTitle(getString(R.string.translate));
        translateElement.setIconDrawable(R.drawable.ic_translate);
        translateElement.setIconTint(R.color.colorPrimary);
        translateElement.setOnClickListener(view -> {
        });


        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.about))
                .addItem(new Element().setIconDrawable(R.drawable.ic_info).setTitle(getString(R.string.version) + " " + BuildConfig.VERSION_NAME + " ("
                        + BuildConfig.VERSION_CODE + ") (" + BuildConfig.APPLICATION_ID + ")").setIconTint(R.color.colorPrimary))
                .addItem(instagramElement)
                .addItem(soucecodeElement)
                .addItem(translateElement)
                .addItem(paypalElement)
                .addItem(accountElement)
                .addItem(playstoreElement)
                .create();

        setContentView(aboutPage);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(AboutActivity.this, MainActivity.class));
    }
}