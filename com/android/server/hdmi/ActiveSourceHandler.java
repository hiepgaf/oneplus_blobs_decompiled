package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;

final class ActiveSourceHandler
{
  private static final String TAG = "ActiveSourceHandler";
  private final IHdmiControlCallback mCallback;
  private final HdmiControlService mService;
  private final HdmiCecLocalDeviceTv mSource;
  
  private ActiveSourceHandler(HdmiCecLocalDeviceTv paramHdmiCecLocalDeviceTv, IHdmiControlCallback paramIHdmiControlCallback)
  {
    this.mSource = paramHdmiCecLocalDeviceTv;
    this.mService = this.mSource.getService();
    this.mCallback = paramIHdmiControlCallback;
  }
  
  static ActiveSourceHandler create(HdmiCecLocalDeviceTv paramHdmiCecLocalDeviceTv, IHdmiControlCallback paramIHdmiControlCallback)
  {
    if (paramHdmiCecLocalDeviceTv == null)
    {
      Slog.e("ActiveSourceHandler", "Wrong arguments");
      return null;
    }
    return new ActiveSourceHandler(paramHdmiCecLocalDeviceTv, paramIHdmiControlCallback);
  }
  
  private final int getSourceAddress()
  {
    return this.mSource.getDeviceInfo().getLogicalAddress();
  }
  
  private void invokeCallback(int paramInt)
  {
    if (this.mCallback == null) {
      return;
    }
    try
    {
      this.mCallback.onComplete(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("ActiveSourceHandler", "Callback failed:" + localRemoteException);
    }
  }
  
  void process(HdmiCecLocalDevice.ActiveSource paramActiveSource, int paramInt)
  {
    HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = this.mSource;
    if (this.mService.getDeviceInfo(paramActiveSource.logicalAddress) == null) {
      localHdmiCecLocalDeviceTv.startNewDeviceAction(paramActiveSource, paramInt);
    }
    if (!localHdmiCecLocalDeviceTv.isProhibitMode())
    {
      localActiveSource = HdmiCecLocalDevice.ActiveSource.of(localHdmiCecLocalDeviceTv.getActiveSource());
      localHdmiCecLocalDeviceTv.updateActiveSource(paramActiveSource);
      if (this.mCallback == null) {}
      for (boolean bool = true;; bool = false)
      {
        if (!localActiveSource.equals(paramActiveSource)) {
          localHdmiCecLocalDeviceTv.setPrevPortId(localHdmiCecLocalDeviceTv.getActivePortId());
        }
        localHdmiCecLocalDeviceTv.updateActiveInput(paramActiveSource.physicalAddress, bool);
        invokeCallback(0);
        return;
      }
    }
    HdmiCecLocalDevice.ActiveSource localActiveSource = localHdmiCecLocalDeviceTv.getActiveSource();
    if (localActiveSource.logicalAddress == getSourceAddress())
    {
      paramActiveSource = HdmiCecMessageBuilder.buildActiveSource(localActiveSource.logicalAddress, localActiveSource.physicalAddress);
      this.mService.sendCecCommand(paramActiveSource);
      localHdmiCecLocalDeviceTv.updateActiveSource(localActiveSource);
      invokeCallback(0);
      return;
    }
    localHdmiCecLocalDeviceTv.startRoutingControl(paramActiveSource.physicalAddress, localActiveSource.physicalAddress, true, this.mCallback);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/ActiveSourceHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */