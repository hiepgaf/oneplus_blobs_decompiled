package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothPbap
{
  private static final boolean DBG = true;
  public static final String PBAP_PREVIOUS_STATE = "android.bluetooth.pbap.intent.PBAP_PREVIOUS_STATE";
  public static final String PBAP_STATE = "android.bluetooth.pbap.intent.PBAP_STATE";
  public static final String PBAP_STATE_CHANGED_ACTION = "android.bluetooth.pbap.intent.action.PBAP_STATE_CHANGED";
  public static final int RESULT_CANCELED = 2;
  public static final int RESULT_FAILURE = 0;
  public static final int RESULT_SUCCESS = 1;
  public static final int STATE_CONNECTED = 2;
  public static final int STATE_CONNECTING = 1;
  public static final int STATE_DISCONNECTED = 0;
  public static final int STATE_ERROR = -1;
  private static final String TAG = "BluetoothPbap";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothPbap", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothPbap.-get0(BluetoothPbap.this))
        {
          try
          {
            BluetoothPbap.-set0(BluetoothPbap.this, null);
            BluetoothPbap.-get1(BluetoothPbap.this).unbindService(BluetoothPbap.-get0(BluetoothPbap.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothPbap", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothPbap.-get0(BluetoothPbap.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothPbap.-get2(BluetoothPbap.this) != null) {
            continue;
          }
          BluetoothPbap.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothPbap", "", localException1);
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
      BluetoothPbap.-wrap0("Proxy object connected");
      BluetoothPbap.-set0(BluetoothPbap.this, IBluetoothPbap.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothPbap.-get3(BluetoothPbap.this) != null) {
        BluetoothPbap.-get3(BluetoothPbap.this).onServiceConnected(BluetoothPbap.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      BluetoothPbap.-wrap0("Proxy object disconnected");
      BluetoothPbap.-set0(BluetoothPbap.this, null);
      if (BluetoothPbap.-get3(BluetoothPbap.this) != null) {
        BluetoothPbap.-get3(BluetoothPbap.this).onServiceDisconnected();
      }
    }
  };
  private final Context mContext;
  private IBluetoothPbap mService;
  private ServiceListener mServiceListener;
  
  public BluetoothPbap(Context paramContext, ServiceListener paramServiceListener)
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
        Log.e("BluetoothPbap", "", paramContext);
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
  
  private static void log(String paramString)
  {
    Log.d("BluetoothPbap", paramString);
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 96	android/bluetooth/BluetoothPbap:mAdapter	Landroid/bluetooth/BluetoothAdapter;
    //   6: invokevirtual 100	android/bluetooth/BluetoothAdapter:getBluetoothManager	()Landroid/bluetooth/IBluetoothManager;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +13 -> 24
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 87	android/bluetooth/BluetoothPbap:mBluetoothStateChangeCallback	Landroid/bluetooth/IBluetoothStateChangeCallback;
    //   19: invokeinterface 136 2 0
    //   24: aload_0
    //   25: getfield 55	android/bluetooth/BluetoothPbap:mConnection	Landroid/content/ServiceConnection;
    //   28: astore_1
    //   29: aload_1
    //   30: monitorenter
    //   31: aload_0
    //   32: getfield 64	android/bluetooth/BluetoothPbap:mService	Landroid/bluetooth/IBluetoothPbap;
    //   35: astore_2
    //   36: aload_2
    //   37: ifnull +19 -> 56
    //   40: aload_0
    //   41: aconst_null
    //   42: putfield 64	android/bluetooth/BluetoothPbap:mService	Landroid/bluetooth/IBluetoothPbap;
    //   45: aload_0
    //   46: getfield 60	android/bluetooth/BluetoothPbap:mContext	Landroid/content/Context;
    //   49: aload_0
    //   50: getfield 55	android/bluetooth/BluetoothPbap:mConnection	Landroid/content/ServiceConnection;
    //   53: invokevirtual 142	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   56: aload_1
    //   57: monitorexit
    //   58: aload_0
    //   59: aconst_null
    //   60: putfield 68	android/bluetooth/BluetoothPbap:mServiceListener	Landroid/bluetooth/BluetoothPbap$ServiceListener;
    //   63: aload_0
    //   64: monitorexit
    //   65: return
    //   66: astore_1
    //   67: ldc 38
    //   69: ldc 112
    //   71: aload_1
    //   72: invokestatic 118	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   75: pop
    //   76: goto -52 -> 24
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    //   84: astore_2
    //   85: ldc 38
    //   87: ldc 112
    //   89: aload_2
    //   90: invokestatic 118	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   93: pop
    //   94: goto -38 -> 56
    //   97: astore_2
    //   98: aload_1
    //   99: monitorexit
    //   100: aload_2
    //   101: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	102	0	this	BluetoothPbap
    //   66	6	1	localException1	Exception
    //   79	20	1	localObject2	Object
    //   35	2	2	localIBluetoothPbap	IBluetoothPbap
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
  
  public boolean disconnect()
  {
    log("disconnect()");
    if (this.mService != null) {
      try
      {
        this.mService.disconnect();
        return true;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothPbap", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothPbap", "Proxy not attached to service");
      log(Log.getStackTraceString(new Throwable()));
    }
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothPbap.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothPbap", "Could not bind to Bluetooth Pbap Service with " + localIntent);
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
        Log.e("BluetoothPbap", localRemoteException.toString());
        return null;
      }
    }
    Log.w("BluetoothPbap", "Proxy not attached to service");
    log(Log.getStackTraceString(new Throwable()));
    return null;
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
        Log.e("BluetoothPbap", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return -1;
      Log.w("BluetoothPbap", "Proxy not attached to service");
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
        Log.e("BluetoothPbap", paramBluetoothDevice.toString());
        log(Log.getStackTraceString(new Throwable()));
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothPbap", "Proxy not attached to service");
    }
  }
  
  public static abstract interface ServiceListener
  {
    public abstract void onServiceConnected(BluetoothPbap paramBluetoothPbap);
    
    public abstract void onServiceDisconnected();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothPbap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */