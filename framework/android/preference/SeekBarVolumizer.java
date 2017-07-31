package android.preference;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.internal.annotations.GuardedBy;

public class SeekBarVolumizer
  implements SeekBar.OnSeekBarChangeListener, Handler.Callback
{
  private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
  private static final int MSG_INIT_SAMPLE = 3;
  private static final int MSG_SET_STREAM_VOLUME = 0;
  private static final int MSG_START_SAMPLE = 1;
  private static final int MSG_STOP_SAMPLE = 2;
  private static final String TAG = "SeekBarVolumizer";
  private boolean mAffectedByRingerMode;
  private final AudioManager mAudioManager;
  private final Callback mCallback;
  private final Context mContext;
  private final Uri mDefaultUri;
  private Handler mHandler;
  private int mLastAudibleStreamVolume;
  private int mLastProgress = -1;
  private final int mMaxStreamVolume;
  private boolean mMuted;
  private final NotificationManager mNotificationManager;
  private boolean mNotificationOrRing;
  private int mOriginalStreamVolume;
  private final Receiver mReceiver = new Receiver(null);
  private int mRingerMode;
  @GuardedBy("this")
  private Ringtone mRingtone;
  private SeekBar mSeekBar;
  private final int mStreamType;
  private final H mUiHandler = new H(null);
  private int mVolumeBeforeMute = -1;
  private Observer mVolumeObserver;
  private int mZenMode;
  
  public SeekBarVolumizer(Context paramContext, int paramInt, Uri paramUri, Callback paramCallback)
  {
    this.mContext = paramContext;
    this.mAudioManager = ((AudioManager)paramContext.getSystemService(AudioManager.class));
    this.mNotificationManager = ((NotificationManager)paramContext.getSystemService(NotificationManager.class));
    this.mStreamType = paramInt;
    this.mAffectedByRingerMode = this.mAudioManager.isStreamAffectedByRingerMode(this.mStreamType);
    this.mNotificationOrRing = isNotificationOrRing(this.mStreamType);
    if (this.mNotificationOrRing) {
      this.mRingerMode = this.mAudioManager.getRingerModeInternal();
    }
    this.mZenMode = this.mNotificationManager.getZenMode();
    this.mMaxStreamVolume = this.mAudioManager.getStreamMaxVolume(this.mStreamType);
    this.mCallback = paramCallback;
    this.mOriginalStreamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
    this.mLastAudibleStreamVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
    this.mMuted = this.mAudioManager.isStreamMute(this.mStreamType);
    if (this.mCallback != null) {
      this.mCallback.onMuted(this.mMuted, isZenMuted());
    }
    paramContext = paramUri;
    if (paramUri == null)
    {
      if (this.mStreamType != 2) {
        break label245;
      }
      paramContext = Settings.System.DEFAULT_RINGTONE_URI;
    }
    for (;;)
    {
      this.mDefaultUri = paramContext;
      return;
      label245:
      if (this.mStreamType == 5) {
        paramContext = Settings.System.DEFAULT_NOTIFICATION_URI;
      } else {
        paramContext = Settings.System.DEFAULT_ALARM_ALERT_URI;
      }
    }
  }
  
  private static boolean isNotificationOrRing(int paramInt)
  {
    return (paramInt == 2) || (paramInt == 5);
  }
  
  private boolean isZenMuted()
  {
    if ((this.mNotificationOrRing) && (this.mZenMode == 3)) {}
    while (this.mZenMode == 2) {
      return true;
    }
    return false;
  }
  
  private void onInitSample()
  {
    try
    {
      this.mRingtone = RingtoneManager.getRingtone(this.mContext, this.mDefaultUri);
      if (this.mRingtone != null) {
        this.mRingtone.setStreamType(this.mStreamType);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  private void onStartSample()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 232	android/preference/SeekBarVolumizer:isSamplePlaying	()Z
    //   4: ifne +70 -> 74
    //   7: aload_0
    //   8: getfield 89	android/preference/SeekBarVolumizer:mCallback	Landroid/preference/SeekBarVolumizer$Callback;
    //   11: ifnull +13 -> 24
    //   14: aload_0
    //   15: getfield 89	android/preference/SeekBarVolumizer:mCallback	Landroid/preference/SeekBarVolumizer$Callback;
    //   18: aload_0
    //   19: invokeinterface 235 2 0
    //   24: aload_0
    //   25: monitorenter
    //   26: aload_0
    //   27: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   30: astore_1
    //   31: aload_1
    //   32: ifnull +40 -> 72
    //   35: aload_0
    //   36: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   39: new 237	android/media/AudioAttributes$Builder
    //   42: dup
    //   43: aload_0
    //   44: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   47: invokevirtual 241	android/media/Ringtone:getAudioAttributes	()Landroid/media/AudioAttributes;
    //   50: invokespecial 244	android/media/AudioAttributes$Builder:<init>	(Landroid/media/AudioAttributes;)V
    //   53: sipush 192
    //   56: invokevirtual 248	android/media/AudioAttributes$Builder:setFlags	(I)Landroid/media/AudioAttributes$Builder;
    //   59: invokevirtual 251	android/media/AudioAttributes$Builder:build	()Landroid/media/AudioAttributes;
    //   62: invokevirtual 254	android/media/Ringtone:setAudioAttributes	(Landroid/media/AudioAttributes;)V
    //   65: aload_0
    //   66: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   69: invokevirtual 257	android/media/Ringtone:play	()V
    //   72: aload_0
    //   73: monitorexit
    //   74: return
    //   75: astore_1
    //   76: ldc 35
    //   78: new 259	java/lang/StringBuilder
    //   81: dup
    //   82: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   85: ldc_w 262
    //   88: invokevirtual 266	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_0
    //   92: getfield 114	android/preference/SeekBarVolumizer:mStreamType	I
    //   95: invokevirtual 269	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   98: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: aload_1
    //   102: invokestatic 279	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   105: pop
    //   106: goto -34 -> 72
    //   109: astore_1
    //   110: aload_0
    //   111: monitorexit
    //   112: aload_1
    //   113: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	SeekBarVolumizer
    //   30	2	1	localRingtone	Ringtone
    //   75	27	1	localThrowable	Throwable
    //   109	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   35	72	75	java/lang/Throwable
    //   26	31	109	finally
    //   35	72	109	finally
    //   76	106	109	finally
  }
  
  private void onStopSample()
  {
    try
    {
      if (this.mRingtone != null) {
        this.mRingtone.stop();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void postSetVolume(int paramInt)
  {
    if (this.mHandler == null) {
      return;
    }
    this.mLastProgress = paramInt;
    this.mHandler.removeMessages(0);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(0));
  }
  
  private void postStartSample()
  {
    if (this.mHandler == null) {
      return;
    }
    this.mHandler.removeMessages(1);
    Handler localHandler = this.mHandler;
    Message localMessage = this.mHandler.obtainMessage(1);
    if (isSamplePlaying()) {}
    for (int i = 1000;; i = 0)
    {
      localHandler.sendMessageDelayed(localMessage, i);
      return;
    }
  }
  
  private void postStopSample()
  {
    if (this.mHandler == null) {
      return;
    }
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
  }
  
  private void updateSlider()
  {
    if ((this.mSeekBar != null) && (this.mAudioManager != null))
    {
      int i = this.mAudioManager.getStreamVolume(this.mStreamType);
      int j = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
      boolean bool = this.mAudioManager.isStreamMute(this.mStreamType);
      this.mUiHandler.postUpdateSlider(i, j, bool);
    }
  }
  
  public void changeVolumeBy(int paramInt)
  {
    this.mSeekBar.incrementProgressBy(paramInt);
    postSetVolume(this.mSeekBar.getProgress());
    postStartSample();
    this.mVolumeBeforeMute = -1;
  }
  
  public SeekBar getSeekBar()
  {
    return this.mSeekBar;
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      Log.e("SeekBarVolumizer", "invalid SeekBarVolumizer message: " + paramMessage.what);
    }
    for (;;)
    {
      return true;
      if ((this.mMuted) && (this.mLastProgress > 0)) {
        this.mAudioManager.adjustStreamVolume(this.mStreamType, 100, 0);
      }
      for (;;)
      {
        this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 1024);
        break;
        if ((!this.mMuted) && (this.mLastProgress == 0)) {
          this.mAudioManager.adjustStreamVolume(this.mStreamType, -100, 0);
        }
      }
      onStartSample();
      continue;
      onStopSample();
      continue;
      onInitSample();
    }
  }
  
  /* Error */
  public boolean isSamplePlaying()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   6: ifnull +15 -> 21
    //   9: aload_0
    //   10: getfield 220	android/preference/SeekBarVolumizer:mRingtone	Landroid/media/Ringtone;
    //   13: invokevirtual 352	android/media/Ringtone:isPlaying	()Z
    //   16: istore_1
    //   17: aload_0
    //   18: monitorexit
    //   19: iload_1
    //   20: ireturn
    //   21: iconst_0
    //   22: istore_1
    //   23: goto -6 -> 17
    //   26: astore_2
    //   27: aload_0
    //   28: monitorexit
    //   29: aload_2
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	SeekBarVolumizer
    //   16	7	1	bool	boolean
    //   26	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	17	26	finally
  }
  
  public void muteVolume()
  {
    if (this.mVolumeBeforeMute != -1)
    {
      this.mSeekBar.setProgress(this.mVolumeBeforeMute);
      postSetVolume(this.mVolumeBeforeMute);
      postStartSample();
      this.mVolumeBeforeMute = -1;
      return;
    }
    this.mVolumeBeforeMute = this.mSeekBar.getProgress();
    this.mSeekBar.setProgress(0);
    postStopSample();
    postSetVolume(0);
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      postSetVolume(paramInt);
    }
    if (this.mCallback != null) {
      this.mCallback.onProgressChanged(paramSeekBar, paramInt, paramBoolean);
    }
  }
  
  public void onRestoreInstanceState(VolumePreference.VolumeStore paramVolumeStore)
  {
    if (paramVolumeStore.volume != -1)
    {
      this.mOriginalStreamVolume = paramVolumeStore.originalVolume;
      this.mLastProgress = paramVolumeStore.volume;
      postSetVolume(this.mLastProgress);
    }
  }
  
  public void onSaveInstanceState(VolumePreference.VolumeStore paramVolumeStore)
  {
    if (this.mLastProgress >= 0)
    {
      paramVolumeStore.volume = this.mLastProgress;
      paramVolumeStore.originalVolume = this.mOriginalStreamVolume;
    }
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar) {}
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    postStartSample();
  }
  
  public void revertVolume()
  {
    this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalStreamVolume, 0);
  }
  
  public void setSeekBar(SeekBar paramSeekBar)
  {
    if (this.mSeekBar != null) {
      this.mSeekBar.setOnSeekBarChangeListener(null);
    }
    this.mSeekBar = paramSeekBar;
    this.mSeekBar.setOnSeekBarChangeListener(null);
    this.mSeekBar.setMax(this.mMaxStreamVolume);
    updateSeekBar();
    this.mSeekBar.setOnSeekBarChangeListener(this);
  }
  
  public void start()
  {
    if (this.mHandler != null) {
      return;
    }
    HandlerThread localHandlerThread = new HandlerThread("SeekBarVolumizer.CallbackHandler");
    localHandlerThread.start();
    this.mHandler = new Handler(localHandlerThread.getLooper(), this);
    this.mHandler.sendEmptyMessage(3);
    this.mVolumeObserver = new Observer(this.mHandler);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS[this.mStreamType]), false, this.mVolumeObserver);
    this.mReceiver.setListening(true);
  }
  
  public void startSample()
  {
    postStartSample();
  }
  
  public void stop()
  {
    if (this.mHandler == null) {
      return;
    }
    postStopSample();
    this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
    this.mReceiver.setListening(false);
    this.mSeekBar.setOnSeekBarChangeListener(null);
    this.mHandler.getLooper().quitSafely();
    this.mHandler = null;
    this.mVolumeObserver = null;
  }
  
  public void stopSample()
  {
    postStopSample();
  }
  
  protected void updateSeekBar()
  {
    boolean bool2 = isZenMuted();
    SeekBar localSeekBar = this.mSeekBar;
    if (bool2) {}
    for (boolean bool1 = false;; bool1 = true)
    {
      localSeekBar.setEnabled(bool1);
      if (!bool2) {
        break;
      }
      this.mSeekBar.setProgress(this.mLastAudibleStreamVolume);
      return;
    }
    if ((this.mNotificationOrRing) && (this.mRingerMode == 1))
    {
      this.mSeekBar.setProgress(0);
      return;
    }
    if (this.mMuted)
    {
      this.mSeekBar.setProgress(0);
      return;
    }
    localSeekBar = this.mSeekBar;
    if (this.mLastProgress > -1) {}
    for (int i = this.mLastProgress;; i = this.mOriginalStreamVolume)
    {
      localSeekBar.setProgress(i);
      return;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onMuted(boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onSampleStarting(SeekBarVolumizer paramSeekBarVolumizer);
  }
  
  private final class H
    extends Handler
  {
    private static final int UPDATE_SLIDER = 1;
    
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      if ((paramMessage.what == 1) && (SeekBarVolumizer.-get8(SeekBarVolumizer.this) != null))
      {
        SeekBarVolumizer.-set1(SeekBarVolumizer.this, paramMessage.arg1);
        SeekBarVolumizer.-set0(SeekBarVolumizer.this, Math.abs(paramMessage.arg2));
        if (paramMessage.arg2 >= 0) {
          break label118;
        }
      }
      label118:
      for (boolean bool = true;; bool = false)
      {
        if (bool != SeekBarVolumizer.-get5(SeekBarVolumizer.this))
        {
          SeekBarVolumizer.-set2(SeekBarVolumizer.this, bool);
          if (SeekBarVolumizer.-get2(SeekBarVolumizer.this) != null) {
            SeekBarVolumizer.-get2(SeekBarVolumizer.this).onMuted(SeekBarVolumizer.-get5(SeekBarVolumizer.this), SeekBarVolumizer.-wrap1(SeekBarVolumizer.this));
          }
        }
        SeekBarVolumizer.this.updateSeekBar();
        return;
      }
    }
    
    public void postUpdateSlider(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = -1;; i = 1)
      {
        obtainMessage(1, paramInt1, paramInt2 * i).sendToTarget();
        return;
      }
    }
  }
  
  private final class Observer
    extends ContentObserver
  {
    public Observer(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      SeekBarVolumizer.-wrap2(SeekBarVolumizer.this);
    }
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private boolean mListening;
    
    private Receiver() {}
    
    private void updateVolumeSlider(int paramInt1, int paramInt2)
    {
      boolean bool;
      if (SeekBarVolumizer.-get7(SeekBarVolumizer.this))
      {
        bool = SeekBarVolumizer.-wrap0(paramInt1);
        if ((SeekBarVolumizer.-get8(SeekBarVolumizer.this) != null) && (bool) && (paramInt2 != -1))
        {
          if (SeekBarVolumizer.-get1(SeekBarVolumizer.this).isStreamMute(SeekBarVolumizer.-get9(SeekBarVolumizer.this))) {
            break label101;
          }
          if (paramInt2 != 0) {
            break label106;
          }
          bool = true;
        }
      }
      for (;;)
      {
        SeekBarVolumizer.-get10(SeekBarVolumizer.this).postUpdateSlider(paramInt2, SeekBarVolumizer.-get4(SeekBarVolumizer.this), bool);
        return;
        if (paramInt1 == SeekBarVolumizer.-get9(SeekBarVolumizer.this))
        {
          bool = true;
          break;
        }
        bool = false;
        break;
        label101:
        bool = true;
        continue;
        label106:
        bool = false;
      }
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ("android.media.VOLUME_CHANGED_ACTION".equals(paramContext)) {
        updateVolumeSlider(paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1), paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1));
      }
      do
      {
        do
        {
          return;
          if (!"android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(paramContext)) {
            break;
          }
          if (SeekBarVolumizer.-get7(SeekBarVolumizer.this)) {
            SeekBarVolumizer.-set3(SeekBarVolumizer.this, SeekBarVolumizer.-get1(SeekBarVolumizer.this).getRingerModeInternal());
          }
        } while (!SeekBarVolumizer.-get0(SeekBarVolumizer.this));
        SeekBarVolumizer.-wrap2(SeekBarVolumizer.this);
        return;
        if ("android.media.STREAM_DEVICES_CHANGED_ACTION".equals(paramContext))
        {
          int i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
          updateVolumeSlider(i, SeekBarVolumizer.-get1(SeekBarVolumizer.this).getStreamVolume(i));
          return;
        }
      } while (!"android.app.action.INTERRUPTION_FILTER_CHANGED".equals(paramContext));
      SeekBarVolumizer.-set4(SeekBarVolumizer.this, SeekBarVolumizer.-get6(SeekBarVolumizer.this).getZenMode());
      SeekBarVolumizer.-wrap2(SeekBarVolumizer.this);
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (this.mListening == paramBoolean) {
        return;
      }
      this.mListening = paramBoolean;
      if (paramBoolean)
      {
        IntentFilter localIntentFilter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        localIntentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        localIntentFilter.addAction("android.app.action.INTERRUPTION_FILTER_CHANGED");
        localIntentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        SeekBarVolumizer.-get3(SeekBarVolumizer.this).registerReceiver(this, localIntentFilter);
        return;
      }
      SeekBarVolumizer.-get3(SeekBarVolumizer.this).unregisterReceiver(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/SeekBarVolumizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */