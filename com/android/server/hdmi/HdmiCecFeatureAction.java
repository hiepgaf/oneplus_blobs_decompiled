package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class HdmiCecFeatureAction
{
  protected static final int MSG_TIMEOUT = 100;
  protected static final int STATE_NONE = 0;
  private static final String TAG = "HdmiCecFeatureAction";
  protected ActionTimer mActionTimer;
  private ArrayList<Pair<HdmiCecFeatureAction, Runnable>> mOnFinishedCallbacks;
  private final HdmiControlService mService;
  private final HdmiCecLocalDevice mSource;
  protected int mState = 0;
  
  HdmiCecFeatureAction(HdmiCecLocalDevice paramHdmiCecLocalDevice)
  {
    this.mSource = paramHdmiCecLocalDevice;
    this.mService = this.mSource.getService();
    this.mActionTimer = createActionTimer(this.mService.getServiceLooper());
  }
  
  private ActionTimer createActionTimer(Looper paramLooper)
  {
    return new ActionTimerHandler(paramLooper);
  }
  
  protected final void addAndStartAction(HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    this.mSource.addAndStartAction(paramHdmiCecFeatureAction);
  }
  
  protected final void addOnFinishedCallback(HdmiCecFeatureAction paramHdmiCecFeatureAction, Runnable paramRunnable)
  {
    if (this.mOnFinishedCallbacks == null) {
      this.mOnFinishedCallbacks = new ArrayList();
    }
    this.mOnFinishedCallbacks.add(Pair.create(paramHdmiCecFeatureAction, paramRunnable));
  }
  
  protected void addTimer(int paramInt1, int paramInt2)
  {
    this.mActionTimer.sendTimerMessage(paramInt1, paramInt2);
  }
  
  void clear()
  {
    this.mState = 0;
    this.mActionTimer.clearTimerMessage();
  }
  
  protected void finish()
  {
    finish(true);
  }
  
  void finish(boolean paramBoolean)
  {
    clear();
    if (paramBoolean) {
      removeAction(this);
    }
    if (this.mOnFinishedCallbacks != null)
    {
      Iterator localIterator = this.mOnFinishedCallbacks.iterator();
      while (localIterator.hasNext())
      {
        Pair localPair = (Pair)localIterator.next();
        if (((HdmiCecFeatureAction)localPair.first).mState != 0) {
          ((Runnable)localPair.second).run();
        }
      }
      this.mOnFinishedCallbacks = null;
    }
  }
  
  protected final <T extends HdmiCecFeatureAction> List<T> getActions(Class<T> paramClass)
  {
    return this.mSource.getActions(paramClass);
  }
  
  protected final HdmiCecMessageCache getCecMessageCache()
  {
    return this.mSource.getCecMessageCache();
  }
  
  protected final int getSourceAddress()
  {
    return this.mSource.getDeviceInfo().getLogicalAddress();
  }
  
  protected final int getSourcePath()
  {
    return this.mSource.getDeviceInfo().getPhysicalAddress();
  }
  
  abstract void handleTimerEvent(int paramInt);
  
  protected final HdmiCecLocalDevice localDevice()
  {
    return this.mSource;
  }
  
  protected final HdmiCecLocalDevicePlayback playback()
  {
    return (HdmiCecLocalDevicePlayback)this.mSource;
  }
  
  protected final void pollDevices(HdmiControlService.DevicePollingCallback paramDevicePollingCallback, int paramInt1, int paramInt2)
  {
    this.mService.pollDevices(paramDevicePollingCallback, getSourceAddress(), paramInt1, paramInt2);
  }
  
  abstract boolean processCommand(HdmiCecMessage paramHdmiCecMessage);
  
  protected final void removeAction(HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    this.mSource.removeAction(paramHdmiCecFeatureAction);
  }
  
  protected final <T extends HdmiCecFeatureAction> void removeAction(Class<T> paramClass)
  {
    this.mSource.removeActionExcept(paramClass, null);
  }
  
  protected final <T extends HdmiCecFeatureAction> void removeActionExcept(Class<T> paramClass, HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    this.mSource.removeActionExcept(paramClass, paramHdmiCecFeatureAction);
  }
  
  protected final void sendCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    this.mService.sendCecCommand(paramHdmiCecMessage);
  }
  
  protected final void sendCommand(HdmiCecMessage paramHdmiCecMessage, HdmiControlService.SendMessageCallback paramSendMessageCallback)
  {
    this.mService.sendCecCommand(paramHdmiCecMessage, paramSendMessageCallback);
  }
  
  protected final void sendUserControlPressedAndReleased(int paramInt1, int paramInt2)
  {
    this.mSource.sendUserControlPressedAndReleased(paramInt1, paramInt2);
  }
  
  void setActionTimer(ActionTimer paramActionTimer)
  {
    this.mActionTimer = paramActionTimer;
  }
  
  abstract boolean start();
  
  boolean started()
  {
    boolean bool = false;
    if (this.mState != 0) {
      bool = true;
    }
    return bool;
  }
  
  protected final HdmiCecLocalDeviceTv tv()
  {
    return (HdmiCecLocalDeviceTv)this.mSource;
  }
  
  static abstract interface ActionTimer
  {
    public abstract void clearTimerMessage();
    
    public abstract void sendTimerMessage(int paramInt, long paramLong);
  }
  
  private class ActionTimerHandler
    extends Handler
    implements HdmiCecFeatureAction.ActionTimer
  {
    public ActionTimerHandler(Looper paramLooper)
    {
      super();
    }
    
    public void clearTimerMessage()
    {
      removeMessages(100);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        Slog.w("HdmiCecFeatureAction", "Unsupported message:" + paramMessage.what);
        return;
      }
      HdmiCecFeatureAction.this.handleTimerEvent(paramMessage.arg1);
    }
    
    public void sendTimerMessage(int paramInt, long paramLong)
    {
      sendMessageDelayed(obtainMessage(100, paramInt, 0), paramLong);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecFeatureAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */