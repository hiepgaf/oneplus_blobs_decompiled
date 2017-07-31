package com.android.server.hdmi;

import android.media.AudioManager;

final class VolumeControlAction
  extends HdmiCecFeatureAction
{
  private static final int MAX_VOLUME = 100;
  private static final int STATE_WAIT_FOR_NEXT_VOLUME_PRESS = 1;
  private static final String TAG = "VolumeControlAction";
  private static final int UNKNOWN_AVR_VOLUME = -1;
  private final int mAvrAddress;
  private boolean mIsVolumeUp;
  private boolean mLastAvrMute;
  private int mLastAvrVolume;
  private long mLastKeyUpdateTime;
  private boolean mSentKeyPressed;
  
  VolumeControlAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean)
  {
    super(paramHdmiCecLocalDevice);
    this.mAvrAddress = paramInt;
    this.mIsVolumeUp = paramBoolean;
    this.mLastAvrVolume = -1;
    this.mLastAvrMute = false;
    this.mSentKeyPressed = false;
    updateLastKeyUpdateTime();
  }
  
  private boolean handleFeatureAbort(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((paramHdmiCecMessage.getParams()[0] & 0xFF) == 68)
    {
      finish();
      return true;
    }
    return false;
  }
  
  private boolean handleReportAudioStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    if ((paramHdmiCecMessage[0] & 0x80) == 128) {}
    for (boolean bool = true;; bool = false)
    {
      int i = paramHdmiCecMessage[0] & 0x7F;
      this.mLastAvrVolume = i;
      this.mLastAvrMute = bool;
      if (shouldUpdateAudioVolume(bool))
      {
        HdmiLogger.debug("Force volume change[mute:%b, volume=%d]", new Object[] { Boolean.valueOf(bool), Integer.valueOf(i) });
        tv().setAudioStatus(bool, i);
        this.mLastAvrVolume = -1;
        this.mLastAvrMute = false;
      }
      return true;
    }
  }
  
  private void resetTimer()
  {
    this.mActionTimer.clearTimerMessage();
    addTimer(1, 300);
  }
  
  public static int scaleToCecVolume(int paramInt1, int paramInt2)
  {
    return paramInt1 * 100 / paramInt2;
  }
  
  public static int scaleToCustomVolume(int paramInt1, int paramInt2)
  {
    return paramInt1 * paramInt2 / 100;
  }
  
  private void sendVolumeKeyPressed()
  {
    int j = getSourceAddress();
    int k = this.mAvrAddress;
    if (this.mIsVolumeUp) {}
    for (int i = 65;; i = 66)
    {
      sendCommand(HdmiCecMessageBuilder.buildUserControlPressed(j, k, i));
      this.mSentKeyPressed = true;
      return;
    }
  }
  
  private void sendVolumeKeyReleased()
  {
    sendCommand(HdmiCecMessageBuilder.buildUserControlReleased(getSourceAddress(), this.mAvrAddress));
    this.mSentKeyPressed = false;
  }
  
  private boolean shouldUpdateAudioVolume(boolean paramBoolean)
  {
    if (paramBoolean) {
      return true;
    }
    AudioManager localAudioManager = tv().getService().getAudioManager();
    int i = localAudioManager.getStreamVolume(3);
    if (this.mIsVolumeUp) {
      return i == localAudioManager.getStreamMaxVolume(3);
    }
    return i == 0;
  }
  
  private void updateLastKeyUpdateTime()
  {
    this.mLastKeyUpdateTime = System.currentTimeMillis();
  }
  
  protected void clear()
  {
    super.clear();
    if (this.mSentKeyPressed) {
      sendVolumeKeyReleased();
    }
    if (this.mLastAvrVolume != -1)
    {
      tv().setAudioStatus(this.mLastAvrMute, this.mLastAvrVolume);
      this.mLastAvrVolume = -1;
      this.mLastAvrMute = false;
    }
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (paramInt != 1) {
      return;
    }
    if (System.currentTimeMillis() - this.mLastKeyUpdateTime >= 300L)
    {
      finish();
      return;
    }
    sendVolumeKeyPressed();
    resetTimer();
  }
  
  void handleVolumeChange(boolean paramBoolean)
  {
    if (this.mIsVolumeUp != paramBoolean)
    {
      HdmiLogger.debug("Volume Key Status Changed[old:%b new:%b]", new Object[] { Boolean.valueOf(this.mIsVolumeUp), Boolean.valueOf(paramBoolean) });
      sendVolumeKeyReleased();
      this.mIsVolumeUp = paramBoolean;
      sendVolumeKeyPressed();
      resetTimer();
    }
    updateLastKeyUpdateTime();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState != 1) || (paramHdmiCecMessage.getSource() != this.mAvrAddress)) {
      return false;
    }
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
    case 122: 
      return handleReportAudioStatus(paramHdmiCecMessage);
    }
    return handleFeatureAbort(paramHdmiCecMessage);
  }
  
  boolean start()
  {
    this.mState = 1;
    sendVolumeKeyPressed();
    resetTimer();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/VolumeControlAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */