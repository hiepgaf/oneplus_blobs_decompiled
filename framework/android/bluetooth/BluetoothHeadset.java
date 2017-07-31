package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothHeadset
  implements BluetoothProfile
{
  public static final String ACTION_AUDIO_STATE_CHANGED = "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED";
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_HF_INDICATOR_VALUE_CHANGED = "codeaurora.bluetooth.headset.action.ACTION_HF_INDICATOR_VALUE_CHANGED";
  public static final String ACTION_VENDOR_SPECIFIC_HEADSET_EVENT = "android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT";
  public static final int AT_CMD_TYPE_ACTION = 4;
  public static final int AT_CMD_TYPE_BASIC = 3;
  public static final int AT_CMD_TYPE_READ = 0;
  public static final int AT_CMD_TYPE_SET = 2;
  public static final int AT_CMD_TYPE_TEST = 1;
  private static final boolean DBG = true;
  public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS";
  public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD";
  public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE";
  public static final String HF_INDICATOR_ASSIGNED_NUMBER = "codeaurora.bluetooth.headset.intent.category.anum";
  public static final String HF_INDICATOR_ASSIGNED_NUMBER_VALUE = "codeaurora.bluetooth.headset.intent.category.anumvalue";
  private static final int MESSAGE_HEADSET_SERVICE_CONNECTED = 100;
  private static final int MESSAGE_HEADSET_SERVICE_DISCONNECTED = 101;
  public static final int STATE_AUDIO_CONNECTED = 12;
  public static final int STATE_AUDIO_CONNECTING = 11;
  public static final int STATE_AUDIO_DISCONNECTED = 10;
  private static final String TAG = "BluetoothHeadset";
  private static final boolean VDBG = true;
  public static final String VENDOR_RESULT_CODE_COMMAND_ANDROID = "+ANDROID";
  public static final String VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY = "android.bluetooth.headset.intent.category.companyid";
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothHeadset", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean)
      {
        Log.d("BluetoothHeadset", "Unbinding service...");
        BluetoothHeadset.this.doUnbind();
        return;
      }
      synchronized (BluetoothHeadset.-get0(BluetoothHeadset.this))
      {
        try
        {
          if (BluetoothHeadset.-get2(BluetoothHeadset.this) == null)
          {
            Log.d("BluetoothHeadset", "Binding service...");
            BluetoothHeadset.this.doBind();
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("BluetoothHeadset", "", localException);
          }
        }
      }
    }
  };
  private final IBluetoothProfileServiceConnection mConnection = new IBluetoothProfileServiceConnection.Stub()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("BluetoothHeadset", "Proxy object connected");
      BluetoothHeadset.-set0(BluetoothHeadset.this, IBluetoothHeadset.Stub.asInterface(paramAnonymousIBinder));
      BluetoothHeadset.-get1(BluetoothHeadset.this).sendMessage(BluetoothHeadset.-get1(BluetoothHeadset.this).obtainMessage(100));
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothHeadset", "Proxy object disconnected");
      BluetoothHeadset.-set0(BluetoothHeadset.this, null);
      BluetoothHeadset.-get1(BluetoothHeadset.this).sendMessage(BluetoothHeadset.-get1(BluetoothHeadset.this).obtainMessage(101));
    }
  };
  private Context mContext;
  private final Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        do
        {
          return;
        } while (BluetoothHeadset.-get3(BluetoothHeadset.this) == null);
        BluetoothHeadset.-get3(BluetoothHeadset.this).onServiceConnected(1, BluetoothHeadset.this);
        return;
      } while (BluetoothHeadset.-get3(BluetoothHeadset.this) == null);
      BluetoothHeadset.-get3(BluetoothHeadset.this).onServiceDisconnected(1);
    }
  };
  private IBluetoothHeadset mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothHeadset(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothHeadset", "", paramContext);
      }
    }
  }
  
  public static boolean isBluetoothVoiceDialingEnabled(Context paramContext)
  {
    return paramContext.getResources().getBoolean(17956952);
  }
  
  private boolean isDisabled()
  {
    return this.mAdapter.getState() == 10;
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
    Log.d("BluetoothHeadset", paramString);
  }
  
  public boolean acceptIncomingConnect(BluetoothDevice paramBluetoothDevice)
  {
    log("acceptIncomingConnect");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.acceptIncomingConnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public void clccResponse(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, String paramString, int paramInt5)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.clccResponse(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean, paramString, paramInt5);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("BluetoothHeadset", paramString.toString());
        return;
      }
    }
    Log.w("BluetoothHeadset", "Proxy not attached to service");
    Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
  }
  
  void close()
  {
    log("close()");
    IBluetoothManager localIBluetoothManager = this.mAdapter.getBluetoothManager();
    if (localIBluetoothManager != null) {}
    try
    {
      localIBluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
      this.mServiceListener = null;
      doUnbind();
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("BluetoothHeadset", "", localException);
      }
    }
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    log("connect(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.connect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean connectAudio()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.connectAudio();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean disableWBS()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.disableWBS();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
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
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean disconnectAudio()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.disconnectAudio();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  boolean doBind()
  {
    try
    {
      boolean bool = this.mAdapter.getBluetoothManager().bindBluetoothProfileService(1, this.mConnection);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothHeadset", "Unable to bind HeadsetService", localRemoteException);
    }
    return false;
  }
  
  void doUnbind()
  {
    synchronized (this.mConnection)
    {
      IBluetoothHeadset localIBluetoothHeadset = this.mService;
      if (localIBluetoothHeadset != null) {}
      try
      {
        this.mAdapter.getBluetoothManager().unbindBluetoothProfileService(1, this.mConnection);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("BluetoothHeadset", "Unable to unbind HeadsetService", localRemoteException);
        }
      }
    }
  }
  
  public boolean enableWBS()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.enableWBS();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean getAudioRouteAllowed()
  {
    log("getAudioRouteAllowed");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.getAudioRouteAllowed();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public int getAudioState(BluetoothDevice paramBluetoothDevice)
  {
    log("getAudioState");
    if ((this.mService == null) || (isDisabled()))
    {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
    for (;;)
    {
      return 10;
      try
      {
        int i = this.mService.getAudioState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", paramBluetoothDevice.toString());
      }
    }
  }
  
  public int getBatteryUsageHint(BluetoothDevice paramBluetoothDevice)
  {
    log("getBatteryUsageHint()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getBatteryUsageHint(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return -1;
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
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
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
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
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
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getPriority(BluetoothDevice paramBluetoothDevice)
  {
    log("getPriority(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getPriority(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return 0;
  }
  
  public boolean isAudioConnected(BluetoothDevice paramBluetoothDevice)
  {
    log("isAudioConnected()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.isAudioConnected(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean isAudioOn()
  {
    log("isAudioOn()");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.isAudioOn();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public void phoneStateChanged(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.phoneStateChanged(paramInt1, paramInt2, paramInt3, paramString, paramInt4);
        return;
      }
      catch (RemoteException paramString)
      {
        Log.e("BluetoothHeadset", paramString.toString());
        return;
      }
    }
    Log.w("BluetoothHeadset", "Proxy not attached to service");
    Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
  }
  
  public boolean rejectIncomingConnect(BluetoothDevice paramBluetoothDevice)
  {
    log("rejectIncomingConnect");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.rejectIncomingConnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean sendVendorSpecificResultCode(BluetoothDevice paramBluetoothDevice, String paramString1, String paramString2)
  {
    log("sendVendorSpecificResultCode()");
    if (paramString1 == null) {
      throw new IllegalArgumentException("command is null");
    }
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.sendVendorSpecificResultCode(paramBluetoothDevice, paramString1, paramString2);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public void setAudioRouteAllowed(boolean paramBoolean)
  {
    log("setAudioRouteAllowed");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.setAudioRouteAllowed(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadset", localRemoteException.toString());
        return;
      }
    }
    Log.w("BluetoothHeadset", "Proxy not attached to service");
    Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
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
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean startScoUsingVirtualVoiceCall(BluetoothDevice paramBluetoothDevice)
  {
    log("startScoUsingVirtualVoiceCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.startScoUsingVirtualVoiceCall(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean startVoiceRecognition(BluetoothDevice paramBluetoothDevice)
  {
    log("startVoiceRecognition()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.startVoiceRecognition(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean stopScoUsingVirtualVoiceCall(BluetoothDevice paramBluetoothDevice)
  {
    log("stopScoUsingVirtualVoiceCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.stopScoUsingVirtualVoiceCall(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadset", "Proxy not attached to service");
      Log.d("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean stopVoiceRecognition(BluetoothDevice paramBluetoothDevice)
  {
    log("stopVoiceRecognition()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.stopVoiceRecognition(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadset", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadset", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHeadset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */