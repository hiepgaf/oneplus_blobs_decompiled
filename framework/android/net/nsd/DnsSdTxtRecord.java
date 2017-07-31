package android.net.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class DnsSdTxtRecord
  implements Parcelable
{
  public static final Parcelable.Creator<DnsSdTxtRecord> CREATOR = new Parcelable.Creator()
  {
    public DnsSdTxtRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      DnsSdTxtRecord localDnsSdTxtRecord = new DnsSdTxtRecord();
      paramAnonymousParcel.readByteArray(DnsSdTxtRecord.-get0(localDnsSdTxtRecord));
      return localDnsSdTxtRecord;
    }
    
    public DnsSdTxtRecord[] newArray(int paramAnonymousInt)
    {
      return new DnsSdTxtRecord[paramAnonymousInt];
    }
  };
  private static final byte mSeperator = 61;
  private byte[] mData;
  
  public DnsSdTxtRecord()
  {
    this.mData = new byte[0];
  }
  
  public DnsSdTxtRecord(DnsSdTxtRecord paramDnsSdTxtRecord)
  {
    if ((paramDnsSdTxtRecord != null) && (paramDnsSdTxtRecord.mData != null)) {
      this.mData = ((byte[])paramDnsSdTxtRecord.mData.clone());
    }
  }
  
  public DnsSdTxtRecord(byte[] paramArrayOfByte)
  {
    this.mData = ((byte[])paramArrayOfByte.clone());
  }
  
  private String getKey(int paramInt)
  {
    int i = 0;
    int j = 0;
    while ((j < paramInt) && (i < this.mData.length))
    {
      i += this.mData[i] + 1;
      j += 1;
    }
    if (i < this.mData.length)
    {
      j = this.mData[i];
      paramInt = 0;
      for (;;)
      {
        if ((paramInt >= j) || (this.mData[(i + paramInt + 1)] == 61)) {
          return new String(this.mData, i + 1, paramInt);
        }
        paramInt += 1;
      }
    }
    return null;
  }
  
  private byte[] getValue(int paramInt)
  {
    int i = 0;
    Object localObject2 = null;
    int j = 0;
    while ((j < paramInt) && (i < this.mData.length))
    {
      i += this.mData[i] + 1;
      j += 1;
    }
    Object localObject1 = localObject2;
    if (i < this.mData.length)
    {
      j = this.mData[i];
      paramInt = 0;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (paramInt < j)
      {
        if (this.mData[(i + paramInt + 1)] == 61)
        {
          localObject1 = new byte[j - paramInt - 1];
          System.arraycopy(this.mData, i + paramInt + 2, (byte[])localObject1, 0, j - paramInt - 1);
        }
      }
      else {
        return (byte[])localObject1;
      }
      paramInt += 1;
    }
  }
  
  private byte[] getValue(String paramString)
  {
    int i = 0;
    for (;;)
    {
      String str = getKey(i);
      if (str == null) {
        break;
      }
      if (paramString.compareToIgnoreCase(str) == 0) {
        return getValue(i);
      }
      i += 1;
    }
    return null;
  }
  
  private String getValueAsString(int paramInt)
  {
    String str = null;
    byte[] arrayOfByte = getValue(paramInt);
    if (arrayOfByte != null) {
      str = new String(arrayOfByte);
    }
    return str;
  }
  
  private void insert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    byte[] arrayOfByte = this.mData;
    if (paramArrayOfByte2 != null) {}
    int j;
    for (int i = paramArrayOfByte2.length;; i = 0)
    {
      j = 0;
      k = 0;
      while ((k < paramInt) && (j < this.mData.length))
      {
        j += (this.mData[j] + 1 & 0xFF);
        k += 1;
      }
    }
    int k = paramArrayOfByte1.length;
    if (paramArrayOfByte2 != null) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramInt = k + i + paramInt;
      k = arrayOfByte.length + paramInt + 1;
      this.mData = new byte[k];
      System.arraycopy(arrayOfByte, 0, this.mData, 0, j);
      int m = arrayOfByte.length - j;
      System.arraycopy(arrayOfByte, j, this.mData, k - m, m);
      this.mData[j] = ((byte)paramInt);
      System.arraycopy(paramArrayOfByte1, 0, this.mData, j + 1, paramArrayOfByte1.length);
      if (paramArrayOfByte2 != null)
      {
        this.mData[(j + 1 + paramArrayOfByte1.length)] = 61;
        System.arraycopy(paramArrayOfByte2, 0, this.mData, paramArrayOfByte1.length + j + 2, i);
      }
      return;
    }
  }
  
  public boolean contains(String paramString)
  {
    int i = 0;
    for (;;)
    {
      String str = getKey(i);
      if (str == null) {
        break;
      }
      if (paramString.compareToIgnoreCase(str) == 0) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof DnsSdTxtRecord)) {
      return false;
    }
    return Arrays.equals(((DnsSdTxtRecord)paramObject).mData, this.mData);
  }
  
  public String get(String paramString)
  {
    Object localObject = null;
    byte[] arrayOfByte = getValue(paramString);
    paramString = (String)localObject;
    if (arrayOfByte != null) {
      paramString = new String(arrayOfByte);
    }
    return paramString;
  }
  
  public byte[] getRawData()
  {
    return (byte[])this.mData.clone();
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.mData);
  }
  
  public int keyCount()
  {
    int i = 0;
    int j = 0;
    while (j < this.mData.length)
    {
      j += (this.mData[j] + 1 & 0xFF);
      i += 1;
    }
    return i;
  }
  
  public int remove(String paramString)
  {
    int j = 0;
    int i = 0;
    while (j < this.mData.length)
    {
      int k = this.mData[j];
      if ((paramString.length() <= k) && ((paramString.length() == k) || (this.mData[(paramString.length() + j + 1)] == 61)) && (paramString.compareToIgnoreCase(new String(this.mData, j + 1, paramString.length())) == 0))
      {
        paramString = this.mData;
        this.mData = new byte[paramString.length - k - 1];
        System.arraycopy(paramString, 0, this.mData, 0, j);
        System.arraycopy(paramString, j + k + 1, this.mData, j, paramString.length - j - k - 1);
        return i;
      }
      j += (k + 1 & 0xFF);
      i += 1;
    }
    return -1;
  }
  
  public void set(String paramString1, String paramString2)
  {
    if (paramString2 != null)
    {
      paramString2 = paramString2.getBytes();
      i = paramString2.length;
    }
    byte[] arrayOfByte;
    for (;;)
    {
      try
      {
        arrayOfByte = paramString1.getBytes("US-ASCII");
        j = 0;
        if (j >= arrayOfByte.length) {
          break;
        }
        if (arrayOfByte[j] != 61) {
          break label69;
        }
        throw new IllegalArgumentException("= is not a valid character in key");
      }
      catch (UnsupportedEncodingException paramString1)
      {
        throw new IllegalArgumentException("key should be US-ASCII");
      }
      paramString2 = null;
      i = 0;
      continue;
      label69:
      j += 1;
    }
    if (arrayOfByte.length + i >= 255) {
      throw new IllegalArgumentException("Key and Value length cannot exceed 255 bytes");
    }
    int j = remove(paramString1);
    int i = j;
    if (j == -1) {
      i = keyCount();
    }
    insert(arrayOfByte, paramString2, i);
  }
  
  public int size()
  {
    return this.mData.length;
  }
  
  public String toString()
  {
    Object localObject = null;
    int i = 0;
    String str1 = getKey(i);
    if (str1 != null)
    {
      str1 = "{" + str1;
      String str2 = getValueAsString(i);
      if (str2 != null)
      {
        str1 = str1 + "=" + str2 + "}";
        label76:
        if (localObject != null) {
          break label112;
        }
      }
      for (;;)
      {
        i += 1;
        localObject = str1;
        break;
        str1 = str1 + "}";
        break label76;
        label112:
        str1 = (String)localObject + ", " + str1;
      }
    }
    if (localObject != null) {
      return (String)localObject;
    }
    return "";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.mData);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/nsd/DnsSdTxtRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */