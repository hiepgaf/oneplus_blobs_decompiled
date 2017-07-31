package com.android.server.hdmi;

import android.util.Slog;

final class SendKeyAction
  extends HdmiCecFeatureAction
{
  private static final int AWAIT_LONGPRESS_MS = 400;
  private static final int AWAIT_RELEASE_KEY_MS = 1000;
  private static final int STATE_CHECKING_LONGPRESS = 1;
  private static final int STATE_PROCESSING_KEYCODE = 2;
  private static final String TAG = "SendKeyAction";
  private int mLastKeycode;
  private long mLastSendKeyTime;
  private final int mTargetAddress;
  
  SendKeyAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt1, int paramInt2)
  {
    super(paramHdmiCecLocalDevice);
    this.mTargetAddress = paramInt1;
    this.mLastKeycode = paramInt2;
  }
  
  private long getCurrentTime()
  {
    return System.currentTimeMillis();
  }
  
  private void sendKeyDown(int paramInt)
  {
    byte[] arrayOfByte = HdmiCecKeycode.androidKeyToCecKey(paramInt);
    if (arrayOfByte == null) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildUserControlPressed(getSourceAddress(), this.mTargetAddress, arrayOfByte));
  }
  
  private void sendKeyUp()
  {
    sendCommand(HdmiCecMessageBuilder.buildUserControlReleased(getSourceAddress(), this.mTargetAddress));
  }
  
  public void handleTimerEvent(int paramInt)
  {
    switch (this.mState)
    {
    default: 
      Slog.w("SendKeyAction", "Not in a valid state");
      return;
    case 1: 
      this.mActionTimer.clearTimerMessage();
      this.mState = 2;
      sendKeyDown(this.mLastKeycode);
      this.mLastSendKeyTime = getCurrentTime();
      addTimer(this.mState, 1000);
      return;
    }
    sendKeyUp();
    finish();
  }
  
  public boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  void processKeyEvent(int paramInt, boolean paramBoolean)
  {
    if ((this.mState != 1) && (this.mState != 2))
    {
      Slog.w("SendKeyAction", "Not in a valid state");
      return;
    }
    if (paramBoolean)
    {
      if (paramInt != this.mLastKeycode)
      {
        sendKeyDown(paramInt);
        this.mLastSendKeyTime = getCurrentTime();
        if (!HdmiCecKeycode.isRepeatableKey(paramInt))
        {
          sendKeyUp();
          finish();
        }
      }
      else if (getCurrentTime() - this.mLastSendKeyTime >= 300L)
      {
        sendKeyDown(paramInt);
        this.mLastSendKeyTime = getCurrentTime();
      }
      this.mActionTimer.clearTimerMessage();
      addTimer(this.mState, 1000);
      this.mLastKeycode = paramInt;
    }
    while (paramInt != this.mLastKeycode) {
      return;
    }
    sendKeyUp();
    finish();
  }
  
  public boolean start()
  {
    sendKeyDown(this.mLastKeycode);
    this.mLastSendKeyTime = getCurrentTime();
    if (!HdmiCecKeycode.isRepeatableKey(this.mLastKeycode))
    {
      sendKeyUp();
      finish();
      return true;
    }
    this.mState = 1;
    addTimer(this.mState, 400);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SendKeyAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */