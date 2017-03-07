package application.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Federico-PC on 05/12/2016.
 */


public class DatabaseConnection extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbingsoft.db";
    private static final int DATABASE_VERSION = 4;

    // Lo statement SQL di creazione del database
    private static final String DATABASE_CREATE = "create table User (email text primary key, password text not null, nome text not null, cognome text not null, data_nascita text, " +
            "luogo_nascita text , provincia text , stato text ," +
            " telefono text , sesso text , cod_fis text);";

    // Costruttore
    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS User");
        onCreate(database);

    }
}


