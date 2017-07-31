package com.android.server.hdmi;

final class RequestArcInitiationAction
  extends RequestArcAction
{
  private static final String TAG = "RequestArcInitiationAction";
  
  RequestArcInitiationAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt)
  {
    super(paramHdmiCecLocalDevice, paramInt);
  }
  
  boolean start()
  {
    this.mState = 1;
    addTimer(this.mState, 2000);
    sendCommand(HdmiCecMessageBuilder.buildRequestArcInitiation(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0)
        {
          RequestArcInitiationAction.this.tv().setArcStatus(false);
          RequestArcInitiationAction.this.finish();
        }
      }
    });
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/RequestArcInitiationAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */