package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;

final class DeviceSelectAction
  extends HdmiCecFeatureAction
{
  private static final int LOOP_COUNTER_MAX = 20;
  private static final int STATE_WAIT_FOR_DEVICE_POWER_ON = 3;
  private static final int STATE_WAIT_FOR_DEVICE_TO_TRANSIT_TO_STANDBY = 2;
  private static final int STATE_WAIT_FOR_REPORT_POWER_STATUS = 1;
  private static final String TAG = "DeviceSelect";
  private static final int TIMEOUT_POWER_ON_MS = 5000;
  private static final int TIMEOUT_TRANSIT_TO_STANDBY_MS = 5000;
  private final IHdmiControlCallback mCallback;
  private final HdmiCecMessage mGivePowerStatus;
  private int mPowerStatusCounter = 0;
  private final HdmiDeviceInfo mTarget;
  
  public DeviceSelectAction(HdmiCecLocalDeviceTv paramHdmiCecLocalDeviceTv, HdmiDeviceInfo paramHdmiDeviceInfo, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDeviceTv);
    this.mCallback = paramIHdmiControlCallback;
    this.mTarget = paramHdmiDeviceInfo;
    this.mGivePowerStatus = HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), getTargetAddress());
  }
  
  private boolean handleReportPowerStatus(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
      sendSetStreamPath();
      return true;
    case 3: 
      if (this.mPowerStatusCounter < 4)
      {
        this.mState = 2;
        addTimer(this.mState, 5000);
        return true;
      }
      sendSetStreamPath();
      return true;
    case 1: 
      if (this.mPowerStatusCounter == 0)
      {
        turnOnDevice();
        return true;
      }
      sendSetStreamPath();
      return true;
    }
    if (this.mPowerStatusCounter < 20)
    {
      this.mState = 3;
      addTimer(this.mState, 5000);
      return true;
    }
    sendSetStreamPath();
    return true;
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
      Slog.e("DeviceSelect", "Callback failed:" + localRemoteException);
    }
  }
  
  private void queryDevicePowerStatus()
  {
    sendCommand(this.mGivePowerStatus, new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0)
        {
          DeviceSelectAction.-wrap0(DeviceSelectAction.this, 7);
          DeviceSelectAction.this.finish();
          return;
        }
      }
    });
    this.mState = 1;
    addTimer(this.mState, 2000);
  }
  
  private void sendSetStreamPath()
  {
    tv().getActiveSource().invalidate();
    tv().setActivePath(this.mTarget.getPhysicalAddress());
    sendCommand(HdmiCecMessageBuilder.buildSetStreamPath(getSourceAddress(), this.mTarget.getPhysicalAddress()));
    invokeCallback(0);
    finish();
  }
  
  private void turnOnDevice()
  {
    sendUserControlPressedAndReleased(this.mTarget.getLogicalAddress(), 64);
    sendUserControlPressedAndReleased(this.mTarget.getLogicalAddress(), 109);
    this.mState = 3;
    addTimer(this.mState, 5000);
  }
  
  int getTargetAddress()
  {
    return this.mTarget.getLogicalAddress();
  }
  
  public void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt)
    {
      Slog.w("DeviceSelect", "Timer in a wrong state. Ignored.");
      return;
    }
    switch (this.mState)
    {
    default: 
      return;
    case 1: 
      if (tv().isPowerStandbyOrTransient())
      {
        invokeCallback(6);
        finish();
        return;
      }
      sendSetStreamPath();
      return;
    }
    this.mPowerStatusCounter += 1;
    queryDevicePowerStatus();
  }
  
  public boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if (paramHdmiCecMessage.getSource() != getTargetAddress()) {
      return false;
    }
    int i = paramHdmiCecMessage.getOpcode();
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    switch (this.mState)
    {
    default: 
      return false;
    }
    if (i == 144) {
      return handleReportPowerStatus(paramHdmiCecMessage[0]);
    }
    return false;
  }
  
  public boolean start()
  {
    queryDevicePowerStatus();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/DeviceSelectAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */