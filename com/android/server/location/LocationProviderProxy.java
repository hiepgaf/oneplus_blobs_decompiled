package com.android.server.location;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import com.android.internal.location.ILocationProvider;
import com.android.internal.location.ILocationProvider.Stub;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.server.LocationManagerService;
import com.android.server.ServiceWatcher;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LocationProviderProxy
  implements LocationProviderInterface
{
  private static final boolean D = LocationManagerService.D;
  private static final String TAG = "LocationProviderProxy";
  private final Context mContext;
  private boolean mEnabled = false;
  private Object mLock = new Object();
  private final String mName;
  private Runnable mNewServiceWork = new Runnable()
  {
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: invokestatic 27	com/android/server/location/LocationProviderProxy:-get0	()Z
      //   3: ifeq +11 -> 14
      //   6: ldc 29
      //   8: ldc 31
      //   10: invokestatic 37	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   13: pop
      //   14: aconst_null
      //   15: astore 4
      //   17: aconst_null
      //   18: astore_2
      //   19: aload_0
      //   20: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   23: invokestatic 41	com/android/server/location/LocationProviderProxy:-get2	(Lcom/android/server/location/LocationProviderProxy;)Ljava/lang/Object;
      //   26: astore_3
      //   27: aload_3
      //   28: monitorenter
      //   29: aload_0
      //   30: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   33: invokestatic 45	com/android/server/location/LocationProviderProxy:-get1	(Lcom/android/server/location/LocationProviderProxy;)Z
      //   36: istore_1
      //   37: aload_0
      //   38: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   41: invokestatic 49	com/android/server/location/LocationProviderProxy:-get3	(Lcom/android/server/location/LocationProviderProxy;)Lcom/android/internal/location/ProviderRequest;
      //   44: astore 5
      //   46: aload_0
      //   47: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   50: invokestatic 53	com/android/server/location/LocationProviderProxy:-get5	(Lcom/android/server/location/LocationProviderProxy;)Landroid/os/WorkSource;
      //   53: astore 6
      //   55: aload_0
      //   56: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   59: invokestatic 57	com/android/server/location/LocationProviderProxy:-wrap0	(Lcom/android/server/location/LocationProviderProxy;)Lcom/android/internal/location/ILocationProvider;
      //   62: astore 7
      //   64: aload_3
      //   65: monitorexit
      //   66: aload 7
      //   68: ifnonnull +9 -> 77
      //   71: return
      //   72: astore_2
      //   73: aload_3
      //   74: monitorexit
      //   75: aload_2
      //   76: athrow
      //   77: aload 7
      //   79: invokeinterface 63 1 0
      //   84: astore_3
      //   85: aload_3
      //   86: ifnonnull +42 -> 128
      //   89: aload_3
      //   90: astore_2
      //   91: aload_3
      //   92: astore 4
      //   94: ldc 29
      //   96: new 65	java/lang/StringBuilder
      //   99: dup
      //   100: invokespecial 66	java/lang/StringBuilder:<init>	()V
      //   103: aload_0
      //   104: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   107: invokestatic 70	com/android/server/location/LocationProviderProxy:-get4	(Lcom/android/server/location/LocationProviderProxy;)Lcom/android/server/ServiceWatcher;
      //   110: invokevirtual 76	com/android/server/ServiceWatcher:getBestPackageName	()Ljava/lang/String;
      //   113: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   116: ldc 82
      //   118: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   121: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   124: invokestatic 88	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   127: pop
      //   128: aload_3
      //   129: astore_2
      //   130: iload_1
      //   131: ifeq +40 -> 171
      //   134: aload_3
      //   135: astore_2
      //   136: aload_3
      //   137: astore 4
      //   139: aload 7
      //   141: invokeinterface 91 1 0
      //   146: aload_3
      //   147: astore_2
      //   148: aload 5
      //   150: ifnull +21 -> 171
      //   153: aload_3
      //   154: astore_2
      //   155: aload_3
      //   156: astore 4
      //   158: aload 7
      //   160: aload 5
      //   162: aload 6
      //   164: invokeinterface 95 3 0
      //   169: aload_3
      //   170: astore_2
      //   171: aload_0
      //   172: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   175: invokestatic 41	com/android/server/location/LocationProviderProxy:-get2	(Lcom/android/server/location/LocationProviderProxy;)Ljava/lang/Object;
      //   178: astore_3
      //   179: aload_3
      //   180: monitorenter
      //   181: aload_0
      //   182: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   185: aload_2
      //   186: invokestatic 99	com/android/server/location/LocationProviderProxy:-set0	(Lcom/android/server/location/LocationProviderProxy;Lcom/android/internal/location/ProviderProperties;)Lcom/android/internal/location/ProviderProperties;
      //   189: pop
      //   190: aload_3
      //   191: monitorexit
      //   192: return
      //   193: astore_3
      //   194: ldc 29
      //   196: new 65	java/lang/StringBuilder
      //   199: dup
      //   200: invokespecial 66	java/lang/StringBuilder:<init>	()V
      //   203: ldc 101
      //   205: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   208: aload_0
      //   209: getfield 14	com/android/server/location/LocationProviderProxy$1:this$0	Lcom/android/server/location/LocationProviderProxy;
      //   212: invokestatic 70	com/android/server/location/LocationProviderProxy:-get4	(Lcom/android/server/location/LocationProviderProxy;)Lcom/android/server/ServiceWatcher;
      //   215: invokevirtual 76	com/android/server/ServiceWatcher:getBestPackageName	()Ljava/lang/String;
      //   218: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   221: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   224: aload_3
      //   225: invokestatic 104	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   228: pop
      //   229: goto -58 -> 171
      //   232: astore_2
      //   233: ldc 29
      //   235: aload_2
      //   236: invokestatic 108	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   239: pop
      //   240: aload 4
      //   242: astore_2
      //   243: goto -72 -> 171
      //   246: astore_2
      //   247: aload_3
      //   248: monitorexit
      //   249: aload_2
      //   250: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	251	0	this	1
      //   36	95	1	bool	boolean
      //   18	1	2	localObject1	Object
      //   72	4	2	localObject2	Object
      //   90	96	2	localObject3	Object
      //   232	4	2	localRemoteException	RemoteException
      //   242	1	2	localObject4	Object
      //   246	4	2	localObject5	Object
      //   193	55	3	localException	Exception
      //   15	226	4	localObject7	Object
      //   44	117	5	localProviderRequest	ProviderRequest
      //   53	110	6	localWorkSource	WorkSource
      //   62	97	7	localILocationProvider	ILocationProvider
      // Exception table:
      //   from	to	target	type
      //   29	64	72	finally
      //   77	85	193	java/lang/Exception
      //   94	128	193	java/lang/Exception
      //   139	146	193	java/lang/Exception
      //   158	169	193	java/lang/Exception
      //   77	85	232	android/os/RemoteException
      //   94	128	232	android/os/RemoteException
      //   139	146	232	android/os/RemoteException
      //   158	169	232	android/os/RemoteException
      //   181	190	246	finally
    }
  };
  private ProviderProperties mProperties;
  private ProviderRequest mRequest = null;
  private final ServiceWatcher mServiceWatcher;
  private WorkSource mWorksource = new WorkSource();
  
  private LocationProviderProxy(Context paramContext, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mName = paramString1;
    this.mServiceWatcher = new ServiceWatcher(this.mContext, "LocationProviderProxy-" + paramString1, paramString2, paramInt1, paramInt2, paramInt3, this.mNewServiceWork, paramHandler);
  }
  
  private boolean bind()
  {
    return this.mServiceWatcher.start();
  }
  
  public static LocationProviderProxy createAndBind(Context paramContext, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler)
  {
    paramContext = new LocationProviderProxy(paramContext, paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramHandler);
    if (paramContext.bind()) {
      return paramContext;
    }
    return null;
  }
  
  private ILocationProvider getService()
  {
    return ILocationProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
  }
  
  public void disable()
  {
    synchronized (this.mLock)
    {
      this.mEnabled = false;
      ??? = getService();
      if (??? == null) {
        return;
      }
    }
    try
    {
      ((ILocationProvider)???).disable();
      return;
    }
    catch (Exception localException)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), localException);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("LocationProviderProxy", localRemoteException);
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.append("REMOTE SERVICE");
    paramPrintWriter.append(" name=").append(this.mName);
    paramPrintWriter.append(" pkg=").append(this.mServiceWatcher.getBestPackageName());
    paramPrintWriter.append(" version=").append("" + this.mServiceWatcher.getBestVersion());
    paramPrintWriter.append('\n');
    ILocationProvider localILocationProvider = getService();
    if (localILocationProvider == null)
    {
      paramPrintWriter.println("service down (null)");
      return;
    }
    paramPrintWriter.flush();
    try
    {
      localILocationProvider.asBinder().dump(paramFileDescriptor, paramArrayOfString);
      return;
    }
    catch (Exception paramFileDescriptor)
    {
      paramPrintWriter.println("service down (Exception)");
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), paramFileDescriptor);
      return;
    }
    catch (RemoteException paramFileDescriptor)
    {
      paramPrintWriter.println("service down (RemoteException)");
      Log.w("LocationProviderProxy", paramFileDescriptor);
    }
  }
  
  public void enable()
  {
    synchronized (this.mLock)
    {
      this.mEnabled = true;
      ??? = getService();
      if (??? == null) {
        return;
      }
    }
    try
    {
      ((ILocationProvider)???).enable();
      return;
    }
    catch (Exception localException)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), localException);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("LocationProviderProxy", localRemoteException);
    }
  }
  
  public String getConnectedPackageName()
  {
    return this.mServiceWatcher.getBestPackageName();
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public ProviderProperties getProperties()
  {
    synchronized (this.mLock)
    {
      ProviderProperties localProviderProperties = this.mProperties;
      return localProviderProperties;
    }
  }
  
  public int getStatus(Bundle paramBundle)
  {
    ILocationProvider localILocationProvider = getService();
    if (localILocationProvider == null) {
      return 1;
    }
    try
    {
      int i = localILocationProvider.getStatus(paramBundle);
      return i;
    }
    catch (Exception paramBundle)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), paramBundle);
      return 1;
    }
    catch (RemoteException paramBundle)
    {
      Log.w("LocationProviderProxy", paramBundle);
    }
    return 1;
  }
  
  public long getStatusUpdateTime()
  {
    ILocationProvider localILocationProvider = getService();
    if (localILocationProvider == null) {
      return 0L;
    }
    try
    {
      long l = localILocationProvider.getStatusUpdateTime();
      return l;
    }
    catch (Exception localException)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), localException);
      return 0L;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("LocationProviderProxy", localRemoteException);
    }
    return 0L;
  }
  
  public boolean isEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mEnabled;
      return bool;
    }
  }
  
  public boolean sendExtraCommand(String paramString, Bundle paramBundle)
  {
    ILocationProvider localILocationProvider = getService();
    if (localILocationProvider == null) {
      return false;
    }
    try
    {
      boolean bool = localILocationProvider.sendExtraCommand(paramString, paramBundle);
      return bool;
    }
    catch (Exception paramString)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), paramString);
      return false;
    }
    catch (RemoteException paramString)
    {
      Log.w("LocationProviderProxy", paramString);
    }
    return false;
  }
  
  public void setRequest(ProviderRequest paramProviderRequest, WorkSource paramWorkSource)
  {
    synchronized (this.mLock)
    {
      this.mRequest = paramProviderRequest;
      this.mWorksource = paramWorkSource;
      ??? = getService();
      if (??? == null) {
        return;
      }
    }
    try
    {
      ((ILocationProvider)???).setRequest(paramProviderRequest, paramWorkSource);
      return;
    }
    catch (Exception paramProviderRequest)
    {
      Log.e("LocationProviderProxy", "Exception from " + this.mServiceWatcher.getBestPackageName(), paramProviderRequest);
      return;
    }
    catch (RemoteException paramProviderRequest)
    {
      Log.w("LocationProviderProxy", paramProviderRequest);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationProviderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */