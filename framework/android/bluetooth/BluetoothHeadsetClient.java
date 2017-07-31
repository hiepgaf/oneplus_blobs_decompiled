package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothHeadsetClient
  implements BluetoothProfile
{
  public static final String ACTION_AG_EVENT = "android.bluetooth.headsetclient.profile.action.AG_EVENT";
  public static final String ACTION_AUDIO_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED";
  public static final String ACTION_CALL_CHANGED = "android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED";
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_LAST_VTAG = "android.bluetooth.headsetclient.profile.action.LAST_VTAG";
  public static final String ACTION_RESULT = "android.bluetooth.headsetclient.profile.action.RESULT";
  public static final int ACTION_RESULT_ERROR = 1;
  public static final int ACTION_RESULT_ERROR_BLACKLISTED = 6;
  public static final int ACTION_RESULT_ERROR_BUSY = 3;
  public static final int ACTION_RESULT_ERROR_CME = 7;
  public static final int ACTION_RESULT_ERROR_DELAYED = 5;
  public static final int ACTION_RESULT_ERROR_NO_ANSWER = 4;
  public static final int ACTION_RESULT_ERROR_NO_CARRIER = 2;
  public static final int ACTION_RESULT_OK = 0;
  public static final int CALL_ACCEPT_HOLD = 1;
  public static final int CALL_ACCEPT_NONE = 0;
  public static final int CALL_ACCEPT_TERMINATE = 2;
  public static final int CME_CORPORATE_PERSONALIZATION_PIN_REQUIRED = 46;
  public static final int CME_CORPORATE_PERSONALIZATION_PUK_REQUIRED = 47;
  public static final int CME_DIAL_STRING_TOO_LONG = 26;
  public static final int CME_EAP_NOT_SUPPORTED = 49;
  public static final int CME_EMERGENCY_SERVICE_ONLY = 32;
  public static final int CME_HIDDEN_KEY_REQUIRED = 48;
  public static final int CME_INCORRECT_PARAMETERS = 50;
  public static final int CME_INCORRECT_PASSWORD = 16;
  public static final int CME_INVALID_CHARACTER_IN_DIAL_STRING = 27;
  public static final int CME_INVALID_CHARACTER_IN_TEXT_STRING = 25;
  public static final int CME_INVALID_INDEX = 21;
  public static final int CME_MEMORY_FAILURE = 23;
  public static final int CME_MEMORY_FULL = 20;
  public static final int CME_NETWORK_PERSONALIZATION_PIN_REQUIRED = 40;
  public static final int CME_NETWORK_PERSONALIZATION_PUK_REQUIRED = 41;
  public static final int CME_NETWORK_SUBSET_PERSONALIZATION_PIN_REQUIRED = 42;
  public static final int CME_NETWORK_SUBSET_PERSONALIZATION_PUK_REQUIRED = 43;
  public static final int CME_NETWORK_TIMEOUT = 31;
  public static final int CME_NOT_FOUND = 22;
  public static final int CME_NOT_SUPPORTED_FOR_VOIP = 34;
  public static final int CME_NO_CONNECTION_TO_PHONE = 1;
  public static final int CME_NO_NETWORK_SERVICE = 30;
  public static final int CME_NO_SIMULTANOUS_VOIP_CS_CALLS = 33;
  public static final int CME_OPERATION_NOT_ALLOWED = 3;
  public static final int CME_OPERATION_NOT_SUPPORTED = 4;
  public static final int CME_PHFSIM_PIN_REQUIRED = 6;
  public static final int CME_PHFSIM_PUK_REQUIRED = 7;
  public static final int CME_PHONE_FAILURE = 0;
  public static final int CME_PHSIM_PIN_REQUIRED = 5;
  public static final int CME_SERVICE_PROVIDER_PERSONALIZATION_PIN_REQUIRED = 44;
  public static final int CME_SERVICE_PROVIDER_PERSONALIZATION_PUK_REQUIRED = 45;
  public static final int CME_SIM_BUSY = 14;
  public static final int CME_SIM_FAILURE = 13;
  public static final int CME_SIM_NOT_INSERTED = 10;
  public static final int CME_SIM_PIN2_REQUIRED = 17;
  public static final int CME_SIM_PIN_REQUIRED = 11;
  public static final int CME_SIM_PUK2_REQUIRED = 18;
  public static final int CME_SIM_PUK_REQUIRED = 12;
  public static final int CME_SIM_WRONG = 15;
  public static final int CME_SIP_RESPONSE_CODE = 35;
  public static final int CME_TEXT_STRING_TOO_LONG = 24;
  private static final boolean DBG = true;
  public static final String EXTRA_AG_FEATURE_3WAY_CALLING = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_3WAY_CALLING";
  public static final String EXTRA_AG_FEATURE_ACCEPT_HELD_OR_WAITING_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ACCEPT_HELD_OR_WAITING_CALL";
  public static final String EXTRA_AG_FEATURE_ATTACH_NUMBER_TO_VT = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ATTACH_NUMBER_TO_VT";
  public static final String EXTRA_AG_FEATURE_ECC = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ECC";
  public static final String EXTRA_AG_FEATURE_MERGE = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_MERGE";
  public static final String EXTRA_AG_FEATURE_MERGE_AND_DETACH = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_MERGE_AND_DETACH";
  public static final String EXTRA_AG_FEATURE_REJECT_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_REJECT_CALL";
  public static final String EXTRA_AG_FEATURE_RELEASE_AND_ACCEPT = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RELEASE_AND_ACCEPT";
  public static final String EXTRA_AG_FEATURE_RELEASE_HELD_OR_WAITING_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RELEASE_HELD_OR_WAITING_CALL";
  public static final String EXTRA_AG_FEATURE_RESPONSE_AND_HOLD = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RESPONSE_AND_HOLD";
  public static final String EXTRA_AG_FEATURE_VOICE_RECOGNITION = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_VOICE_RECOGNITION";
  public static final String EXTRA_AUDIO_WBS = "android.bluetooth.headsetclient.extra.AUDIO_WBS";
  public static final String EXTRA_BATTERY_LEVEL = "android.bluetooth.headsetclient.extra.BATTERY_LEVEL";
  public static final String EXTRA_CALL = "android.bluetooth.headsetclient.extra.CALL";
  public static final String EXTRA_CME_CODE = "android.bluetooth.headsetclient.extra.CME_CODE";
  public static final String EXTRA_IN_BAND_RING = "android.bluetooth.headsetclient.extra.IN_BAND_RING";
  public static final String EXTRA_MANF_ID = "android.bluetooth.headsetclient.extra.MANF_ID";
  public static final String EXTRA_MANF_MODEL = "android.bluetooth.headsetclient.extra.MANF_MODEL";
  public static final String EXTRA_NETWORK_ROAMING = "android.bluetooth.headsetclient.extra.NETWORK_ROAMING";
  public static final String EXTRA_NETWORK_SIGNAL_STRENGTH = "android.bluetooth.headsetclient.extra.NETWORK_SIGNAL_STRENGTH";
  public static final String EXTRA_NETWORK_STATUS = "android.bluetooth.headsetclient.extra.NETWORK_STATUS";
  public static final String EXTRA_NUMBER = "android.bluetooth.headsetclient.extra.NUMBER";
  public static final String EXTRA_OPERATOR_NAME = "android.bluetooth.headsetclient.extra.OPERATOR_NAME";
  public static final String EXTRA_RESULT_CODE = "android.bluetooth.headsetclient.extra.RESULT_CODE";
  public static final String EXTRA_SUBSCRIBER_INFO = "android.bluetooth.headsetclient.extra.SUBSCRIBER_INFO";
  public static final String EXTRA_VOICE_RECOGNITION = "android.bluetooth.headsetclient.extra.VOICE_RECOGNITION";
  public static final int STATE_AUDIO_CONNECTED = 2;
  public static final int STATE_AUDIO_CONNECTING = 1;
  public static final int STATE_AUDIO_DISCONNECTED = 0;
  private static final String TAG = "BluetoothHeadsetClient";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothHeadsetClient", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothHeadsetClient.-get0(BluetoothHeadsetClient.this))
        {
          try
          {
            BluetoothHeadsetClient.-set0(BluetoothHeadsetClient.this, null);
            BluetoothHeadsetClient.-get1(BluetoothHeadsetClient.this).unbindService(BluetoothHeadsetClient.-get0(BluetoothHeadsetClient.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothHeadsetClient", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothHeadsetClient.-get0(BluetoothHeadsetClient.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothHeadsetClient.-get2(BluetoothHeadsetClient.this) != null) {
            continue;
          }
          new Intent(IBluetoothHeadsetClient.class.getName());
          BluetoothHeadsetClient.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothHeadsetClient", "", localException1);
          Object localObject2 = localServiceConnection;
        }
        finally {}
      }
    }
  };
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("BluetoothHeadsetClient", "Proxy object connected");
      BluetoothHeadsetClient.-set0(BluetoothHeadsetClient.this, IBluetoothHeadsetClient.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothHeadsetClient.-get3(BluetoothHeadsetClient.this) != null) {
        BluetoothHeadsetClient.-get3(BluetoothHeadsetClient.this).onServiceConnected(16, BluetoothHeadsetClient.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothHeadsetClient", "Proxy object disconnected");
      BluetoothHeadsetClient.-set0(BluetoothHeadsetClient.this, null);
      if (BluetoothHeadsetClient.-get3(BluetoothHeadsetClient.this) != null) {
        BluetoothHeadsetClient.-get3(BluetoothHeadsetClient.this).onServiceDisconnected(16);
      }
    }
  };
  private Context mContext;
  private IBluetoothHeadsetClient mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothHeadsetClient(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothHeadsetClient", "", paramContext);
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
    Log.d("BluetoothHeadsetClient", paramString);
  }
  
  public boolean acceptCall(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("acceptCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.acceptCall(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
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
        Log.e("BluetoothHeadsetClient", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    }
  }
  
  void close()
  {
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
          IBluetoothHeadsetClient localIBluetoothHeadsetClient = this.mService;
          if (localIBluetoothHeadsetClient != null) {}
          try
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
            this.mServiceListener = null;
            return;
            localException1 = localException1;
            Log.e("BluetoothHeadsetClient", "", localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e("BluetoothHeadsetClient", "", localException2);
            }
          }
        }
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
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
        Log.e("BluetoothHeadsetClient", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean dial(BluetoothDevice paramBluetoothDevice, String paramString)
  {
    log("dial()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.dial(paramBluetoothDevice, paramString);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean dialMemory(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("dialMemory()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.dialMemory(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
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
        Log.e("BluetoothHeadsetClient", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    }
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothHeadsetClient.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothHeadsetClient", "Could not bind to Bluetooth Headset Client Service with " + localIntent);
    return false;
  }
  
  public boolean enterPrivateMode(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("enterPrivateMode()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.enterPrivateMode(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean explicitCallTransfer(BluetoothDevice paramBluetoothDevice)
  {
    log("explicitCallTransfer()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.explicitCallTransfer(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean getAudioRouteAllowed()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        boolean bool = this.mService.getAudioRouteAllowed();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadsetClient", localRemoteException.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public int getAudioState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        int i = this.mService.getAudioState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return 0;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return 0;
  }
  
  public Bundle getCurrentAgEvents(BluetoothDevice paramBluetoothDevice)
  {
    log("getCurrentCalls()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        paramBluetoothDevice = this.mService.getCurrentAgEvents(paramBluetoothDevice);
        return paramBluetoothDevice;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return null;
  }
  
  public Bundle getCurrentAgFeatures(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        paramBluetoothDevice = this.mService.getCurrentAgFeatures(paramBluetoothDevice);
        return paramBluetoothDevice;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", paramBluetoothDevice.toString());
        return null;
      }
    }
    Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    return null;
  }
  
  public List<BluetoothHeadsetClientCall> getCurrentCalls(BluetoothDevice paramBluetoothDevice)
  {
    log("getCurrentCalls()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        paramBluetoothDevice = this.mService.getCurrentCalls(paramBluetoothDevice);
        return paramBluetoothDevice;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return null;
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public boolean getLastVoiceTagNumber(BluetoothDevice paramBluetoothDevice)
  {
    log("getLastVoiceTagNumber()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.getLastVoiceTagNumber(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return 0;
  }
  
  public boolean holdCall(BluetoothDevice paramBluetoothDevice)
  {
    log("holdCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.holdCall(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean redial(BluetoothDevice paramBluetoothDevice)
  {
    log("redial()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.redial(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean rejectCall(BluetoothDevice paramBluetoothDevice)
  {
    log("rejectCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.rejectCall(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
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
        Log.e("BluetoothHeadsetClient", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
      Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean sendDTMF(BluetoothDevice paramBluetoothDevice, byte paramByte)
  {
    log("sendDTMF()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.sendDTMF(paramBluetoothDevice, paramByte);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public void setAudioRouteAllowed(boolean paramBoolean)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.setAudioRouteAllowed(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHeadsetClient", localRemoteException.toString());
        return;
      }
    }
    Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    Log.d("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
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
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean terminateCall(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("terminateCall()");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.terminateCall(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHeadsetClient", Log.getStackTraceString(new Throwable()));
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHeadsetClient", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHeadsetClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */