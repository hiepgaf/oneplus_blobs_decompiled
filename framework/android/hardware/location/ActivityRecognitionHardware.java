package android.hardware.location;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Array;

public class ActivityRecognitionHardware
  extends IActivityRecognitionHardware.Stub
{
  private static final boolean DEBUG = Log.isLoggable("ActivityRecognitionHW", 3);
  private static final String ENFORCE_HW_PERMISSION_MESSAGE = "Permission 'android.permission.LOCATION_HARDWARE' not granted to access ActivityRecognitionHardware";
  private static final int EVENT_TYPE_COUNT = 3;
  private static final int EVENT_TYPE_DISABLED = 0;
  private static final int EVENT_TYPE_ENABLED = 1;
  private static final String HARDWARE_PERMISSION = "android.permission.LOCATION_HARDWARE";
  private static final int INVALID_ACTIVITY_TYPE = -1;
  private static final int NATIVE_SUCCESS_RESULT = 0;
  private static final String TAG = "ActivityRecognitionHW";
  private static ActivityRecognitionHardware sSingletonInstance;
  private static final Object sSingletonInstanceLock = new Object();
  private final Context mContext;
  private final SinkList mSinks = new SinkList(null);
  private final String[] mSupportedActivities;
  private final int mSupportedActivitiesCount;
  private final int[][] mSupportedActivitiesEnabledEvents;
  
  static
  {
    nativeClassInit();
  }
  
  private ActivityRecognitionHardware(Context paramContext)
  {
    nativeInitialize();
    this.mContext = paramContext;
    this.mSupportedActivities = fetchSupportedActivities();
    this.mSupportedActivitiesCount = this.mSupportedActivities.length;
    this.mSupportedActivitiesEnabledEvents = ((int[][])Array.newInstance(Integer.TYPE, new int[] { this.mSupportedActivitiesCount, 3 }));
  }
  
  private void checkPermissions()
  {
    this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Permission 'android.permission.LOCATION_HARDWARE' not granted to access ActivityRecognitionHardware");
  }
  
  private String[] fetchSupportedActivities()
  {
    String[] arrayOfString = nativeGetSupportedActivities();
    if (arrayOfString != null) {
      return arrayOfString;
    }
    return new String[0];
  }
  
  private String getActivityName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mSupportedActivities.length))
    {
      Log.e("ActivityRecognitionHW", String.format("Invalid ActivityType: %d, SupportedActivities: %d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(this.mSupportedActivities.length) }));
      return null;
    }
    return this.mSupportedActivities[paramInt];
  }
  
  private int getActivityType(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return -1;
    }
    int j = this.mSupportedActivities.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(this.mSupportedActivities[i])) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static ActivityRecognitionHardware getInstance(Context paramContext)
  {
    synchronized (sSingletonInstanceLock)
    {
      if (sSingletonInstance == null) {
        sSingletonInstance = new ActivityRecognitionHardware(paramContext);
      }
      paramContext = sSingletonInstance;
      return paramContext;
    }
  }
  
  public static boolean isSupported()
  {
    return nativeIsSupported();
  }
  
  private static native void nativeClassInit();
  
  private native int nativeDisableActivityEvent(int paramInt1, int paramInt2);
  
  private native int nativeEnableActivityEvent(int paramInt1, int paramInt2, long paramLong);
  
  private native int nativeFlush();
  
  private native String[] nativeGetSupportedActivities();
  
  private native void nativeInitialize();
  
  private static native boolean nativeIsSupported();
  
  private native void nativeRelease();
  
  private void onActivityChanged(Event[] paramArrayOfEvent)
  {
    if ((paramArrayOfEvent == null) || (paramArrayOfEvent.length == 0))
    {
      if (DEBUG) {
        Log.d("ActivityRecognitionHW", "No events to broadcast for onActivityChanged.");
      }
      return;
    }
    int j = paramArrayOfEvent.length;
    Object localObject = new ActivityRecognitionEvent[j];
    int i = 0;
    while (i < j)
    {
      Event localEvent = paramArrayOfEvent[i];
      localObject[i] = new ActivityRecognitionEvent(getActivityName(localEvent.activity), localEvent.type, localEvent.timestamp);
      i += 1;
    }
    paramArrayOfEvent = new ActivityChangedEvent((ActivityRecognitionEvent[])localObject);
    j = this.mSinks.beginBroadcast();
    i = 0;
    for (;;)
    {
      if (i < j)
      {
        localObject = (IActivityRecognitionHardwareSink)this.mSinks.getBroadcastItem(i);
        try
        {
          ((IActivityRecognitionHardwareSink)localObject).onActivityChanged(paramArrayOfEvent);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.e("ActivityRecognitionHW", "Error delivering activity changed event.", localRemoteException);
          }
        }
      }
    }
    this.mSinks.finishBroadcast();
  }
  
  public boolean disableActivityEvent(String paramString, int paramInt)
  {
    checkPermissions();
    int i = getActivityType(paramString);
    if (i == -1) {
      return false;
    }
    if (nativeDisableActivityEvent(i, paramInt) == 0)
    {
      this.mSupportedActivitiesEnabledEvents[i][paramInt] = 0;
      return true;
    }
    return false;
  }
  
  public boolean enableActivityEvent(String paramString, int paramInt, long paramLong)
  {
    checkPermissions();
    int i = getActivityType(paramString);
    if (i == -1) {
      return false;
    }
    if (nativeEnableActivityEvent(i, paramInt, paramLong) == 0)
    {
      this.mSupportedActivitiesEnabledEvents[i][paramInt] = 1;
      return true;
    }
    return false;
  }
  
  public boolean flush()
  {
    boolean bool = false;
    checkPermissions();
    if (nativeFlush() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public String[] getSupportedActivities()
  {
    checkPermissions();
    return this.mSupportedActivities;
  }
  
  public boolean isActivitySupported(String paramString)
  {
    checkPermissions();
    return getActivityType(paramString) != -1;
  }
  
  public boolean registerSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
  {
    checkPermissions();
    return this.mSinks.register(paramIActivityRecognitionHardwareSink);
  }
  
  public boolean unregisterSink(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
  {
    checkPermissions();
    return this.mSinks.unregister(paramIActivityRecognitionHardwareSink);
  }
  
  private static class Event
  {
    public int activity;
    public long timestamp;
    public int type;
  }
  
  private class SinkList
    extends RemoteCallbackList<IActivityRecognitionHardwareSink>
  {
    private SinkList() {}
    
    private void disableActivityEventIfEnabled(int paramInt1, int paramInt2)
    {
      if (ActivityRecognitionHardware.-get3(ActivityRecognitionHardware.this)[paramInt1][paramInt2] != 1) {
        return;
      }
      int i = ActivityRecognitionHardware.-wrap0(ActivityRecognitionHardware.this, paramInt1, paramInt2);
      ActivityRecognitionHardware.-get3(ActivityRecognitionHardware.this)[paramInt1][paramInt2] = 0;
      Log.e("ActivityRecognitionHW", String.format("DisableActivityEvent: activityType=%d, eventType=%d, result=%d", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(i) }));
    }
    
    public void onCallbackDied(IActivityRecognitionHardwareSink paramIActivityRecognitionHardwareSink)
    {
      int i = ActivityRecognitionHardware.-get1(ActivityRecognitionHardware.this).getRegisteredCallbackCount();
      if (ActivityRecognitionHardware.-get0()) {
        Log.d("ActivityRecognitionHW", "RegisteredCallbackCount: " + i);
      }
      if (i != 0) {
        return;
      }
      i = 0;
      while (i < ActivityRecognitionHardware.-get2(ActivityRecognitionHardware.this))
      {
        int j = 0;
        while (j < 3)
        {
          disableActivityEventIfEnabled(i, j);
          j += 1;
        }
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ActivityRecognitionHardware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */