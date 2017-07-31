package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;

final class SystemAudioActionFromAvr
  extends SystemAudioAction
{
  SystemAudioActionFromAvr(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice, paramInt, paramBoolean, paramIHdmiControlCallback);
    HdmiUtils.verifyAddressType(getSourceAddress(), 0);
  }
  
  private void handleSystemAudioActionFromAvr()
  {
    if (this.mTargetAudioStatus == tv().isSystemAudioActivated())
    {
      finishWithCallback(0);
      return;
    }
    if (tv().isProhibitMode())
    {
      sendCommand(HdmiCecMessageBuilder.buildFeatureAbortCommand(getSourceAddress(), this.mAvrLogicalAddress, 114, 4));
      this.mTargetAudioStatus = false;
      sendSystemAudioModeRequest();
      return;
    }
    removeAction(SystemAudioAutoInitiationAction.class);
    if (this.mTargetAudioStatus)
    {
      setSystemAudioMode(true);
      startAudioStatusAction();
      return;
    }
    setSystemAudioMode(false);
    finishWithCallback(0);
  }
  
  boolean start()
  {
    removeSystemAudioActionInProgress();
    handleSystemAudioActionFromAvr();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SystemAudioActionFromAvr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */