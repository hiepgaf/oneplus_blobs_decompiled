package android.media.audiofx;

import android.app.ActivityThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;

public class Visualizer
{
  public static final int ALREADY_EXISTS = -2;
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -4;
  public static final int ERROR_DEAD_OBJECT = -7;
  public static final int ERROR_INVALID_OPERATION = -5;
  public static final int ERROR_NO_INIT = -3;
  public static final int ERROR_NO_MEMORY = -6;
  public static final int MEASUREMENT_MODE_NONE = 0;
  public static final int MEASUREMENT_MODE_PEAK_RMS = 1;
  private static final int NATIVE_EVENT_FFT_CAPTURE = 1;
  private static final int NATIVE_EVENT_PCM_CAPTURE = 0;
  private static final int NATIVE_EVENT_SERVER_DIED = 2;
  public static final int SCALING_MODE_AS_PLAYED = 1;
  public static final int SCALING_MODE_NORMALIZED = 0;
  public static final int STATE_ENABLED = 2;
  public static final int STATE_INITIALIZED = 1;
  public static final int STATE_UNINITIALIZED = 0;
  public static final int SUCCESS = 0;
  private static final String TAG = "Visualizer-JAVA";
  private OnDataCaptureListener mCaptureListener = null;
  private int mId;
  private long mJniData;
  private final Object mListenerLock = new Object();
  private NativeEventHandler mNativeEventHandler = null;
  private long mNativeVisualizer;
  private OnServerDiedListener mServerDiedListener = null;
  private int mState = 0;
  private final Object mStateLock = new Object();
  
  static
  {
    System.loadLibrary("audioeffect_jni");
    native_init();
  }
  
  public Visualizer(int paramInt)
    throws UnsupportedOperationException, RuntimeException
  {
    int[] arrayOfInt = new int[1];
    for (;;)
    {
      synchronized (this.mStateLock)
      {
        this.mState = 0;
        paramInt = native_setup(new WeakReference(this), paramInt, arrayOfInt, ActivityThread.currentOpPackageName());
        if ((paramInt == 0) || (paramInt == -2)) {
          break label182;
        }
        Log.e("Visualizer-JAVA", "Error code " + paramInt + " when initializing Visualizer.");
        switch (paramInt)
        {
        case -5: 
          throw new RuntimeException("Cannot initialize Visualizer engine, error: " + paramInt);
        }
      }
      throw new UnsupportedOperationException("Effect library not loaded");
      label182:
      this.mId = localObject2[0];
      if (native_getEnabled()) {}
      for (this.mState = 2;; this.mState = 1) {
        return;
      }
    }
  }
  
  public static native int[] getCaptureSizeRange();
  
  public static native int getMaxCaptureRate();
  
  private final native void native_finalize();
  
  private final native int native_getCaptureSize();
  
  private final native boolean native_getEnabled();
  
  private final native int native_getFft(byte[] paramArrayOfByte);
  
  private final native int native_getMeasurementMode();
  
  private final native int native_getPeakRms(MeasurementPeakRms paramMeasurementPeakRms);
  
  private final native int native_getSamplingRate();
  
  private final native int native_getScalingMode();
  
  private final native int native_getWaveForm(byte[] paramArrayOfByte);
  
  private static final native void native_init();
  
  private final native void native_release();
  
  private final native int native_setCaptureSize(int paramInt);
  
  private final native int native_setEnabled(boolean paramBoolean);
  
  private final native int native_setMeasurementMode(int paramInt);
  
  private final native int native_setPeriodicCapture(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  private final native int native_setScalingMode(int paramInt);
  
  private final native int native_setup(Object paramObject, int paramInt, int[] paramArrayOfInt, String paramString);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (Visualizer)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((Visualizer)paramObject1).mNativeEventHandler != null)
    {
      paramObject2 = ((Visualizer)paramObject1).mNativeEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((Visualizer)paramObject1).mNativeEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public int getCaptureSize()
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("getCaptureSize() called in wrong state: " + this.mState);
      }
    }
    int i = native_getCaptureSize();
    return i;
  }
  
  public boolean getEnabled()
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("getEnabled() called in wrong state: " + this.mState);
      }
    }
    boolean bool = native_getEnabled();
    return bool;
  }
  
  public int getFft(byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState != 2) {
        throw new IllegalStateException("getFft() called in wrong state: " + this.mState);
      }
    }
    int i = native_getFft(paramArrayOfByte);
    return i;
  }
  
  public int getMeasurementMode()
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("getMeasurementMode() called in wrong state: " + this.mState);
      }
    }
    int i = native_getMeasurementMode();
    return i;
  }
  
  public int getMeasurementPeakRms(MeasurementPeakRms paramMeasurementPeakRms)
  {
    if (paramMeasurementPeakRms == null)
    {
      Log.e("Visualizer-JAVA", "Cannot store measurements in a null object");
      return -4;
    }
    synchronized (this.mStateLock)
    {
      if (this.mState != 2) {
        throw new IllegalStateException("getMeasurementPeakRms() called in wrong state: " + this.mState);
      }
    }
    int i = native_getPeakRms(paramMeasurementPeakRms);
    return i;
  }
  
  public int getSamplingRate()
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("getSamplingRate() called in wrong state: " + this.mState);
      }
    }
    int i = native_getSamplingRate();
    return i;
  }
  
  public int getScalingMode()
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("getScalingMode() called in wrong state: " + this.mState);
      }
    }
    int i = native_getScalingMode();
    return i;
  }
  
  public int getWaveForm(byte[] paramArrayOfByte)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState != 2) {
        throw new IllegalStateException("getWaveForm() called in wrong state: " + this.mState);
      }
    }
    int i = native_getWaveForm(paramArrayOfByte);
    return i;
  }
  
  public void release()
  {
    synchronized (this.mStateLock)
    {
      native_release();
      this.mState = 0;
      return;
    }
  }
  
  public int setCaptureSize(int paramInt)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState != 1) {
        throw new IllegalStateException("setCaptureSize() called in wrong state: " + this.mState);
      }
    }
    paramInt = native_setCaptureSize(paramInt);
    return paramInt;
  }
  
  public int setDataCaptureListener(OnDataCaptureListener paramOnDataCaptureListener, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mListenerLock)
    {
      this.mCaptureListener = paramOnDataCaptureListener;
      if (paramOnDataCaptureListener == null)
      {
        paramBoolean1 = false;
        paramBoolean2 = false;
      }
      paramInt = native_setPeriodicCapture(paramInt, paramBoolean1, paramBoolean2);
      if ((paramInt == 0) && (paramOnDataCaptureListener != null) && (this.mNativeEventHandler == null))
      {
        paramOnDataCaptureListener = Looper.myLooper();
        if (paramOnDataCaptureListener != null) {
          this.mNativeEventHandler = new NativeEventHandler(this, paramOnDataCaptureListener);
        }
      }
      else
      {
        return paramInt;
      }
    }
    paramOnDataCaptureListener = Looper.getMainLooper();
    if (paramOnDataCaptureListener != null)
    {
      this.mNativeEventHandler = new NativeEventHandler(this, paramOnDataCaptureListener);
      return paramInt;
    }
    this.mNativeEventHandler = null;
    return -3;
  }
  
  public int setEnabled(boolean paramBoolean)
    throws IllegalStateException
  {
    int j = 2;
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("setEnabled() called in wrong state: " + this.mState);
      }
    }
    int k = 0;
    if ((paramBoolean) && (this.mState == 1))
    {
      k = native_setEnabled(paramBoolean);
      i = k;
      if (k == 0) {
        if (!paramBoolean) {
          break label131;
        }
      }
    }
    label131:
    for (int i = j;; i = 1)
    {
      this.mState = i;
      i = k;
      int m;
      do
      {
        do
        {
          return i;
          i = k;
        } while (paramBoolean);
        m = this.mState;
        i = k;
      } while (m != 2);
      break;
    }
  }
  
  public int setMeasurementMode(int paramInt)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("setMeasurementMode() called in wrong state: " + this.mState);
      }
    }
    paramInt = native_setMeasurementMode(paramInt);
    return paramInt;
  }
  
  public int setScalingMode(int paramInt)
    throws IllegalStateException
  {
    synchronized (this.mStateLock)
    {
      if (this.mState == 0) {
        throw new IllegalStateException("setScalingMode() called in wrong state: " + this.mState);
      }
    }
    paramInt = native_setScalingMode(paramInt);
    return paramInt;
  }
  
  public int setServerDiedListener(OnServerDiedListener paramOnServerDiedListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mServerDiedListener = paramOnServerDiedListener;
      return 0;
    }
  }
  
  public static final class MeasurementPeakRms
  {
    public int mPeak;
    public int mRms;
  }
  
  private class NativeEventHandler
    extends Handler
  {
    private Visualizer mVisualizer;
    
    public NativeEventHandler(Visualizer paramVisualizer, Looper paramLooper)
    {
      super();
      this.mVisualizer = paramVisualizer;
    }
    
    private void handleCaptureMessage(Message paramMessage)
    {
      Visualizer.OnDataCaptureListener localOnDataCaptureListener;
      int i;
      synchronized (Visualizer.-get1(Visualizer.this))
      {
        localOnDataCaptureListener = Visualizer.-get0(this.mVisualizer);
        if (localOnDataCaptureListener != null)
        {
          ??? = (byte[])paramMessage.obj;
          i = paramMessage.arg1;
        }
        switch (paramMessage.what)
        {
        default: 
          Log.e("Visualizer-JAVA", "Unknown native event in handleCaptureMessge: " + paramMessage.what);
          return;
        }
      }
      localOnDataCaptureListener.onWaveFormDataCapture(this.mVisualizer, (byte[])???, i);
      return;
      localOnDataCaptureListener.onFftDataCapture(this.mVisualizer, (byte[])???, i);
    }
    
    private void handleServerDiedMessage(Message arg1)
    {
      synchronized (Visualizer.-get1(Visualizer.this))
      {
        Visualizer.OnServerDiedListener localOnServerDiedListener = Visualizer.-get2(this.mVisualizer);
        if (localOnServerDiedListener != null) {
          localOnServerDiedListener.onServerDied();
        }
        return;
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (this.mVisualizer == null) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        Log.e("Visualizer-JAVA", "Unknown native event: " + paramMessage.what);
        return;
      case 0: 
      case 1: 
        handleCaptureMessage(paramMessage);
        return;
      }
      handleServerDiedMessage(paramMessage);
    }
  }
  
  public static abstract interface OnDataCaptureListener
  {
    public abstract void onFftDataCapture(Visualizer paramVisualizer, byte[] paramArrayOfByte, int paramInt);
    
    public abstract void onWaveFormDataCapture(Visualizer paramVisualizer, byte[] paramArrayOfByte, int paramInt);
  }
  
  public static abstract interface OnServerDiedListener
  {
    public abstract void onServerDied();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiofx/Visualizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */