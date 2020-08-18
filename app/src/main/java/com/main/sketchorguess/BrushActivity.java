package com.main.sketchorguess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class BrushActivity extends AppCompatActivity {

    private DrawCanvas drawView;
    private BrushComponentView brushComponentView;
    private BrushData brushData;
    private Paint paint;
    private ArrayList<BrushData> dataArray;

    private View brushComponent_layout;
    private View chatComponent_layout;

    private SeekBar seekBar;

    private ImageButton pencilButton;
    private ImageButton eraseButton;
    private ImageButton clearButton;

    private boolean isPencil = true;

    private ListView chatList;
    private ArrayList<TextData> chatTextList;
    private ImageButton sendTextButton;
    private EditText editTextText;
    private Context context;
    private ChatAdapter adapter;

    private Toolbar toolbar;
    private MenuBuilder menuBuilder;


    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brush);

        drawView = (DrawCanvas) findViewById(R.id.DrawCanvas);

        brushComponent_layout = (View) findViewById(R.id.brush_component_layout);
        chatComponent_layout = (View) findViewById(R.id.chat_component_layout);


        brushComponentView = (BrushComponentView) findViewById(R.id.brush_component_view);

        brushComponentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                brushComponentView.setBrushColor((int)event.getX(),(int) event.getY());
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {

                        if(brushComponent_layout.getVisibility() == View.INVISIBLE) {
                            brushData = new BrushData();
                            paint = new Paint();
                            if(isPencil)
                                paint.setColor(brushComponentView.getMarkedPaint().getColor());
                            else
                                paint.setColor(Color.WHITE);
                            paint.setStrokeWidth(seekBar.getProgress() * 2);
                            brushData.brushWidth = seekBar.getProgress();
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
                        }
                        return true;
                    }
                });
            }
        }).start();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigation);

        bottomNavigationView.setBackground(new ColorDrawable(getResources().getColor(R.color.colorBottomBar)));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_1:
                        brushComponent_layout.setVisibility(View.INVISIBLE);
                        if(chatComponent_layout.getVisibility() == View.VISIBLE){
                            chatComponent_layout.setVisibility(View.INVISIBLE);
                        }else{
                            item.setChecked(true);
                            chatComponent_layout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.page_2:
                        chatComponent_layout.setVisibility(View.INVISIBLE);
                        if(brushComponent_layout.getVisibility() == View.VISIBLE) {

                            brushComponent_layout.setVisibility(View.INVISIBLE);
                        }
                        else {
                            item.setChecked(true);
                            brushComponent_layout.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        dataArray = new ArrayList<>();

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        pencilButton = (ImageButton) findViewById(R.id.pencil_button);
        eraseButton = (ImageButton) findViewById(R.id.erase_button);
        clearButton = (ImageButton) findViewById(R.id.clear_button);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataArray.clear();
                drawView.invalidate();
            }
        });

        pencilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPencil = true;
            }
        });

        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPencil = false;
            }
        });

        chatList = (ListView) findViewById(R.id.chat_list);
        chatTextList = new ArrayList<>();
        sendTextButton = (ImageButton) findViewById(R.id.send_answer_button);
        editTextText = (EditText) findViewById(R.id.write_edit_text);
        context = this;
        adapter = new ChatAdapter(context, chatTextList);

        chatList.setAdapter(adapter);

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextText.getText().toString().equals("")) {
                    TextData data = new TextData("USER", editTextText.getText().toString());
                    chatTextList.add(data);
                    chatList.setAdapter(adapter);
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.brush_toolbar);
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorBottomBar)));
        toolbar.setTitle("__  __  __  __  __  __");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0,0,0,"Hello World: "+10);

        return super.onPrepareOptionsMenu(menu);
    }
}
