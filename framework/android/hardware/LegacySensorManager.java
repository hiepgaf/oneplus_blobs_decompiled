package android.hardware;

import android.view.IRotationWatcher.Stub;
import android.view.IWindowManager;
import java.util.HashMap;
import java.util.Iterator;

final class LegacySensorManager
{
  private static boolean sInitialized;
  private static int sRotation = 0;
  private static IWindowManager sWindowManager;
  private final HashMap<SensorListener, LegacyListener> mLegacyListenersMap;
  private final SensorManager mSensorManager;
  
  /* Error */
  public LegacySensorManager(SensorManager paramSensorManager)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 34	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 36	java/util/HashMap
    //   8: dup
    //   9: invokespecial 37	java/util/HashMap:<init>	()V
    //   12: putfield 39	android/hardware/LegacySensorManager:mLegacyListenersMap	Ljava/util/HashMap;
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 41	android/hardware/LegacySensorManager:mSensorManager	Landroid/hardware/SensorManager;
    //   20: ldc 43
    //   22: monitorenter
    //   23: getstatic 45	android/hardware/LegacySensorManager:sInitialized	Z
    //   26: ifne +41 -> 67
    //   29: ldc 47
    //   31: invokestatic 53	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   34: invokestatic 59	android/view/IWindowManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/view/IWindowManager;
    //   37: putstatic 61	android/hardware/LegacySensorManager:sWindowManager	Landroid/view/IWindowManager;
    //   40: getstatic 61	android/hardware/LegacySensorManager:sWindowManager	Landroid/view/IWindowManager;
    //   43: astore_1
    //   44: aload_1
    //   45: ifnull +22 -> 67
    //   48: getstatic 61	android/hardware/LegacySensorManager:sWindowManager	Landroid/view/IWindowManager;
    //   51: new 6	android/hardware/LegacySensorManager$1
    //   54: dup
    //   55: aload_0
    //   56: invokespecial 64	android/hardware/LegacySensorManager$1:<init>	(Landroid/hardware/LegacySensorManager;)V
    //   59: invokeinterface 70 2 0
    //   64: putstatic 27	android/hardware/LegacySensorManager:sRotation	I
    //   67: ldc 43
    //   69: monitorexit
    //   70: return
    //   71: astore_1
    //   72: ldc 43
    //   74: monitorexit
    //   75: aload_1
    //   76: athrow
    //   77: astore_1
    //   78: goto -11 -> 67
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	LegacySensorManager
    //   0	81	1	paramSensorManager	SensorManager
    // Exception table:
    //   from	to	target	type
    //   23	44	71	finally
    //   48	67	71	finally
    //   48	67	77	android/os/RemoteException
  }
  
  static int getRotation()
  {
    try
    {
      int i = sRotation;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  static void onRotationChanged(int paramInt)
  {
    try
    {
      sRotation = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private boolean registerLegacyListener(int paramInt1, int paramInt2, SensorListener paramSensorListener, int paramInt3, int paramInt4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    Sensor localSensor;
    if ((paramInt3 & paramInt1) != 0)
    {
      localSensor = this.mSensorManager.getDefaultSensor(paramInt2);
      bool1 = bool2;
      if (localSensor == null) {}
    }
    synchronized (this.mLegacyListenersMap)
    {
      LegacyListener localLegacyListener2 = (LegacyListener)this.mLegacyListenersMap.get(paramSensorListener);
      LegacyListener localLegacyListener1 = localLegacyListener2;
      if (localLegacyListener2 == null)
      {
        localLegacyListener1 = new LegacyListener(paramSensorListener);
        this.mLegacyListenersMap.put(paramSensorListener, localLegacyListener1);
      }
      if (localLegacyListener1.registerSensor(paramInt1))
      {
        bool1 = this.mSensorManager.registerListener(localLegacyListener1, localSensor, paramInt4);
        return bool1;
      }
      bool1 = true;
    }
  }
  
  private void unregisterLegacyListener(int paramInt1, int paramInt2, SensorListener paramSensorListener, int paramInt3)
  {
    Sensor localSensor;
    if ((paramInt3 & paramInt1) != 0)
    {
      localSensor = this.mSensorManager.getDefaultSensor(paramInt2);
      if (localSensor == null) {}
    }
    synchronized (this.mLegacyListenersMap)
    {
      LegacyListener localLegacyListener = (LegacyListener)this.mLegacyListenersMap.get(paramSensorListener);
      if ((localLegacyListener != null) && (localLegacyListener.unregisterSensor(paramInt1)))
      {
        this.mSensorManager.unregisterListener(localLegacyListener, localSensor);
        if (!localLegacyListener.hasSensors()) {
          this.mLegacyListenersMap.remove(paramSensorListener);
        }
      }
      return;
    }
  }
  
  public int getSensors()
  {
    int i = 0;
    Iterator localIterator = this.mSensorManager.getFullSensorList().iterator();
    while (localIterator.hasNext()) {
      switch (((Sensor)localIterator.next()).getType())
      {
      default: 
        break;
      case 1: 
        i |= 0x2;
        break;
      case 2: 
        i |= 0x8;
        break;
      case 3: 
        i |= 0x81;
      }
    }
    return i;
  }
  
  public boolean registerListener(SensorListener paramSensorListener, int paramInt1, int paramInt2)
  {
    if (paramSensorListener == null) {
      return false;
    }
    boolean bool;
    if (!registerLegacyListener(2, 1, paramSensorListener, paramInt1, paramInt2))
    {
      bool = false;
      if (registerLegacyListener(8, 2, paramSensorListener, paramInt1, paramInt2)) {
        break label82;
      }
      label34:
      if (registerLegacyListener(128, 3, paramSensorListener, paramInt1, paramInt2)) {
        break label88;
      }
      label48:
      if (registerLegacyListener(1, 3, paramSensorListener, paramInt1, paramInt2)) {
        break label94;
      }
    }
    for (;;)
    {
      if (registerLegacyListener(4, 7, paramSensorListener, paramInt1, paramInt2)) {
        break label100;
      }
      return bool;
      bool = true;
      break;
      label82:
      bool = true;
      break label34;
      label88:
      bool = true;
      break label48;
      label94:
      bool = true;
    }
    label100:
    return true;
  }
  
  public void unregisterListener(SensorListener paramSensorListener, int paramInt)
  {
    if (paramSensorListener == null) {
      return;
    }
    unregisterLegacyListener(2, 1, paramSensorListener, paramInt);
    unregisterLegacyListener(8, 2, paramSensorListener, paramInt);
    unregisterLegacyListener(128, 3, paramSensorListener, paramInt);
    unregisterLegacyListener(1, 3, paramSensorListener, paramInt);
    unregisterLegacyListener(4, 7, paramSensorListener, paramInt);
  }
  
  private static final class LegacyListener
    implements SensorEventListener
  {
    private int mSensors;
    private SensorListener mTarget;
    private float[] mValues = new float[6];
    private final LegacySensorManager.LmsFilter mYawfilter = new LegacySensorManager.LmsFilter();
    
    LegacyListener(SensorListener paramSensorListener)
    {
      this.mTarget = paramSensorListener;
      this.mSensors = 0;
    }
    
    private static int getLegacySensorType(int paramInt)
    {
      switch (paramInt)
      {
      case 4: 
      case 5: 
      case 6: 
      default: 
        return 0;
      case 1: 
        return 2;
      case 2: 
        return 8;
      case 3: 
        return 128;
      }
      return 4;
    }
    
    private static boolean hasOrientationSensor(int paramInt)
    {
      boolean bool = false;
      if ((paramInt & 0x81) != 0) {
        bool = true;
      }
      return bool;
    }
    
    private void mapSensorDataToWindow(int paramInt1, float[] paramArrayOfFloat, int paramInt2)
    {
      float f1 = paramArrayOfFloat[0];
      float f2 = paramArrayOfFloat[1];
      float f3 = paramArrayOfFloat[2];
      switch (paramInt1)
      {
      default: 
        label60:
        paramArrayOfFloat[0] = f1;
        paramArrayOfFloat[1] = f2;
        paramArrayOfFloat[2] = f3;
        paramArrayOfFloat[3] = f1;
        paramArrayOfFloat[4] = f2;
        paramArrayOfFloat[5] = f3;
        if ((paramInt2 & 0x1) != 0) {
          switch (paramInt1)
          {
          }
        }
        break;
      }
      for (;;)
      {
        if ((paramInt2 & 0x2) != 0)
        {
          f1 = paramArrayOfFloat[0];
          f2 = paramArrayOfFloat[1];
          f3 = paramArrayOfFloat[2];
        }
        switch (paramInt1)
        {
        default: 
          return;
          f3 = -f3;
          break label60;
          f1 = -f1;
          f2 = -f2;
          f3 = -f3;
          break label60;
          f1 = -f1;
          f2 = -f2;
          break label60;
          paramArrayOfFloat[0] = (-f2);
          paramArrayOfFloat[1] = f1;
          paramArrayOfFloat[2] = f3;
        }
      }
      if (f1 < 270.0F) {}
      for (int i = 90;; i = 65266)
      {
        paramArrayOfFloat[0] = (i + f1);
        paramArrayOfFloat[1] = f3;
        paramArrayOfFloat[2] = f2;
        break;
      }
      paramArrayOfFloat[0] = (-f1);
      paramArrayOfFloat[1] = (-f2);
      paramArrayOfFloat[2] = f3;
      return;
      if (f1 >= 180.0F) {}
      for (f1 -= 180.0F;; f1 += 180.0F)
      {
        paramArrayOfFloat[0] = f1;
        paramArrayOfFloat[1] = (-f2);
        paramArrayOfFloat[2] = (-f3);
        return;
      }
    }
    
    boolean hasSensors()
    {
      boolean bool = false;
      if (this.mSensors != 0) {
        bool = true;
      }
      return bool;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt)
    {
      try
      {
        this.mTarget.onAccuracyChanged(getLegacySensorType(paramSensor.getType()), paramInt);
        return;
      }
      catch (AbstractMethodError paramSensor) {}
    }
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      float[] arrayOfFloat = this.mValues;
      arrayOfFloat[0] = paramSensorEvent.values[0];
      arrayOfFloat[1] = paramSensorEvent.values[1];
      arrayOfFloat[2] = paramSensorEvent.values[2];
      int i = paramSensorEvent.sensor.getType();
      int j = getLegacySensorType(i);
      mapSensorDataToWindow(j, arrayOfFloat, LegacySensorManager.getRotation());
      if (i == 3)
      {
        if ((this.mSensors & 0x80) != 0) {
          this.mTarget.onSensorChanged(128, arrayOfFloat);
        }
        if ((this.mSensors & 0x1) != 0)
        {
          arrayOfFloat[0] = this.mYawfilter.filter(paramSensorEvent.timestamp, arrayOfFloat[0]);
          this.mTarget.onSensorChanged(1, arrayOfFloat);
        }
        return;
      }
      this.mTarget.onSensorChanged(j, arrayOfFloat);
    }
    
    boolean registerSensor(int paramInt)
    {
      if ((this.mSensors & paramInt) != 0) {
        return false;
      }
      boolean bool = hasOrientationSensor(this.mSensors);
      this.mSensors |= paramInt;
      return (!bool) || (!hasOrientationSensor(paramInt));
    }
    
    boolean unregisterSensor(int paramInt)
    {
      if ((this.mSensors & paramInt) == 0) {
        return false;
      }
      this.mSensors &= paramInt;
      return (!hasOrientationSensor(paramInt)) || (!hasOrientationSensor(this.mSensors));
    }
  }
  
  private static final class LmsFilter
  {
    private static final int COUNT = 12;
    private static final float PREDICTION_RATIO = 0.33333334F;
    private static final float PREDICTION_TIME = 0.08F;
    private static final int SENSORS_RATE_MS = 20;
    private int mIndex = 12;
    private long[] mT = new long[24];
    private float[] mV = new float[24];
    
    public float filter(long paramLong, float paramFloat)
    {
      float f1 = paramFloat;
      float f2 = this.mV[this.mIndex];
      if (paramFloat - f2 > 180.0F) {
        f1 = paramFloat - 360.0F;
      }
      float f4;
      float f3;
      for (;;)
      {
        this.mIndex += 1;
        if (this.mIndex >= 24) {
          this.mIndex = 12;
        }
        this.mV[this.mIndex] = f1;
        this.mT[this.mIndex] = paramLong;
        this.mV[(this.mIndex - 12)] = f1;
        this.mT[(this.mIndex - 12)] = paramLong;
        paramFloat = 0.0F;
        f2 = 0.0F;
        f1 = 0.0F;
        f4 = 0.0F;
        f3 = 0.0F;
        int i = 0;
        while (i < 11)
        {
          int j = this.mIndex - 1 - i;
          float f5 = this.mV[j];
          float f6 = (float)(this.mT[j] / 2L + this.mT[(j + 1)] / 2L - paramLong) * 1.0E-9F;
          float f7 = (float)(this.mT[j] - this.mT[(j + 1)]) * 1.0E-9F;
          f7 *= f7;
          f3 += f5 * f7;
          f4 += f6 * f7 * f6;
          f1 += f6 * f7;
          f2 += f6 * f7 * f5;
          paramFloat += f7;
          i += 1;
        }
        if (f2 - paramFloat > 180.0F) {
          f1 = paramFloat + 360.0F;
        }
      }
      f2 = (f3 * f4 + f1 * f2) / (paramFloat * f4 + f1 * f1);
      f1 = (f2 + 0.08F * ((paramFloat * f2 - f3) / f1)) * 0.0027777778F;
      if (f1 >= 0.0F) {}
      for (f2 = f1;; f2 = -f1)
      {
        paramFloat = f1;
        if (f2 >= 0.5F) {
          paramFloat = f1 - (float)Math.ceil(0.5F + f1) + 1.0F;
        }
        f1 = paramFloat;
        if (paramFloat < 0.0F) {
          f1 = paramFloat + 1.0F;
        }
        return f1 * 360.0F;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/LegacySensorManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */