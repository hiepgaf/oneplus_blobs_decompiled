package com.android.server.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.IRemoteDisplayCallback.Stub;
import android.media.IRemoteDisplayProvider;
import android.media.IRemoteDisplayProvider.Stub;
import android.media.RemoteDisplayState;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;

final class RemoteDisplayProviderProxy
  implements ServiceConnection
{
  private static final boolean DEBUG = Log.isLoggable("RemoteDisplayProvider", 3);
  private static final String TAG = "RemoteDisplayProvider";
  private Connection mActiveConnection;
  private boolean mBound;
  private final ComponentName mComponentName;
  private boolean mConnectionReady;
  private final Context mContext;
  private int mDiscoveryMode;
  private RemoteDisplayState mDisplayState;
  private Callback mDisplayStateCallback;
  private final Runnable mDisplayStateChanged = new Runnable()
  {
    public void run()
    {
      RemoteDisplayProviderProxy.-set0(RemoteDisplayProviderProxy.this, false);
      if (RemoteDisplayProviderProxy.-get1(RemoteDisplayProviderProxy.this) != null) {
        RemoteDisplayProviderProxy.-get1(RemoteDisplayProviderProxy.this).onDisplayStateChanged(RemoteDisplayProviderProxy.this, RemoteDisplayProviderProxy.-get0(RemoteDisplayProviderProxy.this));
      }
    }
  };
  private final Handler mHandler;
  private boolean mRunning;
  private boolean mScheduledDisplayStateChangedCallback;
  private String mSelectedDisplayId;
  private final int mUserId;
  
  public RemoteDisplayProviderProxy(Context paramContext, ComponentName paramComponentName, int paramInt)
  {
    this.mContext = paramContext;
    this.mComponentName = paramComponentName;
    this.mUserId = paramInt;
    this.mHandler = new Handler();
  }
  
  private void bind()
  {
    Intent localIntent;
    if (!this.mBound)
    {
      if (DEBUG) {
        Slog.d("RemoteDisplayProvider", this + ": Binding");
      }
      localIntent = new Intent("com.android.media.remotedisplay.RemoteDisplayProvider");
      localIntent.setComponent(this.mComponentName);
    }
    try
    {
      this.mBound = this.mContext.bindServiceAsUser(localIntent, this, 67108865, new UserHandle(this.mUserId));
      if ((!this.mBound) && (DEBUG)) {
        Slog.d("RemoteDisplayProvider", this + ": Bind failed");
      }
      return;
    }
    catch (SecurityException localSecurityException)
    {
      while (!DEBUG) {}
      Slog.d("RemoteDisplayProvider", this + ": Bind failed", localSecurityException);
    }
  }
  
  private void disconnect()
  {
    if (this.mActiveConnection != null)
    {
      if (this.mSelectedDisplayId != null) {
        this.mActiveConnection.disconnect(this.mSelectedDisplayId);
      }
      this.mConnectionReady = false;
      this.mActiveConnection.dispose();
      this.mActiveConnection = null;
      setDisplayState(null);
    }
  }
  
  private void onConnectionDied(Connection paramConnection)
  {
    if (this.mActiveConnection == paramConnection)
    {
      if (DEBUG) {
        Slog.d("RemoteDisplayProvider", this + ": Service connection died");
      }
      disconnect();
    }
  }
  
  private void onConnectionReady(Connection paramConnection)
  {
    if (this.mActiveConnection == paramConnection)
    {
      this.mConnectionReady = true;
      if (this.mDiscoveryMode != 0) {
        this.mActiveConnection.setDiscoveryMode(this.mDiscoveryMode);
      }
      if (this.mSelectedDisplayId != null) {
        this.mActiveConnection.connect(this.mSelectedDisplayId);
      }
    }
  }
  
  private void onDisplayStateChanged(Connection paramConnection, RemoteDisplayState paramRemoteDisplayState)
  {
    if (this.mActiveConnection == paramConnection)
    {
      if (DEBUG) {
        Slog.d("RemoteDisplayProvider", this + ": State changed, state=" + paramRemoteDisplayState);
      }
      setDisplayState(paramRemoteDisplayState);
    }
  }
  
  private void setDisplayState(RemoteDisplayState paramRemoteDisplayState)
  {
    if (!Objects.equals(this.mDisplayState, paramRemoteDisplayState))
    {
      this.mDisplayState = paramRemoteDisplayState;
      if (!this.mScheduledDisplayStateChangedCallback)
      {
        this.mScheduledDisplayStateChangedCallback = true;
        this.mHandler.post(this.mDisplayStateChanged);
      }
    }
  }
  
  private boolean shouldBind()
  {
    return (this.mRunning) && ((this.mDiscoveryMode != 0) || (this.mSelectedDisplayId != null));
  }
  
  private void unbind()
  {
    if (this.mBound)
    {
      if (DEBUG) {
        Slog.d("RemoteDisplayProvider", this + ": Unbinding");
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
  
  public void adjustDisplayVolume(int paramInt)
  {
    if ((this.mConnectionReady) && (this.mSelectedDisplayId != null)) {
      this.mActiveConnection.adjustVolume(this.mSelectedDisplayId, paramInt);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "Proxy");
    paramPrintWriter.println(paramString + "  mUserId=" + this.mUserId);
    paramPrintWriter.println(paramString + "  mRunning=" + this.mRunning);
    paramPrintWriter.println(paramString + "  mBound=" + this.mBound);
    paramPrintWriter.println(paramString + "  mActiveConnection=" + this.mActiveConnection);
    paramPrintWriter.println(paramString + "  mConnectionReady=" + this.mConnectionReady);
    paramPrintWriter.println(paramString + "  mDiscoveryMode=" + this.mDiscoveryMode);
    paramPrintWriter.println(paramString + "  mSelectedDisplayId=" + this.mSelectedDisplayId);
    paramPrintWriter.println(paramString + "  mDisplayState=" + this.mDisplayState);
  }
  
  public RemoteDisplayState getDisplayState()
  {
    return this.mDisplayState;
  }
  
  public String getFlattenedComponentName()
  {
    return this.mComponentName.flattenToShortString();
  }
  
  public boolean hasComponentName(String paramString1, String paramString2)
  {
    if (this.mComponentName.getPackageName().equals(paramString1)) {
      return this.mComponentName.getClassName().equals(paramString2);
    }
    return false;
  }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    if (DEBUG) {
      Slog.d("RemoteDisplayProvider", this + ": Connected");
    }
    if (this.mBound)
    {
      disconnect();
      paramComponentName = IRemoteDisplayProvider.Stub.asInterface(paramIBinder);
      if (paramComponentName == null) {
        break label108;
      }
      paramComponentName = new Connection(paramComponentName);
      if (!paramComponentName.register()) {
        break label75;
      }
      this.mActiveConnection = paramComponentName;
    }
    label75:
    while (!DEBUG) {
      return;
    }
    Slog.d("RemoteDisplayProvider", this + ": Registration failed");
    return;
    label108:
    Slog.e("RemoteDisplayProvider", this + ": Service returned invalid remote display provider binder");
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    if (DEBUG) {
      Slog.d("RemoteDisplayProvider", this + ": Service disconnected");
    }
    disconnect();
  }
  
  public void rebindIfDisconnected()
  {
    if ((this.mActiveConnection == null) && (shouldBind()))
    {
      unbind();
      bind();
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mDisplayStateCallback = paramCallback;
  }
  
  public void setDiscoveryMode(int paramInt)
  {
    if (this.mDiscoveryMode != paramInt)
    {
      this.mDiscoveryMode = paramInt;
      if (this.mConnectionReady) {
        this.mActiveConnection.setDiscoveryMode(paramInt);
      }
      updateBinding();
    }
  }
  
  public void setDisplayVolume(int paramInt)
  {
    if ((this.mConnectionReady) && (this.mSelectedDisplayId != null)) {
      this.mActiveConnection.setVolume(this.mSelectedDisplayId, paramInt);
    }
  }
  
  public void setSelectedDisplay(String paramString)
  {
    if (!Objects.equals(this.mSelectedDisplayId, paramString))
    {
      if ((this.mConnectionReady) && (this.mSelectedDisplayId != null)) {
        this.mActiveConnection.disconnect(this.mSelectedDisplayId);
      }
      this.mSelectedDisplayId = paramString;
      if ((this.mConnectionReady) && (paramString != null)) {
        this.mActiveConnection.connect(paramString);
      }
      updateBinding();
    }
  }
  
  public void start()
  {
    if (!this.mRunning)
    {
      if (DEBUG) {
        Slog.d("RemoteDisplayProvider", this + ": Starting");
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
        Slog.d("RemoteDisplayProvider", this + ": Stopping");
      }
      this.mRunning = false;
      updateBinding();
    }
  }
  
  public String toString()
  {
    return "Service connection " + this.mComponentName.flattenToShortString();
  }
  
  public static abstract interface Callback
  {
    public abstract void onDisplayStateChanged(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy, RemoteDisplayState paramRemoteDisplayState);
  }
  
  private final class Connection
    implements IBinder.DeathRecipient
  {
    private final RemoteDisplayProviderProxy.ProviderCallback mCallback;
    private final IRemoteDisplayProvider mProvider;
    
    public Connection(IRemoteDisplayProvider paramIRemoteDisplayProvider)
    {
      this.mProvider = paramIRemoteDisplayProvider;
      this.mCallback = new RemoteDisplayProviderProxy.ProviderCallback(this);
    }
    
    public void adjustVolume(String paramString, int paramInt)
    {
      try
      {
        this.mProvider.adjustVolume(paramString, paramInt);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("RemoteDisplayProvider", "Failed to deliver request to adjust display volume.", paramString);
      }
    }
    
    public void binderDied()
    {
      RemoteDisplayProviderProxy.-get2(RemoteDisplayProviderProxy.this).post(new Runnable()
      {
        public void run()
        {
          RemoteDisplayProviderProxy.-wrap0(RemoteDisplayProviderProxy.this, RemoteDisplayProviderProxy.Connection.this);
        }
      });
    }
    
    public void connect(String paramString)
    {
      try
      {
        this.mProvider.connect(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("RemoteDisplayProvider", "Failed to deliver request to connect to display.", paramString);
      }
    }
    
    public void disconnect(String paramString)
    {
      try
      {
        this.mProvider.disconnect(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("RemoteDisplayProvider", "Failed to deliver request to disconnect from display.", paramString);
      }
    }
    
    public void dispose()
    {
      this.mProvider.asBinder().unlinkToDeath(this, 0);
      this.mCallback.dispose();
    }
    
    void postStateChanged(final RemoteDisplayState paramRemoteDisplayState)
    {
      RemoteDisplayProviderProxy.-get2(RemoteDisplayProviderProxy.this).post(new Runnable()
      {
        public void run()
        {
          RemoteDisplayProviderProxy.-wrap2(RemoteDisplayProviderProxy.this, RemoteDisplayProviderProxy.Connection.this, paramRemoteDisplayState);
        }
      });
    }
    
    public boolean register()
    {
      try
      {
        this.mProvider.asBinder().linkToDeath(this, 0);
        this.mProvider.setCallback(this.mCallback);
        RemoteDisplayProviderProxy.-get2(RemoteDisplayProviderProxy.this).post(new Runnable()
        {
          public void run()
          {
            RemoteDisplayProviderProxy.-wrap1(RemoteDisplayProviderProxy.this, RemoteDisplayProviderProxy.Connection.this);
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
    
    public void setDiscoveryMode(int paramInt)
    {
      try
      {
        this.mProvider.setDiscoveryMode(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("RemoteDisplayProvider", "Failed to deliver request to set discovery mode.", localRemoteException);
      }
    }
    
    public void setVolume(String paramString, int paramInt)
    {
      try
      {
        this.mProvider.setVolume(paramString, paramInt);
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e("RemoteDisplayProvider", "Failed to deliver request to set display volume.", paramString);
      }
    }
  }
  
  private static final class ProviderCallback
    extends IRemoteDisplayCallback.Stub
  {
    private final WeakReference<RemoteDisplayProviderProxy.Connection> mConnectionRef;
    
    public ProviderCallback(RemoteDisplayProviderProxy.Connection paramConnection)
    {
      this.mConnectionRef = new WeakReference(paramConnection);
    }
    
    public void dispose()
    {
      this.mConnectionRef.clear();
    }
    
    public void onStateChanged(RemoteDisplayState paramRemoteDisplayState)
      throws RemoteException
    {
      RemoteDisplayProviderProxy.Connection localConnection = (RemoteDisplayProviderProxy.Connection)this.mConnectionRef.get();
      if (localConnection != null) {
        localConnection.postStateChanged(paramRemoteDisplayState);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/RemoteDisplayProviderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */