package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;

final class RoutingControlAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAIT_FOR_REPORT_POWER_STATUS = 2;
  private static final int STATE_WAIT_FOR_ROUTING_INFORMATION = 1;
  private static final String TAG = "RoutingControlAction";
  private static final int TIMEOUT_REPORT_POWER_STATUS_MS = 1000;
  private static final int TIMEOUT_ROUTING_INFORMATION_MS = 1000;
  private final IHdmiControlCallback mCallback;
  private int mCurrentRoutingPath;
  private final boolean mNotifyInputChange;
  private final boolean mQueryDevicePowerStatus;
  
  RoutingControlAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice);
    this.mCallback = paramIHdmiControlCallback;
    this.mCurrentRoutingPath = paramInt;
    this.mQueryDevicePowerStatus = paramBoolean;
    if (paramIHdmiControlCallback == null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mNotifyInputChange = paramBoolean;
      return;
    }
  }
  
  private void finishWithCallback(int paramInt)
  {
    invokeCallback(paramInt);
    finish();
  }
  
  private int getTvPowerStatus()
  {
    return tv().getPowerStatus();
  }
  
  private void handlDevicePowerStatusAckResult(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mState = 2;
      addTimer(this.mState, 1000);
      return;
    }
    updateActiveInput();
    sendSetStreamPath();
    finishWithCallback(0);
  }
  
  private void handleReportPowerStatus(int paramInt)
  {
    if (isPowerOnOrTransient(getTvPowerStatus()))
    {
      updateActiveInput();
      if (isPowerOnOrTransient(paramInt)) {
        sendSetStreamPath();
      }
    }
    finishWithCallback(0);
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
    catch (RemoteException localRemoteException) {}
  }
  
  private static boolean isPowerOnOrTransient(int paramInt)
  {
    return (paramInt == 0) || (paramInt == 2);
  }
  
  private void queryDevicePowerStatus(int paramInt, HdmiControlService.SendMessageCallback paramSendMessageCallback)
  {
    sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), paramInt), paramSendMessageCallback);
  }
  
  private void sendSetStreamPath()
  {
    sendCommand(HdmiCecMessageBuilder.buildSetStreamPath(getSourceAddress(), this.mCurrentRoutingPath));
  }
  
  private void updateActiveInput()
  {
    HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = tv();
    localHdmiCecLocalDeviceTv.setPrevPortId(localHdmiCecLocalDeviceTv.getActivePortId());
    localHdmiCecLocalDeviceTv.updateActiveInput(this.mCurrentRoutingPath, this.mNotifyInputChange);
  }
  
  public void handleTimerEvent(int paramInt)
  {
    if ((this.mState != paramInt) || (this.mState == 0))
    {
      Slog.w("CEC", "Timer in a wrong state. Ignored.");
      return;
    }
    switch (paramInt)
    {
    default: 
      return;
    case 1: 
      HdmiDeviceInfo localHdmiDeviceInfo = tv().getDeviceInfoByPath(this.mCurrentRoutingPath);
      if ((localHdmiDeviceInfo != null) && (this.mQueryDevicePowerStatus))
      {
        queryDevicePowerStatus(localHdmiDeviceInfo.getLogicalAddress(), new HdmiControlService.SendMessageCallback()
        {
          public void onSendCompleted(int paramAnonymousInt)
          {
            boolean bool = false;
            RoutingControlAction localRoutingControlAction = RoutingControlAction.this;
            if (paramAnonymousInt == 0) {
              bool = true;
            }
            RoutingControlAction.-wrap0(localRoutingControlAction, bool);
          }
        });
        return;
      }
      updateActiveInput();
      finishWithCallback(0);
      return;
    }
    if (isPowerOnOrTransient(getTvPowerStatus()))
    {
      updateActiveInput();
      sendSetStreamPath();
    }
    finishWithCallback(0);
  }
  
  public boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getOpcode();
    byte[] arrayOfByte = paramHdmiCecMessage.getParams();
    if ((this.mState == 1) && (i == 129))
    {
      i = HdmiUtils.twoBytesToInt(arrayOfByte);
      if (!HdmiUtils.isInActiveRoutingPath(this.mCurrentRoutingPath, i)) {
        return true;
      }
      this.mCurrentRoutingPath = i;
      removeActionExcept(RoutingControlAction.class, this);
      addTimer(this.mState, 1000);
      return true;
    }
    if ((this.mState == 2) && (i == 144))
    {
      handleReportPowerStatus(paramHdmiCecMessage.getParams()[0]);
      return true;
    }
    return false;
  }
  
  public boolean start()
  {
    this.mState = 1;
    addTimer(this.mState, 1000);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/RoutingControlAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */