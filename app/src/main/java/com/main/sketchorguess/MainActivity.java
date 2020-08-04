package com.main.sketchorguess;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    DrawCanvas drawView;
    BrushComponentView brushComponentView;
    BrushData brushData;
    Paint paint;
    ArrayList<BrushData> dataArray;



    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawCanvas) findViewById(R.id.DrawCanvas);


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
