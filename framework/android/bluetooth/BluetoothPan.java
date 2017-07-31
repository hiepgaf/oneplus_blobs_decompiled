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

public final class BluetoothPan
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED";
  private static final boolean DBG = true;
  public static final String EXTRA_LOCAL_ROLE = "android.bluetooth.pan.extra.LOCAL_ROLE";
  public static final int LOCAL_NAP_ROLE = 1;
  public static final int LOCAL_PANU_ROLE = 2;
  public static final int PAN_CONNECT_FAILED_ALREADY_CONNECTED = 1001;
  public static final int PAN_CONNECT_FAILED_ATTEMPT_FAILED = 1002;
  public static final int PAN_DISCONNECT_FAILED_NOT_CONNECTED = 1000;
  public static final int PAN_OPERATION_GENERIC_FAILURE = 1003;
  public static final int PAN_OPERATION_SUCCESS = 1004;
  public static final int PAN_ROLE_NONE = 0;
  public static final int REMOTE_NAP_ROLE = 1;
  public static final int REMOTE_PANU_ROLE = 2;
  private static final String TAG = "BluetoothPan";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("BluetoothPan", "BluetoothPAN Proxy object connected");
      BluetoothPan.-set0(BluetoothPan.this, IBluetoothPan.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothPan.-get3(BluetoothPan.this) != null) {
        BluetoothPan.-get3(BluetoothPan.this).onServiceConnected(5, BluetoothPan.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothPan", "BluetoothPAN Proxy object disconnected");
      BluetoothPan.-set0(BluetoothPan.this, null);
      if (BluetoothPan.-get3(BluetoothPan.this) != null) {
        BluetoothPan.-get3(BluetoothPan.this).onServiceDisconnected(5);
      }
    }
  };
  private Context mContext;
  private IBluetoothPan mPanService;
  private BluetoothProfile.ServiceListener mServiceListener;
  private final IBluetoothStateChangeCallback mStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothPan", "onBluetoothStateChange on: " + paramAnonymousBoolean);
      if (paramAnonymousBoolean) {
        try
        {
          if (BluetoothPan.-get2(BluetoothPan.this) == null) {
            BluetoothPan.this.doBind();
          }
          return;
        }
        catch (SecurityException localSecurityException)
        {
          Log.e("BluetoothPan", "onBluetoothStateChange: could not bind to PAN service: ", localSecurityException);
          return;
        }
        catch (IllegalStateException localIllegalStateException)
        {
          Log.e("BluetoothPan", "onBluetoothStateChange: could not bind to PAN service: ", localIllegalStateException);
          return;
        }
      }
      synchronized (BluetoothPan.-get0(BluetoothPan.this))
      {
        try
        {
          BluetoothPan.-set0(BluetoothPan.this, null);
          BluetoothPan.-get1(BluetoothPan.this).unbindService(BluetoothPan.-get0(BluetoothPan.this));
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("BluetoothPan", "", localException);
          }
        }
      }
    }
  };
  
  BluetoothPan(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
  {
    this.mContext = paramContext;
    this.mServiceListener = paramServiceListener;
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    try
    {
      this.mAdapter.getBluetoothManager().registerStateChangeCallback(this.mStateChangeCallback);
      doBind();
      return;
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        Log.w("BluetoothPan", "Unable to register BluetoothStateChangeCallback", paramContext);
      }
    }
  }
  
  private boolean isEnabled()
  {
    return this.mAdapter.getState() == 12;
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
    Log.d("BluetoothPan", paramString);
  }
  
  void close()
  {
    ??? = this.mAdapter.getBluetoothManager();
    if (??? != null) {}
    try
    {
      ((IBluetoothManager)???).unregisterStateChangeCallback(this.mStateChangeCallback);
    }
    catch (RemoteException localRemoteException)
    {
      synchronized (this.mConnection)
      {
        for (;;)
        {
          IBluetoothPan localIBluetoothPan = this.mPanService;
          if (localIBluetoothPan != null) {}
          try
          {
            this.mPanService = null;
            this.mContext.unbindService(this.mConnection);
            this.mServiceListener = null;
            return;
            localRemoteException = localRemoteException;
            Log.w("BluetoothPan", "Unable to unregister BluetoothStateChangeCallback", localRemoteException);
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Log.e("BluetoothPan", "", localException);
            }
          }
        }
      }
    }
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    log("connect(" + paramBluetoothDevice + ")");
    if ((this.mPanService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mPanService.connect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mPanService == null) {
      Log.w("BluetoothPan", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    log("disconnect(" + paramBluetoothDevice + ")");
    if ((this.mPanService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mPanService.disconnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mPanService == null) {
      Log.w("BluetoothPan", "Proxy not attached to service");
    }
    return false;
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothPan.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothPan", "Could not bind to Bluetooth Pan Service with " + localIntent);
    return false;
  }
  
  protected void finalize()
  {
    close();
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if ((this.mPanService != null) && (isEnabled())) {
      try
      {
        List localList = this.mPanService.getConnectedDevices();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mPanService == null) {
      Log.w("BluetoothPan", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mPanService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mPanService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mPanService == null) {
      Log.w("BluetoothPan", "Proxy not attached to service");
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    if ((this.mPanService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mPanService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mPanService == null) {
      Log.w("BluetoothPan", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public boolean isTetheringOn()
  {
    if ((this.mPanService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mPanService.isTetheringOn();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
      }
    }
    return false;
  }
  
  public void setBluetoothTethering(boolean paramBoolean)
  {
    log("setBluetoothTethering(" + paramBoolean + ")");
    if ((this.mPanService != null) && (isEnabled())) {}
    try
    {
      this.mPanService.setBluetoothTethering(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothPan", "Stack:" + Log.getStackTraceString(new Throwable()));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothPan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */