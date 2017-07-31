package com.android.server;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Slog;

public class AnyMotionDetector
{
  private static final long ACCELEROMETER_DATA_TIMEOUT_MILLIS = 3000L;
  private static final boolean DEBUG = false;
  private static final long ORIENTATION_MEASUREMENT_DURATION_MILLIS = 2500L;
  private static final long ORIENTATION_MEASUREMENT_INTERVAL_MILLIS = 5000L;
  public static final int RESULT_MOVED = 1;
  public static final int RESULT_STATIONARY = 0;
  public static final int RESULT_UNKNOWN = -1;
  private static final int SAMPLING_INTERVAL_MILLIS = 40;
  private static final int STALE_MEASUREMENT_TIMEOUT_MILLIS = 120000;
  private static final int STATE_ACTIVE = 1;
  private static final int STATE_INACTIVE = 0;
  private static final String TAG = "AnyMotionDetector";
  private static final long WAKELOCK_TIMEOUT_MILLIS = 30000L;
  private final float THRESHOLD_ENERGY = 5.0F;
  private Sensor mAccelSensor;
  private DeviceIdleCallback mCallback = null;
  private Vector3 mCurrentGravityVector = null;
  private final Handler mHandler;
  private final SensorEventListener mListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      int i = -1;
      synchronized (AnyMotionDetector.-get2(AnyMotionDetector.this))
      {
        paramAnonymousSensorEvent = new AnyMotionDetector.Vector3(SystemClock.elapsedRealtime(), paramAnonymousSensorEvent.values[0], paramAnonymousSensorEvent.values[1], paramAnonymousSensorEvent.values[2]);
        AnyMotionDetector.-get5(AnyMotionDetector.this).accumulate(paramAnonymousSensorEvent);
        if (AnyMotionDetector.-get5(AnyMotionDetector.this).getSampleCount() >= AnyMotionDetector.-get4(AnyMotionDetector.this)) {
          i = AnyMotionDetector.-wrap0(AnyMotionDetector.this);
        }
        if (i != -1)
        {
          AnyMotionDetector.-get1(AnyMotionDetector.this).removeCallbacks(AnyMotionDetector.-get7(AnyMotionDetector.this));
          AnyMotionDetector.-set2(AnyMotionDetector.this, false);
          AnyMotionDetector.-get0(AnyMotionDetector.this).onAnyMotionResult(i);
        }
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private boolean mMeasurementInProgress;
  private final Runnable mMeasurementTimeout = new Runnable()
  {
    public void run()
    {
      synchronized (AnyMotionDetector.-get2(AnyMotionDetector.this))
      {
        if (AnyMotionDetector.-get3(AnyMotionDetector.this))
        {
          AnyMotionDetector.-set0(AnyMotionDetector.this, false);
          int i = AnyMotionDetector.-wrap0(AnyMotionDetector.this);
          if (i != -1)
          {
            AnyMotionDetector.-get1(AnyMotionDetector.this).removeCallbacks(AnyMotionDetector.-get7(AnyMotionDetector.this));
            AnyMotionDetector.-set2(AnyMotionDetector.this, false);
            AnyMotionDetector.-get0(AnyMotionDetector.this).onAnyMotionResult(i);
          }
        }
        return;
      }
    }
  };
  private boolean mMeasurementTimeoutIsActive;
  private int mNumSufficientSamples;
  private Vector3 mPreviousGravityVector = null;
  private RunningSignalStats mRunningStats;
  private SensorManager mSensorManager;
  private final Runnable mSensorRestart = new Runnable()
  {
    public void run()
    {
      synchronized (AnyMotionDetector.-get2(AnyMotionDetector.this))
      {
        if (AnyMotionDetector.-get6(AnyMotionDetector.this))
        {
          AnyMotionDetector.-set1(AnyMotionDetector.this, false);
          AnyMotionDetector.-wrap1(AnyMotionDetector.this);
        }
        return;
      }
    }
  };
  private boolean mSensorRestartIsActive;
  private int mState;
  private final float mThresholdAngle;
  private PowerManager.WakeLock mWakeLock;
  private final Runnable mWakelockTimeout = new Runnable()
  {
    public void run()
    {
      synchronized (AnyMotionDetector.-get2(AnyMotionDetector.this))
      {
        if (AnyMotionDetector.-get8(AnyMotionDetector.this))
        {
          AnyMotionDetector.-set2(AnyMotionDetector.this, false);
          AnyMotionDetector.this.stop();
        }
        return;
      }
    }
  };
  private boolean mWakelockTimeoutIsActive;
  
  public AnyMotionDetector(PowerManager paramPowerManager, Handler paramHandler, SensorManager paramSensorManager, DeviceIdleCallback paramDeviceIdleCallback, float paramFloat)
  {
    synchronized (this.mLock)
    {
      this.mWakeLock = paramPowerManager.newWakeLock(1, "AnyMotionDetector");
      this.mWakeLock.setReferenceCounted(false);
      this.mHandler = paramHandler;
      this.mSensorManager = paramSensorManager;
      this.mAccelSensor = this.mSensorManager.getDefaultSensor(1);
      this.mMeasurementInProgress = false;
      this.mMeasurementTimeoutIsActive = false;
      this.mWakelockTimeoutIsActive = false;
      this.mSensorRestartIsActive = false;
      this.mState = 0;
      this.mCallback = paramDeviceIdleCallback;
      this.mThresholdAngle = paramFloat;
      this.mRunningStats = new RunningSignalStats();
      this.mNumSufficientSamples = ((int)Math.ceil(62.5D));
      return;
    }
  }
  
  private void startOrientationMeasurementLocked()
  {
    if ((!this.mMeasurementInProgress) && (this.mAccelSensor != null))
    {
      if (this.mSensorManager.registerListener(this.mListener, this.mAccelSensor, 40000))
      {
        this.mMeasurementInProgress = true;
        this.mRunningStats.reset();
      }
      Message localMessage = Message.obtain(this.mHandler, this.mMeasurementTimeout);
      this.mHandler.sendMessageDelayed(localMessage, 3000L);
      this.mMeasurementTimeoutIsActive = true;
    }
  }
  
  private int stopOrientationMeasurementLocked()
  {
    int i = -1;
    if (this.mMeasurementInProgress)
    {
      this.mHandler.removeCallbacks(this.mMeasurementTimeout);
      this.mMeasurementTimeoutIsActive = false;
      this.mSensorManager.unregisterListener(this.mListener);
      this.mMeasurementInProgress = false;
      this.mPreviousGravityVector = this.mCurrentGravityVector;
      this.mCurrentGravityVector = this.mRunningStats.getRunningAverage();
      if (this.mRunningStats.getSampleCount() == 0) {
        Slog.w("AnyMotionDetector", "No accelerometer data acquired for orientation measurement.");
      }
      this.mRunningStats.reset();
      i = getStationaryStatus();
      if (i != -1)
      {
        if (this.mWakeLock.isHeld())
        {
          this.mHandler.removeCallbacks(this.mWakelockTimeout);
          this.mWakelockTimeoutIsActive = false;
          this.mWakeLock.release();
        }
        this.mState = 0;
      }
    }
    else
    {
      return i;
    }
    Message localMessage = Message.obtain(this.mHandler, this.mSensorRestart);
    this.mHandler.sendMessageDelayed(localMessage, 5000L);
    this.mSensorRestartIsActive = true;
    return i;
  }
  
  public void checkForAnyMotion()
  {
    if (this.mState != 1) {}
    synchronized (this.mLock)
    {
      this.mState = 1;
      this.mCurrentGravityVector = null;
      this.mPreviousGravityVector = null;
      this.mWakeLock.acquire();
      Message localMessage = Message.obtain(this.mHandler, this.mWakelockTimeout);
      this.mHandler.sendMessageDelayed(localMessage, 30000L);
      this.mWakelockTimeoutIsActive = true;
      startOrientationMeasurementLocked();
      return;
    }
  }
  
  public int getStationaryStatus()
  {
    if ((this.mPreviousGravityVector == null) || (this.mCurrentGravityVector == null)) {
      return -1;
    }
    float f = this.mPreviousGravityVector.normalized().angleBetween(this.mCurrentGravityVector.normalized());
    if ((f < this.mThresholdAngle) && (this.mRunningStats.getEnergy() < 5.0F)) {
      return 0;
    }
    if (Float.isNaN(f)) {
      return 1;
    }
    if (this.mCurrentGravityVector.timeMillisSinceBoot - this.mPreviousGravityVector.timeMillisSinceBoot > 120000L) {
      return -1;
    }
    return 1;
  }
  
  public void stop()
  {
    synchronized (this.mLock)
    {
      if (this.mState == 1) {
        this.mState = 0;
      }
      this.mHandler.removeCallbacks(this.mMeasurementTimeout);
      this.mHandler.removeCallbacks(this.mSensorRestart);
      this.mMeasurementTimeoutIsActive = false;
      this.mSensorRestartIsActive = false;
      if (this.mMeasurementInProgress)
      {
        this.mMeasurementInProgress = false;
        this.mSensorManager.unregisterListener(this.mListener);
      }
      this.mCurrentGravityVector = null;
      this.mPreviousGravityVector = null;
      if (this.mWakeLock.isHeld())
      {
        this.mHandler.removeCallbacks(this.mWakelockTimeout);
        this.mWakelockTimeoutIsActive = false;
        this.mWakeLock.release();
      }
      return;
    }
  }
  
  static abstract interface DeviceIdleCallback
  {
    public abstract void onAnyMotionResult(int paramInt);
  }
  
  private static class RunningSignalStats
  {
    AnyMotionDetector.Vector3 currentVector;
    float energy;
    AnyMotionDetector.Vector3 previousVector;
    AnyMotionDetector.Vector3 runningSum;
    int sampleCount;
    
    public RunningSignalStats()
    {
      reset();
    }
    
    public void accumulate(AnyMotionDetector.Vector3 paramVector3)
    {
      if (paramVector3 == null) {
        return;
      }
      this.sampleCount += 1;
      this.runningSum = this.runningSum.plus(paramVector3);
      this.previousVector = this.currentVector;
      this.currentVector = paramVector3;
      if (this.previousVector != null)
      {
        paramVector3 = this.currentVector.minus(this.previousVector);
        float f1 = paramVector3.x;
        float f2 = paramVector3.x;
        float f3 = paramVector3.y;
        float f4 = paramVector3.y;
        float f5 = paramVector3.z;
        float f6 = paramVector3.z;
        this.energy += f1 * f2 + f3 * f4 + f5 * f6;
      }
    }
    
    public float getEnergy()
    {
      return this.energy;
    }
    
    public AnyMotionDetector.Vector3 getRunningAverage()
    {
      if (this.sampleCount > 0) {
        return this.runningSum.times(1.0F / this.sampleCount);
      }
      return null;
    }
    
    public int getSampleCount()
    {
      return this.sampleCount;
    }
    
    public void reset()
    {
      this.previousVector = null;
      this.currentVector = null;
      this.runningSum = new AnyMotionDetector.Vector3(0L, 0.0F, 0.0F, 0.0F);
      this.energy = 0.0F;
      this.sampleCount = 0;
    }
    
    public String toString()
    {
      String str1;
      if (this.currentVector == null)
      {
        str1 = "null";
        if (this.previousVector != null) {
          break label134;
        }
      }
      label134:
      for (String str2 = "null";; str2 = this.previousVector.toString())
      {
        str2 = "" + "previousVector = " + str2;
        str1 = str2 + ", currentVector = " + str1;
        str1 = str1 + ", sampleCount = " + this.sampleCount;
        return str1 + ", energy = " + this.energy;
        str1 = this.currentVector.toString();
        break;
      }
    }
  }
  
  public static final class Vector3
  {
    public long timeMillisSinceBoot;
    public float x;
    public float y;
    public float z;
    
    public Vector3(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.timeMillisSinceBoot = paramLong;
      this.x = paramFloat1;
      this.y = paramFloat2;
      this.z = paramFloat3;
    }
    
    public float angleBetween(Vector3 paramVector3)
    {
      float f = Math.abs((float)Math.toDegrees(Math.atan2(cross(paramVector3).norm(), dotProduct(paramVector3))));
      Slog.d("AnyMotionDetector", "angleBetween: this = " + toString() + ", other = " + paramVector3.toString() + ", degrees = " + f);
      return f;
    }
    
    public Vector3 cross(Vector3 paramVector3)
    {
      return new Vector3(paramVector3.timeMillisSinceBoot, this.y * paramVector3.z - this.z * paramVector3.y, this.z * paramVector3.x - this.x * paramVector3.z, this.x * paramVector3.y - this.y * paramVector3.x);
    }
    
    public float dotProduct(Vector3 paramVector3)
    {
      return this.x * paramVector3.x + this.y * paramVector3.y + this.z * paramVector3.z;
    }
    
    public Vector3 minus(Vector3 paramVector3)
    {
      return new Vector3(paramVector3.timeMillisSinceBoot, this.x - paramVector3.x, this.y - paramVector3.y, this.z - paramVector3.z);
    }
    
    public float norm()
    {
      return (float)Math.sqrt(dotProduct(this));
    }
    
    public Vector3 normalized()
    {
      float f = norm();
      return new Vector3(this.timeMillisSinceBoot, this.x / f, this.y / f, this.z / f);
    }
    
    public Vector3 plus(Vector3 paramVector3)
    {
      long l = paramVector3.timeMillisSinceBoot;
      float f1 = this.x;
      float f2 = paramVector3.x;
      float f3 = this.y;
      float f4 = paramVector3.y;
      float f5 = this.z;
      return new Vector3(l, f2 + f1, f4 + f3, paramVector3.z + f5);
    }
    
    public Vector3 times(float paramFloat)
    {
      return new Vector3(this.timeMillisSinceBoot, this.x * paramFloat, this.y * paramFloat, this.z * paramFloat);
    }
    
    public String toString()
    {
      String str = "" + "timeMillisSinceBoot=" + this.timeMillisSinceBoot;
      str = str + " | x=" + this.x;
      str = str + ", y=" + this.y;
      return str + ", z=" + this.z;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/AnyMotionDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */