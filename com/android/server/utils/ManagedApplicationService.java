package com.android.server.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import java.util.Objects;

public class ManagedApplicationService
{
  private final String TAG = getClass().getSimpleName();
  private IInterface mBoundInterface;
  private final BinderChecker mChecker;
  private final int mClientLabel;
  private final ComponentName mComponent;
  private ServiceConnection mConnection;
  private final Context mContext;
  private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      synchronized (ManagedApplicationService.-get5(ManagedApplicationService.this))
      {
        ManagedApplicationService.-set0(ManagedApplicationService.this, null);
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private ServiceConnection mPendingConnection;
  private PendingEvent mPendingEvent;
  private final String mSettingsAction;
  private final int mUserId;
  
  private ManagedApplicationService(Context paramContext, ComponentName paramComponentName, int paramInt1, int paramInt2, String paramString, BinderChecker paramBinderChecker)
  {
    this.mContext = paramContext;
    this.mComponent = paramComponentName;
    this.mUserId = paramInt1;
    this.mClientLabel = paramInt2;
    this.mSettingsAction = paramString;
    this.mChecker = paramBinderChecker;
  }
  
  public static ManagedApplicationService build(Context paramContext, ComponentName paramComponentName, int paramInt1, int paramInt2, String paramString, BinderChecker paramBinderChecker)
  {
    return new ManagedApplicationService(paramContext, paramComponentName, paramInt1, paramInt2, paramString, paramBinderChecker);
  }
  
  private boolean matches(ComponentName paramComponentName, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Objects.equals(this.mComponent, paramComponentName))
    {
      bool1 = bool2;
      if (this.mUserId == paramInt) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void connect()
  {
    synchronized (this.mLock)
    {
      if (this.mConnection == null)
      {
        localObject2 = this.mPendingConnection;
        if (localObject2 == null) {}
      }
      else
      {
        return;
      }
      final Object localObject2 = PendingIntent.getActivity(this.mContext, 0, new Intent(this.mSettingsAction), 0);
      localObject2 = new Intent().setComponent(this.mComponent).putExtra("android.intent.extra.client_label", this.mClientLabel).putExtra("android.intent.extra.client_intent", (Parcelable)localObject2);
      ServiceConnection local2 = new ServiceConnection()
      {
        /* Error */
        public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 5
          //   3: aconst_null
          //   4: astore 4
          //   6: aload_0
          //   7: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   10: invokestatic 34	com/android/server/utils/ManagedApplicationService:-get5	(Lcom/android/server/utils/ManagedApplicationService;)Ljava/lang/Object;
          //   13: astore 6
          //   15: aload 6
          //   17: monitorenter
          //   18: aload_0
          //   19: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   22: invokestatic 38	com/android/server/utils/ManagedApplicationService:-get6	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/content/ServiceConnection;
          //   25: aload_0
          //   26: if_acmpne +192 -> 218
          //   29: aload_0
          //   30: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   33: aconst_null
          //   34: invokestatic 42	com/android/server/utils/ManagedApplicationService:-set2	(Lcom/android/server/utils/ManagedApplicationService;Landroid/content/ServiceConnection;)Landroid/content/ServiceConnection;
          //   37: pop
          //   38: aload_0
          //   39: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   42: aload_0
          //   43: invokestatic 45	com/android/server/utils/ManagedApplicationService:-set1	(Lcom/android/server/utils/ManagedApplicationService;Landroid/content/ServiceConnection;)Landroid/content/ServiceConnection;
          //   46: pop
          //   47: aload 5
          //   49: astore_3
          //   50: aload 4
          //   52: astore_1
          //   53: aload_2
          //   54: aload_0
          //   55: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   58: invokestatic 49	com/android/server/utils/ManagedApplicationService:-get4	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/os/IBinder$DeathRecipient;
          //   61: iconst_0
          //   62: invokeinterface 55 3 0
          //   67: aload 5
          //   69: astore_3
          //   70: aload 4
          //   72: astore_1
          //   73: aload_0
          //   74: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   77: aload_0
          //   78: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   81: invokestatic 59	com/android/server/utils/ManagedApplicationService:-get2	(Lcom/android/server/utils/ManagedApplicationService;)Lcom/android/server/utils/ManagedApplicationService$BinderChecker;
          //   84: aload_2
          //   85: invokeinterface 65 2 0
          //   90: invokestatic 69	com/android/server/utils/ManagedApplicationService:-set0	(Lcom/android/server/utils/ManagedApplicationService;Landroid/os/IInterface;)Landroid/os/IInterface;
          //   93: pop
          //   94: aload 5
          //   96: astore_3
          //   97: aload 4
          //   99: astore_1
          //   100: aload_0
          //   101: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   104: invokestatic 59	com/android/server/utils/ManagedApplicationService:-get2	(Lcom/android/server/utils/ManagedApplicationService;)Lcom/android/server/utils/ManagedApplicationService$BinderChecker;
          //   107: aload_0
          //   108: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   111: invokestatic 73	com/android/server/utils/ManagedApplicationService:-get1	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/os/IInterface;
          //   114: invokeinterface 77 2 0
          //   119: ifne +35 -> 154
          //   122: aload 5
          //   124: astore_3
          //   125: aload 4
          //   127: astore_1
          //   128: aload_0
          //   129: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   132: invokestatic 81	com/android/server/utils/ManagedApplicationService:-get3	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/content/Context;
          //   135: aload_0
          //   136: invokevirtual 87	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
          //   139: aload 5
          //   141: astore_3
          //   142: aload 4
          //   144: astore_1
          //   145: aload_0
          //   146: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   149: aconst_null
          //   150: invokestatic 69	com/android/server/utils/ManagedApplicationService:-set0	(Lcom/android/server/utils/ManagedApplicationService;Landroid/os/IInterface;)Landroid/os/IInterface;
          //   153: pop
          //   154: aload 5
          //   156: astore_3
          //   157: aload 4
          //   159: astore_1
          //   160: aload_0
          //   161: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   164: invokestatic 73	com/android/server/utils/ManagedApplicationService:-get1	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/os/IInterface;
          //   167: astore_2
          //   168: aload_2
          //   169: astore_3
          //   170: aload 4
          //   172: astore_1
          //   173: aload_0
          //   174: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   177: invokestatic 91	com/android/server/utils/ManagedApplicationService:-get7	(Lcom/android/server/utils/ManagedApplicationService;)Lcom/android/server/utils/ManagedApplicationService$PendingEvent;
          //   180: astore 4
          //   182: aload_2
          //   183: astore_3
          //   184: aload 4
          //   186: astore_1
          //   187: aload_0
          //   188: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   191: aconst_null
          //   192: invokestatic 95	com/android/server/utils/ManagedApplicationService:-set3	(Lcom/android/server/utils/ManagedApplicationService;Lcom/android/server/utils/ManagedApplicationService$PendingEvent;)Lcom/android/server/utils/ManagedApplicationService$PendingEvent;
          //   195: pop
          //   196: aload 4
          //   198: astore_1
          //   199: aload 6
          //   201: monitorexit
          //   202: aload_2
          //   203: ifnull +14 -> 217
          //   206: aload_1
          //   207: ifnull +10 -> 217
          //   210: aload_1
          //   211: aload_2
          //   212: invokeinterface 101 2 0
          //   217: return
          //   218: aload_0
          //   219: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   222: invokestatic 81	com/android/server/utils/ManagedApplicationService:-get3	(Lcom/android/server/utils/ManagedApplicationService;)Landroid/content/Context;
          //   225: aload_0
          //   226: invokevirtual 87	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
          //   229: aload 6
          //   231: monitorexit
          //   232: return
          //   233: astore_2
          //   234: aload_0
          //   235: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   238: invokestatic 105	com/android/server/utils/ManagedApplicationService:-get0	(Lcom/android/server/utils/ManagedApplicationService;)Ljava/lang/String;
          //   241: new 107	java/lang/StringBuilder
          //   244: dup
          //   245: invokespecial 108	java/lang/StringBuilder:<init>	()V
          //   248: ldc 110
          //   250: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   253: aload_0
          //   254: getfield 21	com/android/server/utils/ManagedApplicationService$2:val$intent	Landroid/content/Intent;
          //   257: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   260: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   263: aload_2
          //   264: invokestatic 127	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   267: pop
          //   268: aload_0
          //   269: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   272: aconst_null
          //   273: invokestatic 69	com/android/server/utils/ManagedApplicationService:-set0	(Lcom/android/server/utils/ManagedApplicationService;Landroid/os/IInterface;)Landroid/os/IInterface;
          //   276: pop
          //   277: aload_3
          //   278: astore_2
          //   279: goto -80 -> 199
          //   282: astore_1
          //   283: aload 6
          //   285: monitorexit
          //   286: aload_1
          //   287: athrow
          //   288: astore_1
          //   289: aload_0
          //   290: getfield 19	com/android/server/utils/ManagedApplicationService$2:this$0	Lcom/android/server/utils/ManagedApplicationService;
          //   293: invokestatic 105	com/android/server/utils/ManagedApplicationService:-get0	(Lcom/android/server/utils/ManagedApplicationService;)Ljava/lang/String;
          //   296: ldc -127
          //   298: aload_1
          //   299: invokestatic 132	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   302: pop
          //   303: return
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	304	0	this	2
          //   0	304	1	paramAnonymousComponentName	ComponentName
          //   0	304	2	paramAnonymousIBinder	IBinder
          //   49	229	3	localObject1	Object
          //   4	193	4	localPendingEvent	ManagedApplicationService.PendingEvent
          //   1	154	5	localObject2	Object
          //   13	271	6	localObject3	Object
          // Exception table:
          //   from	to	target	type
          //   53	67	233	android/os/RemoteException
          //   73	94	233	android/os/RemoteException
          //   100	122	233	android/os/RemoteException
          //   128	139	233	android/os/RemoteException
          //   145	154	233	android/os/RemoteException
          //   160	168	233	android/os/RemoteException
          //   173	182	233	android/os/RemoteException
          //   187	196	233	android/os/RemoteException
          //   18	47	282	finally
          //   53	67	282	finally
          //   73	94	282	finally
          //   100	122	282	finally
          //   128	139	282	finally
          //   145	154	282	finally
          //   160	168	282	finally
          //   173	182	282	finally
          //   187	196	282	finally
          //   218	229	282	finally
          //   234	277	282	finally
          //   210	217	288	java/lang/RuntimeException
          //   210	217	288	android/os/RemoteException
        }
        
        public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
        {
          Slog.w(ManagedApplicationService.-get0(ManagedApplicationService.this), "Service disconnected: " + localObject2);
          ManagedApplicationService.-set1(ManagedApplicationService.this, null);
          ManagedApplicationService.-set0(ManagedApplicationService.this, null);
        }
      };
      this.mPendingConnection = local2;
      try
      {
        if (!this.mContext.bindServiceAsUser((Intent)localObject2, local2, 67108865, new UserHandle(this.mUserId))) {
          Slog.w(this.TAG, "Unable to bind service: " + localObject2);
        }
        return;
      }
      catch (SecurityException localSecurityException)
      {
        for (;;)
        {
          Slog.w(this.TAG, "Unable to bind service: " + localObject2, localSecurityException);
        }
      }
    }
  }
  
  public void disconnect()
  {
    synchronized (this.mLock)
    {
      this.mPendingConnection = null;
      if (this.mConnection != null)
      {
        this.mContext.unbindService(this.mConnection);
        this.mConnection = null;
      }
      this.mBoundInterface = null;
      return;
    }
  }
  
  public boolean disconnectIfNotMatching(ComponentName paramComponentName, int paramInt)
  {
    if (matches(paramComponentName, paramInt)) {
      return false;
    }
    disconnect();
    return true;
  }
  
  public ComponentName getComponent()
  {
    return this.mComponent;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public void sendEvent(PendingEvent paramPendingEvent)
  {
    IInterface localIInterface;
    synchronized (this.mLock)
    {
      localIInterface = this.mBoundInterface;
      if (localIInterface == null) {
        this.mPendingEvent = paramPendingEvent;
      }
      if (localIInterface == null) {}
    }
  }
  
  public static abstract interface BinderChecker
  {
    public abstract IInterface asInterface(IBinder paramIBinder);
    
    public abstract boolean checkType(IInterface paramIInterface);
  }
  
  public static abstract interface PendingEvent
  {
    public abstract void runEvent(IInterface paramIInterface)
      throws RemoteException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/utils/ManagedApplicationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */