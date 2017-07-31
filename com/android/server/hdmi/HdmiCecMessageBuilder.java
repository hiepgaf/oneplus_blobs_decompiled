package com.android.server.hdmi;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HdmiCecMessageBuilder
{
  private static final int OSD_NAME_MAX_LENGTH = 13;
  
  static HdmiCecMessage buildActiveSource(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, 15, 130, physicalAddressToParam(paramInt2));
  }
  
  static HdmiCecMessage buildCecVersion(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildCommand(paramInt1, paramInt2, 158, new byte[] { (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildClearAnalogueTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 51, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildClearDigitalTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 153, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildClearExternalTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 161, paramArrayOfByte);
  }
  
  private static HdmiCecMessage buildCommand(int paramInt1, int paramInt2, int paramInt3)
  {
    return new HdmiCecMessage(paramInt1, paramInt2, paramInt3, HdmiCecMessage.EMPTY_PARAM);
  }
  
  private static HdmiCecMessage buildCommand(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    return new HdmiCecMessage(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildDeviceVendorIdCommand(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, 15, 135, new byte[] { (byte)(paramInt2 >> 16 & 0xFF), (byte)(paramInt2 >> 8 & 0xFF), (byte)(paramInt2 & 0xFF) });
  }
  
  static HdmiCecMessage buildFeatureAbortCommand(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return buildCommand(paramInt1, paramInt2, 0, new byte[] { (byte)(paramInt3 & 0xFF), (byte)(paramInt4 & 0xFF) });
  }
  
  static HdmiCecMessage buildGiveAudioStatus(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 113);
  }
  
  static HdmiCecMessage buildGiveDevicePowerStatus(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 143);
  }
  
  static HdmiCecMessage buildGiveDeviceVendorIdCommand(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 140);
  }
  
  static HdmiCecMessage buildGiveOsdNameCommand(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 70);
  }
  
  static HdmiCecMessage buildGivePhysicalAddress(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 131);
  }
  
  static HdmiCecMessage buildGiveSystemAudioModeStatus(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 125);
  }
  
  static HdmiCecMessage buildInactiveSource(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, 0, 157, physicalAddressToParam(paramInt2));
  }
  
  static HdmiCecMessage buildRecordOff(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 11);
  }
  
  static HdmiCecMessage buildRecordOn(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 9, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildReportArcInitiated(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 193);
  }
  
  static HdmiCecMessage buildReportArcTerminated(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 194);
  }
  
  static HdmiCecMessage buildReportMenuStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildCommand(paramInt1, paramInt2, 142, new byte[] { (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildReportPhysicalAddressCommand(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildCommand(paramInt1, 15, 132, new byte[] { (byte)(paramInt2 >> 8 & 0xFF), (byte)(paramInt2 & 0xFF), (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildReportPowerStatus(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildCommand(paramInt1, paramInt2, 144, new byte[] { (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildRequestArcInitiation(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 195);
  }
  
  static HdmiCecMessage buildRequestArcTermination(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 196);
  }
  
  static HdmiCecMessage buildRoutingChange(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildCommand(paramInt1, 15, 128, new byte[] { (byte)(paramInt2 >> 8 & 0xFF), (byte)(paramInt2 & 0xFF), (byte)(paramInt3 >> 8 & 0xFF), (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildSetAnalogueTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 52, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildSetDigitalTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 151, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildSetExternalTimer(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 162, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildSetMenuLanguageCommand(int paramInt, String paramString)
  {
    if (paramString.length() != 3) {
      return null;
    }
    paramString = paramString.toLowerCase();
    return buildCommand(paramInt, 15, 50, new byte[] { (byte)(paramString.charAt(0) & 0xFF), (byte)(paramString.charAt(1) & 0xFF), (byte)(paramString.charAt(2) & 0xFF) });
  }
  
  static HdmiCecMessage buildSetOsdNameCommand(int paramInt1, int paramInt2, String paramString)
  {
    int i = Math.min(paramString.length(), 13);
    try
    {
      paramString = paramString.substring(0, i).getBytes("US-ASCII");
      return buildCommand(paramInt1, paramInt2, 71, paramString);
    }
    catch (UnsupportedEncodingException paramString) {}
    return null;
  }
  
  static HdmiCecMessage buildSetStreamPath(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, 15, 134, physicalAddressToParam(paramInt2));
  }
  
  public static HdmiCecMessage buildStandby(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 54);
  }
  
  static HdmiCecMessage buildSystemAudioModeRequest(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (paramBoolean) {
      return buildCommand(paramInt1, paramInt2, 112, physicalAddressToParam(paramInt3));
    }
    return buildCommand(paramInt1, paramInt2, 112);
  }
  
  static HdmiCecMessage buildTextViewOn(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 13);
  }
  
  static HdmiCecMessage buildUserControlPressed(int paramInt1, int paramInt2, int paramInt3)
  {
    return buildUserControlPressed(paramInt1, paramInt2, new byte[] { (byte)(paramInt3 & 0xFF) });
  }
  
  static HdmiCecMessage buildUserControlPressed(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 68, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildUserControlReleased(int paramInt1, int paramInt2)
  {
    return buildCommand(paramInt1, paramInt2, 69);
  }
  
  static HdmiCecMessage buildVendorCommand(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return buildCommand(paramInt1, paramInt2, 137, paramArrayOfByte);
  }
  
  static HdmiCecMessage buildVendorCommandWithId(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 3];
    arrayOfByte[0] = ((byte)(paramInt3 >> 16 & 0xFF));
    arrayOfByte[1] = ((byte)(paramInt3 >> 8 & 0xFF));
    arrayOfByte[2] = ((byte)(paramInt3 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 3, paramArrayOfByte.length);
    return buildCommand(paramInt1, paramInt2, 160, arrayOfByte);
  }
  
  static HdmiCecMessage of(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    return new HdmiCecMessage(paramInt1, paramInt2, paramArrayOfByte[0], Arrays.copyOfRange(paramArrayOfByte, 1, paramArrayOfByte.length));
  }
  
  private static byte[] physicalAddressToParam(int paramInt)
  {
    return new byte[] { (byte)(paramInt >> 8 & 0xFF), (byte)(paramInt & 0xFF) };
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecMessageBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */