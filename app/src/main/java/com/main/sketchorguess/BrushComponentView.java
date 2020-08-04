package com.main.sketchorguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BrushComponentView extends View {
    ArrayList<Rect> listRect;
    ArrayList<Paint> listColor;
    public BrushComponentView(Context context) {
        super(context);
        init(null);
    }

    public BrushComponentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BrushComponentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(@Nullable AttributeSet set){
        listRect = new ArrayList<>();
        listColor = new ArrayList<>();
        loadRect();
        invalidate();
    }

    private void loadRect(){
        Rect rect = new Rect();
        Paint paint = new Paint();

        paint.setColor(Color.GREEN);
        rect.set(0,0,100,100);
        listColor.add(paint);
        listRect.add(rect);


        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        rect.set(100,0,200,100);
        listColor.add(paint);
        listRect.add(rect);

        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.RED);
        rect.set(200,0,300,100);
        listColor.add(paint);
        listRect.add(rect);

        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        rect.set(300,0,400,100);
        listColor.add(paint);
        listRect.add(rect);

        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        rect.set(400,0,500,100);
        listColor.add(paint);
        listRect.add(rect);

        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.CYAN);
        rect.set(500,0,600,100);
        listColor.add(paint);
        listRect.add(rect);

        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.GRAY);
        rect.set(600,0,700,100);
        listColor.add(paint);
        listRect.add(rect);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(int i = 0; i < listRect.size(); i++){
            canvas.drawRect(listRect.get(i), listColor.get(i));
        }
    }

    public void setBrushColor(int x, int y){

        Log.i("XTOUCH", Integer.toString(x));
        Log.i("YTOUCH", Integer.toString(y));

    }
}
