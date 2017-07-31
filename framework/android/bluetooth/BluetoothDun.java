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

public final class BluetoothDun
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "codeaurora.bluetooth.dun.profile.action.CONNECTION_STATE_CHANGED";
  private static final boolean DBG = false;
  private static final String TAG = "BluetoothDun";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      BluetoothDun.-set0(BluetoothDun.this, IBluetoothDun.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothDun.-get3(BluetoothDun.this) != null) {
        BluetoothDun.-get3(BluetoothDun.this).onServiceConnected(21, BluetoothDun.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      BluetoothDun.-set0(BluetoothDun.this, null);
      if (BluetoothDun.-get3(BluetoothDun.this) != null) {
        BluetoothDun.-get3(BluetoothDun.this).onServiceDisconnected(21);
      }
    }
  };
  private Context mContext;
  private IBluetoothDun mDunService;
  private BluetoothProfile.ServiceListener mServiceListener;
  private IBluetoothStateChangeCallback mStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothDun", "onBluetoothStateChange on: " + paramAnonymousBoolean);
      if (paramAnonymousBoolean) {
        try
        {
          if (BluetoothDun.-get2(BluetoothDun.this) == null)
          {
            Log.d("BluetoothDun", "onBluetoothStateChange call bindService");
            BluetoothDun.this.doBind();
          }
          return;
        }
        catch (SecurityException localSecurityException)
        {
          Log.e("BluetoothDun", "onBluetoothStateChange: could not bind to DUN service: ", localSecurityException);
          return;
        }
        catch (IllegalStateException localIllegalStateException)
        {
          Log.e("BluetoothDun", "onBluetoothStateChange: could not bind to DUN service: ", localIllegalStateException);
          return;
        }
      }
      synchronized (BluetoothDun.-get0(BluetoothDun.this))
      {
        IBluetoothDun localIBluetoothDun = BluetoothDun.-get2(BluetoothDun.this);
        if (localIBluetoothDun != null) {}
        try
        {
          BluetoothDun.-set0(BluetoothDun.this, null);
          BluetoothDun.-get1(BluetoothDun.this).unbindService(BluetoothDun.-get0(BluetoothDun.this));
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("BluetoothDun", "", localException);
          }
        }
      }
    }
  };
  
  BluetoothDun(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
  {
    this.mContext = paramContext;
    this.mServiceListener = paramServiceListener;
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    try
    {
      this.mAdapter.getBluetoothManager().registerStateChangeCallback(this.mStateChangeCallback);
      Log.d("BluetoothDun", "BluetoothDun() call bindService");
      doBind();
      return;
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        Log.w("BluetoothDun", "Unable to register BluetoothStateChangeCallback", paramContext);
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
    Log.d("BluetoothDun", paramString);
  }
  
  void close()
  {
    this.mServiceListener = null;
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
          IBluetoothDun localIBluetoothDun = this.mDunService;
          if (localIBluetoothDun != null) {}
          try
          {
            this.mDunService = null;
            this.mContext.unbindService(this.mConnection);
            return;
            localRemoteException = localRemoteException;
            Log.w("BluetoothDun", "Unable to unregister BluetoothStateChangeCallback", localRemoteException);
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Log.e("BluetoothDun", "", localException);
            }
          }
        }
      }
    }
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mDunService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mDunService.disconnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothDun", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mDunService == null) {
      Log.w("BluetoothDun", "Proxy not attached to service");
    }
    return false;
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothDun.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothDun", "Could not bind to Bluetooth Dun Service with " + localIntent);
    return false;
  }
  
  protected void finalize()
  {
    close();
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if ((this.mDunService != null) && (isEnabled())) {
      try
      {
        List localList = this.mDunService.getConnectedDevices();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothDun", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mDunService == null) {
      Log.w("BluetoothDun", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mDunService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mDunService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothDun", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mDunService == null) {
      Log.w("BluetoothDun", "Proxy not attached to service");
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    if ((this.mDunService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mDunService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothDun", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mDunService == null) {
      Log.w("BluetoothDun", "Proxy not attached to service");
    }
    return new ArrayList();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothDun.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */