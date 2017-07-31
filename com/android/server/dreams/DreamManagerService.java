package com.android.server.dreams;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.hardware.input.InputManagerInternal;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager.Stub;
import android.util.Slog;
import android.view.Display;
import com.android.internal.hardware.AmbientDisplayConfiguration;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.DumpUtils.Dump;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;

public final class DreamManagerService
  extends SystemService
{
  private static final boolean DEBUG = false;
  private static final String TAG = "DreamManagerService";
  private final Context mContext;
  private final DreamController mController;
  private final DreamController.Listener mControllerListener = new DreamController.Listener()
  {
    public void onDreamStopped(Binder paramAnonymousBinder)
    {
      synchronized (DreamManagerService.-get5(DreamManagerService.this))
      {
        if (DreamManagerService.-get4(DreamManagerService.this) == paramAnonymousBinder) {
          DreamManagerService.-wrap5(DreamManagerService.this);
        }
        return;
      }
    }
  };
  private boolean mCurrentDreamCanDoze;
  private int mCurrentDreamDozeScreenBrightness = -1;
  private int mCurrentDreamDozeScreenState = 0;
  private boolean mCurrentDreamIsDozing;
  private boolean mCurrentDreamIsTest;
  private boolean mCurrentDreamIsWaking;
  private ComponentName mCurrentDreamName;
  private Binder mCurrentDreamToken;
  private int mCurrentDreamUserId;
  private AmbientDisplayConfiguration mDozeConfig;
  private final ContentObserver mDozeEnabledObserver = new ContentObserver(null)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      DreamManagerService.-wrap17(DreamManagerService.this);
    }
  };
  private final PowerManager.WakeLock mDozeWakeLock;
  private final DreamHandler mHandler;
  private final Object mLock = new Object();
  private final PowerManager mPowerManager;
  private final PowerManagerInternal mPowerManagerInternal;
  private final Runnable mSystemPropertiesChanged = new Runnable()
  {
    public void run()
    {
      synchronized (DreamManagerService.-get5(DreamManagerService.this))
      {
        if ((DreamManagerService.-get3(DreamManagerService.this) != null) && (DreamManagerService.-get2(DreamManagerService.this)))
        {
          boolean bool = DreamManagerService.-get3(DreamManagerService.this).equals(DreamManagerService.-wrap2(DreamManagerService.this));
          if (!bool) {}
        }
        else
        {
          return;
        }
        DreamManagerService.-get6(DreamManagerService.this).wakeUp(SystemClock.uptimeMillis(), "android.server.dreams:SYSPROP");
      }
    }
  };
  
  public DreamManagerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mHandler = new DreamHandler(FgThread.get().getLooper());
    this.mController = new DreamController(paramContext, this.mHandler, this.mControllerListener);
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mPowerManagerInternal = ((PowerManagerInternal)getLocalService(PowerManagerInternal.class));
    this.mDozeWakeLock = this.mPowerManager.newWakeLock(64, "DreamManagerService");
    this.mDozeConfig = new AmbientDisplayConfiguration(this.mContext);
  }
  
  private void checkPermission(String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission(paramString) != 0) {
      throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + paramString);
    }
  }
  
  private ComponentName chooseDreamForUser(boolean paramBoolean, int paramInt)
  {
    Object localObject2 = null;
    if (paramBoolean)
    {
      localObject1 = getDozeComponent(paramInt);
      if (validateDream((ComponentName)localObject1)) {
        return (ComponentName)localObject1;
      }
      return null;
    }
    ComponentName[] arrayOfComponentName = getDreamComponentsForUser(paramInt);
    Object localObject1 = localObject2;
    if (arrayOfComponentName != null)
    {
      localObject1 = localObject2;
      if (arrayOfComponentName.length != 0) {
        localObject1 = arrayOfComponentName[0];
      }
    }
    return (ComponentName)localObject1;
  }
  
  private void cleanupDreamLocked()
  {
    this.mCurrentDreamToken = null;
    this.mCurrentDreamName = null;
    this.mCurrentDreamIsTest = false;
    this.mCurrentDreamCanDoze = false;
    this.mCurrentDreamUserId = 0;
    this.mCurrentDreamIsWaking = false;
    if (this.mCurrentDreamIsDozing)
    {
      this.mCurrentDreamIsDozing = false;
      this.mDozeWakeLock.release();
    }
    this.mCurrentDreamDozeScreenState = 0;
    this.mCurrentDreamDozeScreenBrightness = -1;
  }
  
  private static ComponentName[] componentsFromString(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = paramString.split(",");
    ComponentName[] arrayOfComponentName = new ComponentName[paramString.length];
    int i = 0;
    while (i < paramString.length)
    {
      arrayOfComponentName[i] = ComponentName.unflattenFromString(paramString[i]);
      i += 1;
    }
    return arrayOfComponentName;
  }
  
  private static String componentsToString(ComponentName[] paramArrayOfComponentName)
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramArrayOfComponentName != null)
    {
      int j = paramArrayOfComponentName.length;
      while (i < j)
      {
        ComponentName localComponentName = paramArrayOfComponentName[i];
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append(localComponentName.flattenToString());
        i += 1;
      }
    }
    return localStringBuilder.toString();
  }
  
  private void dumpInternal(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("DREAM MANAGER (dumpsys dreams)");
    paramPrintWriter.println();
    paramPrintWriter.println("mCurrentDreamToken=" + this.mCurrentDreamToken);
    paramPrintWriter.println("mCurrentDreamName=" + this.mCurrentDreamName);
    paramPrintWriter.println("mCurrentDreamUserId=" + this.mCurrentDreamUserId);
    paramPrintWriter.println("mCurrentDreamIsTest=" + this.mCurrentDreamIsTest);
    paramPrintWriter.println("mCurrentDreamCanDoze=" + this.mCurrentDreamCanDoze);
    paramPrintWriter.println("mCurrentDreamIsDozing=" + this.mCurrentDreamIsDozing);
    paramPrintWriter.println("mCurrentDreamIsWaking=" + this.mCurrentDreamIsWaking);
    paramPrintWriter.println("mCurrentDreamDozeScreenState=" + Display.stateToString(this.mCurrentDreamDozeScreenState));
    paramPrintWriter.println("mCurrentDreamDozeScreenBrightness=" + this.mCurrentDreamDozeScreenBrightness);
    paramPrintWriter.println("getDozeComponent()=" + getDozeComponent());
    paramPrintWriter.println();
    DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump()
    {
      public void dump(PrintWriter paramAnonymousPrintWriter, String paramAnonymousString)
      {
        DreamManagerService.-get1(DreamManagerService.this).dump(paramAnonymousPrintWriter);
      }
    }, paramPrintWriter, "", 200L);
  }
  
  private void finishSelfInternal(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mCurrentDreamToken == paramIBinder) {
        stopDreamLocked(paramBoolean);
      }
      return;
    }
  }
  
  private ComponentName getDefaultDreamComponentForUser(int paramInt)
  {
    String str = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "screensaver_default_component", paramInt);
    if (str == null) {
      return null;
    }
    return ComponentName.unflattenFromString(str);
  }
  
  private ComponentName getDozeComponent()
  {
    return getDozeComponent(ActivityManager.getCurrentUser());
  }
  
  private ComponentName getDozeComponent(int paramInt)
  {
    if (this.mDozeConfig.enabled(paramInt)) {
      return ComponentName.unflattenFromString(this.mDozeConfig.ambientDisplayComponent());
    }
    return null;
  }
  
  private ComponentName[] getDreamComponentsForUser(int paramInt)
  {
    Object localObject = componentsFromString(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "screensaver_components", paramInt));
    ArrayList localArrayList = new ArrayList();
    if (localObject != null)
    {
      int i = 0;
      int j = localObject.length;
      while (i < j)
      {
        ComponentName localComponentName = localObject[i];
        if (validateDream(localComponentName)) {
          localArrayList.add(localComponentName);
        }
        i += 1;
      }
    }
    if (localArrayList.isEmpty())
    {
      localObject = getDefaultDreamComponentForUser(paramInt);
      if (localObject != null)
      {
        Slog.w("DreamManagerService", "Falling back to default dream " + localObject);
        localArrayList.add(localObject);
      }
    }
    return (ComponentName[])localArrayList.toArray(new ComponentName[localArrayList.size()]);
  }
  
  private ServiceInfo getServiceInfo(ComponentName paramComponentName)
  {
    ServiceInfo localServiceInfo = null;
    if (paramComponentName != null) {}
    try
    {
      localServiceInfo = this.mContext.getPackageManager().getServiceInfo(paramComponentName, 268435456);
      return localServiceInfo;
    }
    catch (PackageManager.NameNotFoundException paramComponentName) {}
    return null;
  }
  
  /* Error */
  private boolean isDreamingInternal()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 94	com/android/server/dreams/DreamManagerService:mLock	Ljava/lang/Object;
    //   6: astore 4
    //   8: aload 4
    //   10: monitorenter
    //   11: iload_2
    //   12: istore_1
    //   13: aload_0
    //   14: getfield 90	com/android/server/dreams/DreamManagerService:mCurrentDreamToken	Landroid/os/Binder;
    //   17: ifnull +14 -> 31
    //   20: aload_0
    //   21: getfield 309	com/android/server/dreams/DreamManagerService:mCurrentDreamIsTest	Z
    //   24: istore_1
    //   25: iload_1
    //   26: ifeq +10 -> 36
    //   29: iload_2
    //   30: istore_1
    //   31: aload 4
    //   33: monitorexit
    //   34: iload_1
    //   35: ireturn
    //   36: aload_0
    //   37: getfield 313	com/android/server/dreams/DreamManagerService:mCurrentDreamIsWaking	Z
    //   40: istore_3
    //   41: iload_2
    //   42: istore_1
    //   43: iload_3
    //   44: ifne -13 -> 31
    //   47: iconst_1
    //   48: istore_1
    //   49: goto -18 -> 31
    //   52: astore 5
    //   54: aload 4
    //   56: monitorexit
    //   57: aload 5
    //   59: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	this	DreamManagerService
    //   12	37	1	bool1	boolean
    //   1	41	2	bool2	boolean
    //   40	4	3	bool3	boolean
    //   6	49	4	localObject1	Object
    //   52	6	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	25	52	finally
    //   36	41	52	finally
  }
  
  private void requestAwakenInternal()
  {
    long l = SystemClock.uptimeMillis();
    this.mPowerManager.userActivity(l, false);
    stopDreamInternal(false);
  }
  
  private void requestDreamInternal()
  {
    long l = SystemClock.uptimeMillis();
    this.mPowerManager.userActivity(l, true);
    this.mPowerManager.nap(l);
  }
  
  private void setDreamComponentsForUser(int paramInt, ComponentName[] paramArrayOfComponentName)
  {
    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "screensaver_components", componentsToString(paramArrayOfComponentName), paramInt);
  }
  
  private void startDozingInternal(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      if ((this.mCurrentDreamToken == paramIBinder) && (this.mCurrentDreamCanDoze))
      {
        this.mCurrentDreamDozeScreenState = paramInt1;
        this.mCurrentDreamDozeScreenBrightness = paramInt2;
        this.mPowerManagerInternal.setDozeOverrideFromDreamManager(paramInt1, paramInt2);
        if (!this.mCurrentDreamIsDozing)
        {
          this.mCurrentDreamIsDozing = true;
          this.mDozeWakeLock.acquire();
        }
      }
      return;
    }
  }
  
  private void startDreamInternal(boolean paramBoolean)
  {
    int i = ActivityManager.getCurrentUser();
    ComponentName localComponentName = chooseDreamForUser(paramBoolean, i);
    if (localComponentName != null) {}
    synchronized (this.mLock)
    {
      startDreamLocked(localComponentName, false, paramBoolean, i);
      return;
    }
  }
  
  private void startDreamLocked(ComponentName paramComponentName, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if ((Objects.equal(this.mCurrentDreamName, paramComponentName)) && (this.mCurrentDreamIsTest == paramBoolean1) && (this.mCurrentDreamCanDoze == paramBoolean2) && (this.mCurrentDreamUserId == paramInt)) {
      return;
    }
    stopDreamLocked(true);
    Slog.i("DreamManagerService", "Entering dreamland.");
    Binder localBinder = new Binder();
    this.mCurrentDreamToken = localBinder;
    this.mCurrentDreamName = paramComponentName;
    this.mCurrentDreamIsTest = paramBoolean1;
    this.mCurrentDreamCanDoze = paramBoolean2;
    this.mCurrentDreamUserId = paramInt;
    PowerManager.WakeLock localWakeLock = this.mPowerManager.newWakeLock(1, "startDream");
    this.mHandler.post(localWakeLock.wrap(new -void_startDreamLocked_android_content_ComponentName_name_boolean_isTest_boolean_canDoze_int_userId_LambdaImpl0(localBinder, paramComponentName, paramBoolean1, paramBoolean2, paramInt, localWakeLock)));
  }
  
  private void stopDozingInternal(IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      if ((this.mCurrentDreamToken == paramIBinder) && (this.mCurrentDreamIsDozing))
      {
        this.mCurrentDreamIsDozing = false;
        this.mDozeWakeLock.release();
        this.mPowerManagerInternal.setDozeOverrideFromDreamManager(0, -1);
      }
      return;
    }
  }
  
  private void stopDreamInternal(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      stopDreamLocked(paramBoolean);
      return;
    }
  }
  
  private void stopDreamLocked(final boolean paramBoolean)
  {
    if (this.mCurrentDreamToken != null)
    {
      if (!paramBoolean) {
        break label42;
      }
      Slog.i("DreamManagerService", "Leaving dreamland.");
      cleanupDreamLocked();
    }
    for (;;)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          DreamManagerService.-get1(DreamManagerService.this).stopDream(paramBoolean);
        }
      });
      return;
      label42:
      if (this.mCurrentDreamIsWaking) {
        return;
      }
      Slog.i("DreamManagerService", "Gently waking up from dream.");
      this.mCurrentDreamIsWaking = true;
    }
  }
  
  private void testDreamInternal(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mLock)
    {
      startDreamLocked(paramComponentName, true, false, paramInt);
      return;
    }
  }
  
  private boolean validateDream(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return false;
    }
    ServiceInfo localServiceInfo = getServiceInfo(paramComponentName);
    if (localServiceInfo == null)
    {
      Slog.w("DreamManagerService", "Dream " + paramComponentName + " does not exist");
      return false;
    }
    if ((localServiceInfo.applicationInfo.targetSdkVersion < 21) || ("android.permission.BIND_DREAM_SERVICE".equals(localServiceInfo.permission))) {
      return true;
    }
    Slog.w("DreamManagerService", "Dream " + paramComponentName + " is not available because its manifest is missing the " + "android.permission.BIND_DREAM_SERVICE" + " permission on the dream service declaration.");
    return false;
  }
  
  private void writePulseGestureEnabled()
  {
    boolean bool = validateDream(getDozeComponent());
    ((InputManagerInternal)LocalServices.getService(InputManagerInternal.class)).setPulseGestureEnabled(bool);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 600)
    {
      if (Build.IS_DEBUGGABLE) {
        SystemProperties.addChangeCallback(this.mSystemPropertiesChanged);
      }
      this.mContext.registerReceiver(new BroadcastReceiver()new IntentFilter
      {
        public void onReceive(Context arg1, Intent paramAnonymousIntent)
        {
          DreamManagerService.-wrap17(DreamManagerService.this);
          synchronized (DreamManagerService.-get5(DreamManagerService.this))
          {
            DreamManagerService.-wrap15(DreamManagerService.this, false);
            return;
          }
        }
      }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mHandler);
      this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_pulse_on_double_tap"), false, this.mDozeEnabledObserver, -1);
      writePulseGestureEnabled();
    }
  }
  
  public void onStart()
  {
    publishBinderService("dreams", new BinderService(null));
    publishLocalService(DreamManagerInternal.class, new LocalService(null));
  }
  
  private final class BinderService
    extends IDreamManager.Stub
  {
    private BinderService() {}
    
    public void awaken()
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.WRITE_DREAM_STATE");
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap8(DreamManagerService.this);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void dream()
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.WRITE_DREAM_STATE");
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap9(DreamManagerService.this);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (DreamManagerService.-get0(DreamManagerService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump DreamManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap6(DreamManagerService.this, paramPrintWriter);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void finishSelf(IBinder paramIBinder, boolean paramBoolean)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("token must not be null");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap7(DreamManagerService.this, paramIBinder, paramBoolean);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public ComponentName getDefaultDreamComponent()
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.READ_DREAM_STATE");
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        ComponentName localComponentName = DreamManagerService.-wrap1(DreamManagerService.this, i);
        return localComponentName;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public ComponentName[] getDreamComponents()
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.READ_DREAM_STATE");
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        ComponentName[] arrayOfComponentName = DreamManagerService.-wrap0(DreamManagerService.this, i);
        return arrayOfComponentName;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isDreaming()
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.READ_DREAM_STATE");
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = DreamManagerService.-wrap3(DreamManagerService.this);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setDreamComponents(ComponentName[] paramArrayOfComponentName)
    {
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.WRITE_DREAM_STATE");
      int i = UserHandle.getCallingUserId();
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap10(DreamManagerService.this, i, paramArrayOfComponentName);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void startDozing(IBinder paramIBinder, int paramInt1, int paramInt2)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("token must not be null");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap11(DreamManagerService.this, paramIBinder, paramInt1, paramInt2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void stopDozing(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        throw new IllegalArgumentException("token must not be null");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap13(DreamManagerService.this, paramIBinder);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void testDream(ComponentName paramComponentName)
    {
      if (paramComponentName == null) {
        throw new IllegalArgumentException("dream must not be null");
      }
      DreamManagerService.-wrap4(DreamManagerService.this, "android.permission.WRITE_DREAM_STATE");
      int i = UserHandle.getCallingUserId();
      int j = ActivityManager.getCurrentUser();
      if (i != j)
      {
        Slog.w("DreamManagerService", "Aborted attempt to start a test dream while a different  user is active: callingUserId=" + i + ", currentUserId=" + j);
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DreamManagerService.-wrap16(DreamManagerService.this, paramComponentName, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private final class DreamHandler
    extends Handler
  {
    public DreamHandler(Looper paramLooper)
    {
      super(null, true);
    }
  }
  
  private final class LocalService
    extends DreamManagerInternal
  {
    private LocalService() {}
    
    public boolean isDreaming()
    {
      return DreamManagerService.-wrap3(DreamManagerService.this);
    }
    
    public void startDream(boolean paramBoolean)
    {
      DreamManagerService.-wrap12(DreamManagerService.this, paramBoolean);
    }
    
    public void stopDream(boolean paramBoolean)
    {
      DreamManagerService.-wrap14(DreamManagerService.this, paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/dreams/DreamManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */