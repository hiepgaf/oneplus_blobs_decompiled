package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

final class HotplugDetectionAction
  extends HdmiCecFeatureAction
{
  private static final int AVR_COUNT_MAX = 3;
  private static final int NUM_OF_ADDRESS = 15;
  private static final int POLLING_INTERVAL_MS = 5000;
  private static final int STATE_WAIT_FOR_NEXT_POLLING = 1;
  private static final String TAG = "HotPlugDetectionAction";
  private static final int TIMEOUT_COUNT = 3;
  private int mAvrStatusCount = 0;
  private int mTimeoutCount = 0;
  
  HotplugDetectionAction(HdmiCecLocalDevice paramHdmiCecLocalDevice)
  {
    super(paramHdmiCecLocalDevice);
  }
  
  private void addDevice(int paramInt)
  {
    sendCommand(HdmiCecMessageBuilder.buildGivePhysicalAddress(getSourceAddress(), paramInt));
  }
  
  private static BitSet addressListToBitSet(List<Integer> paramList)
  {
    BitSet localBitSet = new BitSet(15);
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localBitSet.set(((Integer)paramList.next()).intValue());
    }
    return localBitSet;
  }
  
  private void checkHotplug(List<Integer> paramList, boolean paramBoolean)
  {
    BitSet localBitSet1 = infoListToBitSet(tv().getDeviceInfoList(false), paramBoolean);
    paramList = addressListToBitSet(paramList);
    BitSet localBitSet2 = complement(localBitSet1, paramList);
    int i = -1;
    for (;;)
    {
      int j = localBitSet2.nextSetBit(i + 1);
      if (j == -1) {
        break;
      }
      if (j == 5)
      {
        HdmiDeviceInfo localHdmiDeviceInfo = tv().getAvrDeviceInfo();
        if ((localHdmiDeviceInfo != null) && (tv().isConnected(localHdmiDeviceInfo.getPortId())))
        {
          this.mAvrStatusCount += 1;
          Slog.w("HotPlugDetectionAction", "Ack not returned from AVR. count: " + this.mAvrStatusCount);
          i = j;
          if (this.mAvrStatusCount < 3) {
            continue;
          }
        }
      }
      Slog.v("HotPlugDetectionAction", "Remove device by hot-plug detection:" + j);
      removeDevice(j);
      i = j;
    }
    if (!localBitSet2.get(5)) {
      this.mAvrStatusCount = 0;
    }
    paramList = complement(paramList, localBitSet1);
    i = -1;
    for (;;)
    {
      i = paramList.nextSetBit(i + 1);
      if (i == -1) {
        break;
      }
      Slog.v("HotPlugDetectionAction", "Add device by hot-plug detection:" + i);
      addDevice(i);
    }
  }
  
  private static BitSet complement(BitSet paramBitSet1, BitSet paramBitSet2)
  {
    paramBitSet1 = (BitSet)paramBitSet1.clone();
    paramBitSet1.andNot(paramBitSet2);
    return paramBitSet1;
  }
  
  private static BitSet infoListToBitSet(List<HdmiDeviceInfo> paramList, boolean paramBoolean)
  {
    BitSet localBitSet = new BitSet(15);
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)paramList.next();
      if (paramBoolean)
      {
        if (localHdmiDeviceInfo.getDeviceType() == 5) {
          localBitSet.set(localHdmiDeviceInfo.getLogicalAddress());
        }
      }
      else {
        localBitSet.set(localHdmiDeviceInfo.getLogicalAddress());
      }
    }
    return localBitSet;
  }
  
  private void mayCancelDeviceSelect(int paramInt)
  {
    List localList = getActions(DeviceSelectAction.class);
    if (localList.isEmpty()) {
      return;
    }
    if (((DeviceSelectAction)localList.get(0)).getTargetAddress() == paramInt) {
      removeAction(DeviceSelectAction.class);
    }
  }
  
  private void mayCancelOneTouchRecord(int paramInt)
  {
    Iterator localIterator = getActions(OneTouchRecordAction.class).iterator();
    while (localIterator.hasNext())
    {
      OneTouchRecordAction localOneTouchRecordAction = (OneTouchRecordAction)localIterator.next();
      if (localOneTouchRecordAction.getRecorderAddress() == paramInt) {
        removeAction(localOneTouchRecordAction);
      }
    }
  }
  
  private void mayChangeRoutingPath(int paramInt)
  {
    HdmiDeviceInfo localHdmiDeviceInfo = tv().getCecDeviceInfo(paramInt);
    if (localHdmiDeviceInfo != null) {
      tv().handleRemoveActiveRoutingPath(localHdmiDeviceInfo.getPhysicalAddress());
    }
  }
  
  private void mayDisableSystemAudioAndARC(int paramInt)
  {
    if (HdmiUtils.getTypeFromAddress(paramInt) != 5) {
      return;
    }
    tv().setSystemAudioMode(false, true);
    if (tv().isArcEstablished())
    {
      tv().setAudioReturnChannel(false);
      addAndStartAction(new RequestArcTerminationAction(localDevice(), paramInt));
    }
  }
  
  private void pollAllDevices()
  {
    Slog.v("HotPlugDetectionAction", "Poll all devices.");
    pollDevices(new HdmiControlService.DevicePollingCallback()
    {
      public void onPollingFinished(List<Integer> paramAnonymousList)
      {
        HotplugDetectionAction.-wrap0(HotplugDetectionAction.this, paramAnonymousList, false);
      }
    }, 65537, 1);
  }
  
  private void pollAudioSystem()
  {
    Slog.v("HotPlugDetectionAction", "Poll audio system.");
    pollDevices(new HdmiControlService.DevicePollingCallback()
    {
      public void onPollingFinished(List<Integer> paramAnonymousList)
      {
        HotplugDetectionAction.-wrap0(HotplugDetectionAction.this, paramAnonymousList, true);
      }
    }, 65538, 1);
  }
  
  private void pollDevices()
  {
    if (this.mTimeoutCount == 0) {
      pollAllDevices();
    }
    for (;;)
    {
      addTimer(this.mState, 5000);
      return;
      if (tv().isSystemAudioActivated()) {
        pollAudioSystem();
      }
    }
  }
  
  private void removeDevice(int paramInt)
  {
    mayChangeRoutingPath(paramInt);
    mayCancelDeviceSelect(paramInt);
    mayCancelOneTouchRecord(paramInt);
    mayDisableSystemAudioAndARC(paramInt);
    tv().removeCecDevice(paramInt);
  }
  
  void handleTimerEvent(int paramInt)
  {
    if (this.mState != paramInt) {
      return;
    }
    if (this.mState == 1)
    {
      this.mTimeoutCount = ((this.mTimeoutCount + 1) % 3);
      pollDevices();
    }
  }
  
  void pollAllDevicesNow()
  {
    this.mActionTimer.clearTimerMessage();
    this.mTimeoutCount = 0;
    this.mState = 1;
    pollAllDevices();
    addTimer(this.mState, 5000);
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  boolean start()
  {
    Slog.v("HotPlugDetectionAction", "Hot-plug dection started.");
    this.mState = 1;
    this.mTimeoutCount = 0;
    addTimer(this.mState, 5000);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HotplugDetectionAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */