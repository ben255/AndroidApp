package com.main.sketchorguess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

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

    //serversideinfo
    private String username = "";
    private String gameSessionId = "";

    JSONArray startTurnData;

    int playerSize = 2;
    String[] playerUsername = new String[playerSize];
    String time = "";
    int countdown = 240;
    int[] playerId = new int[playerSize];
    String[] playerColor = new String[playerSize];
    int[] playerScore = new int[playerSize];
    String currentWord = "";
    String hiddenWord = "";
    boolean runTurn = true;

    Thread mainThread;
    Thread startNewTurnThread;
    boolean newTurn = false;

    String currentPlayer = "";

    Thread uploadBitmapThread;
    Thread downloadBitmapThread;
    boolean uploadBitmap = false;
    boolean downloadBitmap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brush);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        gameSessionId = intent.getStringExtra("gamesessionid");


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
                drawView.invalidateBitmap();
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
                    TextData data = new TextData(username, editTextText.getText().toString());
                    chatTextList.add(data);
                    chatList.setAdapter(adapter);
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.brush_toolbar);
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorBottomBar)));
        setSupportActionBar(toolbar);
        setTitleMain("Starting Soon");

        playerColor[0] = "#a4c639";
        playerColor[1] = "#356bb8";
 //       playerColor[2] = "#bfbf38";
   //     playerColor[3] = "#b5344e";
     //   playerColor[4] = "#d6a4e5";
       // playerColor[5] = "#303da6";

        JSONObject jobject = null;
        try {
            jobject = new JSONObject(gameSessionId);
            gameSessionId = jobject.get("gamesessionid").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    protected void onStart(){
        super.onStart();
        drawView.invalidate();

        runTurn = true;
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("add currentplayer", currentPlayer);
                if(currentPlayer.equals(username)){
                    Log.i("add", "uploading");
                    uploadBitmap = true;
                    uploadBitmapThread.start();
                }else{
                    Log.i("add", "downloading");
                    downloadBitmap = true;
                    downloadBitmapThread.start();
                }
                while(runTurn) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int max = countdown/60;
                    countdown--;
                    final StringBuilder temp = new StringBuilder();

                    int tempMin = (countdown-(max*60));
                    if(tempMin == -1)
                        tempMin = 59;
                    temp.append(+max+":"+tempMin);

                    if(countdown <= 0){
                        runTurn = false;
                        countdown = 240;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitleMain(hiddenWord+"   "+temp.toString());
                        }
                    });
                }

            }
        });
        startup();
    }
    private void startup(){


        startNewTurnThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!newTurn){
                    syncTurn(gameSessionId, username);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startTurn(gameSessionId);
            }
        });
        startNewTurnThread.start();

        uploadBitmapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(uploadBitmap){
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    uploadBitmapToServer(gameSessionId);
                }
            }
        });

        downloadBitmapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(downloadBitmap){
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    downloadBitmapFromServer(gameSessionId);
                }
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for(int i = 0; i < playerUsername.length; i++) {

            SpannableString s = new SpannableString(playerUsername[i]+": "+playerScore[i]);

            s.setSpan(new ForegroundColorSpan(Color.parseColor(playerColor[i])), 0, s.length(), 0);
            menu.add(0, 0, 0, s);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setTitleMain(String title){
        getSupportActionBar().setTitle(title);
    }


    private void startTurn(final String gameId) {
        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/startturn";

        Map<String, String> parmas = new HashMap<>();
        Bitmap bit;
        if(drawView.getBitmap() == null){
            bit = BitmapFactory.decodeResource(getResources(), R.drawable.chat_icon);
            parmas.put("gamesessionid", gameId);
            parmas.put("bitmap",getBytesFromBitmap(bit).toString());
        }else{
            parmas.put("gamesessionid", gameId);
            parmas.put("bitmap", getBytesFromBitmap(drawView.getBitmap()).toString());
        }



        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.i("Respons", response.toString());
                try {
                    startTurnData = response.getJSONArray("sessioninfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int x = 0; x < startTurnData.length(); x++){
                    try {
                        JSONObject temp = startTurnData.getJSONObject(x);
                        playerUsername[x] = temp.get("uname").toString();
                        time = temp.get("currtime").toString();
                        playerId[x] = (int) Integer.valueOf(temp.get("id").toString());
                        playerScore[x] = (int) Integer.valueOf(temp.get("score").toString());
                        currentWord = temp.get("currword").toString();
                        currentPlayer = temp.get("currplayer").toString();

                        Log.i("adduser****",playerUsername[x]+" "+time+" "+playerId[x]+" "+playerScore[x]+" "+currentWord+" "+currentPlayer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final StringBuilder s = new StringBuilder("");
                    for(int i = 0; i < currentWord.length(); i++){
                        s.append("__");
                        s.append(" ");
                    }
                    hiddenWord = s.toString();

                }
                mainThread.start();
                runTurn = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adduser****",error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/*
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 500;
            }

            @Override
            public int getCurrentRetryCount() {
                return 500;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });*/

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void syncTurn(String gameId, String username){

        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/syncturn";

        Map<String, String> parmas = new HashMap<>();

        parmas.put("gamesessionid", gameId);
        parmas.put("username", username);



        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Log.i("Respons", response.toString());
                try {
                    String ready = response.get("turnready").toString();
                    Log.i("adduser****",ready);
                    if(ready.matches("true")){
                        newTurn = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adduser****",error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/*
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 500;
            }

            @Override
            public int getCurrentRetryCount() {
                return 500;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });*/

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }

    private void uploadBitmapToServer(String gameId){

        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/uploadingbitmap";

        Map<String, String> parmas = new HashMap<>();
        parmas.put("gamesessionid", gameId);
        parmas.put("bitmap", encodeBase64(drawView.getBitmap()));
        //Log.i("add***encode", encodeBase64(drawView.getBitmap()));



        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("addsucess", "response.get().toString()");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adduser**up**",error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/*
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 500;
            }

            @Override
            public int getCurrentRetryCount() {
                return 500;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });*/

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }
    private void downloadBitmapFromServer(String gameId){

        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/downloadbitmap";

        Map<String, String> parmas = new HashMap<>();

        parmas.put("gamesessionid", gameId);



        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //Bitmap bit = decodeBase64( response.get("bitmap").toString());
                    JSONArray jsonArray = response.getJSONArray("bitdata");
                    JSONObject jObject = jsonArray.getJSONObject(0);
                    JSONObject jdataObject = jObject.getJSONObject("bitmap");
                    JSONArray jdataArray = jdataObject.getJSONArray("data");

                    StringBuilder s = new StringBuilder();
                    for(int i = 0; i < jdataArray.length(); i ++){
                        char c = (char)jdataArray.getInt(i);
                        s.append(c);
                    }
                    Log.i("add..String", s.toString());
                    //Log.i("add***decode", jObject.get("bitmap").toString());
                    //Log.i("add....**", jdataObject.get("data").toString());


                    //JSONArray jArrayObject = jdataObject.getJSONArray("data");
                    //Log.i("add....**", jArrayObject.get(0).toString());

                    Bitmap bit = decodeBase64(s.toString());

                    drawView.setBitmap(bit);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adduser****download",error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
/*
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 500;
            }

            @Override
            public int getCurrentRetryCount() {
                return 500;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });*/

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            Log.i("add**************", stream.toByteArray().toString());
            return stream.toByteArray();
        }
        return null;
    }
    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
        }
        return null;
    }
    public String encodeBase64(Bitmap bit){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 100, baos); // Could be Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.WEBP
        byte[] bai = baos.toByteArray();

        return Base64.encodeToString(bai, Base64.DEFAULT);
    }
    public Bitmap decodeBase64(String base64Image){
        byte[] data = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bm;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        return bm;
    }
}
