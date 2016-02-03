package edu.uw.bn22.yama;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class compose extends AppCompatActivity {
    private static final String TAG = "SMS App";
    private static final int PICK_CONTACT_REQUEST = 1;
    private Uri data = Uri.parse("content://contacts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Button sender = (Button) findViewById(R.id.send);
        Button contacts = (Button) findViewById(R.id.btnSearch);

        //Initializes a search button that allows the user to search through the contacts of the phone
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, data);
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            }
        });

        FloatingActionButton reading = (FloatingActionButton) findViewById(R.id.fab2);
        reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(compose.this, reading.class);
                startActivity(intent);
            }
        });


        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sent = "SMS was sent";
                String delivered = "SMS was delivered";

                EditText inputAddress = (EditText) findViewById(R.id.userAddress);
                EditText inputMessage = (EditText) findViewById(R.id.userMessage);

                //Gets the inputs of the message and the phone number of the receiver
                String address = inputAddress.getText().toString();
                String message = inputMessage.getText().toString();

                PendingIntent send = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(sent), 0);
                PendingIntent deliver = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(delivered), 0);

                //Provides feedback to let the user know that their SMS message was sent
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (getResultCode() == Activity.RESULT_OK) {
                            Toast.makeText(context, "SMS was successfully sent", Toast.LENGTH_LONG).show();
                        } else if (getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE) {
                            Toast.makeText(context, "Generic Failure found", Toast.LENGTH_LONG).show();
                        } else if (getResultCode() == SmsManager.RESULT_ERROR_NO_SERVICE) {
                            Toast.makeText(context, "Service Unavailable", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new IntentFilter(sent));

                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (getResultCode() == Activity.RESULT_OK) {
                            Toast.makeText(context, "SMS was successfully delivered", Toast.LENGTH_LONG).show();
                        } else if (getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(context, "SMS was not successfully delivered", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new IntentFilter(delivered));

                //Sends the SMS to the targeted phone
                try {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(address, null, message, send, deliver);
                    Log.v(TAG, "SMS was sent");
                } catch (Exception e) {
                    Log.v(TAG, "SMS was not sent");
                }

                //Empties the targeted phone and message for the next SMS
                inputAddress.setText("");
                inputMessage.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Searches the clicked contact for their phone number
                String[] columns = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), columns, null, null, null);
                cursor.moveToFirst();
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                //Enters the clicked phone number as the targeted phone number
                EditText contactInfo = (EditText) findViewById(R.id.userAddress);
                contactInfo.setText(phone.replaceAll("\\D", ""));
            }
        }
    }
}
