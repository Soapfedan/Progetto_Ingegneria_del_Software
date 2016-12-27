package com.core.progettoingegneriadelsoftware;

/**
 * Created by Niccolò on 28/12/2016.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

/**
 * Created by Niccolò on 27/12/2016.
 */

public class ActivityMaps extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button b_map = (Button) findViewById(R.id.but_map_exit);
        b_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(),
                        Home.class);
                startActivity(intent);
            }
        });
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
}

