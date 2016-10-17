package com.example.yu.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private ChatDatabaseHelper chatDatabaseHelper;
    private SQLiteDatabase sqlDB;
    private String[] allMessages = { ChatDatabaseHelper.KEY_ID,
            ChatDatabaseHelper.KEY_MESSAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //initialize these variables using findViewById()
        Button sendButton = (Button) findViewById(R.id.sendButton);
        final EditText editText = (EditText) findViewById(R.id.editText);
        ListView listView = (ListView) findViewById(R.id.listView);

        //Lab 5
        //it creates a temporary ChatDatabaseHelper object,
        //which then gets a writeable database and stores that as an instance variable.
        chatDatabaseHelper = new ChatDatabaseHelper(this);
        sqlDB = chatDatabaseHelper.getWritableDatabase();

        //lab 5 - Step 5
        //After opening the database, execute a query for any existing chat messages and add them
        //into the ArrayList of messages that was created in Lab4
        Cursor cursor = sqlDB.query(ChatDatabaseHelper.TABLE_NAME,
                allMessages, null, null, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            String message = cursor.getString( cursor.getColumnIndex( ChatDatabaseHelper.KEY_MESSAGE) );
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + message );
            list.add(message);
            cursor.moveToNext();
        }

        int cursorColumnCount = cursor.getColumnCount();
        Log.i(ACTIVITY_NAME, "Cursor’s column count =" + cursorColumnCount );
        for(int j=0; j<cursorColumnCount; j++) {
            Log.i(ACTIVITY_NAME, "Cursor’s column name =" + cursor.getColumnName(j) );
        }

        // make sure to close the cursor
        cursor.close();

        //Lab 4 - Step 10
        final ArrayAdapter<String> messageAdapter = new ChatAdapter(this, list);
        listView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                list.add(message);
                editText.setText(""); //clear the text
                messageAdapter.notifyDataSetChanged();

                //Lab 5 - insert the new message to the database
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, message);
                sqlDB.insert(ChatDatabaseHelper.TABLE_NAME, null,
                        values);
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

        //Lab 5 - close the database that you opened in onCreate()
        chatDatabaseHelper.close();
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
