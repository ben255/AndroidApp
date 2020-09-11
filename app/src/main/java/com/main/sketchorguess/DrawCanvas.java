package com.main.sketchorguess;

import android.content.Context;
import android.graphics.Bitmap;
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
    Paint paint, background;
    Bitmap bitmap;
    Canvas secondCanvas;
    boolean runOnce = true;

    public DrawCanvas(Context context){
        super(context);
        init();
    }
    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        background = new Paint();
        background.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(runOnce){
            bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            secondCanvas = new Canvas(bitmap);
            runOnce = false;
        }
        if(dataArray != null && dataArray.size() > 0) {
            for (int i = 0; i < dataArray.size(); i++) {
                if (i > 0) {
                    int dx = dataArray.get(i).x - dataArray.get(i - 1).x;
                    int dy = dataArray.get(i).y - dataArray.get(i - 1).y;
                    if ((Math.pow(dx, 2) + Math.pow(dy, 2)) > 10 && (Math.pow(dx, 2) + Math.pow(dy, 2)) < 5000) {

                        secondCanvas.drawLine(dataArray.get(i - 1).x, dataArray.get(i - 1).y, dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).paint);
                    } else
                        secondCanvas.drawCircle(dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).brushWidth, dataArray.get(i).paint);

                } else
                    secondCanvas.drawCircle(dataArray.get(i).x, dataArray.get(i).y, dataArray.get(i).brushWidth, dataArray.get(i).paint);
            }

            secondCanvas.drawCircle(dataArray.get(dataArray.size()-1).x,dataArray.get(dataArray.size()-1).y,dataArray.get(dataArray.size()-1).brushWidth,paint);
        }
        canvas.drawBitmap(bitmap, 0, 0, background);

    }

    public void invalidateBitmap(){
        bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        secondCanvas = new Canvas(bitmap);
    }
    public void setBrushData(ArrayList<BrushData> dataArray) {
        this.dataArray = dataArray;

        invalidate();
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
}
