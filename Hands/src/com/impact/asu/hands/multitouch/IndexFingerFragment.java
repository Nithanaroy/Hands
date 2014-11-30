package com.impact.asu.hands.multitouch;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.impact.asu.hands.R;

public class IndexFingerFragment extends Fragment {
	
	FingerDown fingerDown;
	
	public IndexFingerFragment() {
		
		this.fingerDown = null;
		System.out.print("No fingerDown object received in IndexFinger Constructor");
	}
	
	public IndexFingerFragment(FingerDown fingerDown) {
		

		this.fingerDown = fingerDown;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_indexfinger, null);
		
		ImageButton imageBtn = (ImageButton) rootView.findViewById(R.id.imageButtonIndex);
		
		imageBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x,y;
				
				x = event.getX();
				y = event.getY();
				
				switch(event.getAction()){
				
				case MotionEvent.ACTION_DOWN:
					Toast.makeText(getActivity(), "IndexDown, x: " + x + " y: " + y, Toast.LENGTH_SHORT).show();
					fingerDown.setIndexFinger(true);
					break;	
				case MotionEvent.ACTION_UP:
					Toast.makeText(getActivity(), "IndexUp, x: " + x + " y: " + y, Toast.LENGTH_SHORT).show();
					fingerDown.setIndexFinger(false);
					break;		
				}		
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		return rootView;
}
	
}
