package com.example.hellogooglemaps;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ParsePushMessageAdapter extends ArrayAdapter<ParsePushMessage> {
  private Context mContext;
  private List<ParsePushMessage> mMessages;

  public ParsePushMessageAdapter(Context context, List<ParsePushMessage> objects) {
      super(context, R.layout.message_row_item, objects);
      this.mContext = context;
      this.mMessages = objects;
  }

  public View getView(int position, View convertView, ViewGroup parent){
      if(convertView == null){
          LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
          convertView = mLayoutInflater.inflate(R.layout.message_row_item, null);
      }

      ParsePushMessage message = mMessages.get(position);

      TextView descriptionView = (TextView) convertView.findViewById(R.id.message);

      descriptionView.setText(message.getMessage());
      
      return convertView;
  }

}
