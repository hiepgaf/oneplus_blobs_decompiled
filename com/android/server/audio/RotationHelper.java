package com.android.server.audio;

import android.content.Context;
import android.media.AudioSystem;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import com.android.server.policy.WindowOrientationListener;

class RotationHelper
{
  private static final String TAG = "AudioService.RotationHelper";
  private static Context sContext;
  private static int sDeviceRotation = 0;
  private static AudioOrientationListener sOrientationListener;
  private static final Object sRotationLock = new Object();
  private static AudioWindowOrientationListener sWindowOrientationListener;
  
  static void disable()
  {
    if (sWindowOrientationListener != null)
    {
      sWindowOrientationListener.disable();
      return;
    }
    sOrientationListener.disable();
  }
  
  static void enable()
  {
    if (sWindowOrientationListener != null) {
      sWindowOrientationListener.enable();
    }
    for (;;)
    {
      updateOrientation();
      return;
      sOrientationListener.enable();
    }
  }
  
  static void init(Context paramContext, Handler paramHandler)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("Invalid null context");
    }
    sContext = paramContext;
    sWindowOrientationListener = new AudioWindowOrientationListener(paramContext, paramHandler);
    sWindowOrientationListener.enable();
    if (!sWindowOrientationListener.canDetectOrientation())
    {
      Log.i("AudioService.RotationHelper", "Not using WindowOrientationListener, reverting to OrientationListener");
      sWindowOrientationListener.disable();
      sWindowOrientationListener = null;
      sOrientationListener = new AudioOrientationListener(paramContext);
      sOrientationListener.enable();
    }
  }
  
  private static void publishRotation(int paramInt)
  {
    Log.v("AudioService.RotationHelper", "publishing device rotation =" + paramInt + " (x90deg)");
    switch (paramInt)
    {
    default: 
      Log.e("AudioService.RotationHelper", "Unknown device rotation");
      return;
    case 0: 
      AudioSystem.setParameters("rotation=0");
      return;
    case 1: 
      AudioSystem.setParameters("rotation=90");
      return;
    case 2: 
      AudioSystem.setParameters("rotation=180");
      return;
    }
    AudioSystem.setParameters("rotation=270");
  }
  
  static void updateOrientation()
  {
    int i = ((WindowManager)sContext.getSystemService("window")).getDefaultDisplay().getRotation();
    synchronized (sRotationLock)
    {
      if (i != sDeviceRotation)
      {
        sDeviceRotation = i;
        publishRotation(sDeviceRotation);
      }
      return;
    }
  }
  
  static final class AudioOrientationListener
    extends OrientationEventListener
  {
    AudioOrientationListener(Context paramContext)
    {
      super();
    }
    
    public void onOrientationChanged(int paramInt) {}
  }
  
  static final class AudioWindowOrientationListener
    extends WindowOrientationListener
  {
    private static RotationHelper.RotationCheckThread sRotationCheckThread;
    
    AudioWindowOrientationListener(Context paramContext, Handler paramHandler)
    {
      super(paramHandler);
    }
    
    public void onProposedRotationChanged(int paramInt)
    {
      
      if (sRotationCheckThread != null) {
        sRotationCheckThread.endCheck();
      }
      sRotationCheckThread = new RotationHelper.RotationCheckThread();
      sRotationCheckThread.beginCheck();
    }
  }
  
  static final class RotationCheckThread
    extends Thread
  {
    private final int[] WAIT_TIMES_MS = { 10, 20, 50, 100, 100, 200, 200, 500 };
    private final Object mCounterLock = new Object();
    private int mWaitCounter;
    
    RotationCheckThread()
    {
      super();
    }
    
    void beginCheck()
    {
      synchronized (this.mCounterLock)
      {
        this.mWaitCounter = 0;
      }
      try
      {
        start();
        return;
      }
      catch (IllegalStateException localIllegalStateException) {}
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    
    void endCheck()
    {
      synchronized (this.mCounterLock)
      {
        this.mWaitCounter = this.WAIT_TIMES_MS.length;
        return;
      }
    }
    
    public void run()
    {
      while (this.mWaitCounter < this.WAIT_TIMES_MS.length) {
        synchronized (this.mCounterLock)
        {
          int i;
          if (this.mWaitCounter < this.WAIT_TIMES_MS.length)
          {
            i = this.WAIT_TIMES_MS[this.mWaitCounter];
            this.mWaitCounter += 1;
            if (i > 0)
            {
              long l = i;
              try
              {
                sleep(l);
                RotationHelper.updateOrientation();
              }
              catch (InterruptedException localInterruptedException) {}
            }
          }
          else
          {
            i = 0;
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/audio/RotationHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */