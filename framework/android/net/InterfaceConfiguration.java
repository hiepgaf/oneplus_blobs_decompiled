package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.collect.Sets;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;

public class InterfaceConfiguration
  implements Parcelable
{
  public static final Parcelable.Creator<InterfaceConfiguration> CREATOR = new Parcelable.Creator()
  {
    public InterfaceConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      InterfaceConfiguration localInterfaceConfiguration = new InterfaceConfiguration();
      InterfaceConfiguration.-set1(localInterfaceConfiguration, paramAnonymousParcel.readString());
      if (paramAnonymousParcel.readByte() == 1) {
        InterfaceConfiguration.-set0(localInterfaceConfiguration, (LinkAddress)paramAnonymousParcel.readParcelable(null));
      }
      int j = paramAnonymousParcel.readInt();
      int i = 0;
      while (i < j)
      {
        InterfaceConfiguration.-get0(localInterfaceConfiguration).add(paramAnonymousParcel.readString());
        i += 1;
      }
      return localInterfaceConfiguration;
    }
    
    public InterfaceConfiguration[] newArray(int paramAnonymousInt)
    {
      return new InterfaceConfiguration[paramAnonymousInt];
    }
  };
  private static final String FLAG_DOWN = "down";
  private static final String FLAG_UP = "up";
  private LinkAddress mAddr;
  private HashSet<String> mFlags = Sets.newHashSet();
  private String mHwAddr;
  
  private static void validateFlag(String paramString)
  {
    if (paramString.indexOf(' ') >= 0) {
      throw new IllegalArgumentException("flag contains space: " + paramString);
    }
  }
  
  public void clearFlag(String paramString)
  {
    validateFlag(paramString);
    this.mFlags.remove(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Iterable<String> getFlags()
  {
    return this.mFlags;
  }
  
  public String getHardwareAddress()
  {
    return this.mHwAddr;
  }
  
  public LinkAddress getLinkAddress()
  {
    return this.mAddr;
  }
  
  public boolean hasFlag(String paramString)
  {
    validateFlag(paramString);
    return this.mFlags.contains(paramString);
  }
  
  public boolean isActive()
  {
    try
    {
      if (hasFlag("up"))
      {
        byte[] arrayOfByte = this.mAddr.getAddress().getAddress();
        int j = arrayOfByte.length;
        int i = 0;
        while (i < j)
        {
          int k = arrayOfByte[i];
          if (k != 0) {
            return true;
          }
          i += 1;
        }
      }
      return false;
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
  }
  
  public void setFlag(String paramString)
  {
    validateFlag(paramString);
    this.mFlags.add(paramString);
  }
  
  public void setHardwareAddress(String paramString)
  {
    this.mHwAddr = paramString;
  }
  
  public void setInterfaceDown()
  {
    this.mFlags.remove("up");
    this.mFlags.add("down");
  }
  
  public void setInterfaceUp()
  {
    this.mFlags.remove("down");
    this.mFlags.add("up");
  }
  
  public void setLinkAddress(LinkAddress paramLinkAddress)
  {
    this.mAddr = paramLinkAddress;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("mHwAddr=").append(this.mHwAddr);
    localStringBuilder.append(" mAddr=").append(String.valueOf(this.mAddr));
    localStringBuilder.append(" mFlags=").append(getFlags());
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mHwAddr);
    if (this.mAddr != null)
    {
      paramParcel.writeByte((byte)1);
      paramParcel.writeParcelable(this.mAddr, paramInt);
    }
    for (;;)
    {
      paramParcel.writeInt(this.mFlags.size());
      Iterator localIterator = this.mFlags.iterator();
      while (localIterator.hasNext()) {
        paramParcel.writeString((String)localIterator.next());
      }
      paramParcel.writeByte((byte)0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/InterfaceConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */