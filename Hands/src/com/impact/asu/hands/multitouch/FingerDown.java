package com.impact.asu.hands.multitouch;

public class FingerDown {
	
	private boolean indexFinger, middleFinger, ringFinger, pinkyFinger, thumb;
	
	public FingerDown(boolean indexFinger, boolean middleFinger, boolean ringFinger, boolean pinkyFinger, boolean thumb){
		this.indexFinger = indexFinger;
		this.middleFinger = middleFinger;
		this.ringFinger = ringFinger;
		this.pinkyFinger = pinkyFinger;
		this.thumb = thumb;
	}

	public boolean isIndexFinger() {
		return indexFinger;
	}

	public void setIndexFinger(boolean indexFinger) {
		this.indexFinger = indexFinger;
	}

	public boolean isMiddleFinger() {
		return middleFinger;
	}

	public void setMiddleFinger(boolean middleFinger) {
		this.middleFinger = middleFinger;
	}

	public boolean isRingFinger() {
		return ringFinger;
	}

	public void setRingFinger(boolean ringFinger) {
		this.ringFinger = ringFinger;
	}

	public boolean isPinkyFinger() {
		return pinkyFinger;
	}

	public void setPinkyFinger(boolean pinkyFinger) {
		this.pinkyFinger = pinkyFinger;
	}

	public boolean isThumb() {
		return thumb;
	}

	public void setThumb(boolean thumb) {
		this.thumb = thumb;
	}
	
	@Override
	public String toString(){
		
		
		return  ( "" + ((indexFinger) ? 1 : 0) + ((middleFinger) ? 1 : 0) + ((ringFinger) ? 1 : 0) + ((pinkyFinger) ? 1 : 0) + ((thumb) ? 1 : 0)); 
	}
	

}
