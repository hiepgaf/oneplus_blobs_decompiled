package android.hardware.hdmi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class HdmiDeviceInfo
  implements Parcelable
{
  public static final int ADDR_INTERNAL = 0;
  public static final Parcelable.Creator<HdmiDeviceInfo> CREATOR = new Parcelable.Creator()
  {
    public HdmiDeviceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      int k = paramAnonymousParcel.readInt();
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      switch (k)
      {
      default: 
        return null;
      case 0: 
        k = paramAnonymousParcel.readInt();
        int m = paramAnonymousParcel.readInt();
        int n = paramAnonymousParcel.readInt();
        int i1 = paramAnonymousParcel.readInt();
        return new HdmiDeviceInfo(k, i, j, m, n, paramAnonymousParcel.readString(), i1);
      case 1: 
        k = paramAnonymousParcel.readInt();
        return new HdmiDeviceInfo(i, j, paramAnonymousParcel.readInt(), k);
      case 2: 
        return new HdmiDeviceInfo(i, j);
      }
      return HdmiDeviceInfo.INACTIVE_DEVICE;
    }
    
    public HdmiDeviceInfo[] newArray(int paramAnonymousInt)
    {
      return new HdmiDeviceInfo[paramAnonymousInt];
    }
  };
  public static final int DEVICE_AUDIO_SYSTEM = 5;
  public static final int DEVICE_INACTIVE = -1;
  public static final int DEVICE_PLAYBACK = 4;
  public static final int DEVICE_PURE_CEC_SWITCH = 6;
  public static final int DEVICE_RECORDER = 1;
  public static final int DEVICE_RESERVED = 2;
  public static final int DEVICE_TUNER = 3;
  public static final int DEVICE_TV = 0;
  public static final int DEVICE_VIDEO_PROCESSOR = 7;
  private static final int HDMI_DEVICE_TYPE_CEC = 0;
  private static final int HDMI_DEVICE_TYPE_HARDWARE = 2;
  private static final int HDMI_DEVICE_TYPE_INACTIVE = 100;
  private static final int HDMI_DEVICE_TYPE_MHL = 1;
  public static final int ID_INVALID = 65535;
  private static final int ID_OFFSET_CEC = 0;
  private static final int ID_OFFSET_HARDWARE = 192;
  private static final int ID_OFFSET_MHL = 128;
  public static final HdmiDeviceInfo INACTIVE_DEVICE = new HdmiDeviceInfo();
  public static final int PATH_INTERNAL = 0;
  public static final int PATH_INVALID = 65535;
  public static final int PORT_INVALID = -1;
  private final int mAdopterId;
  private final int mDeviceId;
  private final int mDevicePowerStatus;
  private final int mDeviceType;
  private final String mDisplayName;
  private final int mHdmiDeviceType;
  private final int mId;
  private final int mLogicalAddress;
  private final int mPhysicalAddress;
  private final int mPortId;
  private final int mVendorId;
  
  public HdmiDeviceInfo()
  {
    this.mHdmiDeviceType = 100;
    this.mPhysicalAddress = 65535;
    this.mId = 65535;
    this.mLogicalAddress = -1;
    this.mDeviceType = -1;
    this.mPortId = -1;
    this.mDevicePowerStatus = -1;
    this.mDisplayName = "Inactive";
    this.mVendorId = 0;
    this.mDeviceId = -1;
    this.mAdopterId = -1;
  }
  
  public HdmiDeviceInfo(int paramInt1, int paramInt2)
  {
    this.mHdmiDeviceType = 2;
    this.mPhysicalAddress = paramInt1;
    this.mPortId = paramInt2;
    this.mId = idForHardware(paramInt2);
    this.mLogicalAddress = -1;
    this.mDeviceType = 2;
    this.mVendorId = 0;
    this.mDevicePowerStatus = -1;
    this.mDisplayName = ("HDMI" + paramInt2);
    this.mDeviceId = -1;
    this.mAdopterId = -1;
  }
  
  public HdmiDeviceInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mHdmiDeviceType = 1;
    this.mPhysicalAddress = paramInt1;
    this.mPortId = paramInt2;
    this.mId = idForMhlDevice(paramInt2);
    this.mLogicalAddress = -1;
    this.mDeviceType = 2;
    this.mVendorId = 0;
    this.mDevicePowerStatus = -1;
    this.mDisplayName = "Mobile";
    this.mDeviceId = paramInt3;
    this.mAdopterId = paramInt4;
  }
  
  public HdmiDeviceInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString, -1);
  }
  
  public HdmiDeviceInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString, int paramInt6)
  {
    this.mHdmiDeviceType = 0;
    this.mPhysicalAddress = paramInt2;
    this.mPortId = paramInt3;
    this.mId = idForCecDevice(paramInt1);
    this.mLogicalAddress = paramInt1;
    this.mDeviceType = paramInt4;
    this.mVendorId = paramInt5;
    this.mDevicePowerStatus = paramInt6;
    this.mDisplayName = paramString;
    this.mDeviceId = -1;
    this.mAdopterId = -1;
  }
  
  public static int idForCecDevice(int paramInt)
  {
    return paramInt + 0;
  }
  
  public static int idForHardware(int paramInt)
  {
    return paramInt + 192;
  }
  
  public static int idForMhlDevice(int paramInt)
  {
    return paramInt + 128;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof HdmiDeviceInfo)) {
      return false;
    }
    boolean bool1 = bool2;
    if (this.mHdmiDeviceType == ((HdmiDeviceInfo)paramObject).mHdmiDeviceType)
    {
      bool1 = bool2;
      if (this.mPhysicalAddress == ((HdmiDeviceInfo)paramObject).mPhysicalAddress)
      {
        bool1 = bool2;
        if (this.mPortId == ((HdmiDeviceInfo)paramObject).mPortId)
        {
          bool1 = bool2;
          if (this.mLogicalAddress == ((HdmiDeviceInfo)paramObject).mLogicalAddress)
          {
            bool1 = bool2;
            if (this.mDeviceType == ((HdmiDeviceInfo)paramObject).mDeviceType)
            {
              bool1 = bool2;
              if (this.mVendorId == ((HdmiDeviceInfo)paramObject).mVendorId)
              {
                bool1 = bool2;
                if (this.mDevicePowerStatus == ((HdmiDeviceInfo)paramObject).mDevicePowerStatus)
                {
                  bool1 = bool2;
                  if (this.mDisplayName.equals(((HdmiDeviceInfo)paramObject).mDisplayName))
                  {
                    bool1 = bool2;
                    if (this.mDeviceId == ((HdmiDeviceInfo)paramObject).mDeviceId)
                    {
                      bool1 = bool2;
                      if (this.mAdopterId == ((HdmiDeviceInfo)paramObject).mAdopterId) {
                        bool1 = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int getAdopterId()
  {
    return this.mAdopterId;
  }
  
  public int getDeviceId()
  {
    return this.mDeviceId;
  }
  
  public int getDevicePowerStatus()
  {
    return this.mDevicePowerStatus;
  }
  
  public int getDeviceType()
  {
    return this.mDeviceType;
  }
  
  public String getDisplayName()
  {
    return this.mDisplayName;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getLogicalAddress()
  {
    return this.mLogicalAddress;
  }
  
  public int getPhysicalAddress()
  {
    return this.mPhysicalAddress;
  }
  
  public int getPortId()
  {
    return this.mPortId;
  }
  
  public int getVendorId()
  {
    return this.mVendorId;
  }
  
  public boolean isCecDevice()
  {
    boolean bool = false;
    if (this.mHdmiDeviceType == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isInactivated()
  {
    return this.mHdmiDeviceType == 100;
  }
  
  public boolean isMhlDevice()
  {
    return this.mHdmiDeviceType == 1;
  }
  
  public boolean isSourceType()
  {
    if (isCecDevice())
    {
      if ((this.mDeviceType == 4) || (this.mDeviceType == 1)) {}
      while (this.mDeviceType == 3) {
        return true;
      }
      return false;
    }
    return isMhlDevice();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    switch (this.mHdmiDeviceType)
    {
    default: 
      return "";
    case 0: 
      localStringBuffer.append("CEC: ");
      localStringBuffer.append("logical_address: ").append(String.format("0x%02X", new Object[] { Integer.valueOf(this.mLogicalAddress) }));
      localStringBuffer.append(" ");
      localStringBuffer.append("device_type: ").append(this.mDeviceType).append(" ");
      localStringBuffer.append("vendor_id: ").append(this.mVendorId).append(" ");
      localStringBuffer.append("display_name: ").append(this.mDisplayName).append(" ");
      localStringBuffer.append("power_status: ").append(this.mDevicePowerStatus).append(" ");
    }
    for (;;)
    {
      localStringBuffer.append("physical_address: ").append(String.format("0x%04X", new Object[] { Integer.valueOf(this.mPhysicalAddress) }));
      localStringBuffer.append(" ");
      localStringBuffer.append("port_id: ").append(this.mPortId);
      return localStringBuffer.toString();
      localStringBuffer.append("MHL: ");
      localStringBuffer.append("device_id: ").append(String.format("0x%04X", new Object[] { Integer.valueOf(this.mDeviceId) })).append(" ");
      localStringBuffer.append("adopter_id: ").append(String.format("0x%04X", new Object[] { Integer.valueOf(this.mAdopterId) })).append(" ");
      continue;
      localStringBuffer.append("Hardware: ");
      continue;
      localStringBuffer.append("Inactivated: ");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mHdmiDeviceType);
    paramParcel.writeInt(this.mPhysicalAddress);
    paramParcel.writeInt(this.mPortId);
    switch (this.mHdmiDeviceType)
    {
    default: 
      return;
    case 0: 
      paramParcel.writeInt(this.mLogicalAddress);
      paramParcel.writeInt(this.mDeviceType);
      paramParcel.writeInt(this.mVendorId);
      paramParcel.writeInt(this.mDevicePowerStatus);
      paramParcel.writeString(this.mDisplayName);
      return;
    }
    paramParcel.writeInt(this.mDeviceId);
    paramParcel.writeInt(this.mAdopterId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiDeviceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */