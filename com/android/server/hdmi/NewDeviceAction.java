package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import java.io.UnsupportedEncodingException;

final class NewDeviceAction
  extends HdmiCecFeatureAction
{
  static final int STATE_WAITING_FOR_DEVICE_VENDOR_ID = 2;
  static final int STATE_WAITING_FOR_SET_OSD_NAME = 1;
  private static final String TAG = "NewDeviceAction";
  private final int mDeviceLogicalAddress;
  private final int mDevicePhysicalAddress;
  private final int mDeviceType;
  private String mDisplayName;
  private int mTimeoutRetry;
  private int mVendorId;
  
  NewDeviceAction(HdmiCecLocalDevice paramHdmiCecLocalDevice, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramHdmiCecLocalDevice);
    this.mDeviceLogicalAddress = paramInt1;
    this.mDevicePhysicalAddress = paramInt2;
    this.mDeviceType = paramInt3;
    this.mVendorId = 16777215;
  }
  
  private void addDeviceInfo()
  {
    if (!tv().isInDeviceList(this.mDeviceLogicalAddress, this.mDevicePhysicalAddress))
    {
      Slog.w("NewDeviceAction", String.format("Device not found (%02x, %04x)", new Object[] { Integer.valueOf(this.mDeviceLogicalAddress), Integer.valueOf(this.mDevicePhysicalAddress) }));
      return;
    }
    if (this.mDisplayName == null) {
      this.mDisplayName = HdmiUtils.getDefaultDeviceName(this.mDeviceLogicalAddress);
    }
    HdmiDeviceInfo localHdmiDeviceInfo = new HdmiDeviceInfo(this.mDeviceLogicalAddress, this.mDevicePhysicalAddress, tv().getPortId(this.mDevicePhysicalAddress), this.mDeviceType, this.mVendorId, this.mDisplayName);
    tv().addCecDevice(localHdmiDeviceInfo);
    tv().processDelayedMessages(this.mDeviceLogicalAddress);
    if (HdmiUtils.getTypeFromAddress(this.mDeviceLogicalAddress) == 5) {
      tv().onNewAvrAdded(localHdmiDeviceInfo);
    }
  }
  
  private boolean mayProcessCommandIfCached(int paramInt1, int paramInt2)
  {
    HdmiCecMessage localHdmiCecMessage = getCecMessageCache().getMessage(paramInt1, paramInt2);
    if (localHdmiCecMessage != null) {
      return processCommand(localHdmiCecMessage);
    }
    return false;
  }
  
  private void requestOsdName(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mTimeoutRetry = 0;
    }
    this.mState = 1;
    if (mayProcessCommandIfCached(this.mDeviceLogicalAddress, 71)) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildGiveOsdNameCommand(getSourceAddress(), this.mDeviceLogicalAddress));
    addTimer(this.mState, 2000);
  }
  
  private void requestVendorId(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mTimeoutRetry = 0;
    }
    this.mState = 2;
    if (mayProcessCommandIfCached(this.mDeviceLogicalAddress, 135)) {
      return;
    }
    sendCommand(HdmiCecMessageBuilder.buildGiveDeviceVendorIdCommand(getSourceAddress(), this.mDeviceLogicalAddress));
    addTimer(this.mState, 2000);
  }
  
  public void handleTimerEvent(int paramInt)
  {
    if ((this.mState == 0) || (this.mState != paramInt)) {
      return;
    }
    if (paramInt == 1)
    {
      paramInt = this.mTimeoutRetry + 1;
      this.mTimeoutRetry = paramInt;
      if (paramInt < 5)
      {
        requestOsdName(false);
        return;
      }
      requestVendorId(true);
    }
    while (paramInt != 2) {
      return;
    }
    paramInt = this.mTimeoutRetry + 1;
    this.mTimeoutRetry = paramInt;
    if (paramInt < 5)
    {
      requestVendorId(false);
      return;
    }
    addDeviceInfo();
    finish();
  }
  
  boolean isActionOf(HdmiCecLocalDevice.ActiveSource paramActiveSource)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mDeviceLogicalAddress == paramActiveSource.logicalAddress)
    {
      bool1 = bool2;
      if (this.mDevicePhysicalAddress == paramActiveSource.physicalAddress) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getOpcode();
    int j = paramHdmiCecMessage.getSource();
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    if (this.mDeviceLogicalAddress != j) {
      return false;
    }
    if (this.mState == 1)
    {
      if (i == 71) {
        try
        {
          this.mDisplayName = new String(paramHdmiCecMessage, "US-ASCII");
          requestVendorId(true);
          return true;
        }
        catch (UnsupportedEncodingException paramHdmiCecMessage)
        {
          for (;;)
          {
            Slog.e("NewDeviceAction", "Failed to get OSD name: " + paramHdmiCecMessage.getMessage());
          }
        }
      }
      if ((i == 0) && ((paramHdmiCecMessage[0] & 0xFF) == 70))
      {
        requestVendorId(true);
        return true;
      }
    }
    else if (this.mState == 2)
    {
      if (i == 135)
      {
        this.mVendorId = HdmiUtils.threeBytesToInt(paramHdmiCecMessage);
        addDeviceInfo();
        finish();
        return true;
      }
      if ((i == 0) && ((paramHdmiCecMessage[0] & 0xFF) == 140))
      {
        addDeviceInfo();
        finish();
        return true;
      }
    }
    return false;
  }
  
  public boolean start()
  {
    requestOsdName(true);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/NewDeviceAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */