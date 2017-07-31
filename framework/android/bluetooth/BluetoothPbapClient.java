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

public final class BluetoothPbapClient
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED";
  private static final boolean DBG = false;
  public static final int RESULT_CANCELED = 2;
  public static final int RESULT_FAILURE = 0;
  public static final int RESULT_SUCCESS = 1;
  public static final int STATE_ERROR = -1;
  private static final String TAG = "BluetoothPbapClient";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothPbapClient.-get0(BluetoothPbapClient.this))
        {
          try
          {
            BluetoothPbapClient.-set0(BluetoothPbapClient.this, null);
            BluetoothPbapClient.-get1(BluetoothPbapClient.this).unbindService(BluetoothPbapClient.-get0(BluetoothPbapClient.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothPbapClient", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothPbapClient.-get0(BluetoothPbapClient.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothPbapClient.-get2(BluetoothPbapClient.this) != null) {
            continue;
          }
          BluetoothPbapClient.-wrap0(BluetoothPbapClient.this);
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothPbapClient", "", localException1);
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
      BluetoothPbapClient.-set0(BluetoothPbapClient.this, IBluetoothPbapClient.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothPbapClient.-get3(BluetoothPbapClient.this) != null) {
        BluetoothPbapClient.-get3(BluetoothPbapClient.this).onServiceConnected(17, BluetoothPbapClient.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      BluetoothPbapClient.-set0(BluetoothPbapClient.this, null);
      if (BluetoothPbapClient.-get3(BluetoothPbapClient.this) != null) {
        BluetoothPbapClient.-get3(BluetoothPbapClient.this).onServiceDisconnected(17);
      }
    }
  };
  private final Context mContext;
  private IBluetoothPbapClient mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothPbapClient(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
  {
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
        Log.e("BluetoothPbapClient", "", paramContext);
      }
    }
  }
  
  private boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothPbapClient.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothPbapClient", "Could not bind to Bluetooth PBAP Client Service with " + localIntent);
    return false;
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
    Log.d("BluetoothPbapClient", paramString);
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 87	android/bluetooth/BluetoothPbapClient:mAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   6: invokevirtual 91	android/bluetooth/BluetoothAdapter:getBluetoothManager	()Landroid/bluetooth/IBluetoothManager;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +13 -> 24
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 78	android/bluetooth/BluetoothPbapClient:mBluetoothStateChangeCallback	Landroid/bluetooth/IBluetoothStateChangeCallback;
    //   19: invokeinterface 190 2 0
    //   24: aload_0
    //   25: getfield 45	android/bluetooth/BluetoothPbapClient:mConnection	Landroid/content/ServiceConnection;
    //   28: astore_1
    //   29: aload_1
    //   30: monitorenter
    //   31: aload_0
    //   32: getfield 54	android/bluetooth/BluetoothPbapClient:mService	Landroid/bluetooth/IBluetoothPbapClient;
    //   35: astore_2
    //   36: aload_2
    //   37: ifnull +19 -> 56
    //   40: aload_0
    //   41: aconst_null
    //   42: putfield 54	android/bluetooth/BluetoothPbapClient:mService	Landroid/bluetooth/IBluetoothPbapClient;
    //   45: aload_0
    //   46: getfield 50	android/bluetooth/BluetoothPbapClient:mContext	Landroid/content/Context;
    //   49: aload_0
    //   50: getfield 45	android/bluetooth/BluetoothPbapClient:mConnection	Landroid/content/ServiceConnection;
    //   53: invokevirtual 194	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   56: aload_1
    //   57: monitorexit
    //   58: aload_0
    //   59: aconst_null
    //   60: putfield 58	android/bluetooth/BluetoothPbapClient:mServiceListener	Landroid/bluetooth/BluetoothProfile$ServiceListener;
    //   63: aload_0
    //   64: monitorexit
    //   65: return
    //   66: astore_1
    //   67: ldc 28
    //   69: ldc 99
    //   71: aload_1
    //   72: invokestatic 105	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   75: pop
    //   76: goto -52 -> 24
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    //   84: astore_2
    //   85: ldc 28
    //   87: ldc 99
    //   89: aload_2
    //   90: invokestatic 105	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   93: pop
    //   94: goto -38 -> 56
    //   97: astore_2
    //   98: aload_1
    //   99: monitorexit
    //   100: aload_2
    //   101: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	102	0	this	BluetoothPbapClient
    //   66	6	1	localException1	Exception
    //   79	20	1	localObject2	Object
    //   35	2	2	localIBluetoothPbapClient	IBluetoothPbapClient
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
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.connect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        this.mService.disconnect(paramBluetoothDevice);
        return true;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
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
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        List localList = this.mService.getConnectedDevices();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
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
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
    return 0;
  }
  
  public boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
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
        Log.e("BluetoothPbapClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothPbapClient", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothPbapClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */