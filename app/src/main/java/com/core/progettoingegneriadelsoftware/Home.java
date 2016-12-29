package com.core.progettoingegneriadelsoftware;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public boolean logged;
        //elementi grafici dell'activity
    private Button b_log;
    private TextView t_user;
    private TextView t_pass;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        b_log = (Button) findViewById(R.id.but_log);
        t_user = (TextView) findViewById(R.id.edit_user);
        t_pass = (TextView) findViewById(R.id.edit_pass);

        navigationView.setNavigationItemSelectedListener(this);

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }


    protected void onStop() {
        super.onStop();
    }


    public void onDestroy() {
        super.onDestroy();
    }

    @Override   //toglie il focus dal menu quando si clicca su altro layer
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void login() {
        //provo qua se funziona il bottone di login, ma poi andrà messo meglio da altre parti
        //fa comparire elementi del login
        b_log.setVisibility(View.VISIBLE);
        t_user.setVisibility(View.VISIBLE);
        t_pass.setVisibility(View.VISIBLE);

        //deve fare un controllo
        b_log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //imposta la scritta dopo benvenuto
                TextView tv = (TextView)findViewById(R.id.text_logName);
                t_user.setVisibility(View.INVISIBLE);
                t_pass.setVisibility(View.INVISIBLE);
                b_log.setVisibility(View.INVISIBLE);
                tv.setText(t_user.getText().toString());
                logged = true;
                //modifica le voci del menu al momento del login
                //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getMenu().findItem(R.id.nav1).setTitle("Modifica Profilo");
                navigationView.getMenu().findItem(R.id.nav2).setTitle("Logout");
                Toast.makeText(getApplicationContext(),
                        "benvenuto " + t_user.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout() {
        logged = false;
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav1).setTitle("Login");
        navigationView.getMenu().findItem(R.id.nav2).setTitle("Iscriviti");
        TextView tv = (TextView)findViewById(R.id.text_logName);
        tv.setText("utente non registrato");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (logged) {
            if (id == R.id.nav_maps) {
                //passa ad activity maps (per ora vuota)
                Intent intent = new Intent (getApplicationContext(),
                        ActivityMaps.class);
                startActivity(intent);
            } else if (id == R.id.nav1) {

            } else if (id == R.id.nav2) {
                logout();
            }
        }
        else {
            if (id == R.id.nav_maps) {
                //passa ad activity maps (per ora vuota)
                Intent intent = new Intent (getApplicationContext(),
                        ActivityMaps.class);
                startActivity(intent);
                //modifica il profilo
            } else if (id == R.id.nav1) {
                login();
            } else if (id == R.id.nav2) {

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
