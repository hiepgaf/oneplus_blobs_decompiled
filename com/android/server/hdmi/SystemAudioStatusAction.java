package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;

final class SystemAudioStatusAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAIT_FOR_REPORT_AUDIO_STATUS = 1;
  private static final String TAG = "SystemAudioStatusAction";
  private final int mAvrAddress;
  private final IHdmiControlCallback mCallback;
  
  SystemAudioStatusAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice);
    this.mAvrAddress = paramInt;
    this.mCallback = paramIHdmiControlCallback;
  }
  
  private void finishWithCallback(int paramInt)
  {
    if (this.mCallback != null) {}
    try
    {
      this.mCallback.onComplete(paramInt);
      finish();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.e("SystemAudioStatusAction", "Failed to invoke callback.", localRemoteException);
      }
    }
  }
  
  private void handleReportAudioStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    if ((paramHdmiCecMessage[0] & 0x80) == 128) {}
    for (boolean bool = true;; bool = false)
    {
      int i = paramHdmiCecMessage[0];
      tv().setAudioStatus(bool, i & 0x7F);
      if (!(tv().isSystemAudioActivated() ^ bool)) {
        sendUserControlPressedAndReleased(this.mAvrAddress, 67);
      }
      finishWithCallback(0);
      return;
    }
  }
  
  private void handleSendGiveAudioStatusFailure()
  {
    tv().setAudioStatus(false, -1);
    int i = this.mAvrAddress;
    if (tv().isSystemAudioActivated()) {}
    for (boolean bool = false;; bool = true)
    {
      sendUserControlPressedAndReleased(i, HdmiCecKeycode.getMuteKey(bool));
      finishWithCallback(0);
      return;
    }
  }
  
  private void sendGiveAudioStatus()
  {
    sendCommand(HdmiCecMessageBuilder.buildGiveAudioStatus(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0) {
          SystemAudioStatusAction.-wrap0(SystemAudioStatusAction.this);
        }
      }
    });
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt) {
      return;
    }
    handleSendGiveAudioStatusFailure();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (this.mAvrAddress != paramHdmiCecMessage.getSource())) {
      return false;
    }
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
    }
    handleReportAudioStatus(paramHdmiCecMessage);
    return true;
  }
  
  boolean start()
  {
    this.mState = 1;
    addTimer(this.mState, 2000);
    sendGiveAudioStatus();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SystemAudioStatusAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */