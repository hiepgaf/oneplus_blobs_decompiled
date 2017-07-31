package com.android.server.hdmi;

import android.hardware.hdmi.HdmiPortInfo;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;

final class HdmiMhlControllerStub
{
  private static final HdmiPortInfo[] EMPTY_PORT_INFO = new HdmiPortInfo[0];
  private static final int INVALID_DEVICE_ROLES = 0;
  private static final int INVALID_MHL_VERSION = 0;
  private static final int NO_SUPPORTED_FEATURES = 0;
  private static final SparseArray<HdmiMhlLocalDeviceStub> mLocalDevices = new SparseArray();
  
  private HdmiMhlControllerStub(HdmiControlService paramHdmiControlService) {}
  
  static HdmiMhlControllerStub create(HdmiControlService paramHdmiControlService)
  {
    return new HdmiMhlControllerStub(paramHdmiControlService);
  }
  
  HdmiMhlLocalDeviceStub addLocalDevice(HdmiMhlLocalDeviceStub paramHdmiMhlLocalDeviceStub)
  {
    return null;
  }
  
  void clearAllLocalDevices() {}
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter) {}
  
  SparseArray<HdmiMhlLocalDeviceStub> getAllLocalDevices()
  {
    return mLocalDevices;
  }
  
  int getEcbusDeviceRoles(int paramInt)
  {
    return 0;
  }
  
  HdmiMhlLocalDeviceStub getLocalDevice(int paramInt)
  {
    return null;
  }
  
  HdmiMhlLocalDeviceStub getLocalDeviceById(int paramInt)
  {
    return null;
  }
  
  int getMhlVersion(int paramInt)
  {
    return 0;
  }
  
  int getPeerMhlVersion(int paramInt)
  {
    return 0;
  }
  
  HdmiPortInfo[] getPortInfos()
  {
    return EMPTY_PORT_INFO;
  }
  
  int getSupportedFeatures(int paramInt)
  {
    return 0;
  }
  
  boolean isReady()
  {
    return false;
  }
  
  HdmiMhlLocalDeviceStub removeLocalDevice(int paramInt)
  {
    return null;
  }
  
  void sendVendorCommand(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) {}
  
  void setOption(int paramInt1, int paramInt2) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiMhlControllerStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */