package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;

final class HdmiMhlLocalDeviceStub
{
  private static final HdmiDeviceInfo INFO = new HdmiDeviceInfo(65535, -1, -1, -1);
  private final int mPortId;
  private final HdmiControlService mService;
  
  protected HdmiMhlLocalDeviceStub(HdmiControlService paramHdmiControlService, int paramInt)
  {
    this.mService = paramHdmiControlService;
    this.mPortId = paramInt;
  }
  
  HdmiDeviceInfo getInfo()
  {
    return INFO;
  }
  
  int getPortId()
  {
    return this.mPortId;
  }
  
  void onBusOvercurrentDetected(boolean paramBoolean) {}
  
  void onDeviceRemoved() {}
  
  void sendKeyEvent(int paramInt, boolean paramBoolean) {}
  
  void sendStandby() {}
  
  void setBusMode(int paramInt) {}
  
  void setDeviceStatusChange(int paramInt1, int paramInt2) {}
  
  void turnOn(IHdmiControlCallback paramIHdmiControlCallback) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiMhlLocalDeviceStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */