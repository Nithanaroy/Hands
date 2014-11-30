package com.impact.asu.hands.multitouch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.impact.asu.hands.R;
//import com.impact.asu.hands.MenuFragment;

public class MultiTouchActivity extends ActionBarActivity {	
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 3;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_touch);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.multitouchmenu_container, new MultiTouchMenuFragment()).commit();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.option_menu, menu);
		return true;
	}
	
}
