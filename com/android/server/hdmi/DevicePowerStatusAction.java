package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class DevicePowerStatusAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAITING_FOR_REPORT_POWER_STATUS = 1;
  private static final String TAG = "DevicePowerStatusAction";
  private final List<IHdmiControlCallback> mCallbacks = new ArrayList();
  private final int mTargetAddress;
  
  private DevicePowerStatusAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice);
    this.mTargetAddress = paramInt;
    addCallback(paramIHdmiControlCallback);
  }
  
  static DevicePowerStatusAction create(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    if ((paramHdmiCecLocalDevice == null) || (paramIHdmiControlCallback == null))
    {
      Slog.e("DevicePowerStatusAction", "Wrong arguments");
      return null;
    }
    return new DevicePowerStatusAction(paramHdmiCecLocalDevice, paramInt, paramIHdmiControlCallback);
  }
  
  private void invokeCallback(int paramInt)
  {
    try
    {
      Iterator localIterator = this.mCallbacks.iterator();
      while (localIterator.hasNext()) {
        ((IHdmiControlCallback)localIterator.next()).onComplete(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("DevicePowerStatusAction", "Callback failed:" + localRemoteException);
    }
  }
  
  private void queryDevicePowerStatus()
  {
    sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), this.mTargetAddress));
  }
  
  public void addCallback(IHdmiControlCallback paramIHdmiControlCallback)
  {
    this.mCallbacks.add(paramIHdmiControlCallback);
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt) {
      return;
    }
    if (paramInt == 1)
    {
      invokeCallback(-1);
      finish();
    }
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (this.mTargetAddress != paramHdmiCecMessage.getSource())) {
      return false;
    }
    if (paramHdmiCecMessage.getOpcode() == 144)
    {
      invokeCallback(paramHdmiCecMessage.getParams()[0]);
      finish();
      return true;
    }
    return false;
  }
  
  boolean start()
  {
    queryDevicePowerStatus();
    this.mState = 1;
    addTimer(this.mState, 2000);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/DevicePowerStatusAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */