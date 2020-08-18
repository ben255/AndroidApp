package com.main.sketchorguess;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    Context context;

    TextView userName;
    TextView userText;

    ArrayList<TextData> data;

    ChatAdapter(Context context, ArrayList<TextData> data){
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        if(data != null)
            return data.size();
        else
            return 0;
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


        userName.setText(data.get(position).user);
        userText.setText(data.get(position).text);


        return convertView;

    }
}
