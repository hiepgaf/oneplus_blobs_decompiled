package android.os;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public final class PowerManager
{
  public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
  public static final String ACTION_DEVICE_IDLE_MODE_CHANGED = "android.os.action.DEVICE_IDLE_MODE_CHANGED";
  public static final String ACTION_LIGHT_DEVICE_IDLE_MODE_CHANGED = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED";
  public static final String ACTION_POWER_SAVE_MODE_CHANGED = "android.os.action.POWER_SAVE_MODE_CHANGED";
  public static final String ACTION_POWER_SAVE_MODE_CHANGED_INTERNAL = "android.os.action.POWER_SAVE_MODE_CHANGED_INTERNAL";
  public static final String ACTION_POWER_SAVE_MODE_CHANGING = "android.os.action.POWER_SAVE_MODE_CHANGING";
  public static final String ACTION_POWER_SAVE_TEMP_WHITELIST_CHANGED = "android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED";
  public static final String ACTION_POWER_SAVE_WHITELIST_CHANGED = "android.os.action.POWER_SAVE_WHITELIST_CHANGED";
  public static final String ACTION_SCREEN_BRIGHTNESS_BOOST_CHANGED = "android.os.action.SCREEN_BRIGHTNESS_BOOST_CHANGED";
  public static final int BRIGHTNESS_DEFAULT = -1;
  public static final int BRIGHTNESS_OFF = 0;
  public static final int BRIGHTNESS_ON = 255;
  public static final int DOZE_WAKE_LOCK = 64;
  public static final int DRAW_WAKE_LOCK = 128;
  public static final String EXTRA_POWER_SAVE_MODE = "mode";
  @Deprecated
  public static final int FULL_WAKE_LOCK = 26;
  public static final int GO_TO_SLEEP_FLAG_NO_DOZE = 1;
  public static final int GO_TO_SLEEP_REASON_APPLICATION = 0;
  public static final int GO_TO_SLEEP_REASON_DEVICE_ADMIN = 1;
  public static final int GO_TO_SLEEP_REASON_FINGERPRINT = 11;
  public static final int GO_TO_SLEEP_REASON_HDMI = 5;
  public static final int GO_TO_SLEEP_REASON_LID_SWITCH = 3;
  public static final int GO_TO_SLEEP_REASON_POWER_BUTTON = 4;
  public static final int GO_TO_SLEEP_REASON_PROXIMITY = 7;
  public static final int GO_TO_SLEEP_REASON_SLEEP_BUTTON = 6;
  public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
  public static final int ON_AFTER_RELEASE = 536870912;
  public static final int PARTIAL_WAKE_LOCK = 1;
  public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
  public static final String REBOOT_RECOVERY = "recovery";
  public static final String REBOOT_RECOVERY_UPDATE = "recovery-update";
  public static final String REBOOT_REQUESTED_BY_DEVICE_OWNER = "deviceowner";
  public static final String REBOOT_SAFE_MODE = "safemode";
  public static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY = 1;
  @Deprecated
  public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
  @Deprecated
  public static final int SCREEN_DIM_WAKE_LOCK = 6;
  public static final String SCREEN_OFF_REASON = "screenoff_reason";
  public static final String SHUTDOWN_USER_REQUESTED = "userrequested";
  private static final String TAG = "PowerManager";
  public static final int UNIMPORTANT_FOR_LOGGING = 1073741824;
  public static final int USER_ACTIVITY_EVENT_ACCESSIBILITY = 3;
  public static final int USER_ACTIVITY_EVENT_BUTTON = 1;
  public static final int USER_ACTIVITY_EVENT_OTHER = 0;
  public static final int USER_ACTIVITY_EVENT_TOUCH = 2;
  public static final int USER_ACTIVITY_FLAG_INDIRECT = 2;
  public static final int USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS = 1;
  public static final int WAKE_LOCK_LEVEL_MASK = 65535;
  final Context mContext;
  final Handler mHandler;
  IDeviceIdleController mIDeviceIdleController;
  final IPowerManager mService;
  
  public PowerManager(Context paramContext, IPowerManager paramIPowerManager, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mService = paramIPowerManager;
    this.mHandler = paramHandler;
  }
  
  public static boolean useTwilightAdjustmentFeature()
  {
    return SystemProperties.getBoolean("persist.power.usetwilightadj", false);
  }
  
  public static void validateWakeLockParameters(int paramInt, String paramString)
  {
    switch (0xFFFF & paramInt)
    {
    default: 
      throw new IllegalArgumentException("Must specify a valid wake lock level.");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("The tag must not be null.");
    }
  }
  
  public void boostScreenBrightness(long paramLong)
  {
    try
    {
      this.mService.boostScreenBrightness(paramLong);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getDefaultButtonBrightness()
  {
    return this.mContext.getResources().getInteger(84475905);
  }
  
  public int getDefaultScreenBrightnessSetting()
  {
    return this.mContext.getResources().getInteger(17694822);
  }
  
  public int getMaximumScreenBrightnessSetting()
  {
    return this.mContext.getResources().getInteger(17694821);
  }
  
  public int getMinimumScreenBrightnessSetting()
  {
    return this.mContext.getResources().getInteger(17694820);
  }
  
  public void goToSleep(long paramLong)
  {
    goToSleep(paramLong, 0, 0);
  }
  
  public void goToSleep(long paramLong, int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.goToSleep(paramLong, paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isDeviceIdleMode()
  {
    try
    {
      boolean bool = this.mService.isDeviceIdleMode();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  /* Error */
  public boolean isIgnoringBatteryOptimizations(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 200	android/os/PowerManager:mIDeviceIdleController	Landroid/os/IDeviceIdleController;
    //   6: ifnonnull +15 -> 21
    //   9: aload_0
    //   10: ldc -54
    //   12: invokestatic 208	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   15: invokestatic 214	android/os/IDeviceIdleController$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/IDeviceIdleController;
    //   18: putfield 200	android/os/PowerManager:mIDeviceIdleController	Landroid/os/IDeviceIdleController;
    //   21: aload_0
    //   22: monitorexit
    //   23: aload_0
    //   24: getfield 200	android/os/PowerManager:mIDeviceIdleController	Landroid/os/IDeviceIdleController;
    //   27: aload_1
    //   28: invokeinterface 219 2 0
    //   33: istore_2
    //   34: iload_2
    //   35: ireturn
    //   36: astore_1
    //   37: aload_0
    //   38: monitorexit
    //   39: aload_1
    //   40: athrow
    //   41: astore_1
    //   42: aload_1
    //   43: invokevirtual 167	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	PowerManager
    //   0	47	1	paramString	String
    //   33	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	21	36	finally
    //   23	34	41	android/os/RemoteException
  }
  
  public boolean isInteractive()
  {
    try
    {
      boolean bool = this.mService.isInteractive();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isLightDeviceIdleMode()
  {
    try
    {
      boolean bool = this.mService.isLightDeviceIdleMode();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isPowerSaveMode()
  {
    try
    {
      boolean bool = this.mService.isPowerSaveMode();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isScreenBrightnessBoosted()
  {
    try
    {
      boolean bool = this.mService.isScreenBrightnessBoosted();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean isScreenOn()
  {
    return isInteractive();
  }
  
  public boolean isSustainedPerformanceModeSupported()
  {
    return this.mContext.getResources().getBoolean(17957045);
  }
  
  public boolean isWakeLockLevelSupported(int paramInt)
  {
    try
    {
      boolean bool = this.mService.isWakeLockLevelSupported(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void nap(long paramLong)
  {
    try
    {
      this.mService.nap(paramLong);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WakeLock newWakeLock(int paramInt, String paramString)
  {
    validateWakeLockParameters(paramInt, paramString);
    return new WakeLock(paramInt, paramString, this.mContext.getOpPackageName());
  }
  
  public void reboot(String paramString)
  {
    try
    {
      this.mService.reboot(false, paramString, true);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void rebootSafeMode()
  {
    try
    {
      this.mService.rebootSafeMode(false, true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setBacklightBrightness(int paramInt)
  {
    try
    {
      this.mService.setTemporaryScreenBrightnessSettingOverride(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean setPowerSaveMode(boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mService.setPowerSaveMode(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void shutdown(boolean paramBoolean1, String paramString, boolean paramBoolean2)
  {
    try
    {
      this.mService.shutdown(paramBoolean1, paramString, paramBoolean2);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void userActivity(long paramLong, int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.userActivity(paramLong, paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void userActivity(long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      userActivity(paramLong, 0, i);
      return;
    }
  }
  
  public void wakeUp(long paramLong)
  {
    try
    {
      this.mService.wakeUp(paramLong, "wakeUp", this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void wakeUp(long paramLong, String paramString)
  {
    try
    {
      this.mService.wakeUp(paramLong, paramString, this.mContext.getOpPackageName());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public final class WakeLock
  {
    private int mCount;
    private int mFlags;
    private boolean mHeld;
    private String mHistoryTag;
    private final String mPackageName;
    private boolean mRefCounted = true;
    private final Runnable mReleaser = new Runnable()
    {
      public void run()
      {
        PowerManager.WakeLock.this.release();
      }
    };
    private String mTag;
    private final IBinder mToken;
    private final String mTraceName;
    private WorkSource mWorkSource;
    
    WakeLock(int paramInt, String paramString1, String paramString2)
    {
      this.mFlags = paramInt;
      this.mTag = paramString1;
      this.mPackageName = paramString2;
      this.mToken = new Binder();
      this.mTraceName = ("WakeLock (" + this.mTag + ")");
    }
    
    private void acquireLocked()
    {
      if (this.mRefCounted)
      {
        int i = this.mCount;
        this.mCount = (i + 1);
        if (i != 0) {}
      }
      else
      {
        PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
        Trace.asyncTraceBegin(131072L, this.mTraceName, 0);
      }
      try
      {
        PowerManager.this.mService.acquireWakeLock(this.mToken, this.mFlags, this.mTag, this.mPackageName, this.mWorkSource, this.mHistoryTag);
        this.mHeld = true;
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void acquire()
    {
      synchronized (this.mToken)
      {
        acquireLocked();
        return;
      }
    }
    
    public void acquire(long paramLong)
    {
      synchronized (this.mToken)
      {
        acquireLocked();
        PowerManager.this.mHandler.postDelayed(this.mReleaser, paramLong);
        return;
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      synchronized (this.mToken)
      {
        if (this.mHeld)
        {
          Log.wtf("PowerManager", "WakeLock finalized while still held: " + this.mTag);
          Trace.asyncTraceEnd(131072L, this.mTraceName, 0);
        }
        try
        {
          PowerManager.this.mService.releaseWakeLock(this.mToken, 0);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
    }
    
    public String getTag()
    {
      return this.mTag;
    }
    
    public boolean isHeld()
    {
      synchronized (this.mToken)
      {
        boolean bool = this.mHeld;
        return bool;
      }
    }
    
    public void release()
    {
      release(0);
    }
    
    public void release(int paramInt)
    {
      synchronized (this.mToken)
      {
        if (this.mRefCounted)
        {
          int i = this.mCount - 1;
          this.mCount = i;
          if (i != 0) {}
        }
        else
        {
          PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
          if (this.mHeld) {
            Trace.asyncTraceEnd(131072L, this.mTraceName, 0);
          }
        }
      }
    }
    
    public void setHistoryTag(String paramString)
    {
      this.mHistoryTag = paramString;
    }
    
    public void setReferenceCounted(boolean paramBoolean)
    {
      synchronized (this.mToken)
      {
        this.mRefCounted = paramBoolean;
        return;
      }
    }
    
    public void setTag(String paramString)
    {
      this.mTag = paramString;
    }
    
    public void setUnimportantForLogging(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= 0x40000000;
        return;
      }
      this.mFlags &= 0xBFFFFFFF;
    }
    
    /* Error */
    public void setWorkSource(WorkSource paramWorkSource)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 56	android/os/PowerManager$WakeLock:mToken	Landroid/os/IBinder;
      //   4: astore 5
      //   6: aload 5
      //   8: monitorenter
      //   9: aload_1
      //   10: astore 4
      //   12: aload_1
      //   13: ifnull +16 -> 29
      //   16: aload_1
      //   17: astore 4
      //   19: aload_1
      //   20: invokevirtual 183	android/os/WorkSource:size	()I
      //   23: ifne +6 -> 29
      //   26: aconst_null
      //   27: astore 4
      //   29: aload 4
      //   31: ifnonnull +63 -> 94
      //   34: aload_0
      //   35: getfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   38: ifnull +51 -> 89
      //   41: iconst_1
      //   42: istore_2
      //   43: aload_0
      //   44: aconst_null
      //   45: putfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   48: iload_2
      //   49: ifeq +36 -> 85
      //   52: aload_0
      //   53: getfield 113	android/os/PowerManager$WakeLock:mHeld	Z
      //   56: istore_2
      //   57: iload_2
      //   58: ifeq +27 -> 85
      //   61: aload_0
      //   62: getfield 35	android/os/PowerManager$WakeLock:this$0	Landroid/os/PowerManager;
      //   65: getfield 101	android/os/PowerManager:mService	Landroid/os/IPowerManager;
      //   68: aload_0
      //   69: getfield 56	android/os/PowerManager$WakeLock:mToken	Landroid/os/IBinder;
      //   72: aload_0
      //   73: getfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   76: aload_0
      //   77: getfield 105	android/os/PowerManager$WakeLock:mHistoryTag	Ljava/lang/String;
      //   80: invokeinterface 187 4 0
      //   85: aload 5
      //   87: monitorexit
      //   88: return
      //   89: iconst_0
      //   90: istore_2
      //   91: goto -48 -> 43
      //   94: aload_0
      //   95: getfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   98: ifnonnull +27 -> 125
      //   101: iconst_1
      //   102: istore_2
      //   103: aload_0
      //   104: new 179	android/os/WorkSource
      //   107: dup
      //   108: aload 4
      //   110: invokespecial 189	android/os/WorkSource:<init>	(Landroid/os/WorkSource;)V
      //   113: putfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   116: goto -68 -> 48
      //   119: astore_1
      //   120: aload 5
      //   122: monitorexit
      //   123: aload_1
      //   124: athrow
      //   125: aload_0
      //   126: getfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   129: aload 4
      //   131: invokevirtual 193	android/os/WorkSource:diff	(Landroid/os/WorkSource;)Z
      //   134: istore_3
      //   135: iload_3
      //   136: istore_2
      //   137: iload_3
      //   138: ifeq -90 -> 48
      //   141: aload_0
      //   142: getfield 103	android/os/PowerManager$WakeLock:mWorkSource	Landroid/os/WorkSource;
      //   145: aload 4
      //   147: invokevirtual 196	android/os/WorkSource:set	(Landroid/os/WorkSource;)V
      //   150: iload_3
      //   151: istore_2
      //   152: goto -104 -> 48
      //   155: astore_1
      //   156: aload_1
      //   157: invokevirtual 117	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   160: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	161	0	this	WakeLock
      //   0	161	1	paramWorkSource	WorkSource
      //   42	110	2	bool1	boolean
      //   134	17	3	bool2	boolean
      //   10	136	4	localWorkSource	WorkSource
      //   4	117	5	localIBinder	IBinder
      // Exception table:
      //   from	to	target	type
      //   19	26	119	finally
      //   34	41	119	finally
      //   43	48	119	finally
      //   52	57	119	finally
      //   61	85	119	finally
      //   94	101	119	finally
      //   103	116	119	finally
      //   125	135	119	finally
      //   141	150	119	finally
      //   156	161	119	finally
      //   61	85	155	android/os/RemoteException
    }
    
    public String toString()
    {
      synchronized (this.mToken)
      {
        String str = "WakeLock{" + Integer.toHexString(System.identityHashCode(this)) + " held=" + this.mHeld + ", refCount=" + this.mCount + "}";
        return str;
      }
    }
    
    public Runnable wrap(Runnable paramRunnable)
    {
      acquire();
      return new -java_lang_Runnable_wrap_java_lang_Runnable_r_LambdaImpl0(paramRunnable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PowerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */