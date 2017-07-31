package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class DeviceDiscoveryAction
  extends HdmiCecFeatureAction
{
  private static final int STATE_WAITING_FOR_DEVICE_POLLING = 1;
  private static final int STATE_WAITING_FOR_OSD_NAME = 3;
  private static final int STATE_WAITING_FOR_PHYSICAL_ADDRESS = 2;
  private static final int STATE_WAITING_FOR_VENDOR_ID = 4;
  private static final String TAG = "DeviceDiscoveryAction";
  private final DeviceDiscoveryCallback mCallback;
  private final ArrayList<DeviceInfo> mDevices = new ArrayList();
  private int mProcessedDeviceCount = 0;
  private int mTimeoutRetry = 0;
  
  DeviceDiscoveryAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, DeviceDiscoveryCallback paramDeviceDiscoveryCallback)
  {
    super(paramHdmiCecLocalDevice);
    this.mCallback = ((DeviceDiscoveryCallback)Preconditions.checkNotNull(paramDeviceDiscoveryCallback));
  }
  
  private void allocateDevices(List<Integer> paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      DeviceInfo localDeviceInfo = new DeviceInfo(((Integer)paramList.next()).intValue(), null);
      this.mDevices.add(localDeviceInfo);
    }
  }
  
  private void checkAndProceedStage()
  {
    if (this.mDevices.isEmpty())
    {
      wrapUpAndFinish();
      return;
    }
    if (this.mProcessedDeviceCount == this.mDevices.size())
    {
      this.mProcessedDeviceCount = 0;
      switch (this.mState)
      {
      default: 
        return;
      case 2: 
        startOsdNameStage();
        return;
      case 3: 
        startVendorIdStage();
        return;
      }
      wrapUpAndFinish();
      return;
    }
    sendQueryCommand();
  }
  
  private int getPortId(int paramInt)
  {
    return tv().getPortId(paramInt);
  }
  
  private void handleReportPhysicalAddress(HdmiCecMessage paramHdmiCecMessage)
  {
    if (this.mProcessedDeviceCount < this.mDevices.size()) {}
    DeviceInfo localDeviceInfo;
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      localDeviceInfo = (DeviceInfo)this.mDevices.get(this.mProcessedDeviceCount);
      if (DeviceInfo.-get1(localDeviceInfo) == paramHdmiCecMessage.getSource()) {
        break;
      }
      Slog.w("DeviceDiscoveryAction", "Unmatched address[expected:" + DeviceInfo.-get1(localDeviceInfo) + ", actual:" + paramHdmiCecMessage.getSource());
      return;
    }
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    DeviceInfo.-set2(localDeviceInfo, HdmiUtils.twoBytesToInt(paramHdmiCecMessage));
    DeviceInfo.-set3(localDeviceInfo, getPortId(DeviceInfo.-get2(localDeviceInfo)));
    DeviceInfo.-set0(localDeviceInfo, paramHdmiCecMessage[2] & 0xFF);
    tv().updateCecSwitchInfo(DeviceInfo.-get1(localDeviceInfo), DeviceInfo.-get0(localDeviceInfo), DeviceInfo.-get2(localDeviceInfo));
    increaseProcessedDeviceCount();
    checkAndProceedStage();
  }
  
  private void handleSetOsdName(HdmiCecMessage paramHdmiCecMessage)
  {
    if (this.mProcessedDeviceCount < this.mDevices.size()) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      localDeviceInfo = (DeviceInfo)this.mDevices.get(this.mProcessedDeviceCount);
      if (DeviceInfo.-get1(localDeviceInfo) == paramHdmiCecMessage.getSource()) {
        break;
      }
      Slog.w("DeviceDiscoveryAction", "Unmatched address[expected:" + DeviceInfo.-get1(localDeviceInfo) + ", actual:" + paramHdmiCecMessage.getSource());
      return;
    }
    try
    {
      String str = new String(paramHdmiCecMessage.getParams(), "US-ASCII");
      paramHdmiCecMessage = str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        Slog.w("DeviceDiscoveryAction", "Failed to decode display name: " + paramHdmiCecMessage.toString());
        paramHdmiCecMessage = HdmiUtils.getDefaultDeviceName(DeviceInfo.-get1(localDeviceInfo));
      }
    }
    DeviceInfo.-set1(localDeviceInfo, paramHdmiCecMessage);
    increaseProcessedDeviceCount();
    checkAndProceedStage();
  }
  
  private void handleVendorId(HdmiCecMessage paramHdmiCecMessage)
  {
    if (this.mProcessedDeviceCount < this.mDevices.size()) {}
    DeviceInfo localDeviceInfo;
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      localDeviceInfo = (DeviceInfo)this.mDevices.get(this.mProcessedDeviceCount);
      if (DeviceInfo.-get1(localDeviceInfo) == paramHdmiCecMessage.getSource()) {
        break;
      }
      Slog.w("DeviceDiscoveryAction", "Unmatched address[expected:" + DeviceInfo.-get1(localDeviceInfo) + ", actual:" + paramHdmiCecMessage.getSource());
      return;
    }
    DeviceInfo.-set4(localDeviceInfo, HdmiUtils.threeBytesToInt(paramHdmiCecMessage.getParams()));
    increaseProcessedDeviceCount();
    checkAndProceedStage();
  }
  
  private void increaseProcessedDeviceCount()
  {
    this.mProcessedDeviceCount += 1;
    this.mTimeoutRetry = 0;
  }
  
  private boolean mayProcessMessageIfCached(int paramInt1, int paramInt2)
  {
    HdmiCecMessage localHdmiCecMessage = getCecMessageCache().getMessage(paramInt1, paramInt2);
    if (localHdmiCecMessage != null)
    {
      processCommand(localHdmiCecMessage);
      return true;
    }
    return false;
  }
  
  private void queryOsdName(int paramInt)
  {
    if (!verifyValidLogicalAddress(paramInt))
    {
      checkAndProceedStage();
      return;
    }
    this.mActionTimer.clearTimerMessage();
    if (mayProcessMessageIfCached(paramInt, 71)) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildGiveOsdNameCommand(getSourceAddress(), paramInt));
    addTimer(this.mState, 2000);
  }
  
  private void queryPhysicalAddress(int paramInt)
  {
    if (!verifyValidLogicalAddress(paramInt))
    {
      checkAndProceedStage();
      return;
    }
    this.mActionTimer.clearTimerMessage();
    if (mayProcessMessageIfCached(paramInt, 132)) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildGivePhysicalAddress(getSourceAddress(), paramInt));
    addTimer(this.mState, 2000);
  }
  
  private void queryVendorId(int paramInt)
  {
    if (!verifyValidLogicalAddress(paramInt))
    {
      checkAndProceedStage();
      return;
    }
    this.mActionTimer.clearTimerMessage();
    if (mayProcessMessageIfCached(paramInt, 135)) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildGiveDeviceVendorIdCommand(getSourceAddress(), paramInt));
    addTimer(this.mState, 2000);
  }
  
  private void removeDevice(int paramInt)
  {
    this.mDevices.remove(paramInt);
  }
  
  private void sendQueryCommand()
  {
    int i = DeviceInfo.-get1((DeviceInfo)this.mDevices.get(this.mProcessedDeviceCount));
    switch (this.mState)
    {
    default: 
      return;
    case 2: 
      queryPhysicalAddress(i);
      return;
    case 3: 
      queryOsdName(i);
      return;
    }
    queryVendorId(i);
  }
  
  private void startOsdNameStage()
  {
    Slog.v("DeviceDiscoveryAction", "Start [Osd Name Stage]:" + this.mDevices.size());
    this.mProcessedDeviceCount = 0;
    this.mState = 3;
    checkAndProceedStage();
  }
  
  private void startPhysicalAddressStage()
  {
    Slog.v("DeviceDiscoveryAction", "Start [Physical Address Stage]:" + this.mDevices.size());
    this.mProcessedDeviceCount = 0;
    this.mState = 2;
    checkAndProceedStage();
  }
  
  private void startVendorIdStage()
  {
    Slog.v("DeviceDiscoveryAction", "Start [Vendor Id Stage]:" + this.mDevices.size());
    this.mProcessedDeviceCount = 0;
    this.mState = 4;
    checkAndProceedStage();
  }
  
  private boolean verifyValidLogicalAddress(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt < 15) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void wrapUpAndFinish()
  {
    Slog.v("DeviceDiscoveryAction", "---------Wrap up Device Discovery:[" + this.mDevices.size() + "]---------");
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mDevices.iterator();
    while (localIterator.hasNext())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = DeviceInfo.-wrap0((DeviceInfo)localIterator.next());
      Slog.v("DeviceDiscoveryAction", " DeviceInfo: " + localHdmiDeviceInfo);
      localArrayList.add(localHdmiDeviceInfo);
    }
    Slog.v("DeviceDiscoveryAction", "--------------------------------------------");
    this.mCallback.onDeviceDiscoveryDone(localArrayList);
    finish();
    tv().processAllDelayedMessages();
  }
  
  void handleTimerEvent(int paramInt)
  {
    if ((this.mState == 0) || (this.mState != paramInt)) {
      return;
    }
    paramInt = this.mTimeoutRetry + 1;
    this.mTimeoutRetry = paramInt;
    if (paramInt < 5)
    {
      sendQueryCommand();
      return;
    }
    this.mTimeoutRetry = 0;
    Slog.v("DeviceDiscoveryAction", "Timeout[State=" + this.mState + ", Processed=" + this.mProcessedDeviceCount);
    removeDevice(this.mProcessedDeviceCount);
    checkAndProceedStage();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    switch (this.mState)
    {
    default: 
      return false;
    case 2: 
      if (paramHdmiCecMessage.getOpcode() == 132)
      {
        handleReportPhysicalAddress(paramHdmiCecMessage);
        return true;
      }
      return false;
    case 3: 
      if (paramHdmiCecMessage.getOpcode() == 71)
      {
        handleSetOsdName(paramHdmiCecMessage);
        return true;
      }
      return false;
    }
    if (paramHdmiCecMessage.getOpcode() == 135)
    {
      handleVendorId(paramHdmiCecMessage);
      return true;
    }
    return false;
  }
  
  boolean start()
  {
    this.mDevices.clear();
    this.mState = 1;
    pollDevices(new HdmiControlService.DevicePollingCallback()
    {
      public void onPollingFinished(List<Integer> paramAnonymousList)
      {
        if (paramAnonymousList.isEmpty())
        {
          Slog.v("DeviceDiscoveryAction", "No device is detected.");
          DeviceDiscoveryAction.-wrap2(DeviceDiscoveryAction.this);
          return;
        }
        Slog.v("DeviceDiscoveryAction", "Device detected: " + paramAnonymousList);
        DeviceDiscoveryAction.-wrap0(DeviceDiscoveryAction.this, paramAnonymousList);
        DeviceDiscoveryAction.-wrap1(DeviceDiscoveryAction.this);
      }
    }, 131073, 1);
    return true;
  }
  
  static abstract interface DeviceDiscoveryCallback
  {
    public abstract void onDeviceDiscoveryDone(List<HdmiDeviceInfo> paramList);
  }
  
  private static final class DeviceInfo
  {
    private int mDeviceType = -1;
    private String mDisplayName = "";
    private final int mLogicalAddress;
    private int mPhysicalAddress = 65535;
    private int mPortId = -1;
    private int mVendorId = 16777215;
    
    private DeviceInfo(int paramInt)
    {
      this.mLogicalAddress = paramInt;
    }
    
    private HdmiDeviceInfo toHdmiDeviceInfo()
    {
      return new HdmiDeviceInfo(this.mLogicalAddress, this.mPhysicalAddress, this.mPortId, this.mDeviceType, this.mVendorId, this.mDisplayName);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/DeviceDiscoveryAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */