package com.core.progettoingegneriadelsoftware;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import application.MainApplication;
import application.comunication.ServerComunication;
import application.comunication.http.GetReceiver;
import application.user.UserHandler;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

        //menu laterale
    private NavigationView navigationView;
        //elementi grafici dell'activity
    private AlertDialog alert;
    private TextView tv;
        //memorizza alcune informazioni nella memoria interna del dispositivo, per renderle persistenti
    private SharedPreferences prefer;
        //thread usato per la gestione delle notifiche inviate dal server
    private GetReceiver httpServerThread;
        //contatore delle volte in cui si clicca il tasto onBack, utilizzato quando si vuole uscire dall'applicazione
    private int backpress;
        //codice utilizzato come risposta alla richiesta di attivazione della localizzazione sul dispositivo
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
            //impostazioni riguardati i vari componenti grafici
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Go Safe!");

            //barra laterale del menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
            //nel caso in cui l'applicazione lavori su una versione di Android
            //superiore alla 6.0, per far funzionare il Bluetooth bisogna attivare la localizzazione
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) activateLocation();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
            //oggetto alert, visualizzato eventualmente quando vengono immessi dati sbagliati duranti il login
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothings
                    }
                });
        alert = builder.create();
            //elemento grafico usato in alto nel menu (qui compare eventualmente il nome dell'utente, quando sarà effettuato il login
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv = (TextView)headerView.findViewById(R.id.text_logName);


            //thread con il compito di ascoltare le notifiche provenienti dal server
        if(MainApplication.getOnlineMode()) {
            httpServerThread = new GetReceiver();
            httpServerThread.start();
        }
            //prefer permette l'accesso ai dati salvati nella memoria interna del dispositivo
        prefer = getApplicationContext().getSharedPreferences("SessionPref", 0); // 0 - for private mode
        Editor edi = prefer.edit();

            //passata a UserHandler un'istanza delle preferencies, in modo che vengano assegnate
            //eventuali informazioni li salvate, ad esso correlate (es. se già loggato vengono presi i suoi dati dalla memoria)
        UserHandler.setPref(prefer);

            //prima controlla se è già loggato o se ci sono sharedpreferencies
            //qualora il primo caso sia verificato viene scritto il nome dell'utente
            //nel menu a tendina
        if(UserHandler.isLogged()) {
            tv.setText(UserHandler.getNome() + " " + UserHandler.getCognome());
        }
        else {
            tv.setText("Utente non registrato");
        }

        backpress = 0;
            //la creazione di quest'activity può avvenire in due circostanze, il cui discrimine è dato dal messaggio contenuto
            //dentro extras
        Bundle extras = getIntent().getExtras();
            //caso in cui l'activity sia creata per un'emergenza
        if(extras!=null) {
            if(extras.getString("MESSAGE").equals("EMERGENCY")) {
                if (MainApplication.getScanner().getConnection()!=null)
                    Toast.makeText(getApplicationContext(), " La mappa sta per essere caricata," +
                            " un attimo di attesa ", Toast.LENGTH_SHORT).show();
                MainApplication.setEmergency(true);
                finish();
            }
        }
            //caso in cui l'activity è creata per il normale funzionamento dell'app
        else MainApplication.start(this);
    }

    protected void onStart() {
        super.onStart();
            //impostati elementi presenti nel menu
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

    //toglie il focus dal menu quando si clicca su altro layer e gestisce l'uscita dall'applicazione
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            //se il menu è aperto si occupa di chiuderlo
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
            //caso in cui si voglia uscire dall'applicazione
        else {
                //backpress fa in modo che si debba cliccare due volte il bottone per uscire
            backpress = (backpress + 1);
            Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

            if (backpress==2 || backpress >2) {
                Toast.makeText(getApplicationContext(), " Arrivederci ", Toast.LENGTH_SHORT).show();
                    //cliccato due volte il bottone, l'applicazione si prepara per essere spenta
                MainApplication.setIsFinishing(true);
                this.sendBroadcast(new Intent("SuspendScan"));

                if (MainApplication.getOnlineMode()) {
                        //chiuse tutte le connessioni al server
                    MainApplication.closeApp(httpServerThread);
                }


            }
        }

    }

    /**
     * Metodo per la gestione del login, da parte dell'utente (a questo livello solo per la gestione dell'interfaccia
     * grafica)
     */
    private void login() {
            //crea un dialog per la form di login
        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle("Login");
            //inizializzati i componenti del dialog
            // i due bottoni
        Button btnCancel = (Button) loginDialog.findViewById(R.id.btnLoginCancel);
        Button btnLogin = (Button) loginDialog.findViewById(R.id.btnLoginEnter);
            //elementi in cui scrivere
        final EditText txtUser = (EditText) loginDialog.findViewById(R.id.txtLoginUsername);
        final EditText txtPass = (EditText) loginDialog.findViewById(R.id.txtLoginPassword);
        final CheckBox chkLog = (CheckBox) loginDialog.findViewById(R.id.checkLog);
            //gestione dell'evento di click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b=false;
                //richiama il metodo dello user per gestire i dati inerenti il login
                    //in base alla riuscita del login si cambiano i menu oppure si mostra alert
                if(txtUser.getText().toString().equals("")||txtPass.getText().toString().equals("")||
                        txtUser.getText().toString()==null||txtPass.getText().toString()==null ){
                    b=false;
                }else{
                    b = UserHandler.login(txtUser.getText().toString(),txtPass.getText().toString(),chkLog.isChecked());
                }

                if (b) {
                        //modificate le voci del menù
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

    /**
     * Metodo per la gestione del logout da parte dell'utente: viene modificato lo UserHandler dell'applicazione
     * e modificati gli elementi del menù
     */
    private void logout() {
        //richiama il metodo dello user per gestire i dati inerenti il logout dell'utente
        UserHandler.logout();
            //cambiati gli elementi del menù
        setOptionMenu();
    }

    /**
     * Metodo che si occupa delle voci presenti all'interno del menu laterale, distinguendo i casi
     * in cui l'utente abbia effettuato o meno il login (considerando che l'opzione ricerca delle mappe
     * è presente in entrambe le situazioni
     */
    private void setOptionMenu() {
            //parte del menu in cui sono scritti nomi e cognome dell'utente
        tv = (TextView)findViewById(R.id.text_logName);
            //caso in cui l'utente sia loggato: può modificare il profilo, effettuare la logout
        if(UserHandler.isLogged()) {
                //impostate le voci del menu e le rispettive visibilità
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
            //caso in cui l'utente non sia ancora loggato: può entrare o iscriversi
        else {
            navigationView.getMenu().findItem(R.id.nav1).setTitle("Login");
            navigationView.getMenu().findItem(R.id.nav2).setTitle("Iscriviti");
            navigationView.getMenu().findItem(R.id.view_information).setVisible(false);
            if(tv!= null) tv.setText("utente non registrato");
        }
            //in base alla modalità in cui lavora l'applicazione (online o meno)
            //alcuni elementi del menu sono presenti o meno
        if(!MainApplication.getOnlineMode()){
            navigationView.getMenu().findItem(R.id.nav1).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav2).setEnabled(false);
            navigationView.getMenu().findItem(R.id.view_information).setEnabled(false);
        }
    }

        //metodo per la gestione del comportamento degli elementi presenti nel menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
            //caso in cui si vogliano visitare le mappe
        if (id == R.id.nav_maps) {
            Intent intentMap = new Intent (getApplicationContext(),
                    ActivityMaps.class);
            startActivity(intentMap);
        }
        else {
            //controllo che il server sia online e il database raggiungibile
            boolean b = ServerComunication.handShake(ServerComunication.getIP());
            int s=-1;
            if(b) s = ServerComunication.checkVersion();
            if(b==false || s==-1) {
               Toast.makeText(this.getApplicationContext(),"Attenzione il server non e' raggiungibile",Toast.LENGTH_SHORT).show();
                Log.e("ERRORE","ERRORE");
            }else{
                if (UserHandler.isLogged()) {
                    switch(id){
                        case R.id.nav1 :
                            //modifica il profilo
                            Intent intentModify = new Intent (getApplicationContext(),
                                    InformationsHandler.class);
                            intentModify.putExtra("editable", 1);
                            startActivity(intentModify);
                            break;
                        //caso in cui si vogliano vedere le informazioni dell'utente
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
                    //caso in cui si voglia effetuare il login
                    if (id == R.id.nav1) {
                        login();
                        //caso in cui si voglia registrare alla piattaforma
                    } else if (id == R.id.nav2) {
                        Intent intent = new Intent (getApplicationContext(),
                                InformationsHandler.class);
                        startActivity(intent);
                    }
                }
            }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
            //evita che rimanga selezionato l'item del menu
        return false;
    }

    /**
     * Metodo che si occupa dell'attivazione del sistema di localizzazione del dispositivo
     * (questa funzionalità è necessaria per i dispositivi con installata una versione di Android
     * superiore alla 6.0, in quanto senza di essa non può funzionare il Bluetooth)
     */
    private void activateLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i("activate","activate location");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("prova","guaranteed");

                } else {

                    Log.i("prova","non guaranteed");
                }
            }

        }
    }

}