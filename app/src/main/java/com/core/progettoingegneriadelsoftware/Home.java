package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import java.util.ArrayList;

import application.MainApplication;
import application.comunication.http.GetReceiver;
import application.maps.components.Notify;
import application.sharedstorage.Data;
import application.sharedstorage.DataListener;
import application.user.UserHandler;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DataListener {

    //menu laterale
    private NavigationView navigationView;
        //serve per eventuali errori durante il login
    private AlertDialog alert;
    private SharedPreferences prefer;
    private TextView tv;
    private GetReceiver httpServerThread;
    private ArrayList<Notify> notifies;
    private int backpress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //impostazione dei vari componenti grafici
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Go Safe!");



        //thread che resta in ascolto per le notifiche
        httpServerThread = new GetReceiver();
        httpServerThread.start();

        Data.getNotification().addDataListener(this);
        notifies = new ArrayList<>();

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

        prefer = getApplicationContext().getSharedPreferences("SessionPref", 0); // 0 - for private mode
        Editor edi = prefer.edit();

        UserHandler.setPref(prefer);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv = (TextView)headerView.findViewById(R.id.text_logName);

            //prima controlla se è già loggato o se ci sono sharedpreferencies
        if(UserHandler.isLogged()) {
            tv.setText(UserHandler.getNome() + " " + UserHandler.getCognome());
        }
        else {
            tv.setText("Utente non registrato");
        }



        backpress = 0;

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            if(extras.getString("MESSAGE").equals("EMERGENCY")) {
                MainApplication.setEmergency(true);
                finish();
            }
        }
        else MainApplication.start(this);
    }

    protected void onStart() {
        super.onStart();
        setOptionMenu();
        MainApplication.setCurrentActivity(this);
    }

    protected void onResume() {
        super.onResume();
        MainApplication.setVisible(true);
    }

    protected void onPause() {
        super.onPause();
        MainApplication.setVisible(false);
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
                backpress = (backpress + 1);
                Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

                if (backpress>1) {
                    Toast.makeText(getApplicationContext(), " Arrivederci ", Toast.LENGTH_SHORT).show();
//                  TODO uscire spegnendo i receiver
                }
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
        final CheckBox chkLog = (CheckBox) loginDialog.findViewById(R.id.checkLog);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //richiama il metodo dello user per gestire i dati inerenti il login
                    //in base alla riuscita del login si cambiano i menu oppure si mostra alert
                boolean b = UserHandler.login(txtUser.getText().toString(),txtPass.getText().toString(),chkLog.isChecked());
                if (b) {
                    setOptionMenu();

                    Toast.makeText(getApplicationContext(),
                            "benvenuto " + txtUser.getText().toString(),Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                }
                else {
                    if (!MainApplication.getOnlineMode()) alert.setMessage("Server non raggiungibile");
                    else alert.setMessage("login e/o password scorretti");
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
        tv = (TextView)findViewById(R.id.text_logName);
        if(UserHandler.isLogged()) {
            navigationView.getMenu().findItem(R.id.nav1).setTitle("Modifica Profilo");
            navigationView.getMenu().findItem(R.id.nav2).setTitle("Logout");
            navigationView.getMenu().findItem(R.id.view_information).setVisible(true);
            if(tv!= null) {
                if (UserHandler.getMail()==null) tv.setText(prefer.getString("nome",null) + " " + prefer.getString("cognome",null));
                else {
                    tv.setText(UserHandler.getNome() + " " + UserHandler.getCognome());
                }
            }
        }
        else {
            navigationView.getMenu().findItem(R.id.nav1).setTitle("Login");
            navigationView.getMenu().findItem(R.id.nav2).setTitle("Iscriviti");
            navigationView.getMenu().findItem(R.id.view_information).setVisible(false);
            if(tv!= null) tv.setText("utente non registrato");
        }
        if(!MainApplication.getOnlineMode()){
            navigationView.getMenu().findItem(R.id.nav1).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav2).setEnabled(false);
            navigationView.getMenu().findItem(R.id.view_information).setEnabled(false);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (UserHandler.isLogged()) {
            switch(id){
                case R.id.nav_maps:
                //passa ad activity maps (per ora vuota)
                Intent intentMap = new Intent (getApplicationContext(),
                        ActivityMaps.class);
                startActivity(intentMap);
                    break;
                case R.id.nav1 :
                //modifica il profilo
                Intent intentModify = new Intent (getApplicationContext(),
                        InformationsHandler.class);
                    intentModify.putExtra("editable", 1);
                startActivity(intentModify);
                    break;
                case R.id.view_information:
                    Intent intentView = new Intent (getApplicationContext(),
                            InformationsHandler.class);
                    intentView.putExtra("editable", 0);
                    startActivity(intentView);
                    break;
                case R.id.nav2:
                logout();
                    break;
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
        /*if(id == R.id.viewuser){
            DatabaseUtility.viewColumn();
        }else if (id == R.id.delete){
            final Dialog loginDialog = new Dialog(this);
            loginDialog.setContentView(R.layout.login_dialog);
            loginDialog.setTitle("Login");
            //inizializzo componenti del dialog
            Button btnCancel = (Button) loginDialog.findViewById(R.id.btnLoginCancel);
            Button btnConfirm = (Button) loginDialog.findViewById(R.id.btnLoginEnter);
            final EditText txtUser = (EditText) loginDialog.findViewById(R.id.txtLoginUsername);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //richiama il metodo dello user per gestire i dati inerenti il login
                    //in base alla riuscita del login si cambiano i menu oppure si mostra alert
                    DatabaseUtility.deleteColumn(txtUser.getText().toString());


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
        }*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
            //evita che rimanga selezionato l'item del menu
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void retrive() {
//        notifies = Data.getNotification().getNotifies();
//        for (int i = 0;i<notifies.size();i++){
//            Log.i("Notification:","n°: "+notifies.get(i).getId()+
//                    " cod cat: "+notifies.get(i).getCod_cat()+
//                    " floor: "+notifies.get(i).getFloor()+
//                    " room: "+notifies.get(i).getRoom());
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(MainApplication.getEmergency()){
//                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0000"))); //red
//                }else{
//                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5"))); //blue
//                }
//            }
//        });

    }

}
