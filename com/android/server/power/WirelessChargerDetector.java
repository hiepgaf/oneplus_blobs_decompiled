package com.android.server.power;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TimeUtils;
import java.io.PrintWriter;

final class WirelessChargerDetector
{
  private static final boolean DEBUG = false;
  private static final double MAX_GRAVITY = 10.806650161743164D;
  private static final double MIN_GRAVITY = 8.806650161743164D;
  private static final int MIN_SAMPLES = 3;
  private static final double MOVEMENT_ANGLE_COS_THRESHOLD = Math.cos(0.08726646259971647D);
  private static final int SAMPLING_INTERVAL_MILLIS = 50;
  private static final long SETTLE_TIME_MILLIS = 800L;
  private static final String TAG = "WirelessChargerDetector";
  private static final int WIRELESS_CHARGER_TURN_ON_BATTERY_LEVEL_LIMIT = 95;
  private boolean mAtRest;
  private boolean mDetectionInProgress;
  private long mDetectionStartTime;
  private float mFirstSampleX;
  private float mFirstSampleY;
  private float mFirstSampleZ;
  private Sensor mGravitySensor;
  private final Handler mHandler;
  private float mLastSampleX;
  private float mLastSampleY;
  private float mLastSampleZ;
  private final SensorEventListener mListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      synchronized (WirelessChargerDetector.-get0(WirelessChargerDetector.this))
      {
        WirelessChargerDetector.-wrap1(WirelessChargerDetector.this, paramAnonymousSensorEvent.values[0], paramAnonymousSensorEvent.values[1], paramAnonymousSensorEvent.values[2]);
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private int mMovingSamples;
  private boolean mMustUpdateRestPosition;
  private boolean mPoweredWirelessly;
  private float mRestX;
  private float mRestY;
  private float mRestZ;
  private final SensorManager mSensorManager;
  private final Runnable mSensorTimeout = new Runnable()
  {
    public void run()
    {
      synchronized (WirelessChargerDetector.-get0(WirelessChargerDetector.this))
      {
        WirelessChargerDetector.-wrap0(WirelessChargerDetector.this);
        return;
      }
    }
  };
  private final SuspendBlocker mSuspendBlocker;
  private int mTotalSamples;
  
  public WirelessChargerDetector(SensorManager paramSensorManager, SuspendBlocker paramSuspendBlocker, Handler paramHandler)
  {
    this.mSensorManager = paramSensorManager;
    this.mSuspendBlocker = paramSuspendBlocker;
    this.mHandler = paramHandler;
    this.mGravitySensor = paramSensorManager.getDefaultSensor(9);
  }
  
  private void clearAtRestLocked()
  {
    this.mAtRest = false;
    this.mRestX = 0.0F;
    this.mRestY = 0.0F;
    this.mRestZ = 0.0F;
  }
  
  private void finishDetectionLocked()
  {
    if (this.mDetectionInProgress)
    {
      this.mSensorManager.unregisterListener(this.mListener);
      this.mHandler.removeCallbacks(this.mSensorTimeout);
      if (this.mMustUpdateRestPosition)
      {
        clearAtRestLocked();
        if (this.mTotalSamples >= 3) {
          break label131;
        }
        Slog.w("WirelessChargerDetector", "Wireless charger detector is broken.  Only received " + this.mTotalSamples + " samples from the gravity sensor but we " + "need at least " + 3 + " and we expect to see " + "about " + 16L + " on average.");
      }
    }
    for (;;)
    {
      this.mMustUpdateRestPosition = false;
      this.mDetectionInProgress = false;
      this.mSuspendBlocker.release();
      return;
      label131:
      if (this.mMovingSamples == 0)
      {
        this.mAtRest = true;
        this.mRestX = this.mLastSampleX;
        this.mRestY = this.mLastSampleY;
        this.mRestZ = this.mLastSampleZ;
      }
    }
  }
  
  private static boolean hasMoved(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    double d1 = paramFloat1 * paramFloat4 + paramFloat2 * paramFloat5 + paramFloat3 * paramFloat6;
    double d2 = Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 + paramFloat3 * paramFloat3);
    double d3 = Math.sqrt(paramFloat4 * paramFloat4 + paramFloat5 * paramFloat5 + paramFloat6 * paramFloat6);
    if ((d2 < 8.806650161743164D) || (d2 > 10.806650161743164D)) {}
    while ((d3 < 8.806650161743164D) || (d3 > 10.806650161743164D)) {
      return true;
    }
    return d1 < d2 * d3 * MOVEMENT_ANGLE_COS_THRESHOLD;
  }
  
  private void processSampleLocked(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (this.mDetectionInProgress)
    {
      this.mLastSampleX = paramFloat1;
      this.mLastSampleY = paramFloat2;
      this.mLastSampleZ = paramFloat3;
      this.mTotalSamples += 1;
      if (this.mTotalSamples != 1) {
        break label88;
      }
      this.mFirstSampleX = paramFloat1;
      this.mFirstSampleY = paramFloat2;
      this.mFirstSampleZ = paramFloat3;
    }
    for (;;)
    {
      if ((this.mAtRest) && (hasMoved(this.mRestX, this.mRestY, this.mRestZ, paramFloat1, paramFloat2, paramFloat3))) {
        clearAtRestLocked();
      }
      return;
      label88:
      if (hasMoved(this.mFirstSampleX, this.mFirstSampleY, this.mFirstSampleZ, paramFloat1, paramFloat2, paramFloat3)) {
        this.mMovingSamples += 1;
      }
    }
  }
  
  private void startDetectionLocked()
  {
    if ((!this.mDetectionInProgress) && (this.mGravitySensor != null) && (this.mSensorManager.registerListener(this.mListener, this.mGravitySensor, 50000)))
    {
      this.mSuspendBlocker.acquire();
      this.mDetectionInProgress = true;
      this.mDetectionStartTime = SystemClock.uptimeMillis();
      this.mTotalSamples = 0;
      this.mMovingSamples = 0;
      Message localMessage = Message.obtain(this.mHandler, this.mSensorTimeout);
      localMessage.setAsynchronous(true);
      this.mHandler.sendMessageDelayed(localMessage, 800L);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("Wireless Charger Detector State:");
      paramPrintWriter.println("  mGravitySensor=" + this.mGravitySensor);
      paramPrintWriter.println("  mPoweredWirelessly=" + this.mPoweredWirelessly);
      paramPrintWriter.println("  mAtRest=" + this.mAtRest);
      paramPrintWriter.println("  mRestX=" + this.mRestX + ", mRestY=" + this.mRestY + ", mRestZ=" + this.mRestZ);
      paramPrintWriter.println("  mDetectionInProgress=" + this.mDetectionInProgress);
      StringBuilder localStringBuilder = new StringBuilder().append("  mDetectionStartTime=");
      if (this.mDetectionStartTime == 0L)
      {
        str = "0 (never)";
        paramPrintWriter.println(str);
        paramPrintWriter.println("  mMustUpdateRestPosition=" + this.mMustUpdateRestPosition);
        paramPrintWriter.println("  mTotalSamples=" + this.mTotalSamples);
        paramPrintWriter.println("  mMovingSamples=" + this.mMovingSamples);
        paramPrintWriter.println("  mFirstSampleX=" + this.mFirstSampleX + ", mFirstSampleY=" + this.mFirstSampleY + ", mFirstSampleZ=" + this.mFirstSampleZ);
        paramPrintWriter.println("  mLastSampleX=" + this.mLastSampleX + ", mLastSampleY=" + this.mLastSampleY + ", mLastSampleZ=" + this.mLastSampleZ);
        return;
      }
      String str = TimeUtils.formatUptime(this.mDetectionStartTime);
    }
  }
  
  public boolean update(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    for (;;)
    {
      boolean bool2;
      synchronized (this.mLock)
      {
        bool2 = this.mPoweredWirelessly;
        if ((paramBoolean) && (paramInt1 == 4))
        {
          this.mPoweredWirelessly = true;
          this.mMustUpdateRestPosition = true;
          startDetectionLocked();
          boolean bool3 = this.mPoweredWirelessly;
          paramBoolean = bool1;
          if (bool3)
          {
            if (!bool2) {
              break label116;
            }
            paramBoolean = bool1;
          }
          return paramBoolean;
        }
        this.mPoweredWirelessly = false;
        if (!this.mAtRest) {
          continue;
        }
        if ((paramInt1 != 0) && (paramInt1 != 4))
        {
          this.mMustUpdateRestPosition = false;
          clearAtRestLocked();
        }
      }
      startDetectionLocked();
      continue;
      label116:
      paramBoolean = bool1;
      if (paramInt2 < 95)
      {
        bool2 = this.mAtRest;
        paramBoolean = bool1;
        if (!bool2) {
          paramBoolean = true;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/WirelessChargerDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */