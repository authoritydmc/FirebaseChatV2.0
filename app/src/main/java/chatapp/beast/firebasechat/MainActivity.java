package chatapp.beast.firebasechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter tabPagerAdapter;
    private Toolbar toolbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private boolean internetConnected = true;
    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status, false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(CONSTANTS.DATABASE_USER_nodE).child(mAuth.getCurrentUser().getUid());

        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.main_tabs_pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        coordinatorLayout = findViewById(R.id.coordinatorlayout);
        toolbar = findViewById(R.id.app_bar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FireChat");

        databaseReference.child("online").setValue("online");


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goto_login_page();
        } else {

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //USERS EXISTS NO NEED TO LOGOUT
                    if (!dataSnapshot.exists()) {
                        mAuth.signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("DBERROR", "error in dbrefernce main activity on start ");
                }
            });
        }
    }

    private void goto_login_page() {
        Intent intent = new Intent(MainActivity.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("online").setValue("online");

        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
        unregisterReceiver(broadcastReceiver);
    }

    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    private void setSnackbarMessage(String status, boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "woah  !!!  we are back online";
        } else {
            internetStatus = "Uh-huh Connection lost";
        }
        snackbar = Snackbar
                .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_LONG);
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (internetStatus.equalsIgnoreCase("woah  !!!  we are back online")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

    }
}



