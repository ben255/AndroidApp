package com.main.sketchorguess;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

    Context context;

    TextView userName;
    TextView userText;

    ChatAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }


        userName =(TextView) convertView.findViewById(R.id.list_item_name);
        userText = (TextView) convertView.findViewById(R.id.list_item_text);

        if(position == 0) {
            userName.setText("User1");
            userText.setText("Text1");
        }

        if(position == 1) {
            userName.setText("User2");
            userText.setText("Text2");
        }

        if(position == 2) {
            userName.setText("User3");
            userText.setText("Text3");
        }

        if(position == 3) {
            userName.setText("User4");
            userText.setText("Text4");
        }

        if(position == 4) {
            userName.setText("User1");
            userText.setText("Text1");
        }

        if(position == 5) {
            userName.setText("User2");
            userText.setText("Text2");
        }

        if(position == 6) {
            userName.setText("User3");
            userText.setText("Text3");
        }

        if(position == 7) {
            userName.setText("User4");
            userText.setText("Text4");
        }


        return convertView;

    }
}
