package com.main.sketchorguess;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    DrawCanvas drawView;
    BrushData brushData;
    Paint paint;
    ArrayList<BrushData> dataArray;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawCanvas) findViewById(R.id.DrawCanvas);
        drawView.setOnTouchListener(this);


        dataArray = new ArrayList<>();


    }


    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {

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
/*
    brushData.x = (int) motionEvent.getX();
    brushData.y = (int) motionEvent.getY();
    brushData.brushWidth = 10;
    Paint paint = new Paint();
        paint.setColor(Color.BLUE);
    brushData.paint = paint;

        drawView.setBrushData(brushData);*/

}
