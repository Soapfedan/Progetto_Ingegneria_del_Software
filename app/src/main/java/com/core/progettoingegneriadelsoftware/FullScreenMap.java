package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import application.MainApplication;
import application.maps.grid.TouchImageView;
import application.sharedstorage.Data;
import application.sharedstorage.DataListener;
import application.sharedstorage.UserPositions;


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
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private String savedItemClicked;

    private int s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps_scrool);
        Data.getUserPosition().addDataListener(this);
        s = 0;
        position = new int[2];
        //position = MainApplication.getFloors().get("145").getRooms().get("145DICEA").getCoords();
        Data.getUserPosition().addDataListener(this);
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


        if(MainApplication.getEmergency()) {
            MainApplication.initializeScanner(this,"EMERGENCY");
        }
        else {
            MainApplication.initializeScanner(this,"SEARCHING");
        }
    }


    protected void onStart() {
        super.onStart();
        if(!MainApplication.controlBluetooth()) MainApplication.activateBluetooth(this);
        setImageGrid(s);
        setContentView(image);
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
            canvas.drawCircle(position[0], position[1], 50, paint); //x y radius paint
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
        int[] pos = Data.getUserPosition().getPosition();
        position[0] = pos[0];
        position[1] = pos[1];
        setImageGrid(s);
        setContentView(image);
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
