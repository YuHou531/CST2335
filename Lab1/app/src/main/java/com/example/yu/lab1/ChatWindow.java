package com.example.yu.lab1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "ChatWindow";
    protected EditText editText;
    protected ListView listView;
    protected Button send;
    public final ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //initialize these variables using findViewById()
        Button sendButton = (Button) findViewById(R.id.sendButton);
        final EditText editText = (EditText) findViewById(R.id.editText);
        ListView listView = (ListView) findViewById(R.id.listView);

        //Step 10
        final ArrayAdapter<String> messageAdapter = new ChatAdapter(this, list);
        listView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(editText.getText().toString());
                editText.setText(""); //clear the text
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i(ACTIVITY_NAME, "onDestroy()");
    }

}

//Inner class
class ChatAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> list;
    private final Context context;

    ChatAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
    }

    public int getCount() {
        return list.size();
    }

    public String getItem(int position) {
        return list.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View result = null;
        if(position%2 == 0) {
            result = inflater.inflate(R.layout.chat_row_incoming, null);
        }
        else {
            result = inflater.inflate(R.layout.chat_row_outgoing, null);
        }
        TextView message = (TextView)result.findViewById(R.id.message_text);
        message.setText( getItem(position) ); // get the string at position
        return result;
    }
}
