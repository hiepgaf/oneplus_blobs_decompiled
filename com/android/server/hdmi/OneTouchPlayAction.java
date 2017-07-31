package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class OneTouchPlayAction
  extends HdmiCecFeatureAction
{
  private static final int LOOP_COUNTER_MAX = 10;
  private static final int STATE_WAITING_FOR_REPORT_POWER_STATUS = 1;
  private static final String TAG = "OneTouchPlayAction";
  private final List<IHdmiControlCallback> mCallbacks = new ArrayList();
  private int mPowerStatusCounter = 0;
  private final int mTargetAddress;
  
  private OneTouchPlayAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice);
    this.mTargetAddress = paramInt;
    addCallback(paramIHdmiControlCallback);
  }
  
  private void broadcastActiveSource()
  {
    sendCommand(HdmiCecMessageBuilder.buildActiveSource(getSourceAddress(), getSourcePath()));
    playback().setActiveSource(true);
  }
  
  static OneTouchPlayAction create(HdmiCecLocalDevicePlayback paramHdmiCecLocalDevicePlayback, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    if ((paramHdmiCecLocalDevicePlayback == null) || (paramIHdmiControlCallback == null))
    {
      Slog.e("OneTouchPlayAction", "Wrong arguments");
      return null;
    }
    return new OneTouchPlayAction(paramHdmiCecLocalDevicePlayback, paramInt, paramIHdmiControlCallback);
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
      Slog.e("OneTouchPlayAction", "Callback failed:" + localRemoteException);
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
      paramInt = this.mPowerStatusCounter;
      this.mPowerStatusCounter = (paramInt + 1);
      if (paramInt < 10)
      {
        queryDevicePowerStatus();
        addTimer(this.mState, 2000);
      }
    }
    else
    {
      return;
    }
    invokeCallback(1);
    finish();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (this.mTargetAddress != paramHdmiCecMessage.getSource())) {
      return false;
    }
    if (paramHdmiCecMessage.getOpcode() == 144)
    {
      if (paramHdmiCecMessage.getParams()[0] == 0)
      {
        broadcastActiveSource();
        invokeCallback(0);
        finish();
      }
      return true;
    }
    return false;
  }
  
  boolean start()
  {
    sendCommand(HdmiCecMessageBuilder.buildTextViewOn(getSourceAddress(), this.mTargetAddress));
    broadcastActiveSource();
    queryDevicePowerStatus();
    this.mState = 1;
    addTimer(this.mState, 2000);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/OneTouchPlayAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */