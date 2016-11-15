package com.example.yu.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.example.yu.lab1.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MessageListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    protected static final String ACTIVITY_NAME = "MessageListActivity";
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
        setContentView(R.layout.activity_message_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

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

        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
             @Override
              public void onClick(View v) {
                 if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                               .commit();
                    }
                 else {
                        Context context = v.getContext();
                       Intent intent = new Intent(context, MessageDetailActivity.class);
                       intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                       context.startActivity(intent);
                   }
             }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
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
        LayoutInflater inflater = MessageListActivity.this.getLayoutInflater();
        View result = null;
        if(position%2 == 0) {
            result = inflater.inflate(R.layout.chat_row_incoming, null);
        }
        else {
            result = inflater.inflate(R.layout.chat_row_outgoing, null);
        }
        TextView message = (TextView) result.findViewById(R.id.message_text);
        // get the string at position
        final String messageText = getItem(position) ;
        message.setText(messageText);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(MessageDetailFragment.ARG_ITEM_ID, messageText);
                    MessageDetailFragment fragment = new MessageDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.message_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MessageDetailActivity.class);
                    intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, messageText);
                    context.startActivity(intent);
                }
            }
        });
            return result;
    }
}} 