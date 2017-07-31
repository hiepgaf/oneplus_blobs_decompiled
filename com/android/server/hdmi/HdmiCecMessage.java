package com.android.server.hdmi;

import java.util.Arrays;
import libcore.util.EmptyArray;

public final class HdmiCecMessage
{
  public static final byte[] EMPTY_PARAM = EmptyArray.BYTE;
  private final int mDestination;
  private final int mOpcode;
  private final byte[] mParams;
  private final int mSource;
  
  public HdmiCecMessage(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.mSource = paramInt1;
    this.mDestination = paramInt2;
    this.mOpcode = (paramInt3 & 0xFF);
    this.mParams = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  private static String opcodeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return String.format("Opcode: %02X", new Object[] { Integer.valueOf(paramInt) });
    case 0: 
      return "Feature Abort";
    case 4: 
      return "Image View On";
    case 5: 
      return "Tuner Step Increment";
    case 6: 
      return "Tuner Step Decrement";
    case 7: 
      return "Tuner Device Staus";
    case 8: 
      return "Give Tuner Device Status";
    case 9: 
      return "Record On";
    case 10: 
      return "Record Status";
    case 11: 
      return "Record Off";
    case 13: 
      return "Text View On";
    case 15: 
      return "Record Tv Screen";
    case 26: 
      return "Give Deck Status";
    case 27: 
      return "Deck Status";
    case 50: 
      return "Set Menu Language";
    case 51: 
      return "Clear Analog Timer";
    case 52: 
      return "Set Analog Timer";
    case 53: 
      return "Timer Status";
    case 54: 
      return "Standby";
    case 65: 
      return "Play";
    case 66: 
      return "Deck Control";
    case 67: 
      return "Timer Cleared Status";
    case 68: 
      return "User Control Pressed";
    case 69: 
      return "User Control Release";
    case 70: 
      return "Give Osd Name";
    case 71: 
      return "Set Osd Name";
    case 100: 
      return "Set Osd String";
    case 103: 
      return "Set Timer Program Title";
    case 112: 
      return "System Audio Mode Request";
    case 113: 
      return "Give Audio Status";
    case 114: 
      return "Set System Audio Mode";
    case 122: 
      return "Report Audio Status";
    case 125: 
      return "Give System Audio Mode Status";
    case 126: 
      return "System Audio Mode Status";
    case 128: 
      return "Routing Change";
    case 129: 
      return "Routing Information";
    case 130: 
      return "Active Source";
    case 131: 
      return "Give Physical Address";
    case 132: 
      return "Report Physical Address";
    case 133: 
      return "Request Active Source";
    case 134: 
      return "Set Stream Path";
    case 135: 
      return "Device Vendor Id";
    case 137: 
      return "Vendor Commandn";
    case 138: 
      return "Vendor Remote Button Down";
    case 139: 
      return "Vendor Remote Button Up";
    case 140: 
      return "Give Device Vendor Id";
    case 141: 
      return "Menu REquest";
    case 142: 
      return "Menu Status";
    case 143: 
      return "Give Device Power Status";
    case 144: 
      return "Report Power Status";
    case 145: 
      return "Get Menu Language";
    case 146: 
      return "Select Analog Service";
    case 147: 
      return "Select Digital Service";
    case 151: 
      return "Set Digital Timer";
    case 153: 
      return "Clear Digital Timer";
    case 154: 
      return "Set Audio Rate";
    case 157: 
      return "InActive Source";
    case 158: 
      return "Cec Version";
    case 159: 
      return "Get Cec Version";
    case 160: 
      return "Vendor Command With Id";
    case 161: 
      return "Clear External Timer";
    case 162: 
      return "Set External Timer";
    case 163: 
      return "Repot Short Audio Descriptor";
    case 164: 
      return "Request Short Audio Descriptor";
    case 192: 
      return "Initiate ARC";
    case 193: 
      return "Report ARC Initiated";
    case 194: 
      return "Report ARC Terminated";
    case 195: 
      return "Request ARC Initiation";
    case 196: 
      return "Request ARC Termination";
    case 197: 
      return "Terminate ARC";
    case 248: 
      return "Cdc Message";
    }
    return "Abort";
  }
  
  public int getDestination()
  {
    return this.mDestination;
  }
  
  public int getOpcode()
  {
    return this.mOpcode;
  }
  
  public byte[] getParams()
  {
    return this.mParams;
  }
  
  public int getSource()
  {
    return this.mSource;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(String.format("<%s> src: %d, dst: %d", new Object[] { opcodeToString(this.mOpcode), Integer.valueOf(this.mSource), Integer.valueOf(this.mDestination) }));
    if (this.mParams.length > 0)
    {
      localStringBuffer.append(", params:");
      byte[] arrayOfByte = this.mParams;
      int j = arrayOfByte.length;
      int i = 0;
      while (i < j)
      {
        localStringBuffer.append(String.format(" %02X", new Object[] { Byte.valueOf(arrayOfByte[i]) }));
        i += 1;
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */