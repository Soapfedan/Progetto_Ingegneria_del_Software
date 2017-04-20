package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Dimension;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Toast;

import application.MainApplication;
import application.beacon.BeaconScanner;
import application.maps.grid.TouchImageView;
import application.sharedstorage.DataContainer;
import application.sharedstorage.DataListener;
import application.sharedstorage.Positions;


public class FullScreenMap extends AppCompatActivity implements DataListener{
        //indica le volte che si clicca BackButton
    private int backpress;
    //menu laterale
    private NavigationView navigationView;
        //serve per eventuali errori durante il login
    private AlertDialog alert;
    private SharedPreferences prefer;
    private TouchImageView image;
    private int[] position; //0->x 1->y

    final private int[] imageDim = {1600,1000};

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps_scrool);
        int s = 0;
        position = new int[2];
        position = MainApplication.getFloors().get("145").getRooms().get("145DICEA").getCoords();
        DataContainer.getUserPosition().addDataListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            s = extras.getInt("MAP_ID");
            //The key argument here must match that used in the other activity
        }

       /* image = (ImageView) findViewById(R.id.imageHelp);
        image.setImageResource(s);
        image.setScaleType(ImageView.ScaleType.FIT_XY);*/
        image = new TouchImageView(this);
        image.setMaxZoom(4f);
        setImageGrid(s);
        setContentView(image);

        if(MainApplication.getEmergency()) {
            MainApplication.initializeScanner(this,"EMERGENCY");
        }
        else {
            MainApplication.initializeScanner(this,"SEARCHING");
        }
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

    private void setImageGrid(int imageId){
        if(position[0]!=0&&position[1]!=0) {

            BitmapFactory.Options myOptions = new BitmapFactory.Options();
            myOptions.inDither = true;
            myOptions.inScaled = false;
            myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
            myOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId, myOptions);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);


            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


            Canvas canvas = new Canvas(mutableBitmap);
            //int[] c = coordsMapping(position);
            canvas.drawCircle(position[0], position[1], 5, paint); //x y radius paint
            image.setImageBitmap(mutableBitmap);
        }else {
            image.setImageResource(imageId);
        }
    }
/*
    private int[] coordsMapping(int[] co){
        int[] coords = new int[2]; // x y
        double scaleRatiox,scaleRatioy;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
       // float dimx = size.x;
        //float dimy = size.y-px;
        double dimx = wi;
        double dimy = hi - px;
        scaleRatiox = 4.44*dimx/1000;
        scaleRatioy = 6.37*dimy/1600;
        coords[0] = (int)(co[0]*scaleRatiox);
        coords[1] = (int) (dimy) - (int)(co[1]*scaleRatioy);
        coords[0] = 147;
        coords[1] = 473;
        coords[0] = 1600 - (int)(co[0]*4.44);
        coords[1] = 1000 - (int)(co[1]*6.37);
        return coords;
    }
*/
    @Override
    public void update() {

    }

    @Override
    public void retrive() {
        position[0] = DataContainer.getUserPosition().getX();
        position[1] = DataContainer.getUserPosition().getY();
    }

    @Override
    public void onBackPressed() {
        backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress>1) {
            MainApplication.getScanner().suspendScan();
            MainApplication.setEmergency(false);
            this.finish();
        }


    }

}
