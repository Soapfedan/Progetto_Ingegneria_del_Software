package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import application.MainApplication;
import application.user.UserHandler;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //menu laterale
    private NavigationView navigationView;
        //serve per eventuali errori durante il login
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //impostazione dei vari componenti grafici
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothings
                    }
                });
        alert = builder.create();

        //vengono effettuate alcune configurazioni a livello di utente e si software
        MainApplication.start(this);
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

    private void login() {
        //crea un dialog per la form di login
        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle("Login");
        //inizializzo componenti del dialog
        Button btnCancel = (Button) loginDialog.findViewById(R.id.btnLoginCancel);
        Button btnLogin = (Button) loginDialog.findViewById(R.id.btnLoginEnter);
        final EditText txtUser = (EditText) loginDialog.findViewById(R.id.txtLoginUsername);
        final EditText txtPass = (EditText) loginDialog.findViewById(R.id.txtLoginPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //richiama il metodo dello user per gestire i dati inerenti il login
                    //in base alla riuscita del login si cambiano i menu oppure si mostra alert
                boolean b = UserHandler.login(txtUser.getText().toString(),txtPass.getText().toString());
                if (b) {
                    setOptionMenu();

                    Toast.makeText(getApplicationContext(),
                            "benvenuto " + txtUser.getText().toString(),Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                }
                else {
                    alert.setMessage("login e/o password scorretti");
                    alert.show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
            }
        });

        //rende visibile il dialog
        loginDialog.show();

    }


    private void logout() {
        //richiama il metodo dello user per gestire i dati inerenti il logout
        UserHandler.logout();
        setOptionMenu();
    }

    //richiamato quando effettuo login e logout, imposta le voci del menu
    private void setOptionMenu() {
        TextView tv = (TextView)findViewById(R.id.text_logName);

        if(UserHandler.isLogged()) {
            navigationView.getMenu().findItem(R.id.nav1).setTitle("Modifica Profilo");
            navigationView.getMenu().findItem(R.id.nav2).setTitle("Logout");
            tv.setText(UserHandler.getNome() + " " + UserHandler.getCognome());
        }
        else {
            navigationView.getMenu().findItem(R.id.nav1).setTitle("Login");
            navigationView.getMenu().findItem(R.id.nav2).setTitle("Iscriviti");
            tv.setText("utente non registrato");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (UserHandler.isLogged()) {
            if (id == R.id.nav_maps) {
                //passa ad activity maps (per ora vuota)
                Intent intent = new Intent (getApplicationContext(),
                        ActivityMaps.class);
                startActivity(intent);
            } else if (id == R.id.nav1) {
                //modifica il profilo
                Intent intent = new Intent (getApplicationContext(),
                        InformationsHandler.class);
                startActivity(intent);
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

            } else if (id == R.id.nav1) {
                login();
            } else if (id == R.id.nav2) {
                Intent intent = new Intent (getApplicationContext(),
                        InformationsHandler.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
