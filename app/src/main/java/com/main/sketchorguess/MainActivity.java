package com.main.sketchorguess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.Cache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private EditText loginText;

    private ProgressBar spinner;
// ...

    DiskBasedCache cache;
    BasicNetwork network;
    RequestQueue queue;

    private String gameSessionId;
    private String username;
    private Context context;
    private Thread thread;
    private boolean threadRun = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        spinner = (ProgressBar)findViewById(R.id.progress_bar_main);
        spinner.setVisibility(View.GONE);
        loginText = (EditText) findViewById(R.id.login_edit_text);
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginText.getText().toString().matches("")) {

                    Toast.makeText(context, R.string.message_enter_username, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    spinner.setVisibility(View.VISIBLE);
                    addUserPostRequest(loginText.getText().toString());
                    username = loginText.getText().toString();
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(threadRun){
                                try {
                                    checkIfSessionIsFullPostRequest();
                                    sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.start();
                }
                /*
                // To dismiss the dialog

*/
            }
        });

        // Instantiate the cache
        cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        network = new BasicNetwork(new HurlStack());
        queue = Volley.newRequestQueue(this);

        getRequest();


    }

    private void getRequest() {
        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void addUserPostRequest(String username) {
        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/join";

        Map<String, String> parmas = new HashMap<>();
        parmas.put("username", username);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                gameSessionId = response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adduser****",error.toString());
            }
        });
/*
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
*/
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
        });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void checkIfSessionIsFullPostRequest() {
        // Request a string response from the provided URL.
        // Instantiate the RequestQueue.
        String url = "http://10.0.0.9:3000/wait";

        Map<String, String> parmas = new HashMap<>();
        parmas.put("gamesessionid", gameSessionId);
        parmas.put("username", username);

        Log.i("checksession***", gameSessionId+"  "+username);

        final CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, parmas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jobject = new JSONObject(response.toString());
                    if(jobject.get("gamesessionready").toString().matches("true")){
                        threadRun = false;
                        Intent myIntent = new Intent(MainActivity.this, BrushActivity.class);
                        myIntent.putExtra("username", loginText.getText().toString()); //Optional parameters
                        myIntent.putExtra("gamesessionid", gameSessionId);
                        MainActivity.this.startActivity(myIntent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("checksession***",error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}
