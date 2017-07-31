package com.android.server.hdmi;

import android.util.Slog;
import java.util.Arrays;

public class TimerRecordingAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAITING_FOR_TIMER_STATUS = 1;
  private static final String TAG = "TimerRecordingAction";
  private static final int TIMER_STATUS_TIMEOUT_MS = 120000;
  private final byte[] mRecordSource;
  private final int mRecorderAddress;
  private final int mSourceType;
  
  TimerRecordingAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    super(paramHdmiCecLocalDevice);
    this.mRecorderAddress = paramInt1;
    this.mSourceType = paramInt2;
    this.mRecordSource = paramArrayOfByte;
  }
  
  private static int bytesToInt(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length > 4) {
      throw new IllegalArgumentException("Invalid data size:" + Arrays.toString(paramArrayOfByte));
    }
    int j = 0;
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      j |= (paramArrayOfByte[i] & 0xFF) << (3 - i) * 8;
      i += 1;
    }
    return j;
  }
  
  private boolean handleFeatureAbort(HdmiCecMessage paramHdmiCecMessage)
  {
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    int i = paramHdmiCecMessage[0] & 0xFF;
    switch (i)
    {
    default: 
      return false;
    }
    int j = paramHdmiCecMessage[1];
    Slog.i("TimerRecordingAction", "[Feature Abort] for " + i + " reason:" + (j & 0xFF));
    tv().announceTimerRecordingResult(this.mRecorderAddress, 1);
    finish();
    return true;
  }
  
  private boolean handleTimerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    if ((paramHdmiCecMessage.length == 1) || (paramHdmiCecMessage.length == 3))
    {
      tv().announceTimerRecordingResult(this.mRecorderAddress, bytesToInt(paramHdmiCecMessage));
      Slog.i("TimerRecordingAction", "Received [Timer Status Data]:" + Arrays.toString(paramHdmiCecMessage));
    }
    for (;;)
    {
      finish();
      return true;
      Slog.w("TimerRecordingAction", "Invalid [Timer Status Data]:" + Arrays.toString(paramHdmiCecMessage));
    }
  }
  
  private void sendTimerMessage()
  {
    HdmiCecMessage localHdmiCecMessage;
    switch (this.mSourceType)
    {
    default: 
      tv().announceTimerRecordingResult(this.mRecorderAddress, 2);
      finish();
      return;
    case 1: 
      localHdmiCecMessage = HdmiCecMessageBuilder.buildSetDigitalTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
    }
    for (;;)
    {
      sendCommand(localHdmiCecMessage, new HdmiControlService.SendMessageCallback()
      {
        public void onSendCompleted(int paramAnonymousInt)
        {
          if (paramAnonymousInt != 0)
          {
            TimerRecordingAction.this.tv().announceTimerRecordingResult(TimerRecordingAction.-get0(TimerRecordingAction.this), 1);
            TimerRecordingAction.this.finish();
            return;
          }
          TimerRecordingAction.this.mState = 1;
          TimerRecordingAction.this.addTimer(TimerRecordingAction.this.mState, 120000);
        }
      });
      return;
      localHdmiCecMessage = HdmiCecMessageBuilder.buildSetAnalogueTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
      continue;
      localHdmiCecMessage = HdmiCecMessageBuilder.buildSetExternalTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
    }
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt)
    {
      Slog.w("TimerRecordingAction", "Timeout in invalid state:[Expected:" + this.mState + ", Actual:" + paramInt + "]");
      return;
    }
    tv().announceTimerRecordingResult(this.mRecorderAddress, 1);
    finish();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (paramHdmiCecMessage.getSource() != this.mRecorderAddress)) {
      return false;
    }
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
    case 53: 
      return handleTimerStatus(paramHdmiCecMessage);
    }
    return handleFeatureAbort(paramHdmiCecMessage);
  }
  
  boolean start()
  {
    sendTimerMessage();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/TimerRecordingAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */