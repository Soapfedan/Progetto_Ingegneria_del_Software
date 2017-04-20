package application.database;

/**
 * Created by Federico-PC on 23/12/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import java.util.ArrayList;

import application.user.UserProfile;

public class UserAdapter {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = UserAdapter.class.getSimpleName();

    private Context context;
    private SQLiteDatabase database;
    private DatabaseConnection dbHelper;

    // Database fields
    private static final String DATABASE_TABLE      = "User";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME = "nome";
    public static final String KEY_SURNAME = "cognome";
    public static final String KEY_BIRTH_DATE = "data_nascita";
    public static final String KEY_BIRTH_CITY = "luogo_nascita";
    public static final String KEY_PROVINCE = "provincia";
    public static final String KEY_STATE = "stato";
    public static final String KEY_TELEPHONE = "telefono";
    public static final String KEY_SEX = "sesso";
    public static final String KEY_PERSONAL_NUMBER = "cod_fis";

    public UserAdapter(Context context) {
        this.context = context;
    }

    public UserAdapter open() throws SQLException {
        dbHelper = new DatabaseConnection(context);
        database = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(String email,String password, String name, String surname, String birth_date,
                                              String birth_city, String province, String state, String telephone, String sex,
                                              String personal_number) {
        ContentValues values = new ContentValues();
        values.put( KEY_EMAIL, email );
        values.put( KEY_PASSWORD, password );
        values.put( KEY_NAME, name );
        values.put( KEY_SURNAME, surname );
        values.put( KEY_BIRTH_DATE, birth_date );
        values.put( KEY_BIRTH_CITY, birth_city );
        values.put( KEY_PROVINCE, province );
        values.put( KEY_STATE, state );
        values.put( KEY_TELEPHONE, telephone );
        values.put( KEY_SEX, sex );
        values.put( KEY_PERSONAL_NUMBER, personal_number );

        return values;
    }

    //create an user
    public long createUser(String email,String password, String name, String surname, String birth_date,
                           String birth_city, String province, String state, String telephone, String sex,
                           String personal_number ) {
        ContentValues initialValues = createContentValues(email, password, name, surname, birth_date, birth_city,
                province, state, telephone, sex, personal_number);
        //invertiti i rami
        if(checkNewUser(email)==0){
            return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
        }else {
            return -1;
        }
    }

    //update an user
    public boolean updateProfile(String email,String password, String name, String surname, String birth_date,
                                 String birth_city, String province, String state, String telephone, String sex,
                                 String personal_number ) {
        ContentValues updateValues = createContentValues(email, password, name, surname, birth_date, birth_city,
                province, state, telephone, sex, personal_number);
        return database.update(DATABASE_TABLE, updateValues, KEY_EMAIL + "='"+ email
                + "'", null) > 0;
    }

    //delete an user
    public boolean deleteProfile(String email) {
        return database.delete(DATABASE_TABLE, KEY_EMAIL + "='"+ email + "'", null) > 0;
    }


    //get a profile by his mail, if he doesn't exist, the function return 0, otherwise 1
    public int checkNewUser(String mail){
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
                        KEY_EMAIL, KEY_NAME, KEY_SURNAME },
                KEY_EMAIL + "='"+ mail + "'", null, null, null, null, null);
        System.out.println("mail: " + mail + " presente " + mCursor.getCount());
        return mCursor.getCount();
    }


    //get all user proprieties
    public UserProfile getUserProfile(String mail){
        Cursor cursor = database.query(true, DATABASE_TABLE, new String[] {
                        KEY_EMAIL, KEY_PASSWORD, KEY_NAME, KEY_SURNAME, KEY_BIRTH_DATE, KEY_BIRTH_CITY, KEY_PROVINCE, KEY_STATE, KEY_TELEPHONE,
                        KEY_SEX,KEY_PERSONAL_NUMBER},
                KEY_EMAIL + "='"+ mail + "'", null, null, null, null, null);
        UserProfile profile = null;
        if (cursor.moveToFirst()) {

            profile = new UserProfile(cursor.getString(0),    //email
                    cursor.getString(1),    //password
                    cursor.getString(2),    //nome
                    cursor.getString(3),    //cognome
                    cursor.getString(4),    //data di nascita
                    cursor.getString(5),    //luogo di nascita
                    cursor.getString(6),    //provincia
                    cursor.getString(7),    //stato
                    cursor.getString(8),    //telefono
                    cursor.getString(9),    //sesso
                    cursor.getString(10)    //codice fiscale
            );
        }
        return profile;
    }

    //get email and password
    public Cursor getCredential(String mail){
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
                        KEY_EMAIL, KEY_PASSWORD },
                KEY_EMAIL + "="+ mail, null, null, null, null, null);

        return mCursor;
    }

    //get all users
    public ArrayList<UserProfile> getAllUsers(){
        Cursor cursor = database.query(true, DATABASE_TABLE, new String[] {
                        KEY_EMAIL, KEY_NAME, KEY_SURNAME,},
               null, null, null, null, null, null);
        ArrayList<UserProfile> users = new ArrayList<>();
        UserProfile profile = null;
        while (cursor.moveToNext()) {

            profile = new UserProfile(cursor.getString(0),    //email
                    cursor.getString(1),    //nome
                    cursor.getString(2)    //cognome
            );
            users.add(profile);
        }
        return users;
    }

}
