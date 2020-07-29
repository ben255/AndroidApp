package com.main.sketchorguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Set;

public class DrawCanvas extends View{


    ArrayList<BrushData> dataArray;

    public DrawCanvas(Context context){
        super(context);
    }
    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(dataArray != null)
            for(int i = 0; i < dataArray.size(); i++){
                if(i > 0){
                    int dx = dataArray.get(i).x - dataArray.get(i-1).x;
                    int dy = dataArray.get(i).y - dataArray.get(i-1).y;
                    if((Math.pow(dx, 2)+Math.pow(dy,2)) > 10 && (Math.pow(dx, 2)+Math.pow(dy,2)) < 5000){

                        canvas.drawLine(dataArray.get(i-1).x, dataArray.get(i-1).y, dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).paint);
                    }else
                        canvas.drawCircle(dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).brushWidth, dataArray.get(i).paint);

                }else
                    canvas.drawCircle(dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).brushWidth, dataArray.get(i).paint);
            }



    }

    public void setBrushData(ArrayList<BrushData> dataArray) {
        this.dataArray = dataArray;
        invalidate();

    }
}
