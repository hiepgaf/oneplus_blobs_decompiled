package com.android.server.hdmi;

import android.util.Slog;

public class OneTouchRecordAction
  extends HdmiCecFeatureAction
{
  private static final int RECORD_STATUS_TIMEOUT_MS = 120000;
  private static final int STATE_RECORDING_IN_PROGRESS = 2;
  private static final int STATE_WAITING_FOR_RECORD_STATUS = 1;
  private static final String TAG = "OneTouchRecordAction";
  private final byte[] mRecordSource;
  private final int mRecorderAddress;
  
  OneTouchRecordAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, byte[] paramArrayOfByte)
  {
    super(paramHdmiCecLocalDevice);
    this.mRecorderAddress = paramInt;
    this.mRecordSource = paramArrayOfByte;
  }
  
  private boolean handleRecordStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    if (paramHdmiCecMessage.getSource() != this.mRecorderAddress) {
      return false;
    }
    int i = paramHdmiCecMessage.getParams()[0];
    tv().announceOneTouchRecordResult(this.mRecorderAddress, i);
    Slog.i("OneTouchRecordAction", "Got record status:" + i + " from " + paramHdmiCecMessage.getSource());
    switch (i)
    {
    default: 
      finish();
    }
    for (;;)
    {
      return true;
      this.mState = 2;
      this.mActionTimer.clearTimerMessage();
    }
  }
  
  private void sendRecordOn()
  {
    sendCommand(HdmiCecMessageBuilder.buildRecordOn(getSourceAddress(), this.mRecorderAddress, this.mRecordSource), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0)
        {
          OneTouchRecordAction.this.tv().announceOneTouchRecordResult(OneTouchRecordAction.-get0(OneTouchRecordAction.this), 49);
          OneTouchRecordAction.this.finish();
          return;
        }
      }
    });
    this.mState = 1;
    addTimer(this.mState, 120000);
  }
  
  int getRecorderAddress()
  {
    return this.mRecorderAddress;
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt)
    {
      Slog.w("OneTouchRecordAction", "Timeout in invalid state:[Expected:" + this.mState + ", Actual:" + paramInt + "]");
      return;
    }
    tv().announceOneTouchRecordResult(this.mRecorderAddress, 49);
    finish();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (this.mRecorderAddress != paramHdmiCecMessage.getSource())) {
      return false;
    }
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
    }
    return handleRecordStatus(paramHdmiCecMessage);
  }
  
  boolean start()
  {
    sendRecordOn();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/OneTouchRecordAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */