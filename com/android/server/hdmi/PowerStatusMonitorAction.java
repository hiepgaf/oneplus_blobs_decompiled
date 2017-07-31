package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.SparseIntArray;
import java.util.Iterator;
import java.util.List;

public class PowerStatusMonitorAction
  extends HdmiCecFeatureAction
{
  private static final int INVALID_POWER_STATUS = -2;
  private static final int MONITIROING_INTERNAL_MS = 60000;
  private static final int REPORT_POWER_STATUS_TIMEOUT_MS = 5000;
  private static final int STATE_WAIT_FOR_NEXT_MONITORING = 2;
  private static final int STATE_WAIT_FOR_REPORT_POWER_STATUS = 1;
  private static final String TAG = "PowerStatusMonitorAction";
  private final SparseIntArray mPowerStatus = new SparseIntArray();
  
  PowerStatusMonitorAction(HdmiCecLocalDevice paramHdmiCecLocalDevice)
  {
    super(paramHdmiCecLocalDevice);
  }
  
  private boolean handleReportPowerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getSource();
    if (this.mPowerStatus.get(i, -2) == -2) {
      return false;
    }
    updatePowerStatus(i, paramHdmiCecMessage.getParams()[0] & 0xFF, true);
    return true;
  }
  
  private void handleTimeout()
  {
    int i = 0;
    while (i < this.mPowerStatus.size())
    {
      updatePowerStatus(this.mPowerStatus.keyAt(i), -1, false);
      i += 1;
    }
    this.mPowerStatus.clear();
    this.mState = 2;
  }
  
  private void queryPowerStatus()
  {
    Object localObject = tv().getDeviceInfoList(false);
    resetPowerStatus((List)localObject);
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      final int i = ((HdmiDeviceInfo)((Iterator)localObject).next()).getLogicalAddress();
      sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), i), new HdmiControlService.SendMessageCallback()
      {
        public void onSendCompleted(int paramAnonymousInt)
        {
          if (paramAnonymousInt != 0) {
            PowerStatusMonitorAction.-wrap0(PowerStatusMonitorAction.this, i, -1, true);
          }
        }
      });
    }
    this.mState = 1;
    addTimer(2, 60000);
    addTimer(1, 5000);
  }
  
  private void resetPowerStatus(List<HdmiDeviceInfo> paramList)
  {
    this.mPowerStatus.clear();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)paramList.next();
      this.mPowerStatus.append(localHdmiDeviceInfo.getLogicalAddress(), localHdmiDeviceInfo.getDevicePowerStatus());
    }
  }
  
  private void updatePowerStatus(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    tv().updateDevicePowerStatus(paramInt1, paramInt2);
    if (paramBoolean) {
      this.mPowerStatus.delete(paramInt1);
    }
  }
  
  void handleTimerEvent(int paramInt)
  {
    switch (this.mState)
    {
    default: 
      return;
    case 2: 
      queryPowerStatus();
      return;
    }
    handleTimeout();
  }
  
  boolean processCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if ((this.mState == 1) && (paramHdmiCecMessage.getOpcode() == 144)) {
      return handleReportPowerStatus(paramHdmiCecMessage);
    }
    return false;
  }
  
  boolean start()
  {
    queryPowerStatus();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/PowerStatusMonitorAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */