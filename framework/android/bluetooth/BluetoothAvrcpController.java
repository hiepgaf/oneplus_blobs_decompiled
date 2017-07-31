package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothAvrcpController
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.avrcp-controller.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_PLAYER_SETTING = "android.bluetooth.avrcp-controller.profile.action.PLAYER_SETTING";
  public static final String ACTION_TRACK_EVENT = "android.bluetooth.avrcp-controller.profile.action.TRACK_EVENT";
  private static final boolean DBG = false;
  public static final String EXTRA_METADATA = "android.bluetooth.avrcp-controller.profile.extra.METADATA";
  public static final String EXTRA_PLAYBACK = "android.bluetooth.avrcp-controller.profile.extra.PLAYBACK";
  public static final String EXTRA_PLAYER_SETTING = "android.bluetooth.avrcp-controller.profile.extra.PLAYER_SETTING";
  public static final int KEY_STATE_PRESSED = 0;
  public static final int KEY_STATE_RELEASED = 1;
  public static final int PASS_THRU_CMD_ID_BACKWARD = 76;
  public static final int PASS_THRU_CMD_ID_FF = 73;
  public static final int PASS_THRU_CMD_ID_FORWARD = 75;
  public static final int PASS_THRU_CMD_ID_NEXT_GRP = 0;
  public static final int PASS_THRU_CMD_ID_PAUSE = 70;
  public static final int PASS_THRU_CMD_ID_PLAY = 68;
  public static final int PASS_THRU_CMD_ID_PREV_GRP = 1;
  public static final int PASS_THRU_CMD_ID_REWIND = 72;
  public static final int PASS_THRU_CMD_ID_STOP = 69;
  public static final int PASS_THRU_CMD_ID_VOL_DOWN = 66;
  public static final int PASS_THRU_CMD_ID_VOL_UP = 65;
  private static final String TAG = "BluetoothAvrcpController";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothAvrcpController.-get0(BluetoothAvrcpController.this))
        {
          try
          {
            BluetoothAvrcpController.-set0(BluetoothAvrcpController.this, null);
            BluetoothAvrcpController.-get1(BluetoothAvrcpController.this).unbindService(BluetoothAvrcpController.-get0(BluetoothAvrcpController.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothAvrcpController", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothAvrcpController.-get0(BluetoothAvrcpController.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothAvrcpController.-get2(BluetoothAvrcpController.this) != null) {
            continue;
          }
          BluetoothAvrcpController.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothAvrcpController", "", localException1);
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
      BluetoothAvrcpController.-set0(BluetoothAvrcpController.this, IBluetoothAvrcpController.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothAvrcpController.-get3(BluetoothAvrcpController.this) != null) {
        BluetoothAvrcpController.-get3(BluetoothAvrcpController.this).onServiceConnected(12, BluetoothAvrcpController.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      BluetoothAvrcpController.-set0(BluetoothAvrcpController.this, null);
      if (BluetoothAvrcpController.-get3(BluetoothAvrcpController.this) != null) {
        BluetoothAvrcpController.-get3(BluetoothAvrcpController.this).onServiceDisconnected(12);
      }
    }
  };
  private Context mContext;
  private IBluetoothAvrcpController mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothAvrcpController(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothAvrcpController", "", paramContext);
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
    Log.d("BluetoothAvrcpController", paramString);
  }
  
  void close()
  {
    this.mServiceListener = null;
    ??? = this.mAdapter.getBluetoothManager();
    if (??? != null) {}
    try
    {
      ((IBluetoothManager)???).unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
    }
    catch (Exception localException1)
    {
      synchronized (this.mConnection)
      {
        for (;;)
        {
          IBluetoothAvrcpController localIBluetoothAvrcpController = this.mService;
          if (localIBluetoothAvrcpController != null) {}
          try
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
            return;
            localException1 = localException1;
            Log.e("BluetoothAvrcpController", "", localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e("BluetoothAvrcpController", "", localException2);
            }
          }
        }
      }
    }
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothAvrcpController.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothAvrcpController", "Could not bind to Bluetooth AVRCP Controller Service with " + localIntent);
    return false;
  }
  
  public void finalize()
  {
    close();
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
        Log.e("BluetoothAvrcpController", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
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
        Log.e("BluetoothAvrcpController", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
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
        Log.e("BluetoothAvrcpController", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public MediaMetadata getMetadata(BluetoothDevice paramBluetoothDevice)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mService != null)
    {
      localObject1 = localObject2;
      if (!isEnabled()) {}
    }
    try
    {
      localObject1 = this.mService.getMetadata(paramBluetoothDevice);
      return (MediaMetadata)localObject1;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothAvrcpController", "Error talking to BT service in getMetadata() " + paramBluetoothDevice);
    }
    return null;
  }
  
  public PlaybackState getPlaybackState(BluetoothDevice paramBluetoothDevice)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mService != null)
    {
      localObject1 = localObject2;
      if (!isEnabled()) {}
    }
    try
    {
      localObject1 = this.mService.getPlaybackState(paramBluetoothDevice);
      return (PlaybackState)localObject1;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothAvrcpController", "Error talking to BT service in getPlaybackState() " + paramBluetoothDevice);
    }
    return null;
  }
  
  public BluetoothAvrcpPlayerSettings getPlayerSettings(BluetoothDevice paramBluetoothDevice)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mService != null)
    {
      localObject1 = localObject2;
      if (!isEnabled()) {}
    }
    try
    {
      localObject1 = this.mService.getPlayerSettings(paramBluetoothDevice);
      return (BluetoothAvrcpPlayerSettings)localObject1;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothAvrcpController", "Error talking to BT service in getMetadata() " + paramBluetoothDevice);
    }
    return null;
  }
  
  public void sendGroupNavigationCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    Log.d("BluetoothAvrcpController", "sendGroupNavigationCmd dev = " + paramBluetoothDevice + " key " + paramInt1 + " State = " + paramInt2);
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.sendGroupNavigationCmd(paramBluetoothDevice, paramInt1, paramInt2);
        return;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothAvrcpController", "Error talking to BT service in sendGroupNavigationCmd()", paramBluetoothDevice);
        return;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
    }
  }
  
  public void sendPassThroughCmd(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.sendPassThroughCmd(paramBluetoothDevice, paramInt1, paramInt2);
        return;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothAvrcpController", "Error talking to BT service in sendPassThroughCmd()", paramBluetoothDevice);
        return;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
    }
  }
  
  public boolean setPlayerApplicationSetting(BluetoothAvrcpPlayerSettings paramBluetoothAvrcpPlayerSettings)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.setPlayerApplicationSetting(paramBluetoothAvrcpPlayerSettings);
        return bool;
      }
      catch (RemoteException paramBluetoothAvrcpPlayerSettings)
      {
        Log.e("BluetoothAvrcpController", "Error talking to BT service in setPlayerApplicationSetting() " + paramBluetoothAvrcpPlayerSettings);
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothAvrcpController", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothAvrcpController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */