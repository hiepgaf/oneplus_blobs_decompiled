package android.nfc;

import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcBarcode;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public final class Tag
  implements Parcelable
{
  public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator()
  {
    public Tag createFromParcel(Parcel paramAnonymousParcel)
    {
      byte[] arrayOfByte = Tag.readBytesWithNull(paramAnonymousParcel);
      int[] arrayOfInt = new int[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readIntArray(arrayOfInt);
      Bundle[] arrayOfBundle = (Bundle[])paramAnonymousParcel.createTypedArray(Bundle.CREATOR);
      int i = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readInt() == 0) {}
      for (paramAnonymousParcel = INfcTag.Stub.asInterface(paramAnonymousParcel.readStrongBinder());; paramAnonymousParcel = null) {
        return new Tag(arrayOfByte, arrayOfInt, arrayOfBundle, i, paramAnonymousParcel);
      }
    }
    
    public Tag[] newArray(int paramAnonymousInt)
    {
      return new Tag[paramAnonymousInt];
    }
  };
  int mConnectedTechnology;
  final byte[] mId;
  final int mServiceHandle;
  final INfcTag mTagService;
  final Bundle[] mTechExtras;
  final int[] mTechList;
  final String[] mTechStringList;
  
  public Tag(byte[] paramArrayOfByte, int[] paramArrayOfInt, Bundle[] paramArrayOfBundle, int paramInt, INfcTag paramINfcTag)
  {
    if (paramArrayOfInt == null) {
      throw new IllegalArgumentException("rawTargets cannot be null");
    }
    this.mId = paramArrayOfByte;
    this.mTechList = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.mTechStringList = generateTechStringList(paramArrayOfInt);
    this.mTechExtras = ((Bundle[])Arrays.copyOf(paramArrayOfBundle, paramArrayOfInt.length));
    this.mServiceHandle = paramInt;
    this.mTagService = paramINfcTag;
    this.mConnectedTechnology = -1;
  }
  
  public static Tag createMockTag(byte[] paramArrayOfByte, int[] paramArrayOfInt, Bundle[] paramArrayOfBundle)
  {
    return new Tag(paramArrayOfByte, paramArrayOfInt, paramArrayOfBundle, 0, null);
  }
  
  private String[] generateTechStringList(int[] paramArrayOfInt)
  {
    int j = paramArrayOfInt.length;
    String[] arrayOfString = new String[j];
    int i = 0;
    if (i < j)
    {
      switch (paramArrayOfInt[i])
      {
      default: 
        throw new IllegalArgumentException("Unknown tech type " + paramArrayOfInt[i]);
      case 3: 
        arrayOfString[i] = IsoDep.class.getName();
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfString[i] = MifareClassic.class.getName();
        continue;
        arrayOfString[i] = MifareUltralight.class.getName();
        continue;
        arrayOfString[i] = Ndef.class.getName();
        continue;
        arrayOfString[i] = NdefFormatable.class.getName();
        continue;
        arrayOfString[i] = NfcA.class.getName();
        continue;
        arrayOfString[i] = NfcB.class.getName();
        continue;
        arrayOfString[i] = NfcF.class.getName();
        continue;
        arrayOfString[i] = NfcV.class.getName();
        continue;
        arrayOfString[i] = NfcBarcode.class.getName();
      }
    }
    return arrayOfString;
  }
  
  static int[] getTechCodesFromStrings(String[] paramArrayOfString)
    throws IllegalArgumentException
  {
    if (paramArrayOfString == null) {
      throw new IllegalArgumentException("List cannot be null");
    }
    int[] arrayOfInt = new int[paramArrayOfString.length];
    HashMap localHashMap = getTechStringToCodeMap();
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      Integer localInteger = (Integer)localHashMap.get(paramArrayOfString[i]);
      if (localInteger == null) {
        throw new IllegalArgumentException("Unknown tech type " + paramArrayOfString[i]);
      }
      arrayOfInt[i] = localInteger.intValue();
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static HashMap<String, Integer> getTechStringToCodeMap()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put(IsoDep.class.getName(), Integer.valueOf(3));
    localHashMap.put(MifareClassic.class.getName(), Integer.valueOf(8));
    localHashMap.put(MifareUltralight.class.getName(), Integer.valueOf(9));
    localHashMap.put(Ndef.class.getName(), Integer.valueOf(6));
    localHashMap.put(NdefFormatable.class.getName(), Integer.valueOf(7));
    localHashMap.put(NfcA.class.getName(), Integer.valueOf(1));
    localHashMap.put(NfcB.class.getName(), Integer.valueOf(2));
    localHashMap.put(NfcF.class.getName(), Integer.valueOf(4));
    localHashMap.put(NfcV.class.getName(), Integer.valueOf(5));
    localHashMap.put(NfcBarcode.class.getName(), Integer.valueOf(10));
    return localHashMap;
  }
  
  static byte[] readBytesWithNull(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    byte[] arrayOfByte = null;
    if (i >= 0)
    {
      arrayOfByte = new byte[i];
      paramParcel.readByteArray(arrayOfByte);
    }
    return arrayOfByte;
  }
  
  static void writeBytesWithNull(Parcel paramParcel, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      paramParcel.writeInt(-1);
      return;
    }
    paramParcel.writeInt(paramArrayOfByte.length);
    paramParcel.writeByteArray(paramArrayOfByte);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getConnectedTechnology()
  {
    return this.mConnectedTechnology;
  }
  
  public byte[] getId()
  {
    return this.mId;
  }
  
  public int getServiceHandle()
  {
    return this.mServiceHandle;
  }
  
  public INfcTag getTagService()
  {
    return this.mTagService;
  }
  
  public int[] getTechCodeList()
  {
    return this.mTechList;
  }
  
  public Bundle getTechExtras(int paramInt)
  {
    int k = -1;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i < this.mTechList.length)
      {
        if (this.mTechList[i] == paramInt) {
          j = i;
        }
      }
      else
      {
        if (j >= 0) {
          break;
        }
        return null;
      }
      i += 1;
    }
    return this.mTechExtras[j];
  }
  
  public String[] getTechList()
  {
    return this.mTechStringList;
  }
  
  public boolean hasTech(int paramInt)
  {
    int[] arrayOfInt = this.mTechList;
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (arrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public Tag rediscover()
    throws IOException
  {
    if (getConnectedTechnology() != -1) {
      throw new IllegalStateException("Close connection to the technology first!");
    }
    if (this.mTagService == null) {
      throw new IOException("Mock tags don't support this operation.");
    }
    try
    {
      Tag localTag = this.mTagService.rediscover(getServiceHandle());
      if (localTag != null) {
        return localTag;
      }
      throw new IOException("Failed to rediscover tag");
    }
    catch (RemoteException localRemoteException)
    {
      throw new IOException("NFC service dead");
    }
  }
  
  public void setConnectedTechnology(int paramInt)
  {
    try
    {
      if (this.mConnectedTechnology == -1)
      {
        this.mConnectedTechnology = paramInt;
        return;
      }
      throw new IllegalStateException("Close other technology first!");
    }
    finally {}
  }
  
  public void setTechnologyDisconnected()
  {
    this.mConnectedTechnology = -1;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("TAG: Tech [");
    String[] arrayOfString = getTechList();
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append(arrayOfString[i]);
      if (i < j - 1) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mTagService == null) {}
    for (paramInt = 1;; paramInt = 0)
    {
      writeBytesWithNull(paramParcel, this.mId);
      paramParcel.writeInt(this.mTechList.length);
      paramParcel.writeIntArray(this.mTechList);
      paramParcel.writeTypedArray(this.mTechExtras, 0);
      paramParcel.writeInt(this.mServiceHandle);
      paramParcel.writeInt(paramInt);
      if (paramInt == 0) {
        paramParcel.writeStrongBinder(this.mTagService.asBinder());
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/Tag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */