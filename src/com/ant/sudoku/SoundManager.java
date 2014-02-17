package com.ant.sudoku;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	
	static final int SOUND_CLICK_ID = 1;
	static final int SOUND_CLICK_LR_ID = 2;
	static final int SOUND_CLICK_SELECT_ID = 3;
	
	public boolean soundOn;
	private  SoundPool mSoundPool; 
	private  HashMap<Integer, Integer> mSoundPoolMap; 
	private  AudioManager  mAudioManager;
	private  Context mContext;
	
	public SoundManager() {
	}
		
	public void initSounds(Context theContext) {
		soundOn = true;
		mContext = theContext;
	    mSoundPool = new SoundPool(4, AudioManager.STREAM_NOTIFICATION, 0); 
	    mSoundPoolMap = new HashMap<Integer, Integer>(); 
	    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	} 
	
	public void addSound(int index,int SoundID) {
		mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index) {
		if (!soundOn) return;
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION); 
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
	    mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f); 
	}
	
	public void playLoopedSound(int index) {
		if (!soundOn) return;
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
	    mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f); 
	}
	
}