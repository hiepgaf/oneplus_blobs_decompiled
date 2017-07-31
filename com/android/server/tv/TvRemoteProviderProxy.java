package com.android.server.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.tv.ITvRemoteProvider;
import android.media.tv.ITvRemoteProvider.Stub;
import android.media.tv.ITvRemoteServiceInput.Stub;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

final class TvRemoteProviderProxy
  implements ServiceConnection
{
  private static final boolean DEBUG = Log.isLoggable("TvRemoteProvProxy", 2);
  private static final boolean DEBUG_KEY = false;
  protected static final String SERVICE_INTERFACE = "com.android.media.tv.remoteprovider.TvRemoteProvider";
  private static final String TAG = "TvRemoteProvProxy";
  private Connection mActiveConnection;
  private boolean mBound;
  private final ComponentName mComponentName;
  private boolean mConnectionReady;
  private final Context mContext;
  private final Handler mHandler;
  private final Object mLock = new Object();
  private ProviderMethods mProviderMethods;
  private boolean mRunning;
  private final int mUid;
  private final int mUserId;
  
  public TvRemoteProviderProxy(Context paramContext, ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mComponentName = paramComponentName;
    this.mUserId = paramInt1;
    this.mUid = paramInt2;
    this.mHandler = new Handler();
  }
  
  private void bind()
  {
    Intent localIntent;
    if (!this.mBound)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": Binding");
      }
      localIntent = new Intent("com.android.media.tv.remoteprovider.TvRemoteProvider");
      localIntent.setComponent(this.mComponentName);
    }
    try
    {
      this.mBound = this.mContext.bindServiceAsUser(localIntent, this, 67108865, new UserHandle(this.mUserId));
      if ((!this.mBound) && (DEBUG)) {
        Slog.d("TvRemoteProvProxy", this + ": Bind failed");
      }
      return;
    }
    catch (SecurityException localSecurityException)
    {
      while (!DEBUG) {}
      Slog.d("TvRemoteProvProxy", this + ": Bind failed", localSecurityException);
    }
  }
  
  private void disconnect()
  {
    synchronized (this.mLock)
    {
      if (this.mActiveConnection != null)
      {
        this.mConnectionReady = false;
        this.mActiveConnection.dispose();
        this.mActiveConnection = null;
      }
      return;
    }
  }
  
  private void onConnectionDied(Connection paramConnection)
  {
    if (this.mActiveConnection == paramConnection)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": Service connection died");
      }
      disconnect();
    }
  }
  
  private void onConnectionReady(Connection paramConnection)
  {
    synchronized (this.mLock)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", "onConnectionReady");
      }
      if (this.mActiveConnection == paramConnection)
      {
        if (DEBUG) {
          Slog.d("TvRemoteProvProxy", "mConnectionReady = true");
        }
        this.mConnectionReady = true;
      }
      return;
    }
  }
  
  private boolean shouldBind()
  {
    return this.mRunning;
  }
  
  private void unbind()
  {
    if (this.mBound)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": Unbinding");
      }
      this.mBound = false;
      disconnect();
      this.mContext.unbindService(this);
    }
  }
  
  private void updateBinding()
  {
    if (shouldBind())
    {
      bind();
      return;
    }
    unbind();
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "Proxy");
    paramPrintWriter.println(paramString + "  mUserId=" + this.mUserId);
    paramPrintWriter.println(paramString + "  mRunning=" + this.mRunning);
    paramPrintWriter.println(paramString + "  mBound=" + this.mBound);
    paramPrintWriter.println(paramString + "  mActiveConnection=" + this.mActiveConnection);
    paramPrintWriter.println(paramString + "  mConnectionReady=" + this.mConnectionReady);
  }
  
  public boolean hasComponentName(String paramString1, String paramString2)
  {
    if (this.mComponentName.getPackageName().equals(paramString1)) {
      return this.mComponentName.getClassName().equals(paramString2);
    }
    return false;
  }
  
  public void inputBridgeConnected(IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": inputBridgeConnected token: " + paramIBinder);
      }
      if (this.mConnectionReady) {
        this.mActiveConnection.onInputBridgeConnected(paramIBinder);
      }
      return;
    }
  }
  
  public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
  {
    if (DEBUG) {
      Slog.d("TvRemoteProvProxy", this + ": onServiceConnected()");
    }
    if (this.mBound)
    {
      disconnect();
      ??? = ITvRemoteProvider.Stub.asInterface(paramIBinder);
      if (??? == null) {
        break label151;
      }
      paramIBinder = new Connection(???);
      if (!paramIBinder.register()) {
        break label119;
      }
    }
    label119:
    while (!DEBUG) {
      synchronized (this.mLock)
      {
        this.mActiveConnection = paramIBinder;
        if (DEBUG) {
          Slog.d("TvRemoteProvProxy", this + ": Connected successfully.");
        }
        return;
      }
    }
    Slog.d("TvRemoteProvProxy", this + ": Registration failed");
    return;
    label151:
    Slog.e("TvRemoteProvProxy", this + ": Service returned invalid remote-control provider binder");
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    if (DEBUG) {
      Slog.d("TvRemoteProvProxy", this + ": Service disconnected");
    }
    disconnect();
  }
  
  public void rebindIfDisconnected()
  {
    synchronized (this.mLock)
    {
      if ((this.mActiveConnection == null) && (shouldBind()))
      {
        unbind();
        bind();
      }
      return;
    }
  }
  
  public void setProviderSink(ProviderMethods paramProviderMethods)
  {
    this.mProviderMethods = paramProviderMethods;
  }
  
  public void start()
  {
    if (!this.mRunning)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": Starting");
      }
      this.mRunning = true;
      updateBinding();
    }
  }
  
  public void stop()
  {
    if (this.mRunning)
    {
      if (DEBUG) {
        Slog.d("TvRemoteProvProxy", this + ": Stopping");
      }
      this.mRunning = false;
      updateBinding();
    }
  }
  
  private final class Connection
    implements IBinder.DeathRecipient
  {
    private final TvRemoteProviderProxy.RemoteServiceInputProvider mServiceInputProvider;
    private final ITvRemoteProvider mTvRemoteProvider;
    
    public Connection(ITvRemoteProvider paramITvRemoteProvider)
    {
      this.mTvRemoteProvider = paramITvRemoteProvider;
      this.mServiceInputProvider = new TvRemoteProviderProxy.RemoteServiceInputProvider(this);
    }
    
    public void binderDied()
    {
      TvRemoteProviderProxy.-get2(TvRemoteProviderProxy.this).post(new Runnable()
      {
        public void run()
        {
          TvRemoteProviderProxy.-wrap0(TvRemoteProviderProxy.this, TvRemoteProviderProxy.Connection.this);
        }
      });
    }
    
    void clearInputBridge(IBinder paramIBinder)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            if (TvRemoteProviderProxy.-get0()) {
              Slog.d("TvRemoteProvProxy", this + ": clearInputBridge," + " token=" + paramIBinder);
            }
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).clearInputBridge(TvRemoteProviderProxy.this, paramIBinder);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "clearInputBridge, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void closeInputBridge(IBinder paramIBinder)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            if (TvRemoteProviderProxy.-get0()) {
              Slog.d("TvRemoteProvProxy", this + ": closeInputBridge," + " token=" + paramIBinder);
            }
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).closeInputBridge(TvRemoteProviderProxy.this, paramIBinder);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "closeInputBridge, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    public void dispose()
    {
      if (TvRemoteProviderProxy.-get0()) {
        Slog.d("TvRemoteProvProxy", "Connection::dispose()");
      }
      this.mTvRemoteProvider.asBinder().unlinkToDeath(this, 0);
      this.mServiceInputProvider.dispose();
    }
    
    public void onInputBridgeConnected(IBinder paramIBinder)
    {
      if (TvRemoteProviderProxy.-get0()) {
        Slog.d("TvRemoteProvProxy", this + ": onInputBridgeConnected");
      }
      try
      {
        this.mTvRemoteProvider.onInputBridgeConnected(paramIBinder);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        Slog.e("TvRemoteProvProxy", "Failed to deliver onInputBridgeConnected. ", paramIBinder);
      }
    }
    
    void openInputBridge(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            if (TvRemoteProviderProxy.-get0()) {
              Slog.d("TvRemoteProvProxy", this + ": openInputBridge," + " token=" + paramIBinder + ", name=" + paramString);
            }
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).openInputBridge(TvRemoteProviderProxy.this, paramIBinder, paramString, paramInt1, paramInt2, paramInt3);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "openInputBridge, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    public boolean register()
    {
      if (TvRemoteProviderProxy.-get0()) {
        Slog.d("TvRemoteProvProxy", "Connection::register()");
      }
      try
      {
        this.mTvRemoteProvider.asBinder().linkToDeath(this, 0);
        this.mTvRemoteProvider.setRemoteServiceInputSink(this.mServiceInputProvider);
        TvRemoteProviderProxy.-get2(TvRemoteProviderProxy.this).post(new Runnable()
        {
          public void run()
          {
            TvRemoteProviderProxy.-wrap1(TvRemoteProviderProxy.this, TvRemoteProviderProxy.Connection.this);
          }
        });
        return true;
      }
      catch (RemoteException localRemoteException)
      {
        binderDied();
      }
      return false;
    }
    
    void sendKeyDown(IBinder paramIBinder, int paramInt)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendKeyDown(TvRemoteProviderProxy.this, paramIBinder, paramInt);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendKeyDown, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void sendKeyUp(IBinder paramIBinder, int paramInt)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendKeyUp(TvRemoteProviderProxy.this, paramIBinder, paramInt);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendKeyUp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void sendPointerDown(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendPointerDown(TvRemoteProviderProxy.this, paramIBinder, paramInt1, paramInt2, paramInt3);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendPointerDown, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void sendPointerSync(IBinder paramIBinder)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendPointerSync(TvRemoteProviderProxy.this, paramIBinder);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendPointerSync, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void sendPointerUp(IBinder paramIBinder, int paramInt)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendPointerUp(TvRemoteProviderProxy.this, paramIBinder, paramInt);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendPointerUp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
    
    void sendTimestamp(IBinder paramIBinder, long paramLong)
    {
      for (;;)
      {
        synchronized (TvRemoteProviderProxy.-get3(TvRemoteProviderProxy.this))
        {
          if ((TvRemoteProviderProxy.-get1(TvRemoteProviderProxy.this) == this) && (Binder.getCallingUid() == TvRemoteProviderProxy.-get5(TvRemoteProviderProxy.this)))
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              if (TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this) != null) {
                TvRemoteProviderProxy.-get4(TvRemoteProviderProxy.this).sendTimeStamp(TvRemoteProviderProxy.this, paramIBinder, paramLong);
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            finally
            {
              paramIBinder = finally;
              Binder.restoreCallingIdentity(l);
              throw paramIBinder;
            }
          }
        }
        if (TvRemoteProviderProxy.-get0()) {
          Slog.w("TvRemoteProvProxy", "sendTimeStamp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
        }
      }
    }
  }
  
  public static abstract interface ProviderMethods
  {
    public abstract void clearInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder);
    
    public abstract void closeInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder);
    
    public abstract void openInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void sendKeyDown(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt);
    
    public abstract void sendKeyUp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt);
    
    public abstract void sendPointerDown(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void sendPointerSync(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder);
    
    public abstract void sendPointerUp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt);
    
    public abstract void sendTimeStamp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, long paramLong);
  }
  
  private static final class RemoteServiceInputProvider
    extends ITvRemoteServiceInput.Stub
  {
    private final WeakReference<TvRemoteProviderProxy.Connection> mConnectionRef;
    
    public RemoteServiceInputProvider(TvRemoteProviderProxy.Connection paramConnection)
    {
      this.mConnectionRef = new WeakReference(paramConnection);
    }
    
    public void clearInputBridge(IBinder paramIBinder)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.clearInputBridge(paramIBinder);
      }
    }
    
    public void closeInputBridge(IBinder paramIBinder)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.closeInputBridge(paramIBinder);
      }
    }
    
    public void dispose()
    {
      this.mConnectionRef.clear();
    }
    
    public void openInputBridge(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.openInputBridge(paramIBinder, paramString, paramInt1, paramInt2, paramInt3);
      }
    }
    
    public void sendKeyDown(IBinder paramIBinder, int paramInt)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendKeyDown(paramIBinder, paramInt);
      }
    }
    
    public void sendKeyUp(IBinder paramIBinder, int paramInt)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendKeyUp(paramIBinder, paramInt);
      }
    }
    
    public void sendPointerDown(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendPointerDown(paramIBinder, paramInt1, paramInt2, paramInt3);
      }
    }
    
    public void sendPointerSync(IBinder paramIBinder)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendPointerSync(paramIBinder);
      }
    }
    
    public void sendPointerUp(IBinder paramIBinder, int paramInt)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendPointerUp(paramIBinder, paramInt);
      }
    }
    
    public void sendTimestamp(IBinder paramIBinder, long paramLong)
      throws RemoteException
    {
      TvRemoteProviderProxy.Connection localConnection = (TvRemoteProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.sendTimestamp(paramIBinder, paramLong);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvRemoteProviderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */