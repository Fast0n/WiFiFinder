package com.fast0n.wififinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Tovuti;
import com.fast0n.wififinder.java.GPSTracker;
import com.fast0n.wififinder.java.RecyclerItemListener;
import com.fast0n.wififinder.main.Main;
import com.fast0n.wififinder.main.MainAdapter;
import com.fast0n.wififinder.wifi_list.WifiList;
import com.fast0n.wififinder.wifi_list.WifiListAdapter;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    StringBuilder sb = new StringBuilder();
    WifiManager mainWifi;
    MainActivity.WifiReceiver receiverWifi;
    RecyclerView recycler_view, recycler_view2;
    TextView getNome;
    Button addWifi, add;
    EditText editText, editText2;
    List<WifiList> wifiListList = new ArrayList<>();
    List<Main> mainList = new ArrayList<>();
    List<android.net.wifi.ScanResult> wifiList;
    FirebaseDatabase database;
    DatabaseReference databaseRef;
    AdView mAdView;
    LinearLayoutManager llm, llm2;
    ProgressBar progressBar, progressBar2;
    String tv_password, tv_location, nome, position, toggleState;
    FloatingActionButton fab;
    boolean[] counter = {true};
    RadioGroup rg;
    Vibrator v;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private ClipboardManager myClipboard;
    private ClipData myClip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.app_name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /**
         *  Icone della NavigationView colorate
         */
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        // java addresses
        fab = findViewById(R.id.fab);
        mAdView = findViewById(R.id.adView);
        progressBar = findViewById(R.id.spin_kit);
        recycler_view = findViewById(R.id.recycler_view);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        settings = getSharedPreferences("sharedPreferences", 0);
        toggleState = settings.getString("toggleState", null);
        editor = settings.edit();

        //Firebase
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        // banner
        MobileAds.initialize(this, "ca-app-pub-9646303341923759~2289006026");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FoldingCube FoldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(FoldingCube);
        FoldingCube.setColor(getResources().getColor(android.R.color.white));

        recycler_view.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(llm);

        try {
            if (toggleState.equals("0")) {
                FirebaseMessaging.getInstance().subscribeToTopic("news");

            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");

            }
        } catch (Exception ignored) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            editor.putString("toggleState", "0");
            editor.apply();

        }

        recycler_view.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), recycler_view,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View arg1, int position) {
                        if (!isOnline()) {
                            TextView tv_password = arg1.findViewById(R.id.tv_password);
                            String stringPassword = tv_password.getText().toString();

                            if (!stringPassword.equals(getString(R.string.radioButton2).toUpperCase()) && !stringPassword.equals(getString(R.string.radioButton3).toUpperCase())) {

                                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                myClip = ClipData.newPlainText("text", stringPassword);
                                myClipboard.setPrimaryClip(myClip);
                                vibrator();
                                Toasty.warning(MainActivity.this, getString(R.string.toast6), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toasty.info(MainActivity.this, getString(R.string.errorconnection_one), Toast.LENGTH_SHORT, true)
                                    .show();

                        }


                    }

                    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
                    public void onLongClickItem(View arg1, int position) {
                        vibrator();
                        if (!isOnline()) {
                            TextView tv_ssid = arg1.findViewById(R.id.tv_ssid);
                            TextView tv_location = arg1.findViewById(R.id.tv_location);
                            TextView tv_password = arg1.findViewById(R.id.tv_password);

                            String stringSsid = tv_ssid.getText().toString();
                            String stringLocation = tv_location.getText().toString();
                            String stringPassword = tv_password.getText().toString();

                            Dialog dialog3 = new Dialog(MainActivity.this);
                            dialog3.setCancelable(true);
                            dialog3.setContentView(R.layout.map_dialog);
                            Window window = dialog3.getWindow();
                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            window.setGravity(Gravity.CENTER);
                            window.setBackgroundDrawableResource(R.drawable.border_dialog);


                            //java addresses
                            WebView browser = dialog3.findViewById(R.id.webview);
                            ImageButton imageButton = dialog3.findViewById(R.id.imageButton);
                            ImageButton imageButton2 = dialog3.findViewById(R.id.imageButton2);
                            ImageButton imageButton3 = dialog3.findViewById(R.id.imageButton3);
                            ImageButton imageButton4 = dialog3.findViewById(R.id.imageButton4);

                            if (stringPassword.equals(getString(R.string.radioButton2).toUpperCase()) || stringPassword.equals(getString(R.string.radioButton3).toUpperCase()))
                                imageButton3.setVisibility(View.GONE);


                            browser.getSettings().setJavaScriptEnabled(true);
                            browser.setWebViewClient(new WebViewClient());
                            String url = "https://maps.google.com/maps?q=" + stringLocation.replaceAll("\\s+", "%20");
                            browser.loadUrl(url);


                            browser.setOnTouchListener((v, event) -> true);

                            String shareText = "SSID: " + stringSsid + "\nPassword: " + stringPassword + "\n" + getString(R.string.share_text);

                            imageButton.setOnClickListener(view -> {

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));

                            });


                            imageButton2.setOnClickListener(view -> {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                dialog3.dismiss();

                            });

                            imageButton3.setOnClickListener(view -> {
                                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                myClip = ClipData.newPlainText("text", stringPassword);
                                myClipboard.setPrimaryClip(myClip);
                                vibrator();
                                Toasty.warning(MainActivity.this, getString(R.string.toast6), Toast.LENGTH_SHORT).show();

                            });

                            imageButton4.setOnClickListener(view -> {
                                vibrator();


                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setType("text/html");
                                intent.setData(Uri.parse("mailto:Theplayergame97@gmail.com"));
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Wi-Fi Finder Report");

                                @SuppressLint("StringFormatMatches") String text = getString(R.string.email_message, stringLocation.split(",")[3], String.valueOf(stringSsid), String.valueOf(stringPassword));

                                intent.putExtra(Intent.EXTRA_TEXT, text);

                                startActivity(Intent.createChooser(intent, "Send Email"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialog3.dismiss();


                            });

                            dialog3.show();


                        } else
                            Toasty.info(MainActivity.this, getString(R.string.errorconnection_one), Toast.LENGTH_SHORT, true).show();


                    }

                }


        ));


        Tovuti.from(this).monitor((connectionType, isConnected, isFast) -> {

            if (isFast) {
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (counter[0]) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                int size = 0;
                                for (DataSnapshot ignored : dataSnapshot.child(key).getChildren()) {
                                    size++;
                                }

                                for (int i = size - 1; i > -1; i--) {
                                    String name, location, pwdtype, datetime, nameLocation = null;

                                    name = String.valueOf(dataSnapshot.child(key).child(String.valueOf(i)).child("name").getValue());
                                    location = String.valueOf(dataSnapshot.child(key).child(String.valueOf(i)).child("location").getValue());
                                    pwdtype = String.valueOf(dataSnapshot.child(key).child(String.valueOf(i)).child("pwdtype").getValue());
                                    datetime = String.valueOf(dataSnapshot.child(key).child(String.valueOf(i)).child("datetime").getValue());

                                    try {
                                        Double MyLat = Double.parseDouble(location.split(",")[0]);
                                        Double MyLong = Double.parseDouble(location.split(",")[1]);

                                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                        List<Address> addresses;
                                        addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

                                        nameLocation = addresses.get(0).getAddressLine(0);
                                    } catch (NumberFormatException | IOException ignored) {
                                    }

                                    mainList.add(new Main(size - i, name, location, pwdtype, datetime, nameLocation, size));
                                }


                            }


                            MainAdapter ca = new MainAdapter(mainList, MainActivity.this);
                            recycler_view.setAdapter(ca);
                            counter[0] = false;
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                fab.hide();
                String timeStamp = "01-01-2000";
                mainList.add(new Main(0, getString(R.string.errorconnection), "", "", timeStamp, getString(R.string.errorconnection_one), 0));
                MainAdapter ca = new MainAdapter(mainList, MainActivity.this);
                recycler_view.setAdapter(ca);
                counter[0] = false;
                progressBar.setVisibility(View.GONE);

            }
        });

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        // check permission
        TedPermission.with(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


        fab.setOnClickListener(view -> {
            counter[0] = true;
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_dialog);
            Window window = dialog.getWindow();
            assert window != null;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setBackgroundDrawableResource(R.drawable.border_dialog);

            //java addresses
            add = dialog.findViewById(R.id.add);
            addWifi = dialog.findViewById(R.id.addWifi);
            editText = dialog.findViewById(R.id.editText);
            editText2 = dialog.findViewById(R.id.editText2);

            CheckBox checkBox = dialog.findViewById(R.id.checkBox);
            CheckBox checkBox2 = dialog.findViewById(R.id.checkBox2);
            CheckBox checkBox3 = dialog.findViewById(R.id.checkBox3);


            checkBox.setOnClickListener(view1 -> {
                editText2.setEnabled(true);
                editText2.setText("");
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
            });


            checkBox2.setOnClickListener(view1 -> {
                editText2.setEnabled(false);
                editText2.setText(getString(R.string.radioButton2));
                checkBox.setChecked(false);
                checkBox3.setChecked(false);
            });

            checkBox3.setOnClickListener(view1 -> {
                editText2.setEnabled(false);
                editText2.setText(getString(R.string.radioButton3));
                checkBox.setChecked(false);
                checkBox2.setChecked(false);
            });


            show_gps();


            addWifi.setOnClickListener((View v) -> {
                mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert mainWifi != null;
                if (mainWifi.isWifiEnabled()) {
                    Dialog dialog2 = new Dialog(MainActivity.this);
                    dialog2.setCancelable(true);
                    dialog2.setContentView(R.layout.activity_wifi_list);
                    Window window2 = dialog.getWindow();
                    assert window2 != null;
                    window2.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window2.setGravity(Gravity.CENTER);
                    dialog2.show();

                    //java addresses
                    recycler_view2 = dialog2.findViewById(R.id.recycler_view2);
                    progressBar2 = dialog2.findViewById(R.id.spin_kit);

                    recycler_view2.setHasFixedSize(true);
                    llm2 = new LinearLayoutManager(this);
                    llm2.setOrientation(LinearLayoutManager.VERTICAL);
                    recycler_view2.setLayoutManager(llm2);

                    FoldingCube FoldingCube2 = new FoldingCube();
                    progressBar2.setIndeterminateDrawable(FoldingCube2);
                    FoldingCube2.setColor(getResources().getColor(R.color.colorBackgroundCardView));

                    recycler_view2.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), recycler_view2,
                            new RecyclerItemListener.RecyclerTouchListener() {
                                public void onClickItem(View arg1, int position) {
                                    getNome = arg1.findViewById(R.id.tv_ssid);
                                    nome = getNome.getText().toString();
                                    editText.setText(nome.replace("SSID: ", ""));
                                    dialog2.dismiss();
                                }

                                public void onLongClickItem(View v, int position) {
                                }
                            }));


                    receiverWifi = new WifiReceiver();
                    registerReceiver(receiverWifi, new IntentFilter(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    mainWifi.startScan();
                } else
                    Toasty.warning(MainActivity.this, getString(R.string.toast5), Toast.LENGTH_SHORT).show();


            });

            add.setOnClickListener(v -> {
                //add.startLoading();
                if (editText.getText().toString().length() < 1) {
                    //add.loadingFailed();
                    Toasty.warning(MainActivity.this, getString(R.string.toast), Toast.LENGTH_SHORT).show();
                } else if (editText2.getText().toString().length() < 8) {
                    //add.loadingFailed();
                    Toasty.warning(MainActivity.this, getString(R.string.toast2), Toast.LENGTH_SHORT).show();
                } else if (isOnline()) {
                    //add.loadingFailed();
                    Toasty.warning(MainActivity.this, getString(R.string.toast4), Toast.LENGTH_SHORT).show();
                } else {
                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            try {
                                Double MyLat = Double.parseDouble(position.split(",")[0]);
                                Double MyLong = Double.parseDouble(position.split(",")[1]);

                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
                                List<Address> addresses;
                                addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
                                String cityName = addresses.get(0).getAddressLine(0);

                                String db = cityName.split(",")[3].replaceAll("\\s+", "");


                                int size = 0;
                                for (DataSnapshot ignored : dataSnapshot.child(db).getChildren()) {
                                    size++;
                                }

                                if (counter[0]) {
                                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
                                    databaseRef.child(db).child(String.valueOf(size)).child("name").setValue(editText.getText().toString());
                                    databaseRef.child(db).child(String.valueOf(size)).child("location").setValue(position);

                                    int radioButtonID = rg.getCheckedRadioButtonId();
                                    View radioButton = rg.findViewById(radioButtonID);
                                    int idx = rg.indexOfChild(radioButton);

                                    switch (idx) {
                                        case 0:
                                            databaseRef.child(db).child(String.valueOf(size)).child("pwdtype").setValue(editText2.getText().toString());
                                            break;
                                        case 1:
                                            databaseRef.child(db).child(String.valueOf(size)).child("pwdtype").setValue("WI-FI APERTO");
                                            break;
                                        case 2:
                                            databaseRef.child(db).child(String.valueOf(size)).child("pwdtype").setValue("NON FUNZIONA");
                                            break;
                                    }


                                    databaseRef.child(db).child(String.valueOf(size)).child("datetime").setValue(timeStamp);

                                    //add.loadingSuccessful();

                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        dialog.dismiss();
                                        counter[0] = false;
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }, 2000);
                                }
                            } catch (NumberFormatException | IOException ignored) {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            });

            dialog.show();
        });


    }

    public void vibrator() {

        // Vibrate for 50 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // deprecated in API 26
            v.vibrate(50);
        }
    }

    public void show_gps() {
        GPSTracker gps;
        Geocoder geocoder;
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            gps = new GPSTracker(MainActivity.this);

            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                String get_latitude = String.valueOf(latitude);
                String get_longitude = String.valueOf(longitude);
                String stringlat = get_latitude.substring(0, 8);
                String stringlong = get_longitude.substring(0, 8);
                position = stringlat + ", " + stringlong;

                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        String location = addresses.get(0).getLocality();
                        Log.e("Errr", location);
                    }

                } catch (IOException ignored) {
                }

            } else {

                gps.showSettingsAlert();
            }

        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) == null
                || !cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_info)
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        else if (id == R.id.action_settings)
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            wifiListList.clear();
            for (int i = 0; i < wifiList.size(); i++) {
                wifiListList.add(new WifiList((wifiList.get(i)).toString().split(",")[0]));
            }
            WifiListAdapter ca = new WifiListAdapter(wifiListList);
            recycler_view2.setAdapter(ca);
            progressBar2.setVisibility(View.GONE);
        }
    }
}
