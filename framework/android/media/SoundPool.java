package android.media;

import android.app.ActivityThread;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsCallback.Stub;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SoundPool
{
  private static final boolean DEBUG = Log.isLoggable("SoundPool", 3);
  private static final int SAMPLE_LOADED = 1;
  private static final String TAG = "SoundPool";
  private static IAudioService sService;
  private final IAppOpsService mAppOps;
  private final IAppOpsCallback mAppOpsCallback;
  private final AudioAttributes mAttributes;
  private EventHandler mEventHandler;
  private boolean mHasAppOpsPlayAudio;
  private final Object mLock;
  private long mNativeContext;
  private OnLoadCompleteListener mOnLoadCompleteListener;
  
  static
  {
    System.loadLibrary("soundpool");
  }
  
  public SoundPool(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, new AudioAttributes.Builder().setInternalLegacyStreamType(paramInt2).build());
  }
  
  private SoundPool(int paramInt, AudioAttributes paramAudioAttributes)
  {
    if (native_setup(new WeakReference(this), paramInt, paramAudioAttributes) != 0) {
      throw new RuntimeException("Native setup failed");
    }
    this.mLock = new Object();
    this.mAttributes = paramAudioAttributes;
    this.mAppOps = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    updateAppOpsPlayAudio();
    this.mAppOpsCallback = new IAppOpsCallback.Stub()
    {
      public void opChanged(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString)
      {
        paramAnonymousString = SoundPool.-get1(SoundPool.this);
        if (paramAnonymousInt1 == 28) {}
        try
        {
          SoundPool.-wrap0(SoundPool.this);
          return;
        }
        finally
        {
          localObject = finally;
          throw ((Throwable)localObject);
        }
      }
    };
    try
    {
      this.mAppOps.startWatchingMode(28, ActivityThread.currentPackageName(), this.mAppOpsCallback);
      return;
    }
    catch (RemoteException paramAudioAttributes)
    {
      this.mHasAppOpsPlayAudio = false;
    }
  }
  
  private final native int _load(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2, int paramInt);
  
  private final native int _play(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2, int paramInt3, float paramFloat3);
  
  private final native void _setVolume(int paramInt, float paramFloat1, float paramFloat2);
  
  private static IAudioService getService()
  {
    if (sService != null) {
      return sService;
    }
    sService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    return sService;
  }
  
  private boolean isRestricted()
  {
    int i = 0;
    if (this.mHasAppOpsPlayAudio) {
      return false;
    }
    if ((this.mAttributes.getAllFlags() & 0x40) != 0) {
      return false;
    }
    if ((this.mAttributes.getAllFlags() & 0x1) != 0)
    {
      try
      {
        boolean bool = getService().isCameraSoundForced();
        i = bool;
      }
      catch (NullPointerException localNullPointerException)
      {
        for (;;)
        {
          Log.e("SoundPool", "Null AudioService in isRestricted()");
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("SoundPool", "Cannot access AudioService in isRestricted()");
        }
      }
      if (i != 0) {
        return false;
      }
    }
    return true;
  }
  
  private final native void native_release();
  
  private final native int native_setup(Object paramObject1, int paramInt, Object paramObject2);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (SoundPool)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((SoundPool)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((SoundPool)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((SoundPool)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  private void updateAppOpsPlayAudio()
  {
    try
    {
      if (this.mAppOps.checkAudioOperation(28, this.mAttributes.getUsage(), Process.myUid(), ActivityThread.currentPackageName()) == 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.mHasAppOpsPlayAudio = bool;
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      this.mHasAppOpsPlayAudio = false;
    }
  }
  
  public final native void autoPause();
  
  public final native void autoResume();
  
  protected void finalize()
  {
    release();
  }
  
  public int load(Context paramContext, int paramInt1, int paramInt2)
  {
    paramContext = paramContext.getResources().openRawResourceFd(paramInt1);
    paramInt1 = 0;
    if (paramContext != null) {
      paramInt1 = _load(paramContext.getFileDescriptor(), paramContext.getStartOffset(), paramContext.getLength(), paramInt2);
    }
    try
    {
      paramContext.close();
      return paramInt1;
    }
    catch (IOException paramContext) {}
    return paramInt1;
  }
  
  public int load(AssetFileDescriptor paramAssetFileDescriptor, int paramInt)
  {
    if (paramAssetFileDescriptor != null)
    {
      long l = paramAssetFileDescriptor.getLength();
      if (l < 0L) {
        throw new AndroidRuntimeException("no length for fd");
      }
      return _load(paramAssetFileDescriptor.getFileDescriptor(), paramAssetFileDescriptor.getStartOffset(), l, paramInt);
    }
    return 0;
  }
  
  public int load(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2, int paramInt)
  {
    return _load(paramFileDescriptor, paramLong1, paramLong2, paramInt);
  }
  
  public int load(String paramString, int paramInt)
  {
    int j = 0;
    int k = 0;
    int i = j;
    try
    {
      File localFile = new File(paramString);
      i = j;
      ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.open(localFile, 268435456);
      i = k;
      if (localParcelFileDescriptor != null)
      {
        i = j;
        paramInt = _load(localParcelFileDescriptor.getFileDescriptor(), 0L, localFile.length(), paramInt);
        i = paramInt;
        localParcelFileDescriptor.close();
        i = paramInt;
      }
      return i;
    }
    catch (IOException localIOException)
    {
      Log.e("SoundPool", "error loading " + paramString);
    }
    return i;
  }
  
  public final native void pause(int paramInt);
  
  public final int play(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2, int paramInt3, float paramFloat3)
  {
    if (isRestricted())
    {
      paramFloat2 = 0.0F;
      paramFloat1 = 0.0F;
    }
    return _play(paramInt1, paramFloat1, paramFloat2, paramInt2, paramInt3, paramFloat3);
  }
  
  public final void release()
  {
    try
    {
      this.mAppOps.stopWatchingMode(this.mAppOpsCallback);
      native_release();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public final native void resume(int paramInt);
  
  public final native void setLoop(int paramInt1, int paramInt2);
  
  public void setOnLoadCompleteListener(OnLoadCompleteListener paramOnLoadCompleteListener)
  {
    Object localObject = this.mLock;
    if (paramOnLoadCompleteListener != null) {}
    for (;;)
    {
      try
      {
        Looper localLooper = Looper.myLooper();
        if (localLooper != null)
        {
          this.mEventHandler = new EventHandler(localLooper);
          this.mOnLoadCompleteListener = paramOnLoadCompleteListener;
          return;
        }
        localLooper = Looper.getMainLooper();
        if (localLooper != null)
        {
          this.mEventHandler = new EventHandler(localLooper);
          continue;
        }
        this.mEventHandler = null;
      }
      finally {}
      continue;
      this.mEventHandler = null;
    }
  }
  
  public final native void setPriority(int paramInt1, int paramInt2);
  
  public final native void setRate(int paramInt, float paramFloat);
  
  public void setVolume(int paramInt, float paramFloat)
  {
    setVolume(paramInt, paramFloat, paramFloat);
  }
  
  public final void setVolume(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (isRestricted()) {
      return;
    }
    _setVolume(paramInt, paramFloat1, paramFloat2);
  }
  
  public final native void stop(int paramInt);
  
  public final native boolean unload(int paramInt);
  
  public static class Builder
  {
    private AudioAttributes mAudioAttributes;
    private int mMaxStreams = 1;
    
    public SoundPool build()
    {
      if (this.mAudioAttributes == null) {
        this.mAudioAttributes = new AudioAttributes.Builder().setUsage(1).build();
      }
      return new SoundPool(this.mMaxStreams, this.mAudioAttributes, null);
    }
    
    public Builder setAudioAttributes(AudioAttributes paramAudioAttributes)
      throws IllegalArgumentException
    {
      if (paramAudioAttributes == null) {
        throw new IllegalArgumentException("Invalid null AudioAttributes");
      }
      this.mAudioAttributes = paramAudioAttributes;
      return this;
    }
    
    public Builder setMaxStreams(int paramInt)
      throws IllegalArgumentException
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("Strictly positive value required for the maximum number of streams");
      }
      this.mMaxStreams = paramInt;
      return this;
    }
  }
  
  private final class EventHandler
    extends Handler
  {
    public EventHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        Log.e("SoundPool", "Unknown message type " + paramMessage.what);
        return;
      }
      if (SoundPool.-get0()) {
        Log.d("SoundPool", "Sample " + paramMessage.arg1 + " loaded");
      }
      synchronized (SoundPool.-get1(SoundPool.this))
      {
        if (SoundPool.-get2(SoundPool.this) != null) {
          SoundPool.-get2(SoundPool.this).onLoadComplete(SoundPool.this, paramMessage.arg1, paramMessage.arg2);
        }
        return;
      }
    }
  }
  
  public static abstract interface OnLoadCompleteListener
  {
    public abstract void onLoadComplete(SoundPool paramSoundPool, int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SoundPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */