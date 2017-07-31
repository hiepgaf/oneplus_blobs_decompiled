package com.android.server.policy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Handler;
import java.io.PrintWriter;

public abstract class WakeGestureListener
{
  private static final String TAG = "WakeGestureListener";
  private final Handler mHandler;
  private final TriggerEventListener mListener = new TriggerEventListener()
  {
    public void onTrigger(TriggerEvent arg1)
    {
      synchronized (WakeGestureListener.-get1(WakeGestureListener.this))
      {
        WakeGestureListener.-set0(WakeGestureListener.this, false);
        WakeGestureListener.-get0(WakeGestureListener.this).post(WakeGestureListener.-get2(WakeGestureListener.this));
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private Sensor mSensor;
  private final SensorManager mSensorManager;
  private boolean mTriggerRequested;
  private final Runnable mWakeUpRunnable = new Runnable()
  {
    public void run()
    {
      WakeGestureListener.this.onWakeUp();
    }
  };
  
  public WakeGestureListener(Context paramContext, Handler paramHandler)
  {
    this.mSensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.mHandler = paramHandler;
    this.mSensor = this.mSensorManager.getDefaultSensor(23);
  }
  
  public void cancelWakeUpTrigger()
  {
    synchronized (this.mLock)
    {
      if ((this.mSensor != null) && (this.mTriggerRequested))
      {
        this.mTriggerRequested = false;
        this.mSensorManager.cancelTriggerSensor(this.mListener, this.mSensor);
      }
      return;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println(paramString + "WakeGestureListener");
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "mTriggerRequested=" + this.mTriggerRequested);
      paramPrintWriter.println(paramString + "mSensor=" + this.mSensor);
      return;
    }
  }
  
  public boolean isSupported()
  {
    synchronized (this.mLock)
    {
      Sensor localSensor = this.mSensor;
      if (localSensor != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public abstract void onWakeUp();
  
  public void requestWakeUpTrigger()
  {
    synchronized (this.mLock)
    {
      if (this.mSensor != null)
      {
        boolean bool = this.mTriggerRequested;
        if (!bool) {}
      }
      else
      {
        return;
      }
      this.mTriggerRequested = true;
      this.mSensorManager.requestTriggerSensor(this.mListener, this.mSensor);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/WakeGestureListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */