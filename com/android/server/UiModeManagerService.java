package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IUiModeManager.Stub;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.Sandman;
import android.util.Slog;
import com.android.internal.app.DisableCarModeActivity;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import java.io.FileDescriptor;
import java.io.PrintWriter;

final class UiModeManagerService
  extends SystemService
{
  private static final boolean ENABLE_LAUNCH_DESK_DOCK_APP = true;
  private static final boolean LOG = false;
  private static final String TAG = UiModeManager.class.getSimpleName();
  private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      boolean bool = false;
      ??? = UiModeManagerService.this;
      if (paramAnonymousIntent.getIntExtra("plugged", 0) != 0) {
        bool = true;
      }
      UiModeManagerService.-set0(???, bool);
      synchronized (UiModeManagerService.this.mLock)
      {
        if (UiModeManagerService.this.mSystemReady) {
          UiModeManagerService.this.updateLocked(0, 0);
        }
        return;
      }
    }
  };
  private int mCarModeEnableFlags;
  private boolean mCarModeEnabled = false;
  private boolean mCarModeKeepsScreenOn;
  private boolean mCharging = false;
  private boolean mComputedNightMode;
  private Configuration mConfiguration = new Configuration();
  int mCurUiMode = 0;
  private int mDefaultUiModeType;
  private boolean mDeskModeKeepsScreenOn;
  private final BroadcastReceiver mDockModeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
      UiModeManagerService.-wrap2(UiModeManagerService.this, i);
    }
  };
  private int mDockState = 0;
  private boolean mEnableCarDockLaunch = true;
  private final Handler mHandler = new Handler();
  private boolean mHoldingConfiguration = false;
  private int mLastBroadcastState = 0;
  final Object mLock = new Object();
  private int mNightMode = 1;
  private boolean mNightModeLocked = false;
  private NotificationManager mNotificationManager;
  private final BroadcastReceiver mResultReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if (getResultCode() != -1) {
        return;
      }
      int i = paramAnonymousIntent.getIntExtra("enableFlags", 0);
      int j = paramAnonymousIntent.getIntExtra("disableFlags", 0);
      synchronized (UiModeManagerService.this.mLock)
      {
        UiModeManagerService.-wrap0(UiModeManagerService.this, paramAnonymousIntent.getAction(), i, j);
        return;
      }
    }
  };
  private final IBinder mService = new IUiModeManager.Stub()
  {
    /* Error */
    public void disableCarMode(int paramAnonymousInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 22	com/android/server/UiModeManagerService$5:isUiModeLocked	()Z
      //   4: ifeq +13 -> 17
      //   7: invokestatic 26	com/android/server/UiModeManagerService:-get0	()Ljava/lang/String;
      //   10: ldc 28
      //   12: invokestatic 34	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   15: pop
      //   16: return
      //   17: invokestatic 40	android/os/Binder:clearCallingIdentity	()J
      //   20: lstore_2
      //   21: aload_0
      //   22: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   25: getfield 44	com/android/server/UiModeManagerService:mLock	Ljava/lang/Object;
      //   28: astore 4
      //   30: aload 4
      //   32: monitorenter
      //   33: aload_0
      //   34: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   37: iconst_0
      //   38: iconst_0
      //   39: invokevirtual 48	com/android/server/UiModeManagerService:setCarModeLocked	(ZI)V
      //   42: aload_0
      //   43: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   46: getfield 52	com/android/server/UiModeManagerService:mSystemReady	Z
      //   49: ifeq +12 -> 61
      //   52: aload_0
      //   53: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   56: iconst_0
      //   57: iload_1
      //   58: invokevirtual 56	com/android/server/UiModeManagerService:updateLocked	(II)V
      //   61: aload 4
      //   63: monitorexit
      //   64: lload_2
      //   65: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: return
      //   69: astore 5
      //   71: aload 4
      //   73: monitorexit
      //   74: aload 5
      //   76: athrow
      //   77: astore 4
      //   79: lload_2
      //   80: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   83: aload 4
      //   85: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	86	0	this	5
      //   0	86	1	paramAnonymousInt	int
      //   20	60	2	l	long
      //   77	7	4	localObject2	Object
      //   69	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   33	61	69	finally
      //   21	33	77	finally
      //   61	64	77	finally
      //   71	77	77	finally
    }
    
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      if (UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramAnonymousPrintWriter.println("Permission Denial: can't dump uimode service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      UiModeManagerService.this.dumpImpl(paramAnonymousPrintWriter);
    }
    
    /* Error */
    public void enableCarMode(int paramAnonymousInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 22	com/android/server/UiModeManagerService$5:isUiModeLocked	()Z
      //   4: ifeq +13 -> 17
      //   7: invokestatic 26	com/android/server/UiModeManagerService:-get0	()Ljava/lang/String;
      //   10: ldc 111
      //   12: invokestatic 34	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   15: pop
      //   16: return
      //   17: invokestatic 40	android/os/Binder:clearCallingIdentity	()J
      //   20: lstore_2
      //   21: aload_0
      //   22: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   25: getfield 44	com/android/server/UiModeManagerService:mLock	Ljava/lang/Object;
      //   28: astore 4
      //   30: aload 4
      //   32: monitorenter
      //   33: aload_0
      //   34: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   37: iconst_1
      //   38: iload_1
      //   39: invokevirtual 48	com/android/server/UiModeManagerService:setCarModeLocked	(ZI)V
      //   42: aload_0
      //   43: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   46: getfield 52	com/android/server/UiModeManagerService:mSystemReady	Z
      //   49: ifeq +12 -> 61
      //   52: aload_0
      //   53: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   56: iload_1
      //   57: iconst_0
      //   58: invokevirtual 56	com/android/server/UiModeManagerService:updateLocked	(II)V
      //   61: aload 4
      //   63: monitorexit
      //   64: lload_2
      //   65: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: return
      //   69: astore 5
      //   71: aload 4
      //   73: monitorexit
      //   74: aload 5
      //   76: athrow
      //   77: astore 4
      //   79: lload_2
      //   80: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   83: aload 4
      //   85: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	86	0	this	5
      //   0	86	1	paramAnonymousInt	int
      //   20	60	2	l	long
      //   77	7	4	localObject2	Object
      //   69	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   33	61	69	finally
      //   21	33	77	finally
      //   61	64	77	finally
      //   71	77	77	finally
    }
    
    /* Error */
    public int getCurrentModeType()
    {
      // Byte code:
      //   0: invokestatic 40	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_2
      //   4: aload_0
      //   5: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   8: getfield 44	com/android/server/UiModeManagerService:mLock	Ljava/lang/Object;
      //   11: astore 4
      //   13: aload 4
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   20: getfield 116	com/android/server/UiModeManagerService:mCurUiMode	I
      //   23: istore_1
      //   24: aload 4
      //   26: monitorexit
      //   27: lload_2
      //   28: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   31: iload_1
      //   32: bipush 15
      //   34: iand
      //   35: ireturn
      //   36: astore 5
      //   38: aload 4
      //   40: monitorexit
      //   41: aload 5
      //   43: athrow
      //   44: astore 4
      //   46: lload_2
      //   47: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   50: aload 4
      //   52: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	53	0	this	5
      //   23	12	1	i	int
      //   3	44	2	l	long
      //   44	7	4	localObject2	Object
      //   36	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   16	24	36	finally
      //   4	16	44	finally
      //   24	27	44	finally
      //   38	44	44	finally
    }
    
    public int getNightMode()
    {
      synchronized (UiModeManagerService.this.mLock)
      {
        int i = UiModeManagerService.-get1(UiModeManagerService.this);
        return i;
      }
    }
    
    public boolean isNightModeLocked()
    {
      synchronized (UiModeManagerService.this.mLock)
      {
        boolean bool = UiModeManagerService.-get2(UiModeManagerService.this);
        return bool;
      }
    }
    
    public boolean isUiModeLocked()
    {
      synchronized (UiModeManagerService.this.mLock)
      {
        boolean bool = UiModeManagerService.-get3(UiModeManagerService.this);
        return bool;
      }
    }
    
    /* Error */
    public void setNightMode(int paramAnonymousInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 132	com/android/server/UiModeManagerService$5:isNightModeLocked	()Z
      //   4: ifeq +28 -> 32
      //   7: aload_0
      //   8: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   11: invokevirtual 66	com/android/server/UiModeManagerService:getContext	()Landroid/content/Context;
      //   14: ldc -122
      //   16: invokevirtual 74	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
      //   19: ifeq +13 -> 32
      //   22: invokestatic 26	com/android/server/UiModeManagerService:-get0	()Ljava/lang/String;
      //   25: ldc -120
      //   27: invokestatic 34	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   30: pop
      //   31: return
      //   32: iload_1
      //   33: tableswitch	default:+27->60, 0:+54->87, 1:+54->87, 2:+54->87
      //   60: new 138	java/lang/IllegalArgumentException
      //   63: dup
      //   64: new 76	java/lang/StringBuilder
      //   67: dup
      //   68: invokespecial 77	java/lang/StringBuilder:<init>	()V
      //   71: ldc -116
      //   73: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   76: iload_1
      //   77: invokevirtual 90	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   80: invokevirtual 98	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   83: invokespecial 142	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   86: athrow
      //   87: invokestatic 40	android/os/Binder:clearCallingIdentity	()J
      //   90: lstore_2
      //   91: aload_0
      //   92: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   95: getfield 44	com/android/server/UiModeManagerService:mLock	Ljava/lang/Object;
      //   98: astore 4
      //   100: aload 4
      //   102: monitorenter
      //   103: aload_0
      //   104: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   107: invokestatic 121	com/android/server/UiModeManagerService:-get1	(Lcom/android/server/UiModeManagerService;)I
      //   110: iload_1
      //   111: if_icmpeq +38 -> 149
      //   114: aload_0
      //   115: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   118: invokevirtual 66	com/android/server/UiModeManagerService:getContext	()Landroid/content/Context;
      //   121: invokevirtual 146	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   124: ldc -108
      //   126: iload_1
      //   127: invokestatic 154	android/provider/Settings$Secure:putInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)Z
      //   130: pop
      //   131: aload_0
      //   132: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   135: iload_1
      //   136: invokestatic 158	com/android/server/UiModeManagerService:-set1	(Lcom/android/server/UiModeManagerService;I)I
      //   139: pop
      //   140: aload_0
      //   141: getfield 12	com/android/server/UiModeManagerService$5:this$0	Lcom/android/server/UiModeManagerService;
      //   144: iconst_0
      //   145: iconst_0
      //   146: invokevirtual 56	com/android/server/UiModeManagerService:updateLocked	(II)V
      //   149: aload 4
      //   151: monitorexit
      //   152: lload_2
      //   153: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   156: return
      //   157: astore 5
      //   159: aload 4
      //   161: monitorexit
      //   162: aload 5
      //   164: athrow
      //   165: astore 4
      //   167: lload_2
      //   168: invokestatic 60	android/os/Binder:restoreCallingIdentity	(J)V
      //   171: aload 4
      //   173: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	174	0	this	5
      //   0	174	1	paramAnonymousInt	int
      //   90	78	2	l	long
      //   165	7	4	localObject2	Object
      //   157	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   103	149	157	finally
      //   91	103	165	finally
      //   149	152	165	finally
      //   159	165	165	finally
    }
  };
  private int mSetUiMode = 0;
  private StatusBarManager mStatusBarManager;
  boolean mSystemReady;
  private boolean mTelevision;
  private final TwilightListener mTwilightListener = new TwilightListener()
  {
    public void onTwilightStateChanged(TwilightState arg1)
    {
      synchronized (UiModeManagerService.this.mLock)
      {
        if (UiModeManagerService.-get1(UiModeManagerService.this) == 0)
        {
          UiModeManagerService.-wrap1(UiModeManagerService.this);
          UiModeManagerService.this.updateLocked(0, 0);
        }
        return;
      }
    }
  };
  private TwilightManager mTwilightManager;
  private boolean mUiModeLocked = false;
  private PowerManager.WakeLock mWakeLock;
  private boolean mWatch;
  
  public UiModeManagerService(Context paramContext)
  {
    super(paramContext);
  }
  
  private void adjustStatusBarCarModeLocked()
  {
    Object localObject1 = getContext();
    if (this.mStatusBarManager == null) {
      this.mStatusBarManager = ((StatusBarManager)((Context)localObject1).getSystemService("statusbar"));
    }
    Object localObject2;
    if (this.mStatusBarManager != null)
    {
      localObject2 = this.mStatusBarManager;
      if (!this.mCarModeEnabled) {
        break label182;
      }
    }
    label182:
    for (int i = 524288;; i = 0)
    {
      ((StatusBarManager)localObject2).disable(i);
      if (this.mNotificationManager == null) {
        this.mNotificationManager = ((NotificationManager)((Context)localObject1).getSystemService("notification"));
      }
      if (this.mNotificationManager != null)
      {
        if (!this.mCarModeEnabled) {
          break;
        }
        localObject2 = new Intent((Context)localObject1, DisableCarModeActivity.class);
        localObject1 = new Notification.Builder((Context)localObject1).setSmallIcon(17303253).setDefaults(4).setOngoing(true).setWhen(0L).setColor(((Context)localObject1).getColor(17170523)).setContentTitle(((Context)localObject1).getString(17040535)).setContentText(((Context)localObject1).getString(17040536)).setContentIntent(PendingIntent.getActivityAsUser((Context)localObject1, 0, (Intent)localObject2, 0, null, UserHandle.CURRENT));
        this.mNotificationManager.notifyAsUser(null, 17040535, ((Notification.Builder)localObject1).build(), UserHandle.ALL);
      }
      return;
    }
    this.mNotificationManager.cancelAsUser(null, 17040535, UserHandle.ALL);
  }
  
  private static Intent buildHomeIntent(String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory(paramString);
    localIntent.setFlags(270532608);
    return localIntent;
  }
  
  private static boolean isDeskDockState(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      return false;
    }
    return true;
  }
  
  private void sendConfigurationAndStartDreamOrDockAppLocked(String paramString)
  {
    this.mHoldingConfiguration = false;
    updateConfigurationLocked();
    int j = 0;
    int i = j;
    Intent localIntent;
    if (paramString != null)
    {
      localIntent = buildHomeIntent(paramString);
      i = j;
      if (!Sandman.shouldStartDockApp(getContext(), localIntent)) {}
    }
    for (;;)
    {
      try
      {
        k = ActivityManagerNative.getDefault().startActivityWithConfig(null, null, localIntent, null, null, null, 0, 0, this.mConfiguration, null, -2);
        if (k < 0) {
          continue;
        }
        i = 1;
      }
      catch (RemoteException localRemoteException)
      {
        int k;
        Slog.e(TAG, "Could not start dock app: " + localIntent, localRemoteException);
        i = j;
        continue;
        Sandman.startDreamWhenDockedIfAppropriate(getContext());
      }
      sendConfigurationLocked();
      if ((paramString != null) && (i == 0)) {
        continue;
      }
      return;
      i = j;
      if (k != -1)
      {
        Slog.e(TAG, "Could not start dock app: " + localIntent + ", startActivityWithConfig result " + k);
        i = j;
      }
    }
  }
  
  private void sendConfigurationLocked()
  {
    if (this.mSetUiMode != this.mConfiguration.uiMode) {
      this.mSetUiMode = this.mConfiguration.uiMode;
    }
    try
    {
      ActivityManagerNative.getDefault().updateConfiguration(this.mConfiguration);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(TAG, "Failure communicating with activity manager", localRemoteException);
    }
  }
  
  private void updateAfterBroadcastLocked(String paramString, int paramInt1, int paramInt2)
  {
    Object localObject = null;
    if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(paramString))
    {
      paramString = (String)localObject;
      if (this.mEnableCarDockLaunch)
      {
        paramString = (String)localObject;
        if ((paramInt1 & 0x1) != 0) {
          paramString = "android.intent.category.CAR_DOCK";
        }
      }
    }
    for (;;)
    {
      sendConfigurationAndStartDreamOrDockAppLocked(paramString);
      return;
      if (UiModeManager.ACTION_ENTER_DESK_MODE.equals(paramString))
      {
        paramString = (String)localObject;
        if ((paramInt1 & 0x1) != 0) {
          paramString = "android.intent.category.DESK_DOCK";
        }
      }
      else
      {
        paramString = (String)localObject;
        if ((paramInt2 & 0x1) != 0) {
          paramString = "android.intent.category.HOME";
        }
      }
    }
  }
  
  private void updateComputedNightModeLocked()
  {
    if (this.mTwilightManager != null)
    {
      TwilightState localTwilightState = this.mTwilightManager.getLastTwilightState();
      if (localTwilightState != null) {
        this.mComputedNightMode = localTwilightState.isNight();
      }
    }
  }
  
  private void updateConfigurationLocked()
  {
    int i = this.mDefaultUiModeType;
    int j;
    if (this.mUiModeLocked)
    {
      if (this.mNightMode != 0) {
        break label140;
      }
      if (this.mTwilightManager != null) {
        this.mTwilightManager.registerListener(this.mTwilightListener, this.mHandler);
      }
      updateComputedNightModeLocked();
      if (!this.mComputedNightMode) {
        break label134;
      }
      j = 32;
      label57:
      i |= j;
    }
    for (;;)
    {
      this.mCurUiMode = i;
      if (!this.mHoldingConfiguration) {
        this.mConfiguration.uiMode = i;
      }
      return;
      if (this.mTelevision)
      {
        i = 4;
        break;
      }
      if (this.mWatch)
      {
        i = 6;
        break;
      }
      if (this.mCarModeEnabled)
      {
        i = 3;
        break;
      }
      if (!isDeskDockState(this.mDockState)) {
        break;
      }
      i = 2;
      break;
      label134:
      j = 16;
      break label57;
      label140:
      if (this.mTwilightManager != null) {
        this.mTwilightManager.unregisterListener(this.mTwilightListener);
      }
      i |= this.mNightMode << 4;
    }
  }
  
  private void updateDockState(int paramInt)
  {
    boolean bool = true;
    synchronized (this.mLock)
    {
      if (paramInt != this.mDockState)
      {
        this.mDockState = paramInt;
        if (this.mDockState != 2) {
          break label52;
        }
        setCarModeLocked(bool, 0);
        if (this.mSystemReady) {
          updateLocked(1, 0);
        }
      }
      return;
      label52:
      bool = false;
    }
  }
  
  void dumpImpl(PrintWriter paramPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.println("Current UI Mode Service state:");
      paramPrintWriter.print("  mDockState=");
      paramPrintWriter.print(this.mDockState);
      paramPrintWriter.print(" mLastBroadcastState=");
      paramPrintWriter.println(this.mLastBroadcastState);
      paramPrintWriter.print("  mNightMode=");
      paramPrintWriter.print(this.mNightMode);
      paramPrintWriter.print(" mNightModeLocked=");
      paramPrintWriter.print(this.mNightModeLocked);
      paramPrintWriter.print(" mCarModeEnabled=");
      paramPrintWriter.print(this.mCarModeEnabled);
      paramPrintWriter.print(" mComputedNightMode=");
      paramPrintWriter.print(this.mComputedNightMode);
      paramPrintWriter.print(" mCarModeEnableFlags=");
      paramPrintWriter.print(this.mCarModeEnableFlags);
      paramPrintWriter.print(" mEnableCarDockLaunch=");
      paramPrintWriter.println(this.mEnableCarDockLaunch);
      paramPrintWriter.print("  mCurUiMode=0x");
      paramPrintWriter.print(Integer.toHexString(this.mCurUiMode));
      paramPrintWriter.print(" mUiModeLocked=");
      paramPrintWriter.print(this.mUiModeLocked);
      paramPrintWriter.print(" mSetUiMode=0x");
      paramPrintWriter.println(Integer.toHexString(this.mSetUiMode));
      paramPrintWriter.print("  mHoldingConfiguration=");
      paramPrintWriter.print(this.mHoldingConfiguration);
      paramPrintWriter.print(" mSystemReady=");
      paramPrintWriter.println(this.mSystemReady);
      if (this.mTwilightManager != null)
      {
        paramPrintWriter.print("  mTwilightService.getLastTwilightState()=");
        paramPrintWriter.println(this.mTwilightManager.getLastTwilightState());
      }
      return;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500) {}
    synchronized (this.mLock)
    {
      this.mTwilightManager = ((TwilightManager)getLocalService(TwilightManager.class));
      this.mSystemReady = true;
      if (this.mDockState == 2)
      {
        bool = true;
        this.mCarModeEnabled = bool;
        updateComputedNightModeLocked();
        updateLocked(0, 0);
        return;
      }
      boolean bool = false;
    }
  }
  
  /* Error */
  public void onStart()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_1
    //   4: istore_3
    //   5: aload_0
    //   6: invokevirtual 166	com/android/server/UiModeManagerService:getContext	()Landroid/content/Context;
    //   9: astore 5
    //   11: aload_0
    //   12: aload 5
    //   14: ldc_w 485
    //   17: invokevirtual 176	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   20: checkcast 487	android/os/PowerManager
    //   23: bipush 26
    //   25: getstatic 67	com/android/server/UiModeManagerService:TAG	Ljava/lang/String;
    //   28: invokevirtual 491	android/os/PowerManager:newWakeLock	(ILjava/lang/String;)Landroid/os/PowerManager$WakeLock;
    //   31: putfield 493	com/android/server/UiModeManagerService:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   34: aload 5
    //   36: aload_0
    //   37: getfield 152	com/android/server/UiModeManagerService:mDockModeReceiver	Landroid/content/BroadcastReceiver;
    //   40: new 495	android/content/IntentFilter
    //   43: dup
    //   44: ldc_w 497
    //   47: invokespecial 498	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   50: invokevirtual 502	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   53: pop
    //   54: aload 5
    //   56: aload_0
    //   57: getfield 155	com/android/server/UiModeManagerService:mBatteryReceiver	Landroid/content/BroadcastReceiver;
    //   60: new 495	android/content/IntentFilter
    //   63: dup
    //   64: ldc_w 504
    //   67: invokespecial 498	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   70: invokevirtual 502	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   73: pop
    //   74: aload_0
    //   75: getfield 140	com/android/server/UiModeManagerService:mConfiguration	Landroid/content/res/Configuration;
    //   78: invokevirtual 507	android/content/res/Configuration:setToDefaults	()V
    //   81: aload 5
    //   83: invokevirtual 511	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   86: astore 6
    //   88: aload_0
    //   89: aload 6
    //   91: ldc_w 512
    //   94: invokevirtual 517	android/content/res/Resources:getInteger	(I)I
    //   97: putfield 394	com/android/server/UiModeManagerService:mDefaultUiModeType	I
    //   100: aload 6
    //   102: ldc_w 518
    //   105: invokevirtual 517	android/content/res/Resources:getInteger	(I)I
    //   108: iconst_1
    //   109: if_icmpne +163 -> 272
    //   112: iconst_1
    //   113: istore_2
    //   114: aload_0
    //   115: iload_2
    //   116: putfield 520	com/android/server/UiModeManagerService:mCarModeKeepsScreenOn	Z
    //   119: iload 4
    //   121: istore_2
    //   122: aload 6
    //   124: ldc_w 521
    //   127: invokevirtual 517	android/content/res/Resources:getInteger	(I)I
    //   130: iconst_1
    //   131: if_icmpne +5 -> 136
    //   134: iconst_1
    //   135: istore_2
    //   136: aload_0
    //   137: iload_2
    //   138: putfield 523	com/android/server/UiModeManagerService:mDeskModeKeepsScreenOn	Z
    //   141: aload_0
    //   142: aload 6
    //   144: ldc_w 524
    //   147: invokevirtual 527	android/content/res/Resources:getBoolean	(I)Z
    //   150: putfield 129	com/android/server/UiModeManagerService:mEnableCarDockLaunch	Z
    //   153: aload_0
    //   154: aload 6
    //   156: ldc_w 528
    //   159: invokevirtual 527	android/content/res/Resources:getBoolean	(I)Z
    //   162: putfield 79	com/android/server/UiModeManagerService:mUiModeLocked	Z
    //   165: aload_0
    //   166: aload 6
    //   168: ldc_w 529
    //   171: invokevirtual 527	android/content/res/Resources:getBoolean	(I)Z
    //   174: putfield 76	com/android/server/UiModeManagerService:mNightModeLocked	Z
    //   177: aload 5
    //   179: invokevirtual 533	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   182: astore 7
    //   184: iload_3
    //   185: istore_2
    //   186: aload 7
    //   188: ldc_w 535
    //   191: invokevirtual 541	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   194: ifne +12 -> 206
    //   197: aload 7
    //   199: ldc_w 543
    //   202: invokevirtual 541	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   205: istore_2
    //   206: aload_0
    //   207: iload_2
    //   208: putfield 400	com/android/server/UiModeManagerService:mTelevision	Z
    //   211: aload_0
    //   212: aload 7
    //   214: ldc_w 545
    //   217: invokevirtual 541	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   220: putfield 402	com/android/server/UiModeManagerService:mWatch	Z
    //   223: aload 6
    //   225: ldc_w 546
    //   228: invokevirtual 517	android/content/res/Resources:getInteger	(I)I
    //   231: istore_1
    //   232: aload_0
    //   233: aload 5
    //   235: invokevirtual 550	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   238: ldc_w 552
    //   241: iload_1
    //   242: invokestatic 558	android/provider/Settings$Secure:getInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)I
    //   245: putfield 72	com/android/server/UiModeManagerService:mNightMode	I
    //   248: aload_0
    //   249: monitorenter
    //   250: aload_0
    //   251: invokespecial 289	com/android/server/UiModeManagerService:updateConfigurationLocked	()V
    //   254: aload_0
    //   255: invokespecial 312	com/android/server/UiModeManagerService:sendConfigurationLocked	()V
    //   258: aload_0
    //   259: monitorexit
    //   260: aload_0
    //   261: ldc_w 560
    //   264: aload_0
    //   265: getfield 161	com/android/server/UiModeManagerService:mService	Landroid/os/IBinder;
    //   268: invokevirtual 564	com/android/server/UiModeManagerService:publishBinderService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   271: return
    //   272: iconst_0
    //   273: istore_2
    //   274: goto -160 -> 114
    //   277: astore 5
    //   279: aload_0
    //   280: monitorexit
    //   281: aload 5
    //   283: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	UiModeManagerService
    //   231	11	1	i	int
    //   113	161	2	bool1	boolean
    //   4	181	3	bool2	boolean
    //   1	119	4	bool3	boolean
    //   9	225	5	localContext	Context
    //   277	5	5	localObject	Object
    //   86	138	6	localResources	android.content.res.Resources
    //   182	31	7	localPackageManager	android.content.pm.PackageManager
    // Exception table:
    //   from	to	target	type
    //   250	258	277	finally
  }
  
  void setCarModeLocked(boolean paramBoolean, int paramInt)
  {
    if (this.mCarModeEnabled != paramBoolean) {
      this.mCarModeEnabled = paramBoolean;
    }
    this.mCarModeEnableFlags = paramInt;
  }
  
  void updateLocked(int paramInt1, int paramInt2)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    label76:
    boolean bool;
    if (this.mLastBroadcastState == 2)
    {
      adjustStatusBarCarModeLocked();
      localObject1 = UiModeManager.ACTION_EXIT_CAR_MODE;
      if (!this.mCarModeEnabled) {
        break label215;
      }
      if (this.mLastBroadcastState != 2)
      {
        adjustStatusBarCarModeLocked();
        if (localObject1 != null) {
          getContext().sendBroadcastAsUser(new Intent((String)localObject1), UserHandle.ALL);
        }
        this.mLastBroadcastState = 2;
        localObject2 = UiModeManager.ACTION_ENTER_CAR_MODE;
      }
      if (localObject2 == null) {
        break label287;
      }
      localObject1 = new Intent((String)localObject2);
      ((Intent)localObject1).putExtra("enableFlags", paramInt1);
      ((Intent)localObject1).putExtra("disableFlags", paramInt2);
      getContext().sendOrderedBroadcastAsUser((Intent)localObject1, UserHandle.CURRENT, null, this.mResultReceiver, null, -1, null, null);
      this.mHoldingConfiguration = true;
      updateConfigurationLocked();
      if (!this.mCharging) {
        break label399;
      }
      if ((!this.mCarModeEnabled) || (!this.mCarModeKeepsScreenOn) || ((this.mCarModeEnableFlags & 0x2) != 0)) {
        break label378;
      }
      bool = true;
    }
    for (;;)
    {
      if (bool != this.mWakeLock.isHeld())
      {
        if (!bool) {
          break label404;
        }
        this.mWakeLock.acquire();
      }
      return;
      if (!isDeskDockState(this.mLastBroadcastState)) {
        break;
      }
      localObject1 = UiModeManager.ACTION_EXIT_DESK_MODE;
      break;
      label215:
      if (isDeskDockState(this.mDockState))
      {
        if (isDeskDockState(this.mLastBroadcastState)) {
          break label76;
        }
        if (localObject1 != null) {
          getContext().sendBroadcastAsUser(new Intent((String)localObject1), UserHandle.ALL);
        }
        this.mLastBroadcastState = this.mDockState;
        localObject2 = UiModeManager.ACTION_ENTER_DESK_MODE;
        break label76;
      }
      this.mLastBroadcastState = 0;
      localObject2 = localObject1;
      break label76;
      label287:
      localObject2 = null;
      if (this.mCarModeEnabled)
      {
        localObject1 = localObject2;
        if (this.mEnableCarDockLaunch)
        {
          localObject1 = localObject2;
          if ((paramInt1 & 0x1) != 0) {
            localObject1 = "android.intent.category.CAR_DOCK";
          }
        }
      }
      for (;;)
      {
        sendConfigurationAndStartDreamOrDockAppLocked((String)localObject1);
        break;
        if (isDeskDockState(this.mDockState))
        {
          localObject1 = localObject2;
          if ((paramInt1 & 0x1) != 0) {
            localObject1 = "android.intent.category.DESK_DOCK";
          }
        }
        else
        {
          localObject1 = localObject2;
          if ((paramInt2 & 0x1) != 0) {
            localObject1 = "android.intent.category.HOME";
          }
        }
      }
      label378:
      if (this.mCurUiMode == 2)
      {
        bool = this.mDeskModeKeepsScreenOn;
      }
      else
      {
        bool = false;
        continue;
        label399:
        bool = false;
      }
    }
    label404:
    this.mWakeLock.release();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/UiModeManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */