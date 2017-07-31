package com.android.server.hdmi;

import android.util.Slog;

final class SetArcTransmissionStateAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAITING_TIMEOUT = 1;
  private static final String TAG = "SetArcTransmissionStateAction";
  private final int mAvrAddress;
  private final boolean mEnabled;
  
  SetArcTransmissionStateAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean)
  {
    super(paramHdmiCecLocalDevice);
    HdmiUtils.verifyAddressType(getSourceAddress(), 0);
    HdmiUtils.verifyAddressType(paramInt, 5);
    this.mAvrAddress = paramInt;
    this.mEnabled = paramBoolean;
  }
  
  private void sendReportArcInitiated()
  {
    sendCommand(HdmiCecMessageBuilder.buildReportArcInitiated(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        switch (paramAnonymousInt)
        {
        case 0: 
        case 2: 
        case 3: 
        default: 
          return;
        }
        SetArcTransmissionStateAction.-wrap0(SetArcTransmissionStateAction.this, false);
        HdmiLogger.debug("Failed to send <Report Arc Initiated>.", new Object[0]);
        SetArcTransmissionStateAction.this.finish();
      }
    });
  }
  
  private void setArcStatus(boolean paramBoolean)
  {
    boolean bool = tv().setArcStatus(paramBoolean);
    Slog.i("SetArcTransmissionStateAction", "Change arc status [old:" + bool + ", new:" + paramBoolean + "]");
    if ((!paramBoolean) && (bool)) {
      sendCommand(HdmiCecMessageBuilder.buildReportArcTerminated(getSourceAddress(), this.mAvrAddress));
    }
  }
  
  void handleTimerEvent(int paramInt)
  {
    if ((this.mState != paramInt) || (this.mState != 1)) {
      return;
    }
    finish();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if (this.mState != 1) {
      return false;
    }
    if ((paramHdmiCecMessage.getOpcode() == 0) && ((paramHdmiCecMessage.getParams()[0] & 0xFF) == 193))
    {
      HdmiLogger.debug("Feature aborted for <Report Arc Initiated>", new Object[0]);
      setArcStatus(false);
      finish();
      return true;
    }
    return false;
  }
  
  boolean start()
  {
    if (this.mEnabled)
    {
      setArcStatus(true);
      this.mState = 1;
      addTimer(this.mState, 2000);
      sendReportArcInitiated();
      return true;
    }
    setArcStatus(false);
    finish();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SetArcTransmissionStateAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */