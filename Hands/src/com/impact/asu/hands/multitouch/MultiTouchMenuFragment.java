package com.impact.asu.hands.multitouch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asu.impact.asuservices.BluetoothService;
import com.impact.asu.hands.DeviceListActivity;
import com.impact.asu.hands.R;


/*TODO
 * 1. Get another button beside the fingers
 * 2. Associate this button with sending bluetooth
 * 3. Test the button 
 * 		3.1 Sych pocket
 * 		3.2 Receive message
 */
public class MultiTouchMenuFragment extends Fragment {

	//Overall
	private static final String TAG = "BluetoothHandsMain";
	private static final boolean D = true;

	//Bluetooth related
	private BluetoothService mChatService = null;
	BluetoothAdapter mBluetoothAdapter = null;
	private Button mSendButton;
	private String mConnectedDeviceName = null;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 3;

	Fragment frag;
	FragmentTransaction fragTransaction;
	FingerDown fingerDown;
	TextView binaryTouchString;
	View view;
	private static int i;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		fingerDown = new FingerDown(false, false, false, false, false);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getActivity()  , "Bluetooth is not available", Toast.LENGTH_LONG).show();
			//finish();
			return;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(getActivity(), DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(getActivity(), DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		}
		return false;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.option_menu, menu);
	
	}
		
	public MultiTouchMenuFragment() {
		// TODO Auto-generated constructor stub
	}

	private void updateTestEditText() {
		if (binaryTouchString != null){
			binaryTouchString.setText(fingerDown.toString());
		}
		
	}
	
	private void composeMessage() {
		sendMessage(fingerDown.toString());
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		
		setHasOptionsMenu(true);
		view = inflater
				.inflate(R.layout.menu_multitouch, container, false);

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
				;
				setupStage();
		}
		
		
		Button buttonIndexFinger = (Button) view
				.findViewById(R.id.buttonIndexFinger);
		Button buttonMiddleFinger = (Button) view
				.findViewById(R.id.buttonMiddleFinger);
		Button buttonRingFinger = (Button) view
				.findViewById(R.id.buttonRingFinger);
		Button buttonPinkieFinger = (Button) view
				.findViewById(R.id.buttonPinkieFinger);
		Button mSendButton = (Button) view.findViewById(R.id.buttonSendTouchString);
		binaryTouchString = (TextView) view.findViewById(R.id.binaryTouchString);
		
		
		//create thread to update textview 
		//TODO remove ME as thsi is for viewing purposes only
		Thread t = new Thread() {

			  @Override
			  public void run() {
			    try {
			      while (!isInterrupted()) {
			        Thread.sleep(1000);
			        getActivity().runOnUiThread(new Runnable() {
			          @Override
			          public void run() {
			        	 
			        	  binaryTouchString.setText("Update!!!" + i++);
			        	  composeMessage();
			          }

					
			        });
			      }
			    } catch (InterruptedException e) {
			    }
			  }
			};

			t.start();
		
		
		buttonIndexFinger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				frag = new IndexFingerFragment(fingerDown);
				fragTransaction = getFragmentManager().beginTransaction()
						.replace(R.id.index_finger_container, frag);
				fragTransaction.commit();

			}
		});

		buttonMiddleFinger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				frag = new MiddleFingerFragment(fingerDown);
				fragTransaction = getFragmentManager().beginTransaction()
						.replace(R.id.middle_finger_container, frag);
				fragTransaction.commit();

			}
		});

		buttonRingFinger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				frag = new RingFingerFragment(fingerDown);
				fragTransaction = getFragmentManager().beginTransaction()
						.replace(R.id.ring_finger_container, frag);
				fragTransaction.commit();

			}
		});

		buttonPinkieFinger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				frag = new PinkyFingerFragment(fingerDown);
				fragTransaction = getFragmentManager().beginTransaction()
						.replace(R.id.pinky_finger_container, frag);
				fragTransaction.commit();

			}
		});

		return view;
	}

	
	private void setupStage() {
		Log.d(TAG, "setupStage()");

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) view.findViewById(R.id.buttonSendTouchString);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				//TextView view = (TextView) v.findViewById(R.id.edit_text_out);
				//String message = view.getText().toString();
				String message = "testMessage";
				sendMessage(message);
			}
		});

		//mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
		//incomingMessage = (TextView) view.findViewById(R.id.incoming_msg);

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothService(getActivity(), mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}
	
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			//mOutEditText.setText(mOutStringBuffer);
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
				binaryTouchString.setText("Sent:  " + writeMessage);
				break;
			case BluetoothService.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				binaryTouchString.setText(mConnectedDeviceName + ":  " + readMessage);
				break;
			case BluetoothService.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
				Toast.makeText(getActivity(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case BluetoothService.MESSAGE_TOAST:
				Toast.makeText(getActivity(), msg.getData().getString(BluetoothService.TOAST), Toast.LENGTH_SHORT).show();
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
			if (resultCode == getActivity().RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupStage();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
			break;
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == getActivity().RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == getActivity().RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		}

	}
	
	
	private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
	
	
}
