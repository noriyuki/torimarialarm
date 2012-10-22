package jp.cluck.torimarialarm;

import java.util.HashSet;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;


public class VoiceData {
  public static final int VOICES[] = {
    R.raw.libetoribe,
    R.raw.rururururu,
    R.raw.oniichan,
    R.raw.onechan2,
    R.raw.gutennacht,
    R.raw.gutennacht2,
  };

  public static synchronized VoiceData getInstance(Context context) {
    if (sVoiceData == null) {
      sVoiceData = new VoiceData();
      sVoiceData.init(context);
    }
    return sVoiceData;
  }

  private void init(Context context) {
    mContext = context;
    mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    mLoaded = new HashSet<Integer>();
    mSoundIds = new int[VOICES.length];
    
    mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
      @Override
      public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
        mLoaded.add(Integer.valueOf(soundId));
        Log.i("Sound id=" + soundId + " loaded");
      }
    });
    for (int i = 0; i < VOICES.length; ++i) {
      Log.i("Loading sound @" + i);
      mSoundIds[i] = mSoundPool.load(context, VOICES[i], 1);
    }
    mRand = new Random();
  }

  public synchronized void playRandomly() {
    // With probability 0.9, the sound is libetoribe. The remaining 0.1 is
    // equally distributed to the remaining sounds.
    int index = 0;
    if (mRand.nextInt(100) >= 60) {
      index = 1 + mRand.nextInt(VOICES.length - 1);
    }
    final int soundId = mSoundIds[index];
    if (mLoaded.contains(Integer.valueOf(soundId))) {
      AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
      float actualVolume = (float) am.getStreamVolume(AudioManager.STREAM_MUSIC);
      float maxVolume = (float) am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      float volume = actualVolume / maxVolume;
      mSoundPool.play(soundId, volume, volume, 1, 0, 1f);
    }
  }

  private Context mContext = null;
  private SoundPool mSoundPool = null;
  private HashSet<Integer> mLoaded = null;
  private int[] mSoundIds = null;
  private Random mRand = null;

  private static VoiceData sVoiceData = null;
}
