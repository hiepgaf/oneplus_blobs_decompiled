package com.android.server.pm;

import android.app.IEphemeralResolver;
import android.app.IEphemeralResolver.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.EphemeralResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.TimedRemoteCaller;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

final class EphemeralResolverConnection
{
  private static final long BIND_SERVICE_TIMEOUT_MS;
  private volatile boolean mBindRequested;
  private final Context mContext;
  private final GetEphemeralResolveInfoCaller mGetEphemeralResolveInfoCaller = new GetEphemeralResolveInfoCaller();
  private final Intent mIntent;
  private final Object mLock = new Object();
  private IEphemeralResolver mRemoteInstance;
  private final ServiceConnection mServiceConnection = new MyServiceConnection(null);
  
  static
  {
    if ("eng".equals(Build.TYPE)) {}
    for (int i = 300;; i = 200)
    {
      BIND_SERVICE_TIMEOUT_MS = i;
      return;
    }
  }
  
  public EphemeralResolverConnection(Context paramContext, ComponentName paramComponentName)
  {
    this.mContext = paramContext;
    this.mIntent = new Intent("android.intent.action.RESOLVE_EPHEMERAL_PACKAGE").setComponent(paramComponentName);
  }
  
  private void bindLocked()
    throws TimeoutException
  {
    if (this.mRemoteInstance != null) {
      return;
    }
    if (!this.mBindRequested)
    {
      this.mBindRequested = true;
      this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 67108865, UserHandle.SYSTEM);
    }
    long l1 = SystemClock.uptimeMillis();
    for (;;)
    {
      if (this.mRemoteInstance != null)
      {
        this.mLock.notifyAll();
        return;
      }
      long l2 = SystemClock.uptimeMillis();
      l2 = BIND_SERVICE_TIMEOUT_MS - (l2 - l1);
      if (l2 <= 0L) {
        throw new TimeoutException("Didn't bind to resolver in time.");
      }
      try
      {
        this.mLock.wait(l2);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  private IEphemeralResolver getRemoteInstanceLazy()
    throws TimeoutException
  {
    synchronized (this.mLock)
    {
      if (this.mRemoteInstance != null)
      {
        localIEphemeralResolver = this.mRemoteInstance;
        return localIEphemeralResolver;
      }
      bindLocked();
      IEphemeralResolver localIEphemeralResolver = this.mRemoteInstance;
      return localIEphemeralResolver;
    }
  }
  
  private void throwIfCalledOnMainThread()
  {
    if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
      throw new RuntimeException("Cannot invoke on the main thread");
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String paramString)
  {
    for (;;)
    {
      String str;
      synchronized (this.mLock)
      {
        PrintWriter localPrintWriter = paramPrintWriter.append(paramString).append("bound=");
        if (this.mRemoteInstance != null)
        {
          str = "true";
          localPrintWriter.append(str).println();
          paramPrintWriter.flush();
        }
      }
      try
      {
        getRemoteInstanceLazy().asBinder().dump(paramFileDescriptor, new String[] { paramString });
        return;
        str = "false";
        continue;
        paramFileDescriptor = finally;
        throw paramFileDescriptor;
      }
      catch (TimeoutException paramFileDescriptor)
      {
        for (;;) {}
      }
      catch (RemoteException paramFileDescriptor)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  public final List<EphemeralResolveInfo> getEphemeralResolveInfoList(int[] arg1, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 183	com/android/server/pm/EphemeralResolverConnection:throwIfCalledOnMainThread	()V
    //   4: aload_0
    //   5: getfield 62	com/android/server/pm/EphemeralResolverConnection:mGetEphemeralResolveInfoCaller	Lcom/android/server/pm/EphemeralResolverConnection$GetEphemeralResolveInfoCaller;
    //   8: aload_0
    //   9: invokespecial 166	com/android/server/pm/EphemeralResolverConnection:getRemoteInstanceLazy	()Landroid/app/IEphemeralResolver;
    //   12: aload_1
    //   13: iload_2
    //   14: invokevirtual 186	com/android/server/pm/EphemeralResolverConnection$GetEphemeralResolveInfoCaller:getEphemeralResolveInfoList	(Landroid/app/IEphemeralResolver;[II)Ljava/util/List;
    //   17: astore_3
    //   18: aload_0
    //   19: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   22: astore_1
    //   23: aload_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   29: invokevirtual 111	java/lang/Object:notifyAll	()V
    //   32: aload_1
    //   33: monitorexit
    //   34: aload_3
    //   35: areturn
    //   36: astore_3
    //   37: aload_1
    //   38: monitorexit
    //   39: aload_3
    //   40: athrow
    //   41: astore_1
    //   42: aload_0
    //   43: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   46: astore_1
    //   47: aload_1
    //   48: monitorenter
    //   49: aload_0
    //   50: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   53: invokevirtual 111	java/lang/Object:notifyAll	()V
    //   56: aload_1
    //   57: monitorexit
    //   58: aconst_null
    //   59: areturn
    //   60: astore_3
    //   61: aload_1
    //   62: monitorexit
    //   63: aload_3
    //   64: athrow
    //   65: astore_1
    //   66: aload_0
    //   67: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   70: astore_1
    //   71: aload_1
    //   72: monitorenter
    //   73: aload_0
    //   74: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   77: invokevirtual 111	java/lang/Object:notifyAll	()V
    //   80: goto -24 -> 56
    //   83: astore_3
    //   84: aload_1
    //   85: monitorexit
    //   86: aload_3
    //   87: athrow
    //   88: astore_3
    //   89: aload_0
    //   90: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   93: astore_1
    //   94: aload_1
    //   95: monitorenter
    //   96: aload_0
    //   97: getfield 32	com/android/server/pm/EphemeralResolverConnection:mLock	Ljava/lang/Object;
    //   100: invokevirtual 111	java/lang/Object:notifyAll	()V
    //   103: aload_1
    //   104: monitorexit
    //   105: aload_3
    //   106: athrow
    //   107: astore_3
    //   108: aload_1
    //   109: monitorexit
    //   110: aload_3
    //   111: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	this	EphemeralResolverConnection
    //   0	112	2	paramInt	int
    //   17	18	3	localList	List
    //   36	4	3	localObject1	Object
    //   60	4	3	localObject2	Object
    //   83	4	3	localObject3	Object
    //   88	18	3	localObject4	Object
    //   107	4	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   25	32	36	finally
    //   4	18	41	java/util/concurrent/TimeoutException
    //   49	56	60	finally
    //   4	18	65	android/os/RemoteException
    //   73	80	83	finally
    //   4	18	88	finally
    //   96	103	107	finally
  }
  
  private static final class GetEphemeralResolveInfoCaller
    extends TimedRemoteCaller<List<EphemeralResolveInfo>>
  {
    private final IRemoteCallback mCallback = new IRemoteCallback.Stub()
    {
      public void sendResult(Bundle paramAnonymousBundle)
        throws RemoteException
      {
        ArrayList localArrayList = paramAnonymousBundle.getParcelableArrayList("android.app.extra.RESOLVE_INFO");
        int i = paramAnonymousBundle.getInt("android.app.extra.SEQUENCE", -1);
        EphemeralResolverConnection.GetEphemeralResolveInfoCaller.this.onRemoteMethodResult(localArrayList, i);
      }
    };
    
    public GetEphemeralResolveInfoCaller()
    {
      super();
    }
    
    public List<EphemeralResolveInfo> getEphemeralResolveInfoList(IEphemeralResolver paramIEphemeralResolver, int[] paramArrayOfInt, int paramInt)
      throws RemoteException, TimeoutException
    {
      int i = onBeforeRemoteCall();
      paramIEphemeralResolver.getEphemeralResolveInfoList(this.mCallback, paramArrayOfInt, paramInt, i);
      return (List)getResultTimed(i);
    }
  }
  
  private final class MyServiceConnection
    implements ServiceConnection
  {
    private MyServiceConnection() {}
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      synchronized (EphemeralResolverConnection.-get0(EphemeralResolverConnection.this))
      {
        EphemeralResolverConnection.-set0(EphemeralResolverConnection.this, IEphemeralResolver.Stub.asInterface(paramIBinder));
        EphemeralResolverConnection.-get0(EphemeralResolverConnection.this).notifyAll();
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (EphemeralResolverConnection.-get0(EphemeralResolverConnection.this))
      {
        EphemeralResolverConnection.-set0(EphemeralResolverConnection.this, null);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/EphemeralResolverConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */