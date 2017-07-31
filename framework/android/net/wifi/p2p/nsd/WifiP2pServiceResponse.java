package android.net.wifi.p2p.nsd;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WifiP2pServiceResponse
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pServiceResponse> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pServiceResponse createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramAnonymousParcel.readParcelable(null);
      int m = paramAnonymousParcel.readInt();
      byte[] arrayOfByte = null;
      if (m > 0)
      {
        arrayOfByte = new byte[m];
        paramAnonymousParcel.readByteArray(arrayOfByte);
      }
      if (i == 1) {
        return WifiP2pDnsSdServiceResponse.newInstance(j, k, localWifiP2pDevice, arrayOfByte);
      }
      if (i == 2) {
        return WifiP2pUpnpServiceResponse.newInstance(j, k, localWifiP2pDevice, arrayOfByte);
      }
      return new WifiP2pServiceResponse(i, j, k, localWifiP2pDevice, arrayOfByte);
    }
    
    public WifiP2pServiceResponse[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pServiceResponse[paramAnonymousInt];
    }
  };
  private static int MAX_BUF_SIZE = 1024;
  protected byte[] mData;
  protected WifiP2pDevice mDevice;
  protected int mServiceType;
  protected int mStatus;
  protected int mTransId;
  
  protected WifiP2pServiceResponse(int paramInt1, int paramInt2, int paramInt3, WifiP2pDevice paramWifiP2pDevice, byte[] paramArrayOfByte)
  {
    this.mServiceType = paramInt1;
    this.mStatus = paramInt2;
    this.mTransId = paramInt3;
    this.mDevice = paramWifiP2pDevice;
    this.mData = paramArrayOfByte;
  }
  
  private boolean equals(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) && (paramObject2 == null)) {
      return true;
    }
    if (paramObject1 != null) {
      return paramObject1.equals(paramObject2);
    }
    return false;
  }
  
  private static byte[] hexStr2Bin(String paramString)
  {
    int j = paramString.length() / 2;
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    int i = 0;
    while (i < j) {
      try
      {
        arrayOfByte[i] = ((byte)Integer.parseInt(paramString.substring(i * 2, i * 2 + 2), 16));
        i += 1;
      }
      catch (Exception paramString)
      {
        paramString.printStackTrace();
        return null;
      }
    }
    return arrayOfByte;
  }
  
  public static List<WifiP2pServiceResponse> newInstance(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    paramString = paramString.split(" ");
    if (paramString.length != 4) {
      return null;
    }
    WifiP2pDevice localWifiP2pDevice = new WifiP2pDevice();
    localWifiP2pDevice.deviceAddress = paramString[1];
    paramString = hexStr2Bin(paramString[3]);
    if (paramString == null) {
      return null;
    }
    DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(paramString));
    try
    {
      for (;;)
      {
        if (localDataInputStream.available() <= 0) {
          break label275;
        }
        i = localDataInputStream.readUnsignedByte() + (localDataInputStream.readUnsignedByte() << 8) - 3;
        j = localDataInputStream.readUnsignedByte();
        k = localDataInputStream.readUnsignedByte();
        m = localDataInputStream.readUnsignedByte();
        if (i < 0) {
          return null;
        }
        if (i != 0) {
          break;
        }
        if (m == 0) {
          localArrayList.add(new WifiP2pServiceResponse(j, m, k, localWifiP2pDevice, null));
        }
      }
    }
    catch (IOException paramString)
    {
      int i;
      int j;
      int k;
      int m;
      for (;;)
      {
        paramString.printStackTrace();
        if (localArrayList.size() <= 0) {
          break label278;
        }
        return localArrayList;
        if (i <= MAX_BUF_SIZE) {
          break;
        }
        localDataInputStream.skip(i);
      }
      paramString = new byte[i];
      localDataInputStream.readFully(paramString);
      if (j == 1) {
        paramString = WifiP2pDnsSdServiceResponse.newInstance(m, k, localWifiP2pDevice, paramString);
      }
      while ((paramString != null) && (paramString.getStatus() == 0))
      {
        localArrayList.add(paramString);
        break;
        if (j == 2) {
          paramString = WifiP2pUpnpServiceResponse.newInstance(m, k, localWifiP2pDevice, paramString);
        } else {
          paramString = new WifiP2pServiceResponse(j, m, k, localWifiP2pDevice, paramString);
        }
      }
      label275:
      return localArrayList;
    }
    label278:
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof WifiP2pServiceResponse)) {
      return false;
    }
    boolean bool1 = bool2;
    if (((WifiP2pServiceResponse)paramObject).mServiceType == this.mServiceType)
    {
      bool1 = bool2;
      if (((WifiP2pServiceResponse)paramObject).mStatus == this.mStatus)
      {
        bool1 = bool2;
        if (equals(((WifiP2pServiceResponse)paramObject).mDevice.deviceAddress, this.mDevice.deviceAddress)) {
          bool1 = Arrays.equals(((WifiP2pServiceResponse)paramObject).mData, this.mData);
        }
      }
    }
    return bool1;
  }
  
  public byte[] getRawData()
  {
    return this.mData;
  }
  
  public int getServiceType()
  {
    return this.mServiceType;
  }
  
  public WifiP2pDevice getSrcDevice()
  {
    return this.mDevice;
  }
  
  public int getStatus()
  {
    return this.mStatus;
  }
  
  public int getTransactionId()
  {
    return this.mTransId;
  }
  
  public int hashCode()
  {
    int j = 0;
    int k = this.mServiceType;
    int m = this.mStatus;
    int n = this.mTransId;
    int i;
    if (this.mDevice.deviceAddress == null)
    {
      i = 0;
      if (this.mData != null) {
        break label80;
      }
    }
    for (;;)
    {
      return ((((k + 527) * 31 + m) * 31 + n) * 31 + i) * 31 + j;
      i = this.mDevice.deviceAddress.hashCode();
      break;
      label80:
      j = Arrays.hashCode(this.mData);
    }
  }
  
  public void setSrcDevice(WifiP2pDevice paramWifiP2pDevice)
  {
    if (paramWifiP2pDevice == null) {
      return;
    }
    this.mDevice = paramWifiP2pDevice;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("serviceType:").append(this.mServiceType);
    localStringBuffer.append(" status:").append(Status.toString(this.mStatus));
    localStringBuffer.append(" srcAddr:").append(this.mDevice.deviceAddress);
    localStringBuffer.append(" data:").append(Arrays.toString(this.mData));
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mServiceType);
    paramParcel.writeInt(this.mStatus);
    paramParcel.writeInt(this.mTransId);
    paramParcel.writeParcelable(this.mDevice, paramInt);
    if ((this.mData == null) || (this.mData.length == 0))
    {
      paramParcel.writeInt(0);
      return;
    }
    paramParcel.writeInt(this.mData.length);
    paramParcel.writeByteArray(this.mData);
  }
  
  public static class Status
  {
    public static final int BAD_REQUEST = 3;
    public static final int REQUESTED_INFORMATION_NOT_AVAILABLE = 2;
    public static final int SERVICE_PROTOCOL_NOT_AVAILABLE = 1;
    public static final int SUCCESS = 0;
    
    public static String toString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN";
      case 0: 
        return "SUCCESS";
      case 1: 
        return "SERVICE_PROTOCOL_NOT_AVAILABLE";
      case 2: 
        return "REQUESTED_INFORMATION_NOT_AVAILABLE";
      }
      return "BAD_REQUEST";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pServiceResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */