package com.impact.asu.pocket;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asu.impact.asuservices.BluetoothService;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "BluetoothPocket";
	private static final boolean D = true;

	private BluetoothService mChatService = null;

	private final int REQUEST_ENABLE_BT = 1;

	// UI Components
	private Button mSendButton;
	private Button mDatabaseActivity;
	private EditText mOutEditText;
	private TextView incomingMessage;
	private String lastMessage;
	private int counter;

	BluetoothAdapter mBluetoothAdapter = null;
	
	private String mConnectedDeviceName = null;

	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//initialize some variables
		counter = 0;
		lastMessage = "init";
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		mDatabaseActivity = (Button) findViewById(R.id.database_Display);
		mDatabaseActivity.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//opens the new activity TODO implement
				Intent intent = new Intent(getApplicationContext(), DatabaseActivity.class); 
				startActivity(intent);
			}
		});
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupStage();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		ensureDiscoverable();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private void setupStage() {
		Log.d(TAG, "setupStage()");

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});

		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		incomingMessage = (TextView) findViewById(R.id.incoming_msg);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothService.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				incomingMessage.setText("Sent:  " + writeMessage);
				break;
			case BluetoothService.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
			
				//use accelerometer data to detect a begin gesture // alternatively can use simple finger data
				
				//TODO : Simplistic
				/*
				 * 1.Detect an intended sign
				 * 2.Save string to a class variable
				 * 3.Start a timer 
				 * 4.Confirm after 5 seconds 
				 */
				
				if(lastMessage!= null && lastMessage.equals(readMessage.toString())){
					counter++;
					if (counter > 10){
						
						if(readMessage.contains("00000")) incomingMessage.setText(mConnectedDeviceName + ":  " + "getting data ...");
						else if(readMessage.contains("00110")) incomingMessage.setText(mConnectedDeviceName + ":  " + "getting data");
						else if(readMessage.contains("01110"))incomingMessage.setText(mConnectedDeviceName + ":  " + "Last Gesture detected : sorry ");
						else if(readMessage.contains("01010"))incomingMessage.setText(mConnectedDeviceName + ":  " + "Last Gesture detected : bye bye ");
						else if(readMessage.contains("11100"))incomingMessage.setText(mConnectedDeviceName + ":  " + "Last Gesture detected : Jumping detected");
						else if(readMessage.contains("11110"))incomingMessage.setText(mConnectedDeviceName + ":  " + "Last Gesture detected : hello ");
						counter = 0;
					}
					
					
				}
				if(lastMessage.equals("init"))incomingMessage.setText(mConnectedDeviceName + ":  " + "unable to detect sign/gathering data");
				lastMessage = readMessage.toString();
				
				//incomingMessage.setText(mConnectedDeviceName + ":  " + readMessage);
				break;
			case BluetoothService.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case BluetoothService.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothService.TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupStage();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}