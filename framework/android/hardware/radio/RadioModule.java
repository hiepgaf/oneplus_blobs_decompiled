package android.hardware.radio;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

public class RadioModule
  extends RadioTuner
{
  static final int EVENT_AF_SWITCH = 6;
  static final int EVENT_ANTENNA = 2;
  static final int EVENT_CONFIG = 1;
  static final int EVENT_CONTROL = 100;
  static final int EVENT_EA = 7;
  static final int EVENT_HW_FAILURE = 0;
  static final int EVENT_METADATA = 4;
  static final int EVENT_SERVER_DIED = 101;
  static final int EVENT_TA = 5;
  static final int EVENT_TUNED = 3;
  private NativeEventHandlerDelegate mEventHandlerDelegate;
  private int mId;
  private long mNativeContext = 0L;
  
  RadioModule(int paramInt, RadioManager.BandConfig paramBandConfig, boolean paramBoolean, RadioTuner.Callback paramCallback, Handler paramHandler)
  {
    this.mId = paramInt;
    this.mEventHandlerDelegate = new NativeEventHandlerDelegate(paramCallback, paramHandler);
    native_setup(new WeakReference(this), paramBandConfig, paramBoolean);
  }
  
  private native void native_finalize();
  
  private native void native_setup(Object paramObject, RadioManager.BandConfig paramBandConfig, boolean paramBoolean);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (RadioModule)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    paramObject1 = ((RadioModule)paramObject1).mEventHandlerDelegate;
    if (paramObject1 != null)
    {
      paramObject1 = ((NativeEventHandlerDelegate)paramObject1).handler();
      if (paramObject1 != null) {
        ((Handler)paramObject1).sendMessage(((Handler)paramObject1).obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2));
      }
    }
  }
  
  public native int cancel();
  
  public native void close();
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public native int getConfiguration(RadioManager.BandConfig[] paramArrayOfBandConfig);
  
  public native boolean getMute();
  
  public native int getProgramInformation(RadioManager.ProgramInfo[] paramArrayOfProgramInfo);
  
  public native boolean hasControl();
  
  boolean initCheck()
  {
    return this.mNativeContext != 0L;
  }
  
  public native boolean isAntennaConnected();
  
  public native int scan(int paramInt, boolean paramBoolean);
  
  public native int setConfiguration(RadioManager.BandConfig paramBandConfig);
  
  public native int setMute(boolean paramBoolean);
  
  public native int step(int paramInt, boolean paramBoolean);
  
  public native int tune(int paramInt1, int paramInt2);
  
  private class NativeEventHandlerDelegate
  {
    private final Handler mHandler;
    
    NativeEventHandlerDelegate(final RadioTuner.Callback paramCallback, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this$1 = paramHandler.getLooper(); RadioModule.this != null; this$1 = Looper.getMainLooper())
      {
        this.mHandler = new Handler(RadioModule.this)
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            boolean bool3 = true;
            boolean bool2 = true;
            boolean bool1 = true;
            switch (paramAnonymousMessage.what)
            {
            }
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  return;
                                } while (paramCallback == null);
                                paramCallback.onError(0);
                                return;
                                localObject = (RadioManager.BandConfig)paramAnonymousMessage.obj;
                                switch (paramAnonymousMessage.arg1)
                                {
                                }
                              } while (paramCallback == null);
                              paramCallback.onError(4);
                              return;
                            } while (paramCallback == null);
                            paramCallback.onConfigurationChanged((RadioManager.BandConfig)localObject);
                            return;
                          } while (paramCallback == null);
                          localObject = paramCallback;
                          if (paramAnonymousMessage.arg2 == 1) {}
                          for (;;)
                          {
                            ((RadioTuner.Callback)localObject).onAntennaState(bool1);
                            return;
                            bool1 = false;
                          }
                          localObject = (RadioManager.ProgramInfo)paramAnonymousMessage.obj;
                          switch (paramAnonymousMessage.arg1)
                          {
                          }
                        } while (paramCallback == null);
                        paramCallback.onError(2);
                        return;
                      } while (paramCallback == null);
                      paramCallback.onProgramInfoChanged((RadioManager.ProgramInfo)localObject);
                      return;
                    } while (paramCallback == null);
                    paramCallback.onError(3);
                    return;
                    paramAnonymousMessage = (RadioMetadata)paramAnonymousMessage.obj;
                  } while (paramCallback == null);
                  paramCallback.onMetadataChanged(paramAnonymousMessage);
                  return;
                } while (paramCallback == null);
                localObject = paramCallback;
                if (paramAnonymousMessage.arg2 == 1) {}
                for (bool1 = bool3;; bool1 = false)
                {
                  ((RadioTuner.Callback)localObject).onTrafficAnnouncement(bool1);
                  return;
                }
                if (paramCallback != null)
                {
                  localObject = paramCallback;
                  if (paramAnonymousMessage.arg2 != 1) {
                    break;
                  }
                  bool1 = true;
                  ((RadioTuner.Callback)localObject).onEmergencyAnnouncement(bool1);
                }
              } while (paramCallback == null);
              Object localObject = paramCallback;
              if (paramAnonymousMessage.arg2 == 1) {}
              for (bool1 = bool2;; bool1 = false)
              {
                ((RadioTuner.Callback)localObject).onControlChanged(bool1);
                return;
                bool1 = false;
                break;
              }
            } while (paramCallback == null);
            paramCallback.onError(1);
          }
        };
        return;
      }
      this.mHandler = null;
    }
    
    Handler handler()
    {
      return this.mHandler;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/radio/RadioModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */