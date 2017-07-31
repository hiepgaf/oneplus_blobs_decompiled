package com.android.server.tv;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TvRemoteService
  extends SystemService
  implements Watchdog.Monitor
{
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_KEYS = false;
  private static final String TAG = "TvRemoteService";
  private Map<IBinder, UinputBridge> mBridgeMap = new ArrayMap();
  public final UserHandler mHandler;
  private final Object mLock = new Object();
  private ArrayList<TvRemoteProviderProxy> mProviderList = new ArrayList();
  private Map<IBinder, TvRemoteProviderProxy> mProviderMap = new ArrayMap();
  
  public TvRemoteService(Context paramContext)
  {
    super(paramContext);
    this.mHandler = new UserHandler(new UserProvider(this), paramContext);
    Watchdog.getInstance().addMonitor(this);
  }
  
  private void clearInputBridgeInternalLocked(IBinder paramIBinder)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.clear(paramIBinder);
    }
  }
  
  private void closeInputBridgeInternalLocked(IBinder paramIBinder)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.close(paramIBinder);
    }
    this.mBridgeMap.remove(paramIBinder);
  }
  
  private void informInputBridgeConnected(IBinder paramIBinder)
  {
    this.mHandler.obtainMessage(2, 0, 0, paramIBinder).sendToTarget();
  }
  
  private void openInputBridgeInternalLocked(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      if (this.mBridgeMap.containsKey(paramIBinder))
      {
        informInputBridgeConnected(paramIBinder);
        return;
      }
      UinputBridge localUinputBridge = new UinputBridge(paramIBinder, paramString, paramInt1, paramInt2, paramInt3);
      this.mBridgeMap.put(paramIBinder, localUinputBridge);
      this.mProviderMap.put(paramIBinder, paramTvRemoteProviderProxy);
      informInputBridgeConnected(paramIBinder);
      return;
    }
    catch (IOException paramTvRemoteProviderProxy)
    {
      Slog.e("TvRemoteService", "Cannot create device for " + paramString);
    }
  }
  
  private void sendKeyDownInternalLocked(IBinder paramIBinder, int paramInt)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendKeyDown(paramIBinder, paramInt);
    }
  }
  
  private void sendKeyUpInternalLocked(IBinder paramIBinder, int paramInt)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendKeyUp(paramIBinder, paramInt);
    }
  }
  
  private void sendPointerDownInternalLocked(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendPointerDown(paramIBinder, paramInt1, paramInt2, paramInt3);
    }
  }
  
  private void sendPointerSyncInternalLocked(IBinder paramIBinder)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendPointerSync(paramIBinder);
    }
  }
  
  private void sendPointerUpInternalLocked(IBinder paramIBinder, int paramInt)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendPointerUp(paramIBinder, paramInt);
    }
  }
  
  private void sendTimeStampInternalLocked(IBinder paramIBinder, long paramLong)
  {
    UinputBridge localUinputBridge = (UinputBridge)this.mBridgeMap.get(paramIBinder);
    if (localUinputBridge != null) {
      localUinputBridge.sendTimestamp(paramIBinder, paramLong);
    }
  }
  
  public void monitor()
  {
    Object localObject = this.mLock;
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 600) {
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  public void onStart() {}
  
  private final class UserHandler
    extends Handler
  {
    public static final int MSG_INPUT_BRIDGE_CONNECTED = 2;
    public static final int MSG_START = 1;
    private boolean mRunning;
    private final TvRemoteProviderWatcher mWatcher = new TvRemoteProviderWatcher(paramContext, paramUserProvider, this);
    
    public UserHandler(TvRemoteService.UserProvider paramUserProvider, Context paramContext)
    {
      super(null, true);
    }
    
    private void start()
    {
      if (!this.mRunning)
      {
        this.mRunning = true;
        this.mWatcher.start();
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      TvRemoteProviderProxy localTvRemoteProviderProxy;
      do
      {
        return;
        start();
        return;
        paramMessage = (IBinder)paramMessage.obj;
        localTvRemoteProviderProxy = (TvRemoteProviderProxy)TvRemoteService.-get2(TvRemoteService.this).get(paramMessage);
      } while (localTvRemoteProviderProxy == null);
      localTvRemoteProviderProxy.inputBridgeConnected(paramMessage);
    }
  }
  
  private final class UserProvider
    implements TvRemoteProviderWatcher.ProviderMethods, TvRemoteProviderProxy.ProviderMethods
  {
    private final TvRemoteService mService;
    
    public UserProvider(TvRemoteService paramTvRemoteService)
    {
      this.mService = paramTvRemoteService;
    }
    
    public void addProvider(TvRemoteProviderProxy paramTvRemoteProviderProxy)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        paramTvRemoteProviderProxy.setProviderSink(this);
        TvRemoteService.-get1(TvRemoteService.this).add(paramTvRemoteProviderProxy);
        Slog.d("TvRemoteService", "provider: " + paramTvRemoteProviderProxy.toString());
        return;
      }
    }
    
    public void clearInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap0(this.mService, paramIBinder);
        }
        return;
      }
    }
    
    public void closeInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy))
        {
          TvRemoteService.-wrap1(this.mService, paramIBinder);
          TvRemoteService.-get2(TvRemoteService.this).remove(paramIBinder);
        }
        return;
      }
    }
    
    public void openInputBridge(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap2(this.mService, paramTvRemoteProviderProxy, paramIBinder, paramString, paramInt1, paramInt2, paramInt3);
        }
        return;
      }
    }
    
    public void removeProvider(TvRemoteProviderProxy paramTvRemoteProviderProxy)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (!TvRemoteService.-get1(TvRemoteService.this).remove(paramTvRemoteProviderProxy)) {
          Slog.e("TvRemoteService", "Unknown provider " + paramTvRemoteProviderProxy);
        }
        return;
      }
    }
    
    public void sendKeyDown(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap3(this.mService, paramIBinder, paramInt);
        }
        return;
      }
    }
    
    public void sendKeyUp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap4(this.mService, paramIBinder, paramInt);
        }
        return;
      }
    }
    
    public void sendPointerDown(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap5(this.mService, paramIBinder, paramInt1, paramInt2, paramInt3);
        }
        return;
      }
    }
    
    public void sendPointerSync(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap6(this.mService, paramIBinder);
        }
        return;
      }
    }
    
    public void sendPointerUp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, int paramInt)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap7(this.mService, paramIBinder, paramInt);
        }
        return;
      }
    }
    
    public void sendTimeStamp(TvRemoteProviderProxy paramTvRemoteProviderProxy, IBinder paramIBinder, long paramLong)
    {
      synchronized (TvRemoteService.-get0(TvRemoteService.this))
      {
        if (TvRemoteService.-get1(TvRemoteService.this).contains(paramTvRemoteProviderProxy)) {
          TvRemoteService.-wrap8(this.mService, paramIBinder, paramLong);
        }
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvRemoteService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */