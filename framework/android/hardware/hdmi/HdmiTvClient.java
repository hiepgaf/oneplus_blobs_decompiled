package android.hardware.hdmi;

import android.os.RemoteException;
import android.util.Log;
import java.util.Collections;
import java.util.List;
import libcore.util.EmptyArray;

public final class HdmiTvClient
  extends HdmiClient
{
  private static final String TAG = "HdmiTvClient";
  public static final int VENDOR_DATA_SIZE = 16;
  
  HdmiTvClient(IHdmiControlService paramIHdmiControlService)
  {
    super(paramIHdmiControlService);
  }
  
  private void checkTimerRecordingSourceType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Invalid source type:" + paramInt);
    }
  }
  
  static HdmiTvClient create(IHdmiControlService paramIHdmiControlService)
  {
    return new HdmiTvClient(paramIHdmiControlService);
  }
  
  private static IHdmiControlCallback getCallbackWrapper(SelectCallback paramSelectCallback)
  {
    new IHdmiControlCallback.Stub()
    {
      public void onComplete(int paramAnonymousInt)
      {
        this.val$callback.onComplete(paramAnonymousInt);
      }
    };
  }
  
  private static IHdmiInputChangeListener getListenerWrapper(InputChangeListener paramInputChangeListener)
  {
    new IHdmiInputChangeListener.Stub()
    {
      public void onChanged(HdmiDeviceInfo paramAnonymousHdmiDeviceInfo)
      {
        this.val$listener.onChanged(paramAnonymousHdmiDeviceInfo);
      }
    };
  }
  
  private IHdmiMhlVendorCommandListener getListenerWrapper(final HdmiMhlVendorCommandListener paramHdmiMhlVendorCommandListener)
  {
    new IHdmiMhlVendorCommandListener.Stub()
    {
      public void onReceived(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, byte[] paramAnonymousArrayOfByte)
      {
        paramHdmiMhlVendorCommandListener.onReceived(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousArrayOfByte);
      }
    };
  }
  
  private static IHdmiRecordListener getListenerWrapper(HdmiRecordListener paramHdmiRecordListener)
  {
    new IHdmiRecordListener.Stub()
    {
      public byte[] getOneTouchRecordSource(int paramAnonymousInt)
      {
        HdmiRecordSources.RecordSource localRecordSource = this.val$callback.onOneTouchRecordSourceRequested(paramAnonymousInt);
        if (localRecordSource == null) {
          return EmptyArray.BYTE;
        }
        byte[] arrayOfByte = new byte[localRecordSource.getDataSize(true)];
        localRecordSource.toByteArray(true, arrayOfByte, 0);
        return arrayOfByte;
      }
      
      public void onClearTimerRecordingResult(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        this.val$callback.onClearTimerRecordingResult(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void onOneTouchRecordResult(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        this.val$callback.onOneTouchRecordResult(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void onTimerRecordingResult(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        this.val$callback.onTimerRecordingResult(paramAnonymousInt1, HdmiRecordListener.TimerStatusData.parseFrom(paramAnonymousInt2));
      }
    };
  }
  
  public void clearTimerRecording(int paramInt1, int paramInt2, HdmiTimerRecordSources.TimerRecordSource paramTimerRecordSource)
  {
    if (paramTimerRecordSource == null) {
      throw new IllegalArgumentException("source must not be null.");
    }
    checkTimerRecordingSourceType(paramInt2);
    try
    {
      byte[] arrayOfByte = new byte[paramTimerRecordSource.getDataSize()];
      paramTimerRecordSource.toByteArray(arrayOfByte, 0);
      this.mService.clearTimerRecording(paramInt1, paramInt2, arrayOfByte);
      return;
    }
    catch (RemoteException paramTimerRecordSource)
    {
      Log.e("HdmiTvClient", "failed to start record: ", paramTimerRecordSource);
    }
  }
  
  public void deviceSelect(int paramInt, SelectCallback paramSelectCallback)
  {
    if (paramSelectCallback == null) {
      throw new IllegalArgumentException("callback must not be null.");
    }
    try
    {
      this.mService.deviceSelect(paramInt, getCallbackWrapper(paramSelectCallback));
      return;
    }
    catch (RemoteException paramSelectCallback)
    {
      Log.e("HdmiTvClient", "failed to select device: ", paramSelectCallback);
    }
  }
  
  public List<HdmiDeviceInfo> getDeviceList()
  {
    try
    {
      List localList = this.mService.getDeviceList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("TAG", "Failed to call getDeviceList():", localRemoteException);
    }
    return Collections.emptyList();
  }
  
  public int getDeviceType()
  {
    return 0;
  }
  
  public void portSelect(int paramInt, SelectCallback paramSelectCallback)
  {
    if (paramSelectCallback == null) {
      throw new IllegalArgumentException("Callback must not be null");
    }
    try
    {
      this.mService.portSelect(paramInt, getCallbackWrapper(paramSelectCallback));
      return;
    }
    catch (RemoteException paramSelectCallback)
    {
      Log.e("HdmiTvClient", "failed to select port: ", paramSelectCallback);
    }
  }
  
  public void sendMhlVendorCommand(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length != 16)) {
      throw new IllegalArgumentException("Invalid vendor command data.");
    }
    if ((paramInt2 < 0) || (paramInt2 >= 16)) {
      throw new IllegalArgumentException("Invalid offset:" + paramInt2);
    }
    if ((paramInt3 < 0) || (paramInt2 + paramInt3 > 16)) {
      throw new IllegalArgumentException("Invalid length:" + paramInt3);
    }
    try
    {
      this.mService.sendMhlVendorCommand(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
      return;
    }
    catch (RemoteException paramArrayOfByte)
    {
      Log.e("HdmiTvClient", "failed to send vendor command: ", paramArrayOfByte);
    }
  }
  
  public void sendStandby(int paramInt)
  {
    try
    {
      this.mService.sendStandby(getDeviceType(), paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiTvClient", "sendStandby threw exception ", localRemoteException);
    }
  }
  
  public void setHdmiMhlVendorCommandListener(HdmiMhlVendorCommandListener paramHdmiMhlVendorCommandListener)
  {
    if (paramHdmiMhlVendorCommandListener == null) {
      throw new IllegalArgumentException("listener must not be null.");
    }
    try
    {
      this.mService.addHdmiMhlVendorCommandListener(getListenerWrapper(paramHdmiMhlVendorCommandListener));
      return;
    }
    catch (RemoteException paramHdmiMhlVendorCommandListener)
    {
      Log.e("HdmiTvClient", "failed to set hdmi mhl vendor command listener: ", paramHdmiMhlVendorCommandListener);
    }
  }
  
  public void setInputChangeListener(InputChangeListener paramInputChangeListener)
  {
    if (paramInputChangeListener == null) {
      throw new IllegalArgumentException("listener must not be null.");
    }
    try
    {
      this.mService.setInputChangeListener(getListenerWrapper(paramInputChangeListener));
      return;
    }
    catch (RemoteException paramInputChangeListener)
    {
      Log.e("TAG", "Failed to set InputChangeListener:", paramInputChangeListener);
    }
  }
  
  public void setRecordListener(HdmiRecordListener paramHdmiRecordListener)
  {
    if (paramHdmiRecordListener == null) {
      throw new IllegalArgumentException("listener must not be null.");
    }
    try
    {
      this.mService.setHdmiRecordListener(getListenerWrapper(paramHdmiRecordListener));
      return;
    }
    catch (RemoteException paramHdmiRecordListener)
    {
      Log.e("HdmiTvClient", "failed to set record listener.", paramHdmiRecordListener);
    }
  }
  
  public void setSystemAudioMode(boolean paramBoolean, SelectCallback paramSelectCallback)
  {
    try
    {
      this.mService.setSystemAudioMode(paramBoolean, getCallbackWrapper(paramSelectCallback));
      return;
    }
    catch (RemoteException paramSelectCallback)
    {
      Log.e("HdmiTvClient", "failed to set system audio mode:", paramSelectCallback);
    }
  }
  
  public void setSystemAudioMute(boolean paramBoolean)
  {
    try
    {
      this.mService.setSystemAudioMute(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiTvClient", "failed to set mute: ", localRemoteException);
    }
  }
  
  public void setSystemAudioVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mService.setSystemAudioVolume(paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiTvClient", "failed to set volume: ", localRemoteException);
    }
  }
  
  public void startOneTouchRecord(int paramInt, HdmiRecordSources.RecordSource paramRecordSource)
  {
    if (paramRecordSource == null) {
      throw new IllegalArgumentException("source must not be null.");
    }
    try
    {
      byte[] arrayOfByte = new byte[paramRecordSource.getDataSize(true)];
      paramRecordSource.toByteArray(true, arrayOfByte, 0);
      this.mService.startOneTouchRecord(paramInt, arrayOfByte);
      return;
    }
    catch (RemoteException paramRecordSource)
    {
      Log.e("HdmiTvClient", "failed to start record: ", paramRecordSource);
    }
  }
  
  public void startTimerRecording(int paramInt1, int paramInt2, HdmiTimerRecordSources.TimerRecordSource paramTimerRecordSource)
  {
    if (paramTimerRecordSource == null) {
      throw new IllegalArgumentException("source must not be null.");
    }
    checkTimerRecordingSourceType(paramInt2);
    try
    {
      byte[] arrayOfByte = new byte[paramTimerRecordSource.getDataSize()];
      paramTimerRecordSource.toByteArray(arrayOfByte, 0);
      this.mService.startTimerRecording(paramInt1, paramInt2, arrayOfByte);
      return;
    }
    catch (RemoteException paramTimerRecordSource)
    {
      Log.e("HdmiTvClient", "failed to start record: ", paramTimerRecordSource);
    }
  }
  
  public void stopOneTouchRecord(int paramInt)
  {
    try
    {
      this.mService.stopOneTouchRecord(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiTvClient", "failed to stop record: ", localRemoteException);
    }
  }
  
  public static abstract interface HdmiMhlVendorCommandListener
  {
    public abstract void onReceived(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);
  }
  
  public static abstract interface InputChangeListener
  {
    public abstract void onChanged(HdmiDeviceInfo paramHdmiDeviceInfo);
  }
  
  public static abstract interface SelectCallback
  {
    public abstract void onComplete(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiTvClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */