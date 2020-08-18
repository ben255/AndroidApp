package com.main.sketchorguess;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    DrawCanvas drawView;
    BrushComponentView brushComponentView;
    BrushData brushData;
    Paint paint;
    ArrayList<BrushData> dataArray;

    View brushComponent_layout;



    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawCanvas) findViewById(R.id.DrawCanvas);

        brushComponent_layout = (View) findViewById(R.id.brush_component_layout);


        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                brushData = new BrushData();

                paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(20);
                brushData.brushWidth = 10;
                brushData.paint = paint;

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        brushData.x = (int) motionEvent.getX();
                        brushData.y = (int) motionEvent.getY();
                        dataArray.add(brushData);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        brushData.x = (int) motionEvent.getX();
                        brushData.y = (int) motionEvent.getY();
                        dataArray.add(brushData);
                        break;
                }

                drawView.setBrushData(dataArray);
                return true;
            }
        });
        brushComponentView = (BrushComponentView) findViewById(R.id.brush_component_view);
        brushComponentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                brushComponentView.setBrushColor((int)event.getX(),(int) event.getY());
                return false;
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigation);

        bottomNavigationView.setBackground(new ColorDrawable(getResources().getColor(R.color.colorBottomBar)));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_1:
                        Log.i("CLICKED", "CLICKED ONE");
                        break;
                    case R.id.page_2:
                        if(item.isChecked()) {
                            item.setChecked(false);
                            brushComponent_layout.setVisibility(View.INVISIBLE);
                        }
                        else {
                            item.setChecked(false);
                            brushComponent_layout.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        dataArray = new ArrayList<>();


    }


/*
    brushData.x = (int) motionEvent.getX();
    brushData.y = (int) motionEvent.getY();
    brushData.brushWidth = 10;
    Paint paint = new Paint();
        paint.setColor(Color.BLUE);
    brushData.paint = paint;

        drawView.setBrushData(brushData);*/

}
