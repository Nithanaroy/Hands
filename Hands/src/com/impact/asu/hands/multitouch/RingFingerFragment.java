package com.impact.asu.hands.multitouch;
import com.impact.asu.hands.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class RingFingerFragment extends Fragment {
	
	FingerDown fingerDown;
	
	public RingFingerFragment() {
		
		this.fingerDown = null;
		System.out.print("No fingerDown object received in RingFingerFragment Constructor");
	}
	
	public RingFingerFragment(FingerDown fingerDown) {
		

		this.fingerDown = fingerDown;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_ringfinger, null);
		
		ImageButton imageBtn = (ImageButton) rootView
				.findViewById(R.id.imageButtonRing);

		imageBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x, y;

				x = event.getX();
				y = event.getY();

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					Toast.makeText(getActivity(),
							"RingDown, x: " + x + " y: " + y,
							Toast.LENGTH_SHORT).show();
							fingerDown.setRingFinger(true);
					break;
				case MotionEvent.ACTION_UP:
					Toast.makeText(getActivity(),
							"RingUp, x: " + x + " y: " + y, Toast.LENGTH_SHORT)
							.show();
						fingerDown.setRingFinger(false);
					break;
				}
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		return rootView;
	}
	
}
