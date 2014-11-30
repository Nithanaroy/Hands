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

public class PinkyFingerFragment extends Fragment {

	FingerDown fingerDown;

	public PinkyFingerFragment() {

		this.fingerDown = null;
		System.out
				.print("No fingerDown object received in PinkyFingerFragment Constructor");
	}

	public PinkyFingerFragment(FingerDown fingerDown) {

		this.fingerDown = fingerDown;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_pinkyfinger, null);

		ImageButton imageBtn = (ImageButton) rootView
				.findViewById(R.id.imageButtonPinky);

		imageBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x, y;

				x = event.getX();
				y = event.getY();

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					Toast.makeText(getActivity(),
							"PinkyDown, x: " + x + " y: " + y,
							Toast.LENGTH_SHORT).show();
					fingerDown.setPinkyFinger(true);
					break;
				case MotionEvent.ACTION_UP:
					Toast.makeText(getActivity(),
							"PinkyUp, x: " + x + " y: " + y, Toast.LENGTH_SHORT)
							.show();
					fingerDown.setPinkyFinger(false);
					break;
				}
				// TODO Auto-generated method stub
				return true;
			}
		});

		return rootView;
	}

}
