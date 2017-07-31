package android.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import java.io.IOException;
import libcore.util.ZoneInfoDB;
import libcore.util.ZoneInfoDB.TzData;

public class AlarmManager
{
  public static final String ACTION_NEXT_ALARM_CLOCK_CHANGED = "android.app.action.NEXT_ALARM_CLOCK_CHANGED";
  public static final int ELAPSED_REALTIME = 3;
  public static final int ELAPSED_REALTIME_WAKEUP = 2;
  public static final int FLAG_ALLOW_WHILE_IDLE = 4;
  public static final int FLAG_ALLOW_WHILE_IDLE_UNRESTRICTED = 8;
  public static final int FLAG_IDLE_UNTIL = 16;
  public static final int FLAG_STANDALONE = 1;
  public static final int FLAG_WAKE_FROM_IDLE = 2;
  public static final long INTERVAL_DAY = 86400000L;
  public static final long INTERVAL_FIFTEEN_MINUTES = 900000L;
  public static final long INTERVAL_HALF_DAY = 43200000L;
  public static final long INTERVAL_HALF_HOUR = 1800000L;
  public static final long INTERVAL_HOUR = 3600000L;
  public static final String POWER_OFF_ALARM_HANDLED = "1";
  public static final String POWER_OFF_ALARM_HANDLE_FILE = "/persist/alarm/powerOffAlarmHandle";
  public static final String POWER_OFF_ALARM_INSTANCE_FILE = "/persist/alarm/powerOffAlarmInstance";
  public static final String POWER_OFF_ALARM_NOT_HANDLED = "0";
  public static final String POWER_OFF_ALARM_NOT_SET = "0";
  public static final String POWER_OFF_ALARM_SET = "1";
  public static final String POWER_OFF_ALARM_SET_FILE = "/persist/alarm/powerOffAlarmSet";
  public static final String POWER_OFF_ALARM_TIMEZONE_FILE = "/persist/alarm/timezone";
  public static final int RTC = 1;
  public static final int RTC_POWEROFF_WAKEUP = 5;
  public static final int RTC_POWERUP = 5;
  public static final int RTC_WAKEUP = 0;
  private static final String TAG = "AlarmManager";
  public static final long WINDOW_EXACT = 0L;
  public static final long WINDOW_HEURISTIC = -1L;
  private static ArrayMap<OnAlarmListener, ListenerWrapper> sWrappers;
  private final boolean mAlwaysExact;
  private final Handler mMainThreadHandler;
  private final String mPackageName;
  private final IAlarmManager mService;
  private final int mTargetSdkVersion;
  
  AlarmManager(IAlarmManager paramIAlarmManager, Context paramContext)
  {
    this.mService = paramIAlarmManager;
    this.mPackageName = paramContext.getPackageName();
    this.mTargetSdkVersion = paramContext.getApplicationInfo().targetSdkVersion;
    if (this.mTargetSdkVersion < 19) {}
    for (boolean bool = true;; bool = false)
    {
      this.mAlwaysExact = bool;
      this.mMainThreadHandler = new Handler(paramContext.getMainLooper());
      return;
    }
  }
  
  private long legacyExactLength()
  {
    if (this.mAlwaysExact) {
      return 0L;
    }
    return -1L;
  }
  
  /* Error */
  public static String readPowerOffAlarmFile(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore_3
    //   6: new 149	java/io/BufferedReader
    //   9: dup
    //   10: new 151	java/io/FileReader
    //   13: dup
    //   14: aload_0
    //   15: invokespecial 154	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   18: invokespecial 157	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   21: astore_0
    //   22: aload_0
    //   23: invokevirtual 160	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   26: astore_1
    //   27: aload_0
    //   28: ifnull +7 -> 35
    //   31: aload_0
    //   32: invokevirtual 163	java/io/BufferedReader:close	()V
    //   35: aload_1
    //   36: areturn
    //   37: astore_0
    //   38: aload_0
    //   39: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   42: goto -7 -> 35
    //   45: astore_1
    //   46: aload_2
    //   47: astore_0
    //   48: aload_1
    //   49: astore_2
    //   50: aload_0
    //   51: astore_1
    //   52: aload_2
    //   53: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   56: aload_3
    //   57: astore_1
    //   58: aload_0
    //   59: ifnull -24 -> 35
    //   62: aload_0
    //   63: invokevirtual 163	java/io/BufferedReader:close	()V
    //   66: aconst_null
    //   67: areturn
    //   68: astore_0
    //   69: aload_0
    //   70: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   73: aconst_null
    //   74: areturn
    //   75: astore_0
    //   76: aload_1
    //   77: ifnull +7 -> 84
    //   80: aload_1
    //   81: invokevirtual 163	java/io/BufferedReader:close	()V
    //   84: aload_0
    //   85: athrow
    //   86: astore_1
    //   87: aload_1
    //   88: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   91: goto -7 -> 84
    //   94: astore_2
    //   95: aload_0
    //   96: astore_1
    //   97: aload_2
    //   98: astore_0
    //   99: goto -23 -> 76
    //   102: astore_2
    //   103: goto -53 -> 50
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	paramString	String
    //   1	35	1	str1	String
    //   45	4	1	localException1	Exception
    //   51	30	1	localObject1	Object
    //   86	2	1	localException2	Exception
    //   96	1	1	str2	String
    //   3	50	2	localException3	Exception
    //   94	4	2	localObject2	Object
    //   102	1	2	localException4	Exception
    //   5	52	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   31	35	37	java/lang/Exception
    //   6	22	45	java/lang/Exception
    //   62	66	68	java/lang/Exception
    //   6	22	75	finally
    //   52	56	75	finally
    //   80	84	86	java/lang/Exception
    //   22	27	94	finally
    //   22	27	102	java/lang/Exception
  }
  
  private void setImpl(int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2, PendingIntent paramPendingIntent, OnAlarmListener paramOnAlarmListener, String paramString, Handler paramHandler, WorkSource paramWorkSource, AlarmClockInfo paramAlarmClockInfo)
  {
    long l = paramLong1;
    if (paramLong1 < 0L) {
      l = 0L;
    }
    Object localObject = null;
    if (paramOnAlarmListener != null) {}
    for (;;)
    {
      try
      {
        if (sWrappers == null) {
          sWrappers = new ArrayMap();
        }
        ListenerWrapper localListenerWrapper = (ListenerWrapper)sWrappers.get(paramOnAlarmListener);
        localObject = localListenerWrapper;
        if (localListenerWrapper == null) {
          localObject = new ListenerWrapper(paramOnAlarmListener);
        }
      }
      finally
      {
        for (;;)
        {
          try
          {
            sWrappers.put(paramOnAlarmListener, localObject);
            if (paramHandler != null) {
              ((ListenerWrapper)localObject).setHandler(paramHandler);
            }
          }
          finally {}
          try
          {
            this.mService.set(this.mPackageName, paramInt1, l, paramLong2, paramLong3, paramInt2, paramPendingIntent, (IAlarmListener)localObject, paramString, paramWorkSource, paramAlarmClockInfo);
            return;
          }
          catch (RemoteException paramPendingIntent)
          {
            throw paramPendingIntent.rethrowFromSystemServer();
          }
        }
        paramPendingIntent = finally;
      }
      throw paramPendingIntent;
      paramHandler = this.mMainThreadHandler;
    }
  }
  
  /* Error */
  public static void writePowerOffAlarmFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: new 204	java/io/FileWriter
    //   7: dup
    //   8: aload_0
    //   9: iconst_0
    //   10: invokespecial 207	java/io/FileWriter:<init>	(Ljava/lang/String;Z)V
    //   13: astore_0
    //   14: aload_0
    //   15: aload_1
    //   16: invokevirtual 212	java/io/Writer:write	(Ljava/lang/String;)V
    //   19: aload_0
    //   20: invokevirtual 217	java/io/OutputStreamWriter:flush	()V
    //   23: aload_0
    //   24: ifnull +7 -> 31
    //   27: aload_0
    //   28: invokevirtual 218	java/io/OutputStreamWriter:close	()V
    //   31: return
    //   32: astore_0
    //   33: aload_0
    //   34: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   37: goto -6 -> 31
    //   40: astore_1
    //   41: aload_3
    //   42: astore_0
    //   43: aload_0
    //   44: astore_2
    //   45: aload_1
    //   46: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   49: aload_0
    //   50: ifnull -19 -> 31
    //   53: aload_0
    //   54: invokevirtual 218	java/io/OutputStreamWriter:close	()V
    //   57: return
    //   58: astore_0
    //   59: aload_0
    //   60: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   63: return
    //   64: astore_0
    //   65: aload_2
    //   66: ifnull +7 -> 73
    //   69: aload_2
    //   70: invokevirtual 218	java/io/OutputStreamWriter:close	()V
    //   73: aload_0
    //   74: athrow
    //   75: astore_1
    //   76: aload_1
    //   77: invokevirtual 168	java/lang/Throwable:printStackTrace	()V
    //   80: goto -7 -> 73
    //   83: astore_1
    //   84: aload_0
    //   85: astore_2
    //   86: aload_1
    //   87: astore_0
    //   88: goto -23 -> 65
    //   91: astore_1
    //   92: goto -49 -> 43
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	paramString1	String
    //   0	95	1	paramString2	String
    //   1	85	2	str	String
    //   3	39	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	31	32	java/lang/Exception
    //   4	14	40	java/lang/Exception
    //   53	57	58	java/lang/Exception
    //   4	14	64	finally
    //   45	49	64	finally
    //   69	73	75	java/lang/Exception
    //   14	23	83	finally
    //   14	23	91	java/lang/Exception
  }
  
  public void cancel(OnAlarmListener paramOnAlarmListener)
  {
    if (paramOnAlarmListener == null) {
      throw new NullPointerException("cancel() called with a null OnAlarmListener");
    }
    ListenerWrapper localListenerWrapper = null;
    try
    {
      if (sWrappers != null) {
        localListenerWrapper = (ListenerWrapper)sWrappers.get(paramOnAlarmListener);
      }
      if (localListenerWrapper == null)
      {
        Log.w("AlarmManager", "Unrecognized alarm listener " + paramOnAlarmListener);
        return;
      }
    }
    finally {}
    localListenerWrapper.cancel();
  }
  
  public void cancel(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null)
    {
      if (this.mTargetSdkVersion >= 24) {
        throw new NullPointerException("cancel() called with a null PendingIntent");
      }
      Log.e("AlarmManager", "cancel() called with a null PendingIntent");
      return;
    }
    try
    {
      this.mService.remove(paramPendingIntent, null);
      return;
    }
    catch (RemoteException paramPendingIntent)
    {
      throw paramPendingIntent.rethrowFromSystemServer();
    }
  }
  
  public AlarmClockInfo getNextAlarmClock()
  {
    return getNextAlarmClock(UserHandle.myUserId());
  }
  
  public AlarmClockInfo getNextAlarmClock(int paramInt)
  {
    try
    {
      AlarmClockInfo localAlarmClockInfo = this.mService.getNextAlarmClock(paramInt);
      return localAlarmClockInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public long getNextWakeFromIdleTime()
  {
    try
    {
      long l = this.mService.getNextWakeFromIdleTime();
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void set(int paramInt, long paramLong1, long paramLong2, long paramLong3, OnAlarmListener paramOnAlarmListener, Handler paramHandler, WorkSource paramWorkSource)
  {
    setImpl(paramInt, paramLong1, paramLong2, paramLong3, 0, null, paramOnAlarmListener, null, paramHandler, paramWorkSource, null);
  }
  
  public void set(int paramInt, long paramLong1, long paramLong2, long paramLong3, PendingIntent paramPendingIntent, WorkSource paramWorkSource)
  {
    setImpl(paramInt, paramLong1, paramLong2, paramLong3, 0, paramPendingIntent, null, null, null, paramWorkSource, null);
  }
  
  public void set(int paramInt, long paramLong1, long paramLong2, long paramLong3, String paramString, OnAlarmListener paramOnAlarmListener, Handler paramHandler, WorkSource paramWorkSource)
  {
    setImpl(paramInt, paramLong1, paramLong2, paramLong3, 0, null, paramOnAlarmListener, paramString, paramHandler, paramWorkSource, null);
  }
  
  public void set(int paramInt, long paramLong, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong, legacyExactLength(), 0L, 0, paramPendingIntent, null, null, null, null, null);
  }
  
  public void set(int paramInt, long paramLong, String paramString, OnAlarmListener paramOnAlarmListener, Handler paramHandler)
  {
    setImpl(paramInt, paramLong, legacyExactLength(), 0L, 0, null, paramOnAlarmListener, paramString, paramHandler, null, null);
  }
  
  public void setAlarmClock(AlarmClockInfo paramAlarmClockInfo, PendingIntent paramPendingIntent)
  {
    setImpl(0, paramAlarmClockInfo.getTriggerTime(), 0L, 0L, 0, paramPendingIntent, null, null, null, null, paramAlarmClockInfo);
  }
  
  public void setAndAllowWhileIdle(int paramInt, long paramLong, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong, -1L, 0L, 4, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setExact(int paramInt, long paramLong, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong, 0L, 0L, 0, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setExact(int paramInt, long paramLong, String paramString, OnAlarmListener paramOnAlarmListener, Handler paramHandler)
  {
    setImpl(paramInt, paramLong, 0L, 0L, 0, null, paramOnAlarmListener, paramString, paramHandler, null, null);
  }
  
  public void setExactAndAllowWhileIdle(int paramInt, long paramLong, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong, 0L, 0L, 4, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setIdleUntil(int paramInt, long paramLong, String paramString, OnAlarmListener paramOnAlarmListener, Handler paramHandler)
  {
    setImpl(paramInt, paramLong, 0L, 0L, 16, null, paramOnAlarmListener, paramString, paramHandler, null, null);
  }
  
  public void setInexactRepeating(int paramInt, long paramLong1, long paramLong2, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong1, -1L, paramLong2, 0, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setRepeating(int paramInt, long paramLong1, long paramLong2, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong1, legacyExactLength(), paramLong2, 0, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setTime(long paramLong)
  {
    try
    {
      this.mService.setTime(paramLong);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setTimeZone(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return;
    }
    int i;
    if (this.mTargetSdkVersion >= 23) {
      i = 0;
    }
    try
    {
      boolean bool = ZoneInfoDB.getInstance().hasTimeZone(paramString);
      i = bool;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    if (i == 0) {
      throw new IllegalArgumentException("Timezone: " + paramString + " is not an Olson ID");
    }
    try
    {
      this.mService.setTimeZone(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setWindow(int paramInt, long paramLong1, long paramLong2, PendingIntent paramPendingIntent)
  {
    setImpl(paramInt, paramLong1, paramLong2, 0L, 0, paramPendingIntent, null, null, null, null, null);
  }
  
  public void setWindow(int paramInt, long paramLong1, long paramLong2, String paramString, OnAlarmListener paramOnAlarmListener, Handler paramHandler)
  {
    setImpl(paramInt, paramLong1, paramLong2, 0L, 0, null, paramOnAlarmListener, paramString, paramHandler, null, null);
  }
  
  public static final class AlarmClockInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<AlarmClockInfo> CREATOR = new Parcelable.Creator()
    {
      public AlarmManager.AlarmClockInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AlarmManager.AlarmClockInfo(paramAnonymousParcel);
      }
      
      public AlarmManager.AlarmClockInfo[] newArray(int paramAnonymousInt)
      {
        return new AlarmManager.AlarmClockInfo[paramAnonymousInt];
      }
    };
    private final PendingIntent mShowIntent;
    private final long mTriggerTime;
    
    public AlarmClockInfo(long paramLong, PendingIntent paramPendingIntent)
    {
      this.mTriggerTime = paramLong;
      this.mShowIntent = paramPendingIntent;
    }
    
    AlarmClockInfo(Parcel paramParcel)
    {
      this.mTriggerTime = paramParcel.readLong();
      this.mShowIntent = ((PendingIntent)paramParcel.readParcelable(PendingIntent.class.getClassLoader()));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public PendingIntent getShowIntent()
    {
      return this.mShowIntent;
    }
    
    public long getTriggerTime()
    {
      return this.mTriggerTime;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.mTriggerTime);
      paramParcel.writeParcelable(this.mShowIntent, paramInt);
    }
  }
  
  final class ListenerWrapper
    extends IAlarmListener.Stub
    implements Runnable
  {
    IAlarmCompleteListener mCompletion;
    Handler mHandler;
    final AlarmManager.OnAlarmListener mListener;
    
    public ListenerWrapper(AlarmManager.OnAlarmListener paramOnAlarmListener)
    {
      this.mListener = paramOnAlarmListener;
    }
    
    /* Error */
    public void cancel()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 21	android/app/AlarmManager$ListenerWrapper:this$0	Landroid/app/AlarmManager;
      //   4: invokestatic 34	android/app/AlarmManager:-get0	(Landroid/app/AlarmManager;)Landroid/app/IAlarmManager;
      //   7: aconst_null
      //   8: aload_0
      //   9: invokeinterface 40 3 0
      //   14: ldc 8
      //   16: monitorenter
      //   17: invokestatic 44	android/app/AlarmManager:-get1	()Landroid/util/ArrayMap;
      //   20: ifnull +14 -> 34
      //   23: invokestatic 44	android/app/AlarmManager:-get1	()Landroid/util/ArrayMap;
      //   26: aload_0
      //   27: getfield 26	android/app/AlarmManager$ListenerWrapper:mListener	Landroid/app/AlarmManager$OnAlarmListener;
      //   30: invokevirtual 49	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   33: pop
      //   34: ldc 8
      //   36: monitorexit
      //   37: return
      //   38: astore_1
      //   39: aload_1
      //   40: invokevirtual 53	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
      //   43: athrow
      //   44: astore_1
      //   45: ldc 8
      //   47: monitorexit
      //   48: aload_1
      //   49: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	50	0	this	ListenerWrapper
      //   38	2	1	localRemoteException	RemoteException
      //   44	5	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   0	14	38	android/os/RemoteException
      //   17	34	44	finally
    }
    
    public void doAlarm(IAlarmCompleteListener paramIAlarmCompleteListener)
    {
      this.mCompletion = paramIAlarmCompleteListener;
      this.mHandler.post(this);
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: ldc 8
      //   2: monitorenter
      //   3: invokestatic 44	android/app/AlarmManager:-get1	()Landroid/util/ArrayMap;
      //   6: ifnull +14 -> 20
      //   9: invokestatic 44	android/app/AlarmManager:-get1	()Landroid/util/ArrayMap;
      //   12: aload_0
      //   13: getfield 26	android/app/AlarmManager$ListenerWrapper:mListener	Landroid/app/AlarmManager$OnAlarmListener;
      //   16: invokevirtual 49	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   19: pop
      //   20: ldc 8
      //   22: monitorexit
      //   23: aload_0
      //   24: getfield 26	android/app/AlarmManager$ListenerWrapper:mListener	Landroid/app/AlarmManager$OnAlarmListener;
      //   27: invokeinterface 73 1 0
      //   32: aload_0
      //   33: getfield 57	android/app/AlarmManager$ListenerWrapper:mCompletion	Landroid/app/IAlarmCompleteListener;
      //   36: aload_0
      //   37: invokeinterface 79 2 0
      //   42: return
      //   43: astore_1
      //   44: ldc 8
      //   46: monitorexit
      //   47: aload_1
      //   48: athrow
      //   49: astore_1
      //   50: ldc 81
      //   52: ldc 83
      //   54: aload_1
      //   55: invokestatic 89	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   58: pop
      //   59: return
      //   60: astore_1
      //   61: aload_0
      //   62: getfield 57	android/app/AlarmManager$ListenerWrapper:mCompletion	Landroid/app/IAlarmCompleteListener;
      //   65: aload_0
      //   66: invokeinterface 79 2 0
      //   71: aload_1
      //   72: athrow
      //   73: astore_2
      //   74: ldc 81
      //   76: ldc 83
      //   78: aload_2
      //   79: invokestatic 89	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   82: pop
      //   83: goto -12 -> 71
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	86	0	this	ListenerWrapper
      //   43	5	1	localObject1	Object
      //   49	6	1	localException1	Exception
      //   60	12	1	localObject2	Object
      //   73	6	2	localException2	Exception
      // Exception table:
      //   from	to	target	type
      //   3	20	43	finally
      //   32	42	49	java/lang/Exception
      //   23	32	60	finally
      //   61	71	73	java/lang/Exception
    }
    
    public void setHandler(Handler paramHandler)
    {
      this.mHandler = paramHandler;
    }
  }
  
  public static abstract interface OnAlarmListener
  {
    public abstract void onAlarm();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AlarmManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */