package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;

final class SystemAudioActionFromTv
  extends SystemAudioAction
{
  SystemAudioActionFromTv(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice, paramInt, paramBoolean, paramIHdmiControlCallback);
    HdmiUtils.verifyAddressType(getSourceAddress(), 0);
  }
  
  boolean start()
  {
    removeSystemAudioActionInProgress();
    sendSystemAudioModeRequest();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SystemAudioActionFromTv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */