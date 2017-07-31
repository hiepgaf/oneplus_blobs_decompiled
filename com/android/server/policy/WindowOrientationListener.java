package com.android.server.policy;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.List;

public abstract class WindowOrientationListener
{
  private static final int DEFAULT_BATCH_LATENCY = 100000;
  private static boolean LOG = SystemProperties.getBoolean("debug.orientation.log", false);
  private static final String TAG = "WindowOrientationListener";
  private static final boolean USE_GRAVITY_SENSOR = false;
  private int mCurrentRotation = -1;
  private Sensor mDPCSensor;
  private boolean mEnabled;
  private Handler mHandler;
  private final Object mLock = new Object();
  private OrientationJudge mOrientationJudge;
  private int mRate;
  private Sensor mSensor;
  private SensorManager mSensorManager;
  private String mSensorType;
  
  public WindowOrientationListener(Context paramContext, Handler paramHandler)
  {
    this(paramContext, paramHandler, 2);
  }
  
  private WindowOrientationListener(Context paramContext, Handler paramHandler, int paramInt)
  {
    this.mHandler = paramHandler;
    this.mSensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.mRate = paramInt;
    this.mSensor = this.mSensorManager.getDefaultSensor(27);
    if (this.mSensor != null)
    {
      paramHandler = this.mSensorManager.getSensorList(-1);
      int i = paramHandler.size();
      paramInt = 0;
      if (paramInt < i)
      {
        Sensor localSensor = (Sensor)paramHandler.get(paramInt);
        if ("com.qti.sensor.dpc".equals(localSensor.getStringType())) {
          this.mDPCSensor = localSensor;
        }
      }
      else
      {
        this.mOrientationJudge = new OrientationSensorJudge();
      }
    }
    else
    {
      if (this.mOrientationJudge == null)
      {
        this.mSensor = this.mSensorManager.getDefaultSensor(1);
        if (this.mSensor != null) {
          this.mOrientationJudge = new AccelSensorJudge(paramContext);
        }
      }
      return;
    }
    if (LOG)
    {
      if (this.mDPCSensor == null) {
        break label199;
      }
      Slog.d("WindowOrientationListener", "DPC sensor found!");
    }
    for (;;)
    {
      paramInt += 1;
      break;
      label199:
      Slog.e("WindowOrientationListener", "DPC sensor NOT found!!");
    }
  }
  
  public boolean canDetectOrientation()
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
  
  public void disable()
  {
    synchronized (this.mLock)
    {
      if (this.mSensor == null)
      {
        Slog.w("WindowOrientationListener", "Cannot detect sensors. Invalid disable");
        return;
      }
      if (this.mEnabled)
      {
        if (LOG) {
          Slog.d("WindowOrientationListener", "WindowOrientationListener disabled");
        }
        this.mSensorManager.unregisterListener(this.mOrientationJudge);
        this.mEnabled = false;
      }
      return;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println(paramString + "WindowOrientationListener");
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "mEnabled=" + this.mEnabled);
      paramPrintWriter.println(paramString + "mCurrentRotation=" + this.mCurrentRotation);
      paramPrintWriter.println(paramString + "mSensorType=" + this.mSensorType);
      paramPrintWriter.println(paramString + "mSensor=" + this.mSensor);
      paramPrintWriter.println(paramString + "mRate=" + this.mRate);
      if (this.mOrientationJudge != null) {
        this.mOrientationJudge.dumpLocked(paramPrintWriter, paramString);
      }
      return;
    }
  }
  
  public void enable()
  {
    LOG = SystemProperties.getBoolean("debug.orientation.log", false);
    synchronized (this.mLock)
    {
      if (this.mSensor == null)
      {
        Slog.w("WindowOrientationListener", "Cannot detect sensors. Not enabled");
        return;
      }
      if (!this.mEnabled)
      {
        if (LOG) {
          Slog.d("WindowOrientationListener", "WindowOrientationListener enabled");
        }
        this.mOrientationJudge.resetLocked();
        if (this.mSensor.getType() != 1) {
          break label107;
        }
        this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, 100000, this.mHandler);
      }
      label107:
      do
      {
        do
        {
          this.mEnabled = true;
          return;
          this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, this.mHandler);
        } while (this.mDPCSensor == null);
        this.mSensorManager.registerListener(this.mOrientationJudge, this.mDPCSensor, this.mRate, this.mHandler);
      } while (!LOG);
      Slog.d("WindowOrientationListener", "DPC sensor registered!");
    }
  }
  
  public int getProposedRotation()
  {
    synchronized (this.mLock)
    {
      if (this.mEnabled)
      {
        int i = this.mOrientationJudge.getProposedRotationLocked();
        return i;
      }
      return -1;
    }
  }
  
  public abstract void onProposedRotationChanged(int paramInt);
  
  public void onTouchEnd()
  {
    long l = SystemClock.elapsedRealtimeNanos();
    synchronized (this.mLock)
    {
      if (this.mOrientationJudge != null) {
        this.mOrientationJudge.onTouchEndLocked(l);
      }
      return;
    }
  }
  
  public void onTouchStart()
  {
    synchronized (this.mLock)
    {
      if (this.mOrientationJudge != null) {
        this.mOrientationJudge.onTouchStartLocked();
      }
      return;
    }
  }
  
  public void setCurrentRotation(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mCurrentRotation = paramInt;
      return;
    }
  }
  
  final class AccelSensorJudge
    extends WindowOrientationListener.OrientationJudge
  {
    private static final float ACCELERATION_TOLERANCE = 4.0F;
    private static final int ACCELEROMETER_DATA_X = 0;
    private static final int ACCELEROMETER_DATA_Y = 1;
    private static final int ACCELEROMETER_DATA_Z = 2;
    private static final int ADJACENT_ORIENTATION_ANGLE_GAP = 45;
    private static final float FILTER_TIME_CONSTANT_MS = 200.0F;
    private static final float FLAT_ANGLE = 80.0F;
    private static final long FLAT_TIME_NANOS = 1000000000L;
    private static final float MAX_ACCELERATION_MAGNITUDE = 13.80665F;
    private static final long MAX_FILTER_DELTA_TIME_NANOS = 1000000000L;
    private static final int MAX_TILT = 80;
    private static final float MIN_ACCELERATION_MAGNITUDE = 5.80665F;
    private static final float NEAR_ZERO_MAGNITUDE = 1.0F;
    private static final long PROPOSAL_MIN_TIME_SINCE_ACCELERATION_ENDED_NANOS = 500000000L;
    private static final long PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS = 500000000L;
    private static final long PROPOSAL_MIN_TIME_SINCE_SWING_ENDED_NANOS = 300000000L;
    private static final long PROPOSAL_SETTLE_TIME_NANOS = 40000000L;
    private static final float RADIANS_TO_DEGREES = 57.29578F;
    private static final float SWING_AWAY_ANGLE_DELTA = 20.0F;
    private static final long SWING_TIME_NANOS = 300000000L;
    private static final int TILT_HISTORY_SIZE = 200;
    private static final int TILT_OVERHEAD_ENTER = -40;
    private static final int TILT_OVERHEAD_EXIT = -15;
    private boolean mAccelerating;
    private long mAccelerationTimestampNanos;
    private boolean mFlat;
    private long mFlatTimestampNanos;
    private long mLastFilteredTimestampNanos;
    private float mLastFilteredX;
    private float mLastFilteredY;
    private float mLastFilteredZ;
    private boolean mOverhead;
    private int mPredictedRotation;
    private long mPredictedRotationTimestampNanos;
    private int mProposedRotation;
    private long mSwingTimestampNanos;
    private boolean mSwinging;
    private float[] mTiltHistory = new float['È'];
    private int mTiltHistoryIndex;
    private long[] mTiltHistoryTimestampNanos = new long['È'];
    private final int[][] mTiltToleranceConfig = { { -25, 70 }, { -25, 65 }, { -25, 60 }, { -25, 65 } };
    private long mTouchEndedTimestampNanos = Long.MIN_VALUE;
    private boolean mTouched;
    
    public AccelSensorJudge(Context paramContext)
    {
      super();
      this$1 = paramContext.getResources().getIntArray(17235998);
      if (WindowOrientationListener.this.length == 8)
      {
        int i = 0;
        if (i < 4)
        {
          int j = WindowOrientationListener.this[(i * 2)];
          int k = WindowOrientationListener.this[(i * 2 + 1)];
          if ((j >= -90) && (j <= k) && (k <= 90))
          {
            this.mTiltToleranceConfig[i][0] = j;
            this.mTiltToleranceConfig[i][1] = k;
          }
          for (;;)
          {
            i += 1;
            break;
            Slog.wtf("WindowOrientationListener", "config_autoRotationTiltTolerance contains invalid range: min=" + j + ", max=" + k);
          }
        }
      }
      else
      {
        Slog.wtf("WindowOrientationListener", "config_autoRotationTiltTolerance should have exactly 8 elements");
      }
    }
    
    private void addTiltHistoryEntryLocked(long paramLong, float paramFloat)
    {
      this.mTiltHistory[this.mTiltHistoryIndex] = paramFloat;
      this.mTiltHistoryTimestampNanos[this.mTiltHistoryIndex] = paramLong;
      this.mTiltHistoryIndex = ((this.mTiltHistoryIndex + 1) % 200);
      this.mTiltHistoryTimestampNanos[this.mTiltHistoryIndex] = Long.MIN_VALUE;
    }
    
    private void clearPredictedRotationLocked()
    {
      this.mPredictedRotation = -1;
      this.mPredictedRotationTimestampNanos = Long.MIN_VALUE;
    }
    
    private void clearTiltHistoryLocked()
    {
      this.mTiltHistoryTimestampNanos[0] = Long.MIN_VALUE;
      this.mTiltHistoryIndex = 1;
    }
    
    private float getLastTiltLocked()
    {
      int i = nextTiltHistoryIndexLocked(this.mTiltHistoryIndex);
      if (i >= 0) {
        return this.mTiltHistory[i];
      }
      return NaN.0F;
    }
    
    private boolean isAcceleratingLocked(float paramFloat)
    {
      return (paramFloat < 5.80665F) || (paramFloat > 13.80665F);
    }
    
    private boolean isFlatLocked(long paramLong)
    {
      int i = this.mTiltHistoryIndex;
      int j;
      do
      {
        j = nextTiltHistoryIndexLocked(i);
        if ((j < 0) || (this.mTiltHistory[j] < 80.0F)) {
          return false;
        }
        i = j;
      } while (this.mTiltHistoryTimestampNanos[j] + 1000000000L > paramLong);
      return true;
    }
    
    private boolean isOrientationAngleAcceptableLocked(int paramInt1, int paramInt2)
    {
      int i = WindowOrientationListener.-get1(WindowOrientationListener.this);
      if (i >= 0)
      {
        if ((paramInt1 == i) || (paramInt1 == (i + 1) % 4))
        {
          int j = paramInt1 * 90 - 45 + 22;
          if (paramInt1 == 0)
          {
            if ((paramInt2 >= 315) && (paramInt2 < j + 360)) {
              return false;
            }
          }
          else if (paramInt2 < j) {
            return false;
          }
        }
        if ((paramInt1 == i) || (paramInt1 == (i + 3) % 4))
        {
          i = paramInt1 * 90 + 45 - 22;
          if (paramInt1 == 0)
          {
            if ((paramInt2 <= 45) && (paramInt2 > i)) {
              return false;
            }
          }
          else if (paramInt2 > i) {
            return false;
          }
        }
      }
      return true;
    }
    
    private boolean isPredictedRotationAcceptableLocked(long paramLong)
    {
      if (paramLong < this.mPredictedRotationTimestampNanos + 40000000L) {
        return false;
      }
      if (paramLong < this.mFlatTimestampNanos + 500000000L) {
        return false;
      }
      if (paramLong < this.mSwingTimestampNanos + 300000000L) {
        return false;
      }
      if (paramLong < this.mAccelerationTimestampNanos + 500000000L) {
        return false;
      }
      return (!this.mTouched) && (paramLong >= this.mTouchEndedTimestampNanos + 500000000L);
    }
    
    private boolean isSwingingLocked(long paramLong, float paramFloat)
    {
      int i = this.mTiltHistoryIndex;
      int j;
      do
      {
        j = nextTiltHistoryIndexLocked(i);
        if ((j < 0) || (this.mTiltHistoryTimestampNanos[j] + 300000000L < paramLong)) {
          return false;
        }
        i = j;
      } while (this.mTiltHistory[j] + 20.0F > paramFloat);
      return true;
    }
    
    private boolean isTiltAngleAcceptableLocked(int paramInt1, int paramInt2)
    {
      if (paramInt2 >= this.mTiltToleranceConfig[paramInt1][0]) {
        return paramInt2 <= this.mTiltToleranceConfig[paramInt1][1];
      }
      return false;
    }
    
    private int nextTiltHistoryIndexLocked(int paramInt)
    {
      int i = paramInt;
      if (paramInt == 0) {
        i = 200;
      }
      paramInt = i - 1;
      if (this.mTiltHistoryTimestampNanos[paramInt] != Long.MIN_VALUE) {
        return paramInt;
      }
      return -1;
    }
    
    private float remainingMS(long paramLong1, long paramLong2)
    {
      if (paramLong1 >= paramLong2) {
        return 0.0F;
      }
      return (float)(paramLong2 - paramLong1) * 1.0E-6F;
    }
    
    private void updatePredictedRotationLocked(long paramLong, int paramInt)
    {
      if (this.mPredictedRotation != paramInt)
      {
        this.mPredictedRotation = paramInt;
        this.mPredictedRotationTimestampNanos = paramLong;
      }
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "AccelSensorJudge");
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "mProposedRotation=" + this.mProposedRotation);
      paramPrintWriter.println(paramString + "mPredictedRotation=" + this.mPredictedRotation);
      paramPrintWriter.println(paramString + "mLastFilteredX=" + this.mLastFilteredX);
      paramPrintWriter.println(paramString + "mLastFilteredY=" + this.mLastFilteredY);
      paramPrintWriter.println(paramString + "mLastFilteredZ=" + this.mLastFilteredZ);
      long l1 = SystemClock.elapsedRealtimeNanos();
      long l2 = this.mLastFilteredTimestampNanos;
      paramPrintWriter.println(paramString + "mLastFilteredTimestampNanos=" + this.mLastFilteredTimestampNanos + " (" + (float)(l1 - l2) * 1.0E-6F + "ms ago)");
      paramPrintWriter.println(paramString + "mTiltHistory={last: " + getLastTiltLocked() + "}");
      paramPrintWriter.println(paramString + "mFlat=" + this.mFlat);
      paramPrintWriter.println(paramString + "mSwinging=" + this.mSwinging);
      paramPrintWriter.println(paramString + "mAccelerating=" + this.mAccelerating);
      paramPrintWriter.println(paramString + "mOverhead=" + this.mOverhead);
      paramPrintWriter.println(paramString + "mTouched=" + this.mTouched);
      paramPrintWriter.print(paramString + "mTiltToleranceConfig=[");
      int i = 0;
      while (i < 4)
      {
        if (i != 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print("[");
        paramPrintWriter.print(this.mTiltToleranceConfig[i][0]);
        paramPrintWriter.print(", ");
        paramPrintWriter.print(this.mTiltToleranceConfig[i][1]);
        paramPrintWriter.print("]");
        i += 1;
      }
      paramPrintWriter.println("]");
    }
    
    public int getProposedRotationLocked()
    {
      return this.mProposedRotation;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      for (;;)
      {
        float f1;
        float f2;
        long l1;
        int i;
        boolean bool1;
        boolean bool2;
        boolean bool3;
        boolean bool4;
        boolean bool5;
        boolean bool6;
        int j;
        int m;
        synchronized (WindowOrientationListener.-get3(WindowOrientationListener.this))
        {
          f1 = paramSensorEvent.values[0];
          f2 = paramSensorEvent.values[1];
          float f3 = paramSensorEvent.values[2];
          if (WindowOrientationListener.-get0()) {
            Slog.v("WindowOrientationListener", "Raw acceleration vector: x=" + f1 + ", y=" + f2 + ", z=" + f3 + ", magnitude=" + Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3));
          }
          l1 = paramSensorEvent.timestamp;
          long l2 = this.mLastFilteredTimestampNanos;
          float f4 = (float)(l1 - l2) * 1.0E-6F;
          boolean bool9;
          float f5;
          if ((l1 < l2) || (l1 > 1000000000L + l2))
          {
            if (WindowOrientationListener.-get0()) {
              Slog.v("WindowOrientationListener", "Resetting orientation listener.");
            }
            resetLocked();
            i = 1;
            this.mLastFilteredTimestampNanos = l1;
            this.mLastFilteredX = f1;
            this.mLastFilteredY = f2;
            this.mLastFilteredZ = f3;
            bool9 = false;
            bool1 = false;
            bool2 = false;
            boolean bool7 = false;
            bool3 = false;
            boolean bool8 = false;
            bool4 = bool1;
            bool5 = bool7;
            bool6 = bool8;
            if (i == 0)
            {
              f5 = (float)Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
              if (f5 < 1.0F)
              {
                if (WindowOrientationListener.-get0()) {
                  Slog.v("WindowOrientationListener", "Ignoring sensor data, magnitude too close to zero.");
                }
                clearPredictedRotationLocked();
                bool6 = bool8;
                bool5 = bool7;
                bool4 = bool1;
              }
            }
            else
            {
              this.mFlat = bool5;
              this.mSwinging = bool6;
              this.mAccelerating = bool4;
              i = this.mProposedRotation;
              if ((this.mPredictedRotation < 0) || (isPredictedRotationAcceptableLocked(l1))) {
                this.mProposedRotation = this.mPredictedRotation;
              }
              j = this.mProposedRotation;
              if (WindowOrientationListener.-get0()) {
                Slog.v("WindowOrientationListener", "Result: currentRotation=" + WindowOrientationListener.-get1(WindowOrientationListener.this) + ", proposedRotation=" + j + ", predictedRotation=" + this.mPredictedRotation + ", timeDeltaMS=" + f4 + ", isAccelerating=" + bool4 + ", isFlat=" + bool5 + ", isSwinging=" + bool6 + ", isOverhead=" + this.mOverhead + ", isTouched=" + this.mTouched + ", timeUntilSettledMS=" + remainingMS(l1, this.mPredictedRotationTimestampNanos + 40000000L) + ", timeUntilAccelerationDelayExpiredMS=" + remainingMS(l1, this.mAccelerationTimestampNanos + 500000000L) + ", timeUntilFlatDelayExpiredMS=" + remainingMS(l1, this.mFlatTimestampNanos + 500000000L) + ", timeUntilSwingDelayExpiredMS=" + remainingMS(l1, this.mSwingTimestampNanos + 300000000L) + ", timeUntilTouchDelayExpiredMS=" + remainingMS(l1, this.mTouchEndedTimestampNanos + 500000000L));
              }
              if ((j != i) && (j >= 0))
              {
                if (WindowOrientationListener.-get0()) {
                  Slog.v("WindowOrientationListener", "Proposed rotation changed!  proposedRotation=" + j + ", oldProposedRotation=" + i);
                }
                WindowOrientationListener.this.onProposedRotationChanged(j);
              }
            }
          }
          else
          {
            if ((f1 == 0.0F) && (f2 == 0.0F) && (f3 == 0.0F)) {
              continue;
            }
            f5 = f4 / (200.0F + f4);
            f1 = (f1 - this.mLastFilteredX) * f5 + this.mLastFilteredX;
            f2 = (f2 - this.mLastFilteredY) * f5 + this.mLastFilteredY;
            f3 = (f3 - this.mLastFilteredZ) * f5 + this.mLastFilteredZ;
            if (!WindowOrientationListener.-get0()) {
              break label1320;
            }
            Slog.v("WindowOrientationListener", "Filtered acceleration vector: x=" + f1 + ", y=" + f2 + ", z=" + f3 + ", magnitude=" + Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3));
            break label1320;
          }
          bool1 = bool9;
          if (isAcceleratingLocked(f5))
          {
            bool1 = true;
            this.mAccelerationTimestampNanos = l1;
          }
          m = (int)Math.round(Math.asin(f3 / f5) * 57.295780181884766D);
          addTiltHistoryEntryLocked(l1, m);
          if (isFlatLocked(l1))
          {
            bool2 = true;
            this.mFlatTimestampNanos = l1;
          }
          if (isSwingingLocked(l1, m))
          {
            bool3 = true;
            this.mSwingTimestampNanos = l1;
          }
          if (m <= -40)
          {
            this.mOverhead = true;
            if (!this.mOverhead) {
              break label1002;
            }
            if (WindowOrientationListener.-get0()) {
              Slog.v("WindowOrientationListener", "Ignoring sensor data, device is overhead: tiltAngle=" + m);
            }
            clearPredictedRotationLocked();
            bool4 = bool1;
            bool5 = bool2;
            bool6 = bool3;
          }
        }
        if (m >= -15)
        {
          this.mOverhead = false;
          continue;
          label1002:
          if (Math.abs(m) > 80)
          {
            if (WindowOrientationListener.-get0()) {
              Slog.v("WindowOrientationListener", "Ignoring sensor data, tilt angle too high: tiltAngle=" + m);
            }
            clearPredictedRotationLocked();
            bool4 = bool1;
            bool5 = bool2;
            bool6 = bool3;
          }
          else
          {
            j = (int)Math.round(-Math.atan2(-f1, f2) * 57.295780181884766D);
            i = j;
            if (j < 0) {
              i = j + 360;
            }
            int k = (i + 45) / 90;
            j = k;
            if (k == 4) {
              j = 0;
            }
            if ((isTiltAngleAcceptableLocked(j, m)) && (isOrientationAngleAcceptableLocked(j, i)))
            {
              updatePredictedRotationLocked(l1, j);
              bool4 = bool1;
              bool5 = bool2;
              bool6 = bool3;
              if (WindowOrientationListener.-get0())
              {
                Slog.v("WindowOrientationListener", "Predicted: tiltAngle=" + m + ", orientationAngle=" + i + ", predictedRotation=" + this.mPredictedRotation + ", predictedRotationAgeMS=" + (float)(l1 - this.mPredictedRotationTimestampNanos) * 1.0E-6F);
                bool4 = bool1;
                bool5 = bool2;
                bool6 = bool3;
              }
            }
            else
            {
              if (WindowOrientationListener.-get0()) {
                Slog.v("WindowOrientationListener", "Ignoring sensor data, no predicted rotation: tiltAngle=" + m + ", orientationAngle=" + i);
              }
              clearPredictedRotationLocked();
              bool4 = bool1;
              bool5 = bool2;
              bool6 = bool3;
              continue;
              label1320:
              i = 0;
            }
          }
        }
      }
    }
    
    public void onTouchEndLocked(long paramLong)
    {
      this.mTouched = false;
      this.mTouchEndedTimestampNanos = paramLong;
    }
    
    public void onTouchStartLocked()
    {
      this.mTouched = true;
    }
    
    public void resetLocked()
    {
      this.mLastFilteredTimestampNanos = Long.MIN_VALUE;
      this.mProposedRotation = -1;
      this.mFlatTimestampNanos = Long.MIN_VALUE;
      this.mFlat = false;
      this.mSwingTimestampNanos = Long.MIN_VALUE;
      this.mSwinging = false;
      this.mAccelerationTimestampNanos = Long.MIN_VALUE;
      this.mAccelerating = false;
      this.mOverhead = false;
      clearPredictedRotationLocked();
      clearTiltHistoryLocked();
    }
  }
  
  abstract class OrientationJudge
    implements SensorEventListener
  {
    protected static final float MILLIS_PER_NANO = 1.0E-6F;
    protected static final long NANOS_PER_MS = 1000000L;
    protected static final long PROPOSAL_MIN_TIME_SINCE_TOUCH_END_NANOS = 500000000L;
    
    OrientationJudge() {}
    
    public abstract void dumpLocked(PrintWriter paramPrintWriter, String paramString);
    
    public abstract int getProposedRotationLocked();
    
    public abstract void onAccuracyChanged(Sensor paramSensor, int paramInt);
    
    public abstract void onSensorChanged(SensorEvent paramSensorEvent);
    
    public abstract void onTouchEndLocked(long paramLong);
    
    public abstract void onTouchStartLocked();
    
    public abstract void resetLocked();
  }
  
  final class OrientationSensorJudge
    extends WindowOrientationListener.OrientationJudge
  {
    private int mDPCState = -1;
    private int mDesiredRotation = -1;
    private int mProposedRotation = -1;
    private boolean mRotationEvaluationScheduled;
    private Runnable mRotationEvaluator = new Runnable()
    {
      public void run()
      {
        synchronized (WindowOrientationListener.-get3(WindowOrientationListener.this))
        {
          WindowOrientationListener.OrientationSensorJudge.-set0(WindowOrientationListener.OrientationSensorJudge.this, false);
          int i = WindowOrientationListener.OrientationSensorJudge.this.evaluateRotationChangeLocked();
          if (i >= 0) {
            WindowOrientationListener.this.onProposedRotationChanged(i);
          }
          return;
        }
      }
    };
    private long mTouchEndedTimestampNanos = Long.MIN_VALUE;
    private boolean mTouching;
    
    OrientationSensorJudge()
    {
      super();
    }
    
    private boolean isDesiredRotationAcceptableLocked(long paramLong)
    {
      if (this.mTouching) {
        return false;
      }
      return paramLong >= this.mTouchEndedTimestampNanos + 500000000L;
    }
    
    private void scheduleRotationEvaluationIfNecessaryLocked(long paramLong)
    {
      if ((this.mRotationEvaluationScheduled) || (this.mDesiredRotation == this.mProposedRotation))
      {
        if (WindowOrientationListener.-get0()) {
          Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, an evaluation is already scheduled or is unnecessary.");
        }
        return;
      }
      if (this.mTouching)
      {
        if (WindowOrientationListener.-get0()) {
          Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, user is still touching the screen.");
        }
        return;
      }
      long l = this.mTouchEndedTimestampNanos + 500000000L;
      if (paramLong >= l)
      {
        if (WindowOrientationListener.-get0()) {
          Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, already past the next possible time of rotation.");
        }
        return;
      }
      paramLong = Math.ceil((float)(l - paramLong) * 1.0E-6F);
      WindowOrientationListener.-get2(WindowOrientationListener.this).postDelayed(this.mRotationEvaluator, paramLong);
      this.mRotationEvaluationScheduled = true;
    }
    
    private void unscheduleRotationEvaluationLocked()
    {
      if (!this.mRotationEvaluationScheduled) {
        return;
      }
      WindowOrientationListener.-get2(WindowOrientationListener.this).removeCallbacks(this.mRotationEvaluator);
      this.mRotationEvaluationScheduled = false;
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "OrientationSensorJudge");
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "mDesiredRotation=" + this.mDesiredRotation);
      paramPrintWriter.println(paramString + "mProposedRotation=" + this.mProposedRotation);
      paramPrintWriter.println(paramString + "mTouching=" + this.mTouching);
      paramPrintWriter.println(paramString + "mTouchEndedTimestampNanos=" + this.mTouchEndedTimestampNanos);
    }
    
    public int evaluateRotationChangeLocked()
    {
      unscheduleRotationEvaluationLocked();
      if (this.mDPCState == 1)
      {
        if (WindowOrientationListener.-get0()) {
          Slog.d("WindowOrientationListener", "Device in Flat, reset proposed rotation, Desired=" + this.mDesiredRotation);
        }
        this.mProposedRotation = -1;
        return this.mProposedRotation;
      }
      if (this.mDesiredRotation == this.mProposedRotation) {
        return -1;
      }
      long l = SystemClock.elapsedRealtimeNanos();
      if (isDesiredRotationAcceptableLocked(l))
      {
        this.mProposedRotation = this.mDesiredRotation;
        return this.mProposedRotation;
      }
      scheduleRotationEvaluationIfNecessaryLocked(l);
      return -1;
    }
    
    public int getProposedRotationLocked()
    {
      return this.mProposedRotation;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      synchronized (WindowOrientationListener.-get3(WindowOrientationListener.this))
      {
        if (WindowOrientationListener.-get0()) {
          Slog.d("WindowOrientationListener", "onSensorChanged Name=" + paramSensorEvent.sensor.getName() + ", type=" + paramSensorEvent.sensor.getType() + ", event:=" + (int)paramSensorEvent.values[0]);
        }
        if (paramSensorEvent.sensor.getType() == 27)
        {
          this.mDesiredRotation = ((int)paramSensorEvent.values[0]);
          int i = evaluateRotationChangeLocked();
          if (WindowOrientationListener.-get0()) {
            Slog.v("WindowOrientationListener", "Result: currentRotation=" + WindowOrientationListener.-get1(WindowOrientationListener.this) + ", mProposedRotation=" + this.mProposedRotation + ", mDesiredRotation=" + this.mDesiredRotation + ", newRotation=" + i + ", mDPCState=" + this.mDPCState + ", mTouching=" + this.mTouching);
          }
          if (i >= 0) {
            WindowOrientationListener.this.onProposedRotationChanged(i);
          }
          return;
        }
        this.mDPCState = ((int)paramSensorEvent.values[0]);
      }
    }
    
    public void onTouchEndLocked(long paramLong)
    {
      this.mTouching = false;
      this.mTouchEndedTimestampNanos = paramLong;
      if (this.mDesiredRotation != this.mProposedRotation) {
        scheduleRotationEvaluationIfNecessaryLocked(SystemClock.elapsedRealtimeNanos());
      }
    }
    
    public void onTouchStartLocked()
    {
      this.mTouching = true;
    }
    
    public void resetLocked()
    {
      this.mProposedRotation = -1;
      this.mDesiredRotation = -1;
      this.mTouching = false;
      this.mTouchEndedTimestampNanos = Long.MIN_VALUE;
      unscheduleRotationEvaluationLocked();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/WindowOrientationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */