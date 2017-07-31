package android.support.v4.media;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.support.v4.view.KeyEventCompat;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.View;
import android.view.Window;
import java.util.ArrayList;

public class TransportMediator
  extends TransportController
{
  public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
  public static final int FLAG_KEY_MEDIA_NEXT = 128;
  public static final int FLAG_KEY_MEDIA_PAUSE = 16;
  public static final int FLAG_KEY_MEDIA_PLAY = 4;
  public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
  public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
  public static final int FLAG_KEY_MEDIA_REWIND = 2;
  public static final int FLAG_KEY_MEDIA_STOP = 32;
  public static final int KEYCODE_MEDIA_PAUSE = 127;
  public static final int KEYCODE_MEDIA_PLAY = 126;
  public static final int KEYCODE_MEDIA_RECORD = 130;
  final AudioManager mAudioManager;
  final TransportPerformer mCallbacks;
  final Context mContext;
  final TransportMediatorJellybeanMR2 mController;
  final Object mDispatcherState;
  final KeyEvent.Callback mKeyEventCallback = new KeyEvent.Callback()
  {
    public boolean onKeyDown(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (!TransportMediator.isMediaKey(paramAnonymousInt)) {
        return false;
      }
      return TransportMediator.this.mCallbacks.onMediaButtonDown(paramAnonymousInt, paramAnonymousKeyEvent);
    }
    
    public boolean onKeyLongPress(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyMultiple(int paramAnonymousInt1, int paramAnonymousInt2, KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyUp(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (!TransportMediator.isMediaKey(paramAnonymousInt)) {
        return false;
      }
      return TransportMediator.this.mCallbacks.onMediaButtonUp(paramAnonymousInt, paramAnonymousKeyEvent);
    }
  };
  final ArrayList<TransportStateListener> mListeners = new ArrayList();
  final TransportMediatorCallback mTransportKeyCallback = new TransportMediatorCallback()
  {
    public long getPlaybackPosition()
    {
      return TransportMediator.this.mCallbacks.onGetCurrentPosition();
    }
    
    public void handleAudioFocusChange(int paramAnonymousInt)
    {
      TransportMediator.this.mCallbacks.onAudioFocusChange(paramAnonymousInt);
    }
    
    public void handleKey(KeyEvent paramAnonymousKeyEvent)
    {
      paramAnonymousKeyEvent.dispatch(TransportMediator.this.mKeyEventCallback);
    }
    
    public void playbackPositionUpdate(long paramAnonymousLong)
    {
      TransportMediator.this.mCallbacks.onSeekTo(paramAnonymousLong);
    }
  };
  final View mView;
  
  public TransportMediator(Activity paramActivity, TransportPerformer paramTransportPerformer)
  {
    this(paramActivity, null, paramTransportPerformer);
  }
  
  private TransportMediator(Activity paramActivity, View paramView, TransportPerformer paramTransportPerformer)
  {
    Object localObject;
    if (paramActivity == null)
    {
      localObject = paramView.getContext();
      this.mContext = ((Context)localObject);
      this.mCallbacks = paramTransportPerformer;
      this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
      if (paramActivity != null) {
        break label116;
      }
    }
    for (;;)
    {
      this.mView = paramView;
      this.mDispatcherState = KeyEventCompat.getKeyDispatcherState(this.mView);
      if (Build.VERSION.SDK_INT >= 18) {
        break label127;
      }
      this.mController = null;
      return;
      localObject = paramActivity;
      break;
      label116:
      paramView = paramActivity.getWindow().getDecorView();
    }
    label127:
    this.mController = new TransportMediatorJellybeanMR2(this.mContext, this.mAudioManager, this.mView, this.mTransportKeyCallback);
  }
  
  public TransportMediator(View paramView, TransportPerformer paramTransportPerformer)
  {
    this(null, paramView, paramTransportPerformer);
  }
  
  private TransportStateListener[] getListeners()
  {
    if (this.mListeners.size() > 0)
    {
      TransportStateListener[] arrayOfTransportStateListener = new TransportStateListener[this.mListeners.size()];
      this.mListeners.toArray(arrayOfTransportStateListener);
      return arrayOfTransportStateListener;
    }
    return null;
  }
  
  static boolean isMediaKey(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private void pushControllerState()
  {
    if (this.mController == null) {
      return;
    }
    this.mController.refreshState(this.mCallbacks.onIsPlaying(), this.mCallbacks.onGetCurrentPosition(), this.mCallbacks.onGetTransportControlFlags());
  }
  
  private void reportPlayingChanged()
  {
    TransportStateListener[] arrayOfTransportStateListener = getListeners();
    if (arrayOfTransportStateListener == null) {}
    for (;;)
    {
      return;
      int j = arrayOfTransportStateListener.length;
      int i = 0;
      while (i < j)
      {
        arrayOfTransportStateListener[i].onPlayingChanged(this);
        i += 1;
      }
    }
  }
  
  private void reportTransportControlsChanged()
  {
    TransportStateListener[] arrayOfTransportStateListener = getListeners();
    if (arrayOfTransportStateListener == null) {}
    for (;;)
    {
      return;
      int j = arrayOfTransportStateListener.length;
      int i = 0;
      while (i < j)
      {
        arrayOfTransportStateListener[i].onTransportControlsChanged(this);
        i += 1;
      }
    }
  }
  
  public void destroy()
  {
    this.mController.destroy();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return KeyEventCompat.dispatch(paramKeyEvent, this.mKeyEventCallback, this.mDispatcherState, this);
  }
  
  public int getBufferPercentage()
  {
    return this.mCallbacks.onGetBufferPercentage();
  }
  
  public long getCurrentPosition()
  {
    return this.mCallbacks.onGetCurrentPosition();
  }
  
  public long getDuration()
  {
    return this.mCallbacks.onGetDuration();
  }
  
  public Object getRemoteControlClient()
  {
    if (this.mController == null) {
      return null;
    }
    return this.mController.getRemoteControlClient();
  }
  
  public int getTransportControlFlags()
  {
    return this.mCallbacks.onGetTransportControlFlags();
  }
  
  public boolean isPlaying()
  {
    return this.mCallbacks.onIsPlaying();
  }
  
  public void pausePlaying()
  {
    if (this.mController == null) {}
    for (;;)
    {
      this.mCallbacks.onPause();
      pushControllerState();
      reportPlayingChanged();
      return;
      this.mController.pausePlaying();
    }
  }
  
  public void refreshState()
  {
    pushControllerState();
    reportPlayingChanged();
    reportTransportControlsChanged();
  }
  
  public void registerStateListener(TransportStateListener paramTransportStateListener)
  {
    this.mListeners.add(paramTransportStateListener);
  }
  
  public void seekTo(long paramLong)
  {
    this.mCallbacks.onSeekTo(paramLong);
  }
  
  public void startPlaying()
  {
    if (this.mController == null) {}
    for (;;)
    {
      this.mCallbacks.onStart();
      pushControllerState();
      reportPlayingChanged();
      return;
      this.mController.startPlaying();
    }
  }
  
  public void stopPlaying()
  {
    if (this.mController == null) {}
    for (;;)
    {
      this.mCallbacks.onStop();
      pushControllerState();
      reportPlayingChanged();
      return;
      this.mController.stopPlaying();
    }
  }
  
  public void unregisterStateListener(TransportStateListener paramTransportStateListener)
  {
    this.mListeners.remove(paramTransportStateListener);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/TransportMediator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */