package android.net.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class NsdServiceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<NsdServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public NsdServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      NsdServiceInfo localNsdServiceInfo = new NsdServiceInfo();
      NsdServiceInfo.-set2(localNsdServiceInfo, paramAnonymousParcel.readString());
      NsdServiceInfo.-set3(localNsdServiceInfo, paramAnonymousParcel.readString());
      if (paramAnonymousParcel.readInt() == 1) {}
      try
      {
        NsdServiceInfo.-set0(localNsdServiceInfo, InetAddress.getByAddress(paramAnonymousParcel.createByteArray()));
        NsdServiceInfo.-set1(localNsdServiceInfo, paramAnonymousParcel.readInt());
        int j = paramAnonymousParcel.readInt();
        int i = 0;
        while (i < j)
        {
          byte[] arrayOfByte = null;
          if (paramAnonymousParcel.readInt() == 1)
          {
            arrayOfByte = new byte[paramAnonymousParcel.readInt()];
            paramAnonymousParcel.readByteArray(arrayOfByte);
          }
          NsdServiceInfo.-get0(localNsdServiceInfo).put(paramAnonymousParcel.readString(), arrayOfByte);
          i += 1;
        }
        return localNsdServiceInfo;
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;) {}
      }
    }
    
    public NsdServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new NsdServiceInfo[paramAnonymousInt];
    }
  };
  private static final String TAG = "NsdServiceInfo";
  private InetAddress mHost;
  private int mPort;
  private String mServiceName;
  private String mServiceType;
  private final ArrayMap<String, byte[]> mTxtRecord = new ArrayMap();
  
  public NsdServiceInfo() {}
  
  public NsdServiceInfo(String paramString1, String paramString2)
  {
    this.mServiceName = paramString1;
    this.mServiceType = paramString2;
  }
  
  private int getTxtRecordSize()
  {
    int i = 0;
    Iterator localIterator = this.mTxtRecord.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      int k = ((String)((Map.Entry)localObject).getKey()).length();
      localObject = (byte[])((Map.Entry)localObject).getValue();
      if (localObject == null) {}
      for (int j = 0;; j = localObject.length)
      {
        i = i + 2 + k + j;
        break;
      }
    }
    return i;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Map<String, byte[]> getAttributes()
  {
    return Collections.unmodifiableMap(this.mTxtRecord);
  }
  
  public InetAddress getHost()
  {
    return this.mHost;
  }
  
  public int getPort()
  {
    return this.mPort;
  }
  
  public String getServiceName()
  {
    return this.mServiceName;
  }
  
  public String getServiceType()
  {
    return this.mServiceType;
  }
  
  public byte[] getTxtRecord()
  {
    int i = getTxtRecordSize();
    if (i == 0) {
      return new byte[0];
    }
    byte[] arrayOfByte = new byte[i];
    i = 0;
    Iterator localIterator = this.mTxtRecord.entrySet().iterator();
    label183:
    for (;;)
    {
      if (localIterator.hasNext())
      {
        Object localObject = (Map.Entry)localIterator.next();
        String str = (String)((Map.Entry)localObject).getKey();
        localObject = (byte[])((Map.Entry)localObject).getValue();
        int k = i + 1;
        int m = str.length();
        if (localObject == null) {}
        for (int j = 0;; j = localObject.length)
        {
          arrayOfByte[i] = ((byte)(j + m + 1));
          System.arraycopy(str.getBytes(StandardCharsets.US_ASCII), 0, arrayOfByte, k, str.length());
          j = k + str.length();
          i = j + 1;
          arrayOfByte[j] = 61;
          if (localObject == null) {
            break label183;
          }
          System.arraycopy((byte[])localObject, 0, arrayOfByte, i, localObject.length);
          i += localObject.length;
          break;
        }
      }
      return arrayOfByte;
    }
  }
  
  public void removeAttribute(String paramString)
  {
    this.mTxtRecord.remove(paramString);
  }
  
  public void setAttribute(String paramString1, String paramString2)
  {
    if (paramString2 == null) {}
    for (paramString2 = null;; paramString2 = paramString2.getBytes("UTF-8")) {
      try
      {
        setAttribute(paramString1, paramString2);
        return;
      }
      catch (UnsupportedEncodingException paramString1)
      {
        throw new IllegalArgumentException("Value must be UTF-8");
      }
    }
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfByte)
  {
    int j = 0;
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Key cannot be empty");
    }
    int i = 0;
    while (i < paramString.length())
    {
      k = paramString.charAt(i);
      if ((k < 32) || (k > 126)) {
        throw new IllegalArgumentException("Key strings must be printable US-ASCII");
      }
      if (k == 61) {
        throw new IllegalArgumentException("Key strings must not include '='");
      }
      i += 1;
    }
    int k = paramString.length();
    if (paramArrayOfByte == null) {}
    for (i = 0; i + k >= 255; i = paramArrayOfByte.length) {
      throw new IllegalArgumentException("Key length + value length must be < 255 bytes");
    }
    if (paramString.length() > 9) {
      Log.w("NsdServiceInfo", "Key lengths > 9 are discouraged: " + paramString);
    }
    k = getTxtRecordSize();
    int m = paramString.length();
    if (paramArrayOfByte == null) {}
    for (i = j;; i = paramArrayOfByte.length)
    {
      i = i + (m + k) + 2;
      if (i <= 1300) {
        break;
      }
      throw new IllegalArgumentException("Total length of attributes must be < 1300 bytes");
    }
    if (i > 400) {
      Log.w("NsdServiceInfo", "Total length of all attributes exceeds 400 bytes; truncation may occur");
    }
    this.mTxtRecord.put(paramString, paramArrayOfByte);
  }
  
  public void setHost(InetAddress paramInetAddress)
  {
    this.mHost = paramInetAddress;
  }
  
  public void setPort(int paramInt)
  {
    this.mPort = paramInt;
  }
  
  public void setServiceName(String paramString)
  {
    this.mServiceName = paramString;
  }
  
  public void setServiceType(String paramString)
  {
    this.mServiceType = paramString;
  }
  
  public void setTxtRecords(String paramString)
  {
    byte[] arrayOfByte = Base64.decode(paramString, 0);
    int i = 0;
    int k;
    int n;
    int j;
    while (i < arrayOfByte.length)
    {
      k = arrayOfByte[i] & 0xFF;
      n = i + 1;
      if (k == 0)
      {
        j = k;
        try
        {
          throw new IllegalArgumentException("Zero sized txt record");
        }
        catch (IllegalArgumentException paramString)
        {
          Log.e("NsdServiceInfo", "While parsing txt records (pos = " + n + "): " + paramString.getMessage());
          i = j;
        }
        i = n + i;
      }
      else
      {
        j = k;
        i = k;
        if (n + k <= arrayOfByte.length) {
          break label368;
        }
        j = k;
        Log.w("NsdServiceInfo", "Corrupt record length (pos = " + n + "): " + k);
        j = k;
        i = arrayOfByte.length - n;
        break label368;
      }
    }
    Object localObject;
    int m;
    for (;;)
    {
      if (k < n + i)
      {
        if (str == null)
        {
          localObject = paramString;
          j = m;
          if (arrayOfByte[k] != 61) {
            break label383;
          }
          j = i;
          str = new String(arrayOfByte, n, k - n, StandardCharsets.US_ASCII);
          localObject = paramString;
          j = m;
          break label383;
        }
        localObject = paramString;
        if (paramString != null) {
          break label398;
        }
        j = i;
        localObject = new byte[i - str.length() - 1];
        break label398;
      }
      localObject = str;
      if (str == null)
      {
        j = i;
        localObject = new String(arrayOfByte, n, i, StandardCharsets.US_ASCII);
      }
      j = i;
      if (TextUtils.isEmpty((CharSequence)localObject))
      {
        j = i;
        throw new IllegalArgumentException("Invalid txt record (key is empty)");
      }
      j = i;
      if (getAttributes().containsKey(localObject))
      {
        j = i;
        throw new IllegalArgumentException("Invalid txt record (duplicate key \"" + (String)localObject + "\")");
      }
      j = i;
      setAttribute((String)localObject, paramString);
      break;
      return;
      label368:
      String str = null;
      paramString = null;
      m = 0;
      k = n;
    }
    for (;;)
    {
      label383:
      k += 1;
      paramString = (String)localObject;
      m = j;
      break;
      label398:
      localObject[m] = arrayOfByte[k];
      j = m + 1;
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("name: ").append(this.mServiceName).append(", type: ").append(this.mServiceType).append(", host: ").append(this.mHost).append(", port: ").append(this.mPort);
    byte[] arrayOfByte = getTxtRecord();
    if (arrayOfByte != null) {
      localStringBuffer.append(", txtRecord: ").append(new String(arrayOfByte, StandardCharsets.UTF_8));
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mServiceName);
    paramParcel.writeString(this.mServiceType);
    label71:
    String str;
    if (this.mHost != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeByteArray(this.mHost.getAddress());
      paramParcel.writeInt(this.mPort);
      paramParcel.writeInt(this.mTxtRecord.size());
      Iterator localIterator = this.mTxtRecord.keySet().iterator();
      if (!localIterator.hasNext()) {
        return;
      }
      str = (String)localIterator.next();
      byte[] arrayOfByte = (byte[])this.mTxtRecord.get(str);
      if (arrayOfByte == null) {
        break label145;
      }
      paramParcel.writeInt(1);
      paramParcel.writeInt(arrayOfByte.length);
      paramParcel.writeByteArray(arrayOfByte);
    }
    for (;;)
    {
      paramParcel.writeString(str);
      break label71;
      paramParcel.writeInt(0);
      break;
      label145:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/nsd/NsdServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */