package com.android.server.hdmi;

final class SystemAudioAutoInitiationAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAITING_FOR_SYSTEM_AUDIO_MODE_STATUS = 1;
  private final int mAvrAddress;
  
  SystemAudioAutoInitiationAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt)
  {
    super(paramHdmiCecLocalDevice);
    this.mAvrAddress = paramInt;
  }
  
  private boolean canChangeSystemAudio()
  {
    return (!tv().hasAction(SystemAudioActionFromTv.class)) && (!tv().hasAction(SystemAudioActionFromAvr.class));
  }
  
  private void handleSystemAudioModeStatusMessage(boolean paramBoolean)
  {
    if (!canChangeSystemAudio())
    {
      HdmiLogger.debug("Cannot change system audio mode in auto initiation action.", new Object[0]);
      finish();
      return;
    }
    boolean bool = tv().getSystemAudioModeSetting();
    if ((!bool) || (paramBoolean)) {
      tv().setSystemAudioMode(paramBoolean, true);
    }
    for (;;)
    {
      finish();
      return;
      addAndStartAction(new SystemAudioActionFromTv(tv(), this.mAvrAddress, bool, null));
    }
  }
  
  private void handleSystemAudioModeStatusTimeout()
  {
    if (tv().getSystemAudioModeSetting()) {
      if (canChangeSystemAudio()) {
        addAndStartAction(new SystemAudioActionFromTv(tv(), this.mAvrAddress, true, null));
      }
    }
    for (;;)
    {
      finish();
      return;
      tv().setSystemAudioMode(false, true);
    }
  }
  
  private void sendGiveSystemAudioModeStatus()
  {
    sendCommand(HdmiCecMessageBuilder.buildGiveSystemAudioModeStatus(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0)
        {
          SystemAudioAutoInitiationAction.this.tv().setSystemAudioMode(false, true);
          SystemAudioAutoInitiationAction.this.finish();
        }
      }
    });
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt) {
      return;
    }
    switch (this.mState)
    {
    default: 
      return;
    }
    handleSystemAudioModeStatusTimeout();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (this.mAvrAddress != paramHdmiCecMessage.getSource())) {
      return false;
    }
    if (paramHdmiCecMessage.getOpcode() == 126)
    {
      handleSystemAudioModeStatusMessage(HdmiUtils.parseCommandParamSystemAudioStatus(paramHdmiCecMessage));
      return true;
    }
    return false;
  }
  
  boolean start()
  {
    this.mState = 1;
    addTimer(this.mState, 2000);
    sendGiveSystemAudioModeStatus();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SystemAudioAutoInitiationAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */