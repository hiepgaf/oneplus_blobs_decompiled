package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class AudioPortEventHandler
{
  private static final int AUDIOPORT_EVENT_NEW_LISTENER = 4;
  private static final int AUDIOPORT_EVENT_PATCH_LIST_UPDATED = 2;
  private static final int AUDIOPORT_EVENT_PORT_LIST_UPDATED = 1;
  private static final int AUDIOPORT_EVENT_SERVICE_DIED = 3;
  private static final String TAG = "AudioPortEventHandler";
  private Handler mHandler;
  private long mJniCallback;
  private final ArrayList<AudioManager.OnAudioPortUpdateListener> mListeners = new ArrayList();
  
  private native void native_finalize();
  
  private native void native_setup(Object paramObject);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (AudioPortEventHandler)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (paramObject1 != null)
    {
      paramObject1 = ((AudioPortEventHandler)paramObject1).handler();
      if (paramObject1 != null) {
        ((Handler)paramObject1).sendMessage(((Handler)paramObject1).obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2));
      }
    }
  }
  
  protected void finalize()
  {
    native_finalize();
  }
  
  Handler handler()
  {
    return this.mHandler;
  }
  
  /* Error */
  void init()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 68	android/media/AudioPortEventHandler:mHandler	Landroid/os/Handler;
    //   6: astore_1
    //   7: aload_1
    //   8: ifnull +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: invokestatic 75	android/os/Looper:getMainLooper	()Landroid/os/Looper;
    //   17: astore_1
    //   18: aload_1
    //   19: ifnull +31 -> 50
    //   22: aload_0
    //   23: new 6	android/media/AudioPortEventHandler$1
    //   26: dup
    //   27: aload_0
    //   28: aload_1
    //   29: invokespecial 78	android/media/AudioPortEventHandler$1:<init>	(Landroid/media/AudioPortEventHandler;Landroid/os/Looper;)V
    //   32: putfield 68	android/media/AudioPortEventHandler:mHandler	Landroid/os/Handler;
    //   35: aload_0
    //   36: new 45	java/lang/ref/WeakReference
    //   39: dup
    //   40: aload_0
    //   41: invokespecial 80	java/lang/ref/WeakReference:<init>	(Ljava/lang/Object;)V
    //   44: invokespecial 82	android/media/AudioPortEventHandler:native_setup	(Ljava/lang/Object;)V
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_0
    //   51: aconst_null
    //   52: putfield 68	android/media/AudioPortEventHandler:mHandler	Landroid/os/Handler;
    //   55: goto -8 -> 47
    //   58: astore_1
    //   59: aload_0
    //   60: monitorexit
    //   61: aload_1
    //   62: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	63	0	this	AudioPortEventHandler
    //   6	23	1	localObject1	Object
    //   58	4	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	58	finally
    //   14	18	58	finally
    //   22	47	58	finally
    //   50	55	58	finally
  }
  
  void registerListener(AudioManager.OnAudioPortUpdateListener paramOnAudioPortUpdateListener)
  {
    try
    {
      this.mListeners.add(paramOnAudioPortUpdateListener);
      if (this.mHandler != null)
      {
        paramOnAudioPortUpdateListener = this.mHandler.obtainMessage(4, 0, 0, paramOnAudioPortUpdateListener);
        this.mHandler.sendMessage(paramOnAudioPortUpdateListener);
      }
      return;
    }
    finally {}
  }
  
  void unregisterListener(AudioManager.OnAudioPortUpdateListener paramOnAudioPortUpdateListener)
  {
    try
    {
      this.mListeners.remove(paramOnAudioPortUpdateListener);
      return;
    }
    finally
    {
      paramOnAudioPortUpdateListener = finally;
      throw paramOnAudioPortUpdateListener;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioPortEventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */