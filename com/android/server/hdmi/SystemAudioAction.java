package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;
import java.util.List;

abstract class SystemAudioAction
  extends HdmiCecFeatureAction
{
  private static final int MAX_SEND_RETRY_COUNT = 2;
  private static final int OFF_TIMEOUT_MS = 2000;
  private static final int ON_TIMEOUT_MS = 5000;
  private static final int STATE_CHECK_ROUTING_IN_PRGRESS = 1;
  private static final int STATE_WAIT_FOR_SET_SYSTEM_AUDIO_MODE = 2;
  private static final String TAG = "SystemAudioAction";
  protected final int mAvrLogicalAddress;
  private final IHdmiControlCallback mCallback;
  private int mSendRetryCount = 0;
  protected boolean mTargetAudioStatus;
  
  SystemAudioAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt, boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    super(paramHdmiCecLocalDevice);
    HdmiUtils.verifyAddressType(paramInt, 5);
    this.mAvrLogicalAddress = paramInt;
    this.mTargetAudioStatus = paramBoolean;
    this.mCallback = paramIHdmiControlCallback;
  }
  
  private int getSystemAudioModeRequestParam()
  {
    if (tv().getActiveSource().isValid()) {
      return tv().getActiveSource().physicalAddress;
    }
    int i = tv().getActivePath();
    if (i != 65535) {
      return i;
    }
    return 0;
  }
  
  private void handleSendSystemAudioModeRequestTimeout()
  {
    if (this.mTargetAudioStatus)
    {
      int i = this.mSendRetryCount;
      this.mSendRetryCount = (i + 1);
      if (i < 2) {}
    }
    else
    {
      HdmiLogger.debug("[T]:wait for <Set System Audio Mode>.", new Object[0]);
      setSystemAudioMode(false);
      finishWithCallback(1);
      return;
    }
    sendSystemAudioModeRequest();
  }
  
  private void sendSystemAudioModeRequestInternal()
  {
    sendCommand(HdmiCecMessageBuilder.buildSystemAudioModeRequest(getSourceAddress(), this.mAvrLogicalAddress, getSystemAudioModeRequestParam(), this.mTargetAudioStatus), new HdmiControlService.SendMessageCallback()
    {
      public void onSendCompleted(int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0)
        {
          HdmiLogger.debug("Failed to send <System Audio Mode Request>:" + paramAnonymousInt, new Object[0]);
          SystemAudioAction.this.setSystemAudioMode(false);
          SystemAudioAction.this.finishWithCallback(7);
        }
      }
    });
    this.mState = 2;
    int j = this.mState;
    if (this.mTargetAudioStatus) {}
    for (int i = 5000;; i = 2000)
    {
      addTimer(j, i);
      return;
    }
  }
  
  protected void finishWithCallback(int paramInt)
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
        Slog.e("SystemAudioAction", "Failed to invoke callback.", localRemoteException);
      }
    }
  }
  
  final void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt) {
      return;
    }
    switch (this.mState)
    {
    default: 
      return;
    }
    handleSendSystemAudioModeRequestTimeout();
  }
  
  final boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if (paramHdmiCecMessage.getSource() != this.mAvrLogicalAddress) {
      return false;
    }
    switch (this.mState)
    {
    default: 
      return false;
    }
    if ((paramHdmiCecMessage.getOpcode() == 0) && ((paramHdmiCecMessage.getParams()[0] & 0xFF) == 112))
    {
      HdmiLogger.debug("Failed to start system audio mode request.", new Object[0]);
      setSystemAudioMode(false);
      finishWithCallback(5);
      return true;
    }
    boolean bool;
    if ((paramHdmiCecMessage.getOpcode() == 114) && (HdmiUtils.checkCommandSource(paramHdmiCecMessage, this.mAvrLogicalAddress, "SystemAudioAction")))
    {
      bool = HdmiUtils.parseCommandParamSystemAudioStatus(paramHdmiCecMessage);
      if (bool == this.mTargetAudioStatus)
      {
        setSystemAudioMode(bool);
        startAudioStatusAction();
        return true;
      }
    }
    else
    {
      return false;
    }
    HdmiLogger.debug("Unexpected system audio mode request:" + bool, new Object[0]);
    finishWithCallback(5);
    return false;
  }
  
  protected void removeSystemAudioActionInProgress()
  {
    removeActionExcept(SystemAudioActionFromTv.class, this);
    removeActionExcept(SystemAudioActionFromAvr.class, this);
  }
  
  protected void sendSystemAudioModeRequest()
  {
    List localList = getActions(RoutingControlAction.class);
    if (!localList.isEmpty())
    {
      this.mState = 1;
      ((RoutingControlAction)localList.get(0)).addOnFinishedCallback(this, new Runnable()
      {
        public void run()
        {
          SystemAudioAction.-wrap0(SystemAudioAction.this);
        }
      });
      return;
    }
    sendSystemAudioModeRequestInternal();
  }
  
  protected void setSystemAudioMode(boolean paramBoolean)
  {
    tv().setSystemAudioMode(paramBoolean, true);
  }
  
  protected void startAudioStatusAction()
  {
    addAndStartAction(new SystemAudioStatusAction(tv(), this.mAvrLogicalAddress, this.mCallback));
    finish();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/SystemAudioAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */