package com.android.server.hdmi;

abstract class RequestArcAction
  extends HdmiCecFeatureAction
{
  protected static final int STATE_WATING_FOR_REQUEST_ARC_REQUEST_RESPONSE = 1;
  private static final String TAG = "RequestArcAction";
  protected final int mAvrAddress;
  
  RequestArcAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt)
  {
    super(paramHdmiCecLocalDevice);
    HdmiUtils.verifyAddressType(getSourceAddress(), 0);
    HdmiUtils.verifyAddressType(paramInt, 5);
    this.mAvrAddress = paramInt;
  }
  
  protected final void disableArcTransmission()
  {
    addAndStartAction(new SetArcTransmissionStateAction(localDevice(), this.mAvrAddress, false));
  }
  
  final void handleTimerEvent(int paramInt)
  {
    if ((this.mState != paramInt) || (paramInt != 1)) {
      return;
    }
    HdmiLogger.debug("[T] RequestArcAction.", new Object[0]);
    disableArcTransmission();
    finish();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState == 1) && (HdmiUtils.checkCommandSource(paramHdmiCecMessage, this.mAvrAddress, "RequestArcAction"))) {}
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
      return false;
    }
    int i = paramHdmiCecMessage.getParams()[0] & 0xFF;
    if (i == 196)
    {
      disableArcTransmission();
      finish();
      return true;
    }
    if (i == 195)
    {
      tv().setArcStatus(false);
      finish();
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/RequestArcAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */