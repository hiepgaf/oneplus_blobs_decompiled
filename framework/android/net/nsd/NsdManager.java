package android.net.nsd;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import java.util.concurrent.CountDownLatch;

public final class NsdManager
{
  public static final String ACTION_NSD_STATE_CHANGED = "android.net.nsd.STATE_CHANGED";
  private static final int BASE = 393216;
  private static final int BUSY_LISTENER_KEY = -1;
  public static final int DISABLE = 393241;
  public static final int DISCOVER_SERVICES = 393217;
  public static final int DISCOVER_SERVICES_FAILED = 393219;
  public static final int DISCOVER_SERVICES_STARTED = 393218;
  public static final int ENABLE = 393240;
  public static final String EXTRA_NSD_STATE = "nsd_state";
  public static final int FAILURE_ALREADY_ACTIVE = 3;
  public static final int FAILURE_INTERNAL_ERROR = 0;
  public static final int FAILURE_MAX_LIMIT = 4;
  private static final int INVALID_LISTENER_KEY = 0;
  public static final int NATIVE_DAEMON_EVENT = 393242;
  public static final int NSD_STATE_DISABLED = 1;
  public static final int NSD_STATE_ENABLED = 2;
  public static final int PROTOCOL_DNS_SD = 1;
  public static final int REGISTER_SERVICE = 393225;
  public static final int REGISTER_SERVICE_FAILED = 393226;
  public static final int REGISTER_SERVICE_SUCCEEDED = 393227;
  public static final int RESOLVE_SERVICE = 393234;
  public static final int RESOLVE_SERVICE_FAILED = 393235;
  public static final int RESOLVE_SERVICE_SUCCEEDED = 393236;
  public static final int SERVICE_FOUND = 393220;
  public static final int SERVICE_LOST = 393221;
  public static final int STOP_DISCOVERY = 393222;
  public static final int STOP_DISCOVERY_FAILED = 393223;
  public static final int STOP_DISCOVERY_SUCCEEDED = 393224;
  private static final String TAG = "NsdManager";
  public static final int UNREGISTER_SERVICE = 393228;
  public static final int UNREGISTER_SERVICE_FAILED = 393229;
  public static final int UNREGISTER_SERVICE_SUCCEEDED = 393230;
  private final AsyncChannel mAsyncChannel = new AsyncChannel();
  private final CountDownLatch mConnected = new CountDownLatch(1);
  private Context mContext;
  private ServiceHandler mHandler;
  private int mListenerKey = 1;
  private final SparseArray mListenerMap = new SparseArray();
  private final Object mMapLock = new Object();
  INsdManager mService;
  private final SparseArray<NsdServiceInfo> mServiceMap = new SparseArray();
  
  public NsdManager(Context paramContext, INsdManager paramINsdManager)
  {
    this.mService = paramINsdManager;
    this.mContext = paramContext;
    init();
  }
  
  private Object getListener(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    synchronized (this.mMapLock)
    {
      Object localObject2 = this.mListenerMap.get(paramInt);
      return localObject2;
    }
  }
  
  private int getListenerKey(Object paramObject)
  {
    synchronized (this.mMapLock)
    {
      int i = this.mListenerMap.indexOfValue(paramObject);
      if (i != -1)
      {
        i = this.mListenerMap.keyAt(i);
        return i;
      }
      return 0;
    }
  }
  
  private Messenger getMessenger()
  {
    try
    {
      Messenger localMessenger = this.mService.getMessenger();
      return localMessenger;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private NsdServiceInfo getNsdService(int paramInt)
  {
    synchronized (this.mMapLock)
    {
      NsdServiceInfo localNsdServiceInfo = (NsdServiceInfo)this.mServiceMap.get(paramInt);
      return localNsdServiceInfo;
    }
  }
  
  private String getNsdServiceInfoType(NsdServiceInfo paramNsdServiceInfo)
  {
    if (paramNsdServiceInfo == null) {
      return "?";
    }
    return paramNsdServiceInfo.getServiceType();
  }
  
  private void init()
  {
    Messenger localMessenger = getMessenger();
    if (localMessenger == null) {
      throw new RuntimeException("Failed to initialize");
    }
    HandlerThread localHandlerThread = new HandlerThread("NsdManager");
    localHandlerThread.start();
    this.mHandler = new ServiceHandler(localHandlerThread.getLooper());
    this.mAsyncChannel.connect(this.mContext, this.mHandler, localMessenger);
    try
    {
      this.mConnected.await();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.e("NsdManager", "interrupted wait at init");
    }
  }
  
  private int putListener(Object paramObject, NsdServiceInfo paramNsdServiceInfo)
  {
    if (paramObject == null) {
      return 0;
    }
    synchronized (this.mMapLock)
    {
      int i = this.mListenerMap.indexOfValue(paramObject);
      if (i != -1) {
        return -1;
      }
      do
      {
        i = this.mListenerKey;
        this.mListenerKey = (i + 1);
      } while (i == 0);
      this.mListenerMap.put(i, paramObject);
      this.mServiceMap.put(i, paramNsdServiceInfo);
      return i;
    }
  }
  
  private void removeListener(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    synchronized (this.mMapLock)
    {
      this.mListenerMap.remove(paramInt);
      this.mServiceMap.remove(paramInt);
      return;
    }
  }
  
  public void discoverServices(String paramString, int paramInt, DiscoveryListener paramDiscoveryListener)
  {
    if (paramDiscoveryListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Service type cannot be empty");
    }
    if (paramInt != 1) {
      throw new IllegalArgumentException("Unsupported protocol");
    }
    NsdServiceInfo localNsdServiceInfo = new NsdServiceInfo();
    localNsdServiceInfo.setServiceType(paramString);
    paramInt = putListener(paramDiscoveryListener, localNsdServiceInfo);
    if (paramInt == -1) {
      throw new IllegalArgumentException("listener already in use");
    }
    this.mAsyncChannel.sendMessage(393217, 0, paramInt, localNsdServiceInfo);
  }
  
  public void registerService(NsdServiceInfo paramNsdServiceInfo, int paramInt, RegistrationListener paramRegistrationListener)
  {
    if ((TextUtils.isEmpty(paramNsdServiceInfo.getServiceName())) || (TextUtils.isEmpty(paramNsdServiceInfo.getServiceType()))) {
      throw new IllegalArgumentException("Service name or type cannot be empty");
    }
    if (paramNsdServiceInfo.getPort() <= 0) {
      throw new IllegalArgumentException("Invalid port number");
    }
    if (paramRegistrationListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    if (paramInt != 1) {
      throw new IllegalArgumentException("Unsupported protocol");
    }
    paramInt = putListener(paramRegistrationListener, paramNsdServiceInfo);
    if (paramInt == -1) {
      throw new IllegalArgumentException("listener already in use");
    }
    this.mAsyncChannel.sendMessage(393225, 0, paramInt, paramNsdServiceInfo);
  }
  
  public void resolveService(NsdServiceInfo paramNsdServiceInfo, ResolveListener paramResolveListener)
  {
    if ((TextUtils.isEmpty(paramNsdServiceInfo.getServiceName())) || (TextUtils.isEmpty(paramNsdServiceInfo.getServiceType()))) {
      throw new IllegalArgumentException("Service name or type cannot be empty");
    }
    if (paramResolveListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    int i = putListener(paramResolveListener, paramNsdServiceInfo);
    if (i == -1) {
      throw new IllegalArgumentException("listener already in use");
    }
    this.mAsyncChannel.sendMessage(393234, 0, i, paramNsdServiceInfo);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    try
    {
      this.mService.setEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void stopServiceDiscovery(DiscoveryListener paramDiscoveryListener)
  {
    int i = getListenerKey(paramDiscoveryListener);
    if (i == 0) {
      throw new IllegalArgumentException("service discovery not active on listener");
    }
    if (paramDiscoveryListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    this.mAsyncChannel.sendMessage(393222, 0, i);
  }
  
  public void unregisterService(RegistrationListener paramRegistrationListener)
  {
    int i = getListenerKey(paramRegistrationListener);
    if (i == 0) {
      throw new IllegalArgumentException("listener not registered");
    }
    if (paramRegistrationListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    this.mAsyncChannel.sendMessage(393228, 0, i);
  }
  
  public static abstract interface DiscoveryListener
  {
    public abstract void onDiscoveryStarted(String paramString);
    
    public abstract void onDiscoveryStopped(String paramString);
    
    public abstract void onServiceFound(NsdServiceInfo paramNsdServiceInfo);
    
    public abstract void onServiceLost(NsdServiceInfo paramNsdServiceInfo);
    
    public abstract void onStartDiscoveryFailed(String paramString, int paramInt);
    
    public abstract void onStopDiscoveryFailed(String paramString, int paramInt);
  }
  
  public static abstract interface RegistrationListener
  {
    public abstract void onRegistrationFailed(NsdServiceInfo paramNsdServiceInfo, int paramInt);
    
    public abstract void onServiceRegistered(NsdServiceInfo paramNsdServiceInfo);
    
    public abstract void onServiceUnregistered(NsdServiceInfo paramNsdServiceInfo);
    
    public abstract void onUnregistrationFailed(NsdServiceInfo paramNsdServiceInfo, int paramInt);
  }
  
  public static abstract interface ResolveListener
  {
    public abstract void onResolveFailed(NsdServiceInfo paramNsdServiceInfo, int paramInt);
    
    public abstract void onServiceResolved(NsdServiceInfo paramNsdServiceInfo);
  }
  
  private class ServiceHandler
    extends Handler
  {
    ServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject;
      switch (paramMessage.what)
      {
      case 69633: 
      case 69635: 
      default: 
        localObject = NsdManager.-wrap1(NsdManager.this, paramMessage.arg2);
        if (localObject == null)
        {
          Log.d("NsdManager", "Stale key " + paramMessage.arg2);
          return;
        }
        break;
      case 69632: 
        NsdManager.-get0(NsdManager.this).sendMessage(69633);
        return;
      case 69634: 
        NsdManager.-get1(NsdManager.this).countDown();
        return;
      case 69636: 
        Log.e("NsdManager", "Channel lost");
        return;
      }
      NsdServiceInfo localNsdServiceInfo = NsdManager.-wrap0(NsdManager.this, paramMessage.arg2);
      switch (paramMessage.what)
      {
      case 393222: 
      case 393225: 
      case 393228: 
      case 393231: 
      case 393232: 
      case 393233: 
      case 393234: 
      default: 
        Log.d("NsdManager", "Ignored " + paramMessage);
        return;
      case 393218: 
        paramMessage = NsdManager.-wrap2(NsdManager.this, (NsdServiceInfo)paramMessage.obj);
        ((NsdManager.DiscoveryListener)localObject).onDiscoveryStarted(paramMessage);
        return;
      case 393219: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.DiscoveryListener)localObject).onStartDiscoveryFailed(NsdManager.-wrap2(NsdManager.this, localNsdServiceInfo), paramMessage.arg1);
        return;
      case 393220: 
        ((NsdManager.DiscoveryListener)localObject).onServiceFound((NsdServiceInfo)paramMessage.obj);
        return;
      case 393221: 
        ((NsdManager.DiscoveryListener)localObject).onServiceLost((NsdServiceInfo)paramMessage.obj);
        return;
      case 393223: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.DiscoveryListener)localObject).onStopDiscoveryFailed(NsdManager.-wrap2(NsdManager.this, localNsdServiceInfo), paramMessage.arg1);
        return;
      case 393224: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.DiscoveryListener)localObject).onDiscoveryStopped(NsdManager.-wrap2(NsdManager.this, localNsdServiceInfo));
        return;
      case 393226: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.RegistrationListener)localObject).onRegistrationFailed(localNsdServiceInfo, paramMessage.arg1);
        return;
      case 393227: 
        ((NsdManager.RegistrationListener)localObject).onServiceRegistered((NsdServiceInfo)paramMessage.obj);
        return;
      case 393229: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.RegistrationListener)localObject).onUnregistrationFailed(localNsdServiceInfo, paramMessage.arg1);
        return;
      case 393230: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.RegistrationListener)localObject).onServiceUnregistered(localNsdServiceInfo);
        return;
      case 393235: 
        NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
        ((NsdManager.ResolveListener)localObject).onResolveFailed(localNsdServiceInfo, paramMessage.arg1);
        return;
      }
      NsdManager.-wrap3(NsdManager.this, paramMessage.arg2);
      ((NsdManager.ResolveListener)localObject).onServiceResolved((NsdServiceInfo)paramMessage.obj);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/nsd/NsdManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */