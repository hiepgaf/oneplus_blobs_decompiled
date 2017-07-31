package android.media;

import android.util.Log;

public class MediaActionSound
{
  public static final int FOCUS_COMPLETE = 1;
  private static final int NUM_MEDIA_SOUND_STREAMS = 1;
  public static final int SHUTTER_CLICK = 0;
  private static final String[] SOUND_FILES = { "/system/media/audio/ui/camera_click.ogg", "/system/media/audio/ui/camera_focus.ogg", "/system/media/audio/ui/VideoRecord.ogg", "/system/media/audio/ui/VideoStop.ogg" };
  public static final int START_VIDEO_RECORDING = 2;
  private static final int STATE_LOADED = 3;
  private static final int STATE_LOADING = 1;
  private static final int STATE_LOADING_PLAY_REQUESTED = 2;
  private static final int STATE_NOT_LOADED = 0;
  public static final int STOP_VIDEO_RECORDING = 3;
  private static final String TAG = "MediaActionSound";
  private SoundPool.OnLoadCompleteListener mLoadCompleteListener = new SoundPool.OnLoadCompleteListener()
  {
    public void onLoadComplete(SoundPool paramAnonymousSoundPool, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      MediaActionSound.SoundState[] arrayOfSoundState = MediaActionSound.-get0(MediaActionSound.this);
      int j = arrayOfSoundState.length;
      int i = 0;
      MediaActionSound.SoundState localSoundState;
      while (i < j)
      {
        localSoundState = arrayOfSoundState[i];
        if (localSoundState.id != paramAnonymousInt1)
        {
          i += 1;
        }
        else
        {
          paramAnonymousInt1 = 0;
          if (paramAnonymousInt2 == 0) {}
        }
        try
        {
          localSoundState.state = 0;
          localSoundState.id = 0;
          Log.e("MediaActionSound", "OnLoadCompleteListener() error: " + paramAnonymousInt2 + " loading sound: " + localSoundState.name);
          return;
        }
        finally {}
        switch (localSoundState.state)
        {
        }
      }
      for (;;)
      {
        Log.e("MediaActionSound", "OnLoadCompleteListener() called in wrong state: " + localSoundState.state + " for sound: " + localSoundState.name);
        for (;;)
        {
          if (paramAnonymousInt1 != 0) {
            paramAnonymousSoundPool.play(paramAnonymousInt1, 1.0F, 1.0F, 0, 0, 1.0F);
          }
          return;
          localSoundState.state = 3;
          continue;
          paramAnonymousInt1 = localSoundState.id;
          localSoundState.state = 3;
        }
      }
    }
  };
  private SoundPool mSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setFlags(1).setContentType(4).build()).build();
  private SoundState[] mSounds;
  
  public MediaActionSound()
  {
    this.mSoundPool.setOnLoadCompleteListener(this.mLoadCompleteListener);
    this.mSounds = new SoundState[SOUND_FILES.length];
    int i = 0;
    while (i < this.mSounds.length)
    {
      this.mSounds[i] = new SoundState(i);
      i += 1;
    }
  }
  
  private int loadSound(SoundState paramSoundState)
  {
    int i = 0;
    if (this.mSoundPool != null) {
      i = this.mSoundPool.load(SOUND_FILES[paramSoundState.name], 1);
    }
    if (i > 0)
    {
      paramSoundState.state = 1;
      paramSoundState.id = i;
    }
    return i;
  }
  
  public void load(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= SOUND_FILES.length)) {
      throw new RuntimeException("Unknown sound requested: " + paramInt);
    }
    for (;;)
    {
      synchronized (this.mSounds[paramInt])
      {
        switch (???.state)
        {
        case 0: 
          Log.e("MediaActionSound", "load() called in wrong state: " + ??? + " for sound: " + paramInt);
          return;
          if (loadSound(???) <= 0) {
            Log.e("MediaActionSound", "load() error loading sound: " + paramInt);
          }
          break;
        }
      }
    }
  }
  
  public void play(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= SOUND_FILES.length)) {
      throw new RuntimeException("Unknown sound requested: " + paramInt);
    }
    for (;;)
    {
      synchronized (this.mSounds[paramInt])
      {
        switch (???.state)
        {
        case 2: 
          Log.e("MediaActionSound", "play() called in wrong state: " + ???.state + " for sound: " + paramInt);
          return;
        case 0: 
          loadSound(???);
          if (loadSound(???) <= 0) {
            Log.e("MediaActionSound", "play() error loading sound: " + paramInt);
          }
          break;
        }
      }
      ???.state = 2;
      continue;
      this.mSoundPool.play(???.id, 1.0F, 1.0F, 0, 0, 1.0F);
    }
  }
  
  public void release()
  {
    int i = 0;
    if (this.mSoundPool != null)
    {
      SoundState[] arrayOfSoundState = this.mSounds;
      int j = arrayOfSoundState.length;
      for (;;)
      {
        if (i < j) {}
        synchronized (arrayOfSoundState[i])
        {
          ???.state = 0;
          ???.id = 0;
          i += 1;
        }
      }
    }
  }
  
  private class SoundState
  {
    public int id;
    public final int name;
    public int state;
    
    public SoundState(int paramInt)
    {
      this.name = paramInt;
      this.id = 0;
      this.state = 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaActionSound.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */