package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;

public final class UsbPort
  implements Parcelable
{
  public static final Parcelable.Creator<UsbPort> CREATOR = new Parcelable.Creator()
  {
    public UsbPort createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UsbPort(paramAnonymousParcel.readString(), paramAnonymousParcel.readInt());
    }
    
    public UsbPort[] newArray(int paramAnonymousInt)
    {
      return new UsbPort[paramAnonymousInt];
    }
  };
  public static final int DATA_ROLE_DEVICE = 2;
  public static final int DATA_ROLE_HOST = 1;
  public static final int MODE_DFP = 1;
  public static final int MODE_DUAL = 3;
  public static final int MODE_UFP = 2;
  private static final int NUM_DATA_ROLES = 3;
  public static final int POWER_ROLE_SINK = 2;
  public static final int POWER_ROLE_SOURCE = 1;
  private final String mId;
  private final int mSupportedModes;
  
  public UsbPort(String paramString, int paramInt)
  {
    this.mId = paramString;
    this.mSupportedModes = paramInt;
  }
  
  public static void checkRoles(int paramInt1, int paramInt2)
  {
    Preconditions.checkArgumentInRange(paramInt1, 0, 2, "powerRole");
    Preconditions.checkArgumentInRange(paramInt2, 0, 2, "dataRole");
  }
  
  public static int combineRolesAsBit(int paramInt1, int paramInt2)
  {
    checkRoles(paramInt1, paramInt2);
    return 1 << paramInt1 * 3 + paramInt2;
  }
  
  public static String dataRoleToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "no-data";
    case 1: 
      return "host";
    }
    return "device";
  }
  
  public static String modeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "none";
    case 1: 
      return "dfp";
    case 2: 
      return "ufp";
    }
    return "dual";
  }
  
  public static String powerRoleToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "no-power";
    case 1: 
      return "source";
    }
    return "sink";
  }
  
  public static String roleCombinationsToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    int j = 1;
    int i = paramInt;
    paramInt = j;
    if (i != 0)
    {
      j = Integer.numberOfTrailingZeros(i);
      i &= 1 << j;
      int k = j / 3;
      if (paramInt != 0) {
        paramInt = 0;
      }
      for (;;)
      {
        localStringBuilder.append(powerRoleToString(k));
        localStringBuilder.append(':');
        localStringBuilder.append(dataRoleToString(j % 3));
        break;
        localStringBuilder.append(", ");
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public int getSupportedModes()
  {
    return this.mSupportedModes;
  }
  
  public String toString()
  {
    return "UsbPort{id=" + this.mId + ", supportedModes=" + modeToString(this.mSupportedModes) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mId);
    paramParcel.writeInt(this.mSupportedModes);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */