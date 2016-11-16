package com.example.yu.lab1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolbar extends AppCompatActivity {
    String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You selected letter", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean onCreateOptionsMenu (Menu m){
        getMenuInflater().inflate(R.menu.toolbar_menu, m );
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi) {

        switch (mi.getItemId()) {

            case R.id.Choice1:
                Log.d("Toolbar", "Option 1 selected");
                if(msg.equals("")) {
                    Snackbar.make(findViewById(android.R.id.content), "You selected item 1", Snackbar.LENGTH_LONG)
                            .show();
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                            .show();
                }

                break;

            case R.id.Choice2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.conformation);
                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                 //Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.Choice3:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                // Get the layout inflater
                LayoutInflater inflater = this.getLayoutInflater();
                final View inflator = inflater.inflate(R.layout.dialog_msg, null);
                final EditText msgText = (EditText) inflator.findViewById(R.id.dialog_msg);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder1.setView(inflator)
                        // Add action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                msg = msgText.getText().toString();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                //Create the AlertDialog
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                break;

            case R.id.About:
                Context context = getApplicationContext();
                CharSequence text = "Version 1.0, by Yu Hou";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
        }
        return true;
    }
}
