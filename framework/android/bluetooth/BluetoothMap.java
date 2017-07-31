package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothMap
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED";
  private static final boolean DBG = true;
  public static final int RESULT_CANCELED = 2;
  public static final int RESULT_FAILURE = 0;
  public static final int RESULT_SUCCESS = 1;
  public static final int STATE_ERROR = -1;
  private static final String TAG = "BluetoothMap";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothMap", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothMap.-get0(BluetoothMap.this))
        {
          try
          {
            BluetoothMap.-set0(BluetoothMap.this, null);
            BluetoothMap.-get1(BluetoothMap.this).unbindService(BluetoothMap.-get0(BluetoothMap.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothMap", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothMap.-get0(BluetoothMap.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothMap.-get2(BluetoothMap.this) != null) {
            continue;
          }
          BluetoothMap.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothMap", "", localException1);
          Object localObject2 = localServiceConnection;
        }
        finally {}
      }
    }
  };
  private final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      BluetoothMap.-wrap0("Proxy object connected");
      BluetoothMap.-set0(BluetoothMap.this, IBluetoothMap.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothMap.-get3(BluetoothMap.this) != null) {
        BluetoothMap.-get3(BluetoothMap.this).onServiceConnected(9, BluetoothMap.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      BluetoothMap.-wrap0("Proxy object disconnected");
      BluetoothMap.-set0(BluetoothMap.this, null);
      if (BluetoothMap.-get3(BluetoothMap.this) != null) {
        BluetoothMap.-get3(BluetoothMap.this).onServiceDisconnected(9);
      }
    }
  };
  private final Context mContext;
  private IBluetoothMap mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothMap(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
  {
    Log.d("BluetoothMap", "Create BluetoothMap proxy object");
    this.mContext = paramContext;
    this.mServiceListener = paramServiceListener;
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    paramContext = this.mAdapter.getBluetoothManager();
    if (paramContext != null) {}
    try
    {
      paramContext.registerStateChangeCallback(this.mBluetoothStateChangeCallback);
      doBind();
      return;
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        Log.e("BluetoothMap", "", paramContext);
      }
    }
  }
  
  public static boolean doesClassMatchSink(BluetoothClass paramBluetoothClass)
  {
    switch (paramBluetoothClass.getDeviceClass())
    {
    default: 
      return false;
    }
    return true;
  }
  
  private boolean isEnabled()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (localBluetoothAdapter.getState() == 12)) {
      return true;
    }
    log("Bluetooth is Not enabled");
    return false;
  }
  
  private boolean isValidDevice(BluetoothDevice paramBluetoothDevice)
  {
    if (paramBluetoothDevice == null) {
      return false;
    }
    return BluetoothAdapter.checkBluetoothAddress(paramBluetoothDevice.getAddress());
  }
  
  private static void log(String paramString)
  {
    Log.d("BluetoothMap", paramString);
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 94	android/bluetooth/BluetoothMap:mAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   6: invokevirtual 98	android/bluetooth/BluetoothAdapter:getBluetoothManager	()Landroid/bluetooth/IBluetoothManager;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +13 -> 24
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 77	android/bluetooth/BluetoothMap:mBluetoothStateChangeCallback	Landroid/bluetooth/IBluetoothStateChangeCallback;
    //   19: invokeinterface 146 2 0
    //   24: aload_0
    //   25: getfield 45	android/bluetooth/BluetoothMap:mConnection	Landroid/content/ServiceConnection;
    //   28: astore_1
    //   29: aload_1
    //   30: monitorenter
    //   31: aload_0
    //   32: getfield 54	android/bluetooth/BluetoothMap:mService	Landroid/bluetooth/IBluetoothMap;
    //   35: astore_2
    //   36: aload_2
    //   37: ifnull +19 -> 56
    //   40: aload_0
    //   41: aconst_null
    //   42: putfield 54	android/bluetooth/BluetoothMap:mService	Landroid/bluetooth/IBluetoothMap;
    //   45: aload_0
    //   46: getfield 50	android/bluetooth/BluetoothMap:mContext	Landroid/content/Context;
    //   49: aload_0
    //   50: getfield 45	android/bluetooth/BluetoothMap:mConnection	Landroid/content/ServiceConnection;
    //   53: invokevirtual 152	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   56: aload_1
    //   57: monitorexit
    //   58: aload_0
    //   59: aconst_null
    //   60: putfield 58	android/bluetooth/BluetoothMap:mServiceListener	Landroid/bluetooth/BluetoothProfile$ServiceListener;
    //   63: aload_0
    //   64: monitorexit
    //   65: return
    //   66: astore_1
    //   67: ldc 28
    //   69: ldc 110
    //   71: aload_1
    //   72: invokestatic 114	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   75: pop
    //   76: goto -52 -> 24
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    //   84: astore_2
    //   85: ldc 28
    //   87: ldc 110
    //   89: aload_2
    //   90: invokestatic 114	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   93: pop
    //   94: goto -38 -> 56
    //   97: astore_2
    //   98: aload_1
    //   99: monitorexit
    //   100: aload_2
    //   101: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	102	0	this	BluetoothMap
    //   66	6	1	localException1	Exception
    //   79	20	1	localObject2	Object
    //   35	2	2	localIBluetoothMap	IBluetoothMap
    //   84	6	2	localException2	Exception
    //   97	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   14	24	66	java/lang/Exception
    //   2	10	79	finally
    //   14	24	79	finally
    //   24	31	79	finally
    //   56	63	79	finally
    //   67	76	79	finally
    //   98	102	79	finally
    //   40	56	84	java/lang/Exception
    //   31	36	97	finally
    //   40	56	97	finally
    //   85	94	97	finally
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    log("connect(" + paramBluetoothDevice + ")" + "not supported for MAPS");
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    log("disconnect(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.disconnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return false;
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothMap.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothMap", "Could not bind to Bluetooth MAP Service with " + localIntent);
    return false;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public BluetoothDevice getClient()
  {
    if (this.mService != null) {
      try
      {
        BluetoothDevice localBluetoothDevice = this.mService.getClient();
        return localBluetoothDevice;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothMap", localRemoteException.toString());
        return null;
      }
    }
    Log.w("BluetoothMap", "Proxy not attached to service");
    log(Log.getStackTraceString(new Throwable()));
    return null;
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    log("getConnectedDevices()");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        List localList = this.mService.getConnectedDevices();
        return localList;
      }
      catch (NullPointerException localNullPointerException)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    log("getConnectionState(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    log("getDevicesMatchingStates()");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getPriority(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getPriority(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return 0;
  }
  
  public int getState()
  {
    if (this.mService != null) {
      try
      {
        int i = this.mService.getState();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothMap", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return -1;
      Log.w("BluetoothMap", "Proxy not attached to service");
      log(Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean isConnected(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isConnected(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothMap", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothMap", "Proxy not attached to service");
      log(Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("setPriority(" + paramBluetoothDevice + ", " + paramInt + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
    {
      if ((paramInt != 0) && (paramInt != 100)) {
        return false;
      }
      try
      {
        boolean bool = this.mService.setPriority(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothMap", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothMap", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */