package android.service.trust;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import java.util.List;

public class TrustAgentService
  extends Service
{
  private static final boolean DEBUG = false;
  public static final int FLAG_GRANT_TRUST_DISMISS_KEYGUARD = 2;
  public static final int FLAG_GRANT_TRUST_INITIATED_BY_USER = 1;
  private static final int MSG_CONFIGURE = 2;
  private static final int MSG_DEVICE_LOCKED = 4;
  private static final int MSG_DEVICE_UNLOCKED = 5;
  private static final int MSG_TRUST_TIMEOUT = 3;
  private static final int MSG_UNLOCK_ATTEMPT = 1;
  public static final String SERVICE_INTERFACE = "android.service.trust.TrustAgentService";
  public static final String TRUST_AGENT_META_DATA = "android.service.trust.trustagent";
  private final String TAG = TrustAgentService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
  private ITrustAgentServiceCallback mCallback;
  private Handler mHandler = new Handler()
  {
    /* Error */
    public void handleMessage(Message arg1)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_2
      //   2: aload_1
      //   3: getfield 26	android/os/Message:what	I
      //   6: tableswitch	default:+34->40, 1:+35->41, 2:+55->61, 3:+128->134, 4:+136->142, 5:+144->150
      //   40: return
      //   41: aload_0
      //   42: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   45: astore_3
      //   46: aload_1
      //   47: getfield 29	android/os/Message:arg1	I
      //   50: ifeq +5 -> 55
      //   53: iconst_1
      //   54: istore_2
      //   55: aload_3
      //   56: iload_2
      //   57: invokevirtual 33	android/service/trust/TrustAgentService:onUnlockAttempt	(Z)V
      //   60: return
      //   61: aload_1
      //   62: getfield 37	android/os/Message:obj	Ljava/lang/Object;
      //   65: checkcast 39	android/service/trust/TrustAgentService$ConfigurationData
      //   68: astore_3
      //   69: aload_0
      //   70: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   73: aload_3
      //   74: getfield 43	android/service/trust/TrustAgentService$ConfigurationData:options	Ljava/util/List;
      //   77: invokevirtual 47	android/service/trust/TrustAgentService:onConfigure	(Ljava/util/List;)Z
      //   80: istore_2
      //   81: aload_3
      //   82: getfield 51	android/service/trust/TrustAgentService$ConfigurationData:token	Landroid/os/IBinder;
      //   85: ifnull -45 -> 40
      //   88: aload_0
      //   89: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   92: invokestatic 55	android/service/trust/TrustAgentService:-get2	(Landroid/service/trust/TrustAgentService;)Ljava/lang/Object;
      //   95: astore_1
      //   96: aload_1
      //   97: monitorenter
      //   98: aload_0
      //   99: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   102: invokestatic 59	android/service/trust/TrustAgentService:-get0	(Landroid/service/trust/TrustAgentService;)Landroid/service/trust/ITrustAgentServiceCallback;
      //   105: iload_2
      //   106: aload_3
      //   107: getfield 51	android/service/trust/TrustAgentService$ConfigurationData:token	Landroid/os/IBinder;
      //   110: invokeinterface 65 3 0
      //   115: aload_1
      //   116: monitorexit
      //   117: return
      //   118: astore_1
      //   119: aload_0
      //   120: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   123: ldc 67
      //   125: invokestatic 71	android/service/trust/TrustAgentService:-wrap0	(Landroid/service/trust/TrustAgentService;Ljava/lang/String;)V
      //   128: return
      //   129: astore_3
      //   130: aload_1
      //   131: monitorexit
      //   132: aload_3
      //   133: athrow
      //   134: aload_0
      //   135: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   138: invokevirtual 74	android/service/trust/TrustAgentService:onTrustTimeout	()V
      //   141: return
      //   142: aload_0
      //   143: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   146: invokevirtual 77	android/service/trust/TrustAgentService:onDeviceLocked	()V
      //   149: return
      //   150: aload_0
      //   151: getfield 12	android/service/trust/TrustAgentService$1:this$0	Landroid/service/trust/TrustAgentService;
      //   154: invokevirtual 80	android/service/trust/TrustAgentService:onDeviceUnlocked	()V
      //   157: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	158	0	this	1
      //   1	105	2	bool	boolean
      //   45	62	3	localObject1	Object
      //   129	4	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   88	98	118	android/os/RemoteException
      //   115	117	118	android/os/RemoteException
      //   130	134	118	android/os/RemoteException
      //   98	115	129	finally
    }
  };
  private final Object mLock = new Object();
  private boolean mManagingTrust;
  private Runnable mPendingGrantTrustTask;
  
  private void onError(String paramString)
  {
    Slog.v(this.TAG, "Remote exception while " + paramString);
  }
  
  public final void grantTrust(final CharSequence paramCharSequence, final long paramLong, int paramInt)
  {
    synchronized (this.mLock)
    {
      if (!this.mManagingTrust) {
        throw new IllegalStateException("Cannot grant trust if agent is not managing trust. Call setManagingTrust(true) first.");
      }
    }
    ITrustAgentServiceCallback localITrustAgentServiceCallback = this.mCallback;
    if (localITrustAgentServiceCallback != null) {}
    for (;;)
    {
      try
      {
        this.mCallback.grantTrust(paramCharSequence.toString(), paramLong, paramInt);
        return;
      }
      catch (RemoteException paramCharSequence)
      {
        onError("calling enableTrust()");
        continue;
      }
      this.mPendingGrantTrustTask = new Runnable()
      {
        public void run()
        {
          TrustAgentService.this.grantTrust(paramCharSequence, paramLong, this.val$flags);
        }
      };
    }
  }
  
  @Deprecated
  public final void grantTrust(CharSequence paramCharSequence, long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      grantTrust(paramCharSequence, paramLong, i);
      return;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return new TrustAgentServiceWrapper(null);
  }
  
  public boolean onConfigure(List<PersistableBundle> paramList)
  {
    return false;
  }
  
  public void onCreate()
  {
    super.onCreate();
    ComponentName localComponentName = new ComponentName(this, getClass());
    try
    {
      if (!"android.permission.BIND_TRUST_AGENT".equals(getPackageManager().getServiceInfo(localComponentName, 0).permission)) {
        throw new IllegalStateException(localComponentName.flattenToShortString() + " is not declared with the permission " + "\"" + "android.permission.BIND_TRUST_AGENT" + "\"");
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e(this.TAG, "Can't get ServiceInfo for " + localComponentName.toShortString());
    }
  }
  
  public void onDeviceLocked() {}
  
  public void onDeviceUnlocked() {}
  
  public void onTrustTimeout() {}
  
  public void onUnlockAttempt(boolean paramBoolean) {}
  
  public final void revokeTrust()
  {
    synchronized (this.mLock)
    {
      if (this.mPendingGrantTrustTask != null) {
        this.mPendingGrantTrustTask = null;
      }
      ITrustAgentServiceCallback localITrustAgentServiceCallback = this.mCallback;
      if (localITrustAgentServiceCallback != null) {}
      try
      {
        this.mCallback.revokeTrust();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          onError("calling revokeTrust()");
        }
      }
    }
  }
  
  public final void setManagingTrust(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if (this.mManagingTrust != paramBoolean)
      {
        this.mManagingTrust = paramBoolean;
        ITrustAgentServiceCallback localITrustAgentServiceCallback = this.mCallback;
        if (localITrustAgentServiceCallback == null) {}
      }
      try
      {
        this.mCallback.setManagingTrust(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          onError("calling setManagingTrust()");
        }
      }
    }
  }
  
  private static final class ConfigurationData
  {
    final List<PersistableBundle> options;
    final IBinder token;
    
    ConfigurationData(List<PersistableBundle> paramList, IBinder paramIBinder)
    {
      this.options = paramList;
      this.token = paramIBinder;
    }
  }
  
  private final class TrustAgentServiceWrapper
    extends ITrustAgentService.Stub
  {
    private TrustAgentServiceWrapper() {}
    
    public void onConfigure(List<PersistableBundle> paramList, IBinder paramIBinder)
    {
      TrustAgentService.-get1(TrustAgentService.this).obtainMessage(2, new TrustAgentService.ConfigurationData(paramList, paramIBinder)).sendToTarget();
    }
    
    public void onDeviceLocked()
      throws RemoteException
    {
      TrustAgentService.-get1(TrustAgentService.this).obtainMessage(4).sendToTarget();
    }
    
    public void onDeviceUnlocked()
      throws RemoteException
    {
      TrustAgentService.-get1(TrustAgentService.this).obtainMessage(5).sendToTarget();
    }
    
    public void onTrustTimeout()
    {
      TrustAgentService.-get1(TrustAgentService.this).sendEmptyMessage(3);
    }
    
    public void onUnlockAttempt(boolean paramBoolean)
    {
      Handler localHandler = TrustAgentService.-get1(TrustAgentService.this);
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(1, i, 0).sendToTarget();
        return;
      }
    }
    
    public void setCallback(ITrustAgentServiceCallback paramITrustAgentServiceCallback)
    {
      synchronized (TrustAgentService.-get2(TrustAgentService.this))
      {
        TrustAgentService.-set0(TrustAgentService.this, paramITrustAgentServiceCallback);
        boolean bool = TrustAgentService.-get3(TrustAgentService.this);
        if (bool) {}
        try
        {
          TrustAgentService.-get0(TrustAgentService.this).setManagingTrust(TrustAgentService.-get3(TrustAgentService.this));
          if (TrustAgentService.-get4(TrustAgentService.this) != null)
          {
            TrustAgentService.-get4(TrustAgentService.this).run();
            TrustAgentService.-set1(TrustAgentService.this, null);
          }
          return;
        }
        catch (RemoteException paramITrustAgentServiceCallback)
        {
          for (;;)
          {
            TrustAgentService.-wrap0(TrustAgentService.this, "calling setManagingTrust()");
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/trust/TrustAgentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */