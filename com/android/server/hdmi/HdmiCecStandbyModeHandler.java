package com.android.server.hdmi;

import android.util.SparseArray;

public final class HdmiCecStandbyModeHandler
{
  private final CecMessageHandler mAborterIncorrectMode = new Aborter(1);
  private final CecMessageHandler mAborterRefused = new Aborter(4);
  private final CecMessageHandler mAutoOnHandler = new AutoOnHandler(null);
  private final CecMessageHandler mBypasser = new Bypasser(null);
  private final CecMessageHandler mBystander = new Bystander(null);
  private final SparseArray<CecMessageHandler> mCecMessageHandlers = new SparseArray();
  private final CecMessageHandler mDefaultHandler = new Aborter(0);
  private final HdmiControlService mService;
  private final HdmiCecLocalDeviceTv mTv;
  private final UserControlProcessedHandler mUserControlProcessedHandler = new UserControlProcessedHandler(null);
  
  public HdmiCecStandbyModeHandler(HdmiControlService paramHdmiControlService, HdmiCecLocalDeviceTv paramHdmiCecLocalDeviceTv)
  {
    this.mService = paramHdmiControlService;
    this.mTv = paramHdmiCecLocalDeviceTv;
    addHandler(4, this.mAutoOnHandler);
    addHandler(13, this.mAutoOnHandler);
    addHandler(130, this.mBystander);
    addHandler(133, this.mBystander);
    addHandler(128, this.mBystander);
    addHandler(129, this.mBystander);
    addHandler(134, this.mBystander);
    addHandler(54, this.mBystander);
    addHandler(50, this.mBystander);
    addHandler(135, this.mBystander);
    addHandler(69, this.mBystander);
    addHandler(144, this.mBystander);
    addHandler(0, this.mBystander);
    addHandler(157, this.mBystander);
    addHandler(126, this.mBystander);
    addHandler(122, this.mBystander);
    addHandler(10, this.mBystander);
    addHandler(15, this.mAborterIncorrectMode);
    addHandler(192, this.mAborterIncorrectMode);
    addHandler(197, this.mAborterIncorrectMode);
    addHandler(131, this.mBypasser);
    addHandler(145, this.mBypasser);
    addHandler(132, this.mBypasser);
    addHandler(140, this.mBypasser);
    addHandler(70, this.mBypasser);
    addHandler(71, this.mBypasser);
    addHandler(68, this.mUserControlProcessedHandler);
    addHandler(143, this.mBypasser);
    addHandler(255, this.mBypasser);
    addHandler(159, this.mBypasser);
    addHandler(160, this.mAborterIncorrectMode);
    addHandler(114, this.mAborterIncorrectMode);
  }
  
  private void addHandler(int paramInt, CecMessageHandler paramCecMessageHandler)
  {
    this.mCecMessageHandlers.put(paramInt, paramCecMessageHandler);
  }
  
  boolean handleCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    CecMessageHandler localCecMessageHandler = (CecMessageHandler)this.mCecMessageHandlers.get(paramHdmiCecMessage.getOpcode());
    if (localCecMessageHandler != null) {
      return localCecMessageHandler.handle(paramHdmiCecMessage);
    }
    return this.mDefaultHandler.handle(paramHdmiCecMessage);
  }
  
  private final class Aborter
    implements HdmiCecStandbyModeHandler.CecMessageHandler
  {
    private final int mReason;
    
    public Aborter(int paramInt)
    {
      this.mReason = paramInt;
    }
    
    public boolean handle(HdmiCecMessage paramHdmiCecMessage)
    {
      HdmiCecStandbyModeHandler.-get2(HdmiCecStandbyModeHandler.this).maySendFeatureAbortCommand(paramHdmiCecMessage, this.mReason);
      return true;
    }
  }
  
  private final class AutoOnHandler
    implements HdmiCecStandbyModeHandler.CecMessageHandler
  {
    private AutoOnHandler() {}
    
    public boolean handle(HdmiCecMessage paramHdmiCecMessage)
    {
      if (!HdmiCecStandbyModeHandler.-get3(HdmiCecStandbyModeHandler.this).getAutoWakeup())
      {
        HdmiCecStandbyModeHandler.-get1(HdmiCecStandbyModeHandler.this).handle(paramHdmiCecMessage);
        return true;
      }
      return false;
    }
  }
  
  private static final class Bypasser
    implements HdmiCecStandbyModeHandler.CecMessageHandler
  {
    public boolean handle(HdmiCecMessage paramHdmiCecMessage)
    {
      return false;
    }
  }
  
  private static final class Bystander
    implements HdmiCecStandbyModeHandler.CecMessageHandler
  {
    public boolean handle(HdmiCecMessage paramHdmiCecMessage)
    {
      return true;
    }
  }
  
  private static abstract interface CecMessageHandler
  {
    public abstract boolean handle(HdmiCecMessage paramHdmiCecMessage);
  }
  
  private final class UserControlProcessedHandler
    implements HdmiCecStandbyModeHandler.CecMessageHandler
  {
    private UserControlProcessedHandler() {}
    
    public boolean handle(HdmiCecMessage paramHdmiCecMessage)
    {
      if (HdmiCecLocalDevice.isPowerOnOrToggleCommand(paramHdmiCecMessage)) {
        return false;
      }
      if (HdmiCecLocalDevice.isPowerOffOrToggleCommand(paramHdmiCecMessage)) {
        return true;
      }
      return HdmiCecStandbyModeHandler.-get0(HdmiCecStandbyModeHandler.this).handle(paramHdmiCecMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecStandbyModeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */