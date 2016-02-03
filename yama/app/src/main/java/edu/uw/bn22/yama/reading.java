package edu.uw.bn22.yama;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class reading extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = "Reading";
    private SimpleCursorAdapter adapter;
    private String[] getColumns = new String[] {Telephony.Sms.Conversations.ADDRESS, Telephony.Sms.Conversations.BODY, Telephony.Sms.Conversations.DATE, Telephony.Sms.Conversations._ID };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Listens to see if the FloatingActionButton is clicked
        FloatingActionButton compose = (FloatingActionButton) findViewById(R.id.fab);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reading.this, compose.class);
                startActivity(intent);
            }
        });

        //Initializes the loader
        getLoaderManager().initLoader(1, null, this);

        //Initializes the adapter to populate the list view
        int[] index = new int[] { R.id.txtCol1, R.id.txtCol2, R.id.txtCol3};
        ListView messageList = (ListView) findViewById(R.id.messageListView);
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_item,
                null,
                getColumns,
                index,
                0);

        //Modifies the list view row data
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor c, int index) {
                if (index == 2) { //Date
                    long secDate = Long.parseLong(c.getString(index));
                    DateFormat df = new SimpleDateFormat("h:mm aa MM-dd-yyyy");
                    String sendDate = df.format(secDate);
                    TextView textView = (TextView) view.findViewById(R.id.txtCol3);
                    textView.setText(sendDate);
                    return true;
                } else if (index == 1) { //message
                    TextView textView1 = (TextView) view.findViewById(R.id.txtCol2);
                    textView1.setText(c.getString(index));
                    return true;
                } else if (index == 3) {
                    TextView textView2 = (TextView) view.findViewById(R.id.txtCol1);
                    textView2.setText(c.getString(index));
                }
                return false;
            }
        });
        messageList.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            //Creates a new CursorLoader
            Uri getInbox = Uri.parse("content://sms/inbox");
            CursorLoader loader = new CursorLoader(getApplicationContext(), getInbox, getColumns, null, null, null);
            return loader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 1) {
            //Swaps to the new Cursor
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Swaps to null
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Initializes the Setting menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Listeners for when the setting menu is clicked
        switch(item.getItemId()){
            case R.id.menu_item1 :
                Intent intent = new Intent(reading.this, setting.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
