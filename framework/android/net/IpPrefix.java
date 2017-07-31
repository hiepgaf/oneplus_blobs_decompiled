package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Pair;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public final class IpPrefix
  implements Parcelable
{
  public static final Parcelable.Creator<IpPrefix> CREATOR = new Parcelable.Creator()
  {
    public IpPrefix createFromParcel(Parcel paramAnonymousParcel)
    {
      return new IpPrefix(paramAnonymousParcel.createByteArray(), paramAnonymousParcel.readInt());
    }
    
    public IpPrefix[] newArray(int paramAnonymousInt)
    {
      return new IpPrefix[paramAnonymousInt];
    }
  };
  private final byte[] address;
  private final int prefixLength;
  
  public IpPrefix(String paramString)
  {
    paramString = NetworkUtils.parseIpAndMask(paramString);
    this.address = ((InetAddress)paramString.first).getAddress();
    this.prefixLength = ((Integer)paramString.second).intValue();
    checkAndMaskAddressAndPrefixLength();
  }
  
  public IpPrefix(InetAddress paramInetAddress, int paramInt)
  {
    this.address = paramInetAddress.getAddress();
    this.prefixLength = paramInt;
    checkAndMaskAddressAndPrefixLength();
  }
  
  public IpPrefix(byte[] paramArrayOfByte, int paramInt)
  {
    this.address = ((byte[])paramArrayOfByte.clone());
    this.prefixLength = paramInt;
    checkAndMaskAddressAndPrefixLength();
  }
  
  private void checkAndMaskAddressAndPrefixLength()
  {
    if ((this.address.length != 4) && (this.address.length != 16)) {
      throw new IllegalArgumentException("IpPrefix has " + this.address.length + " bytes which is neither 4 nor 16");
    }
    NetworkUtils.maskRawAddress(this.address, this.prefixLength);
  }
  
  public boolean contains(InetAddress paramInetAddress)
  {
    Object localObject = null;
    if (paramInetAddress == null) {}
    for (paramInetAddress = (InetAddress)localObject; (paramInetAddress == null) || (paramInetAddress.length != this.address.length); paramInetAddress = paramInetAddress.getAddress()) {
      return false;
    }
    NetworkUtils.maskRawAddress(paramInetAddress, this.prefixLength);
    return Arrays.equals(this.address, paramInetAddress);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof IpPrefix)) {
      return false;
    }
    paramObject = (IpPrefix)paramObject;
    boolean bool1 = bool2;
    if (Arrays.equals(this.address, ((IpPrefix)paramObject).address))
    {
      bool1 = bool2;
      if (this.prefixLength == ((IpPrefix)paramObject).prefixLength) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public InetAddress getAddress()
  {
    try
    {
      InetAddress localInetAddress = InetAddress.getByAddress(this.address);
      return localInetAddress;
    }
    catch (UnknownHostException localUnknownHostException) {}
    return null;
  }
  
  public int getPrefixLength()
  {
    return this.prefixLength;
  }
  
  public byte[] getRawAddress()
  {
    return (byte[])this.address.clone();
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.address) + this.prefixLength * 11;
  }
  
  public String toString()
  {
    try
    {
      String str = InetAddress.getByAddress(this.address).getHostAddress() + "/" + this.prefixLength;
      return str;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw new IllegalStateException("IpPrefix with invalid address! Shouldn't happen.", localUnknownHostException);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.address);
    paramParcel.writeInt(this.prefixLength);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/IpPrefix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */