package android.bluetooth;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BluetoothGattCharacteristic
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothGattCharacteristic> CREATOR = new Parcelable.Creator()
  {
    public BluetoothGattCharacteristic createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothGattCharacteristic(paramAnonymousParcel, null);
    }
    
    public BluetoothGattCharacteristic[] newArray(int paramAnonymousInt)
    {
      return new BluetoothGattCharacteristic[paramAnonymousInt];
    }
  };
  public static final int FORMAT_FLOAT = 52;
  public static final int FORMAT_SFLOAT = 50;
  public static final int FORMAT_SINT16 = 34;
  public static final int FORMAT_SINT32 = 36;
  public static final int FORMAT_SINT8 = 33;
  public static final int FORMAT_UINT16 = 18;
  public static final int FORMAT_UINT32 = 20;
  public static final int FORMAT_UINT8 = 17;
  public static final int PERMISSION_READ = 1;
  public static final int PERMISSION_READ_ENCRYPTED = 2;
  public static final int PERMISSION_READ_ENCRYPTED_MITM = 4;
  public static final int PERMISSION_WRITE = 16;
  public static final int PERMISSION_WRITE_ENCRYPTED = 32;
  public static final int PERMISSION_WRITE_ENCRYPTED_MITM = 64;
  public static final int PERMISSION_WRITE_SIGNED = 128;
  public static final int PERMISSION_WRITE_SIGNED_MITM = 256;
  public static final int PROPERTY_BROADCAST = 1;
  public static final int PROPERTY_EXTENDED_PROPS = 128;
  public static final int PROPERTY_INDICATE = 32;
  public static final int PROPERTY_NOTIFY = 16;
  public static final int PROPERTY_READ = 2;
  public static final int PROPERTY_SIGNED_WRITE = 64;
  public static final int PROPERTY_WRITE = 8;
  public static final int PROPERTY_WRITE_NO_RESPONSE = 4;
  public static final int WRITE_TYPE_DEFAULT = 2;
  public static final int WRITE_TYPE_NO_RESPONSE = 1;
  public static final int WRITE_TYPE_SIGNED = 4;
  protected List<BluetoothGattDescriptor> mDescriptors;
  protected int mInstance;
  protected int mKeySize = 16;
  protected int mPermissions;
  protected int mProperties;
  protected BluetoothGattService mService;
  protected UUID mUuid;
  protected byte[] mValue;
  protected int mWriteType;
  
  BluetoothGattCharacteristic(BluetoothGattService paramBluetoothGattService, UUID paramUUID, int paramInt1, int paramInt2, int paramInt3)
  {
    initCharacteristic(paramBluetoothGattService, paramUUID, paramInt1, paramInt2, paramInt3);
  }
  
  private BluetoothGattCharacteristic(Parcel paramParcel)
  {
    this.mUuid = ((ParcelUuid)paramParcel.readParcelable(null)).getUuid();
    this.mInstance = paramParcel.readInt();
    this.mProperties = paramParcel.readInt();
    this.mPermissions = paramParcel.readInt();
    this.mKeySize = paramParcel.readInt();
    this.mWriteType = paramParcel.readInt();
    this.mDescriptors = new ArrayList();
    paramParcel = paramParcel.createTypedArrayList(BluetoothGattDescriptor.CREATOR);
    if (paramParcel != null)
    {
      paramParcel = paramParcel.iterator();
      while (paramParcel.hasNext())
      {
        BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor)paramParcel.next();
        localBluetoothGattDescriptor.setCharacteristic(this);
        this.mDescriptors.add(localBluetoothGattDescriptor);
      }
    }
  }
  
  public BluetoothGattCharacteristic(UUID paramUUID, int paramInt1, int paramInt2)
  {
    initCharacteristic(null, paramUUID, 0, paramInt1, paramInt2);
  }
  
  public BluetoothGattCharacteristic(UUID paramUUID, int paramInt1, int paramInt2, int paramInt3)
  {
    initCharacteristic(null, paramUUID, paramInt1, paramInt2, paramInt3);
  }
  
  private float bytesToFloat(byte paramByte1, byte paramByte2)
  {
    int i = unsignedToSigned(unsignedByteToInt(paramByte1) + ((unsignedByteToInt(paramByte2) & 0xF) << 8), 12);
    int j = unsignedToSigned(unsignedByteToInt(paramByte2) >> 4, 4);
    return (float)(i * Math.pow(10.0D, j));
  }
  
  private float bytesToFloat(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    return (float)(unsignedToSigned(unsignedByteToInt(paramByte1) + (unsignedByteToInt(paramByte2) << 8) + (unsignedByteToInt(paramByte3) << 16), 24) * Math.pow(10.0D, paramByte4));
  }
  
  private int getTypeLen(int paramInt)
  {
    return paramInt & 0xF;
  }
  
  private void initCharacteristic(BluetoothGattService paramBluetoothGattService, UUID paramUUID, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mUuid = paramUUID;
    this.mInstance = paramInt1;
    this.mProperties = paramInt2;
    this.mPermissions = paramInt3;
    this.mService = paramBluetoothGattService;
    this.mValue = null;
    this.mDescriptors = new ArrayList();
    if ((this.mProperties & 0x4) != 0)
    {
      this.mWriteType = 1;
      return;
    }
    this.mWriteType = 2;
  }
  
  private int intToSignedBits(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = (1 << paramInt2 - 1) + ((1 << paramInt2 - 1) - 1 & paramInt1);
    }
    return i;
  }
  
  private int unsignedByteToInt(byte paramByte)
  {
    return paramByte & 0xFF;
  }
  
  private int unsignedBytesToInt(byte paramByte1, byte paramByte2)
  {
    return unsignedByteToInt(paramByte1) + (unsignedByteToInt(paramByte2) << 8);
  }
  
  private int unsignedBytesToInt(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    return unsignedByteToInt(paramByte1) + (unsignedByteToInt(paramByte2) << 8) + (unsignedByteToInt(paramByte3) << 16) + (unsignedByteToInt(paramByte4) << 24);
  }
  
  private int unsignedToSigned(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if ((1 << paramInt2 - 1 & paramInt1) != 0) {
      i = ((1 << paramInt2 - 1) - ((1 << paramInt2 - 1) - 1 & paramInt1)) * -1;
    }
    return i;
  }
  
  public boolean addDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
  {
    this.mDescriptors.add(paramBluetoothGattDescriptor);
    paramBluetoothGattDescriptor.setCharacteristic(this);
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BluetoothGattDescriptor getDescriptor(UUID paramUUID)
  {
    Iterator localIterator = this.mDescriptors.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor)localIterator.next();
      if (localBluetoothGattDescriptor.getUuid().equals(paramUUID)) {
        return localBluetoothGattDescriptor;
      }
    }
    return null;
  }
  
  BluetoothGattDescriptor getDescriptor(UUID paramUUID, int paramInt)
  {
    Iterator localIterator = this.mDescriptors.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor)localIterator.next();
      if ((localBluetoothGattDescriptor.getUuid().equals(paramUUID)) && (localBluetoothGattDescriptor.getInstanceId() == paramInt)) {
        return localBluetoothGattDescriptor;
      }
    }
    return null;
  }
  
  public List<BluetoothGattDescriptor> getDescriptors()
  {
    return this.mDescriptors;
  }
  
  public Float getFloatValue(int paramInt1, int paramInt2)
  {
    if (getTypeLen(paramInt1) + paramInt2 > this.mValue.length) {
      return null;
    }
    switch (paramInt1)
    {
    case 51: 
    default: 
      return null;
    case 50: 
      return Float.valueOf(bytesToFloat(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)]));
    }
    return Float.valueOf(bytesToFloat(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)], this.mValue[(paramInt2 + 2)], this.mValue[(paramInt2 + 3)]));
  }
  
  public int getInstanceId()
  {
    return this.mInstance;
  }
  
  public Integer getIntValue(int paramInt1, int paramInt2)
  {
    if (getTypeLen(paramInt1) + paramInt2 > this.mValue.length) {
      return null;
    }
    switch (paramInt1)
    {
    default: 
      return null;
    case 17: 
      return Integer.valueOf(unsignedByteToInt(this.mValue[paramInt2]));
    case 18: 
      return Integer.valueOf(unsignedBytesToInt(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)]));
    case 20: 
      return Integer.valueOf(unsignedBytesToInt(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)], this.mValue[(paramInt2 + 2)], this.mValue[(paramInt2 + 3)]));
    case 33: 
      return Integer.valueOf(unsignedToSigned(unsignedByteToInt(this.mValue[paramInt2]), 8));
    case 34: 
      return Integer.valueOf(unsignedToSigned(unsignedBytesToInt(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)]), 16));
    }
    return Integer.valueOf(unsignedToSigned(unsignedBytesToInt(this.mValue[paramInt2], this.mValue[(paramInt2 + 1)], this.mValue[(paramInt2 + 2)], this.mValue[(paramInt2 + 3)]), 32));
  }
  
  int getKeySize()
  {
    return this.mKeySize;
  }
  
  public int getPermissions()
  {
    return this.mPermissions;
  }
  
  public int getProperties()
  {
    return this.mProperties;
  }
  
  public BluetoothGattService getService()
  {
    return this.mService;
  }
  
  public String getStringValue(int paramInt)
  {
    if ((this.mValue == null) || (paramInt > this.mValue.length)) {
      return null;
    }
    byte[] arrayOfByte = new byte[this.mValue.length - paramInt];
    int i = 0;
    while (i != this.mValue.length - paramInt)
    {
      arrayOfByte[i] = this.mValue[(paramInt + i)];
      i += 1;
    }
    return new String(arrayOfByte);
  }
  
  public UUID getUuid()
  {
    return this.mUuid;
  }
  
  public byte[] getValue()
  {
    return this.mValue;
  }
  
  public int getWriteType()
  {
    return this.mWriteType;
  }
  
  public void setKeySize(int paramInt)
  {
    this.mKeySize = paramInt;
  }
  
  void setService(BluetoothGattService paramBluetoothGattService)
  {
    this.mService = paramBluetoothGattService;
  }
  
  public boolean setValue(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 + getTypeLen(paramInt2);
    if (this.mValue == null) {
      this.mValue = new byte[i];
    }
    if (i > this.mValue.length) {
      return false;
    }
    int j = paramInt1;
    int k = paramInt1;
    i = paramInt1;
    switch (paramInt2)
    {
    default: 
      return false;
    case 33: 
      j = intToSignedBits(paramInt1, 8);
    case 17: 
      this.mValue[paramInt3] = ((byte)(j & 0xFF));
    }
    for (;;)
    {
      return true;
      k = intToSignedBits(paramInt1, 16);
      this.mValue[paramInt3] = ((byte)(k & 0xFF));
      this.mValue[(paramInt3 + 1)] = ((byte)(k >> 8 & 0xFF));
      continue;
      i = intToSignedBits(paramInt1, 32);
      byte[] arrayOfByte = this.mValue;
      paramInt1 = paramInt3 + 1;
      arrayOfByte[paramInt3] = ((byte)(i & 0xFF));
      arrayOfByte = this.mValue;
      paramInt2 = paramInt1 + 1;
      arrayOfByte[paramInt1] = ((byte)(i >> 8 & 0xFF));
      this.mValue[paramInt2] = ((byte)(i >> 16 & 0xFF));
      this.mValue[(paramInt2 + 1)] = ((byte)(i >> 24 & 0xFF));
    }
  }
  
  public boolean setValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt4 + getTypeLen(paramInt3);
    if (this.mValue == null) {
      this.mValue = new byte[i];
    }
    if (i > this.mValue.length) {
      return false;
    }
    byte[] arrayOfByte;
    switch (paramInt3)
    {
    case 51: 
    default: 
      return false;
    case 50: 
      paramInt1 = intToSignedBits(paramInt1, 12);
      paramInt2 = intToSignedBits(paramInt2, 4);
      arrayOfByte = this.mValue;
      paramInt3 = paramInt4 + 1;
      arrayOfByte[paramInt4] = ((byte)(paramInt1 & 0xFF));
      this.mValue[paramInt3] = ((byte)(paramInt1 >> 8 & 0xF));
      arrayOfByte = this.mValue;
      arrayOfByte[paramInt3] = ((byte)(arrayOfByte[paramInt3] + (byte)((paramInt2 & 0xF) << 4)));
    }
    for (;;)
    {
      return true;
      paramInt1 = intToSignedBits(paramInt1, 24);
      paramInt2 = intToSignedBits(paramInt2, 8);
      arrayOfByte = this.mValue;
      paramInt3 = paramInt4 + 1;
      arrayOfByte[paramInt4] = ((byte)(paramInt1 & 0xFF));
      arrayOfByte = this.mValue;
      paramInt4 = paramInt3 + 1;
      arrayOfByte[paramInt3] = ((byte)(paramInt1 >> 8 & 0xFF));
      arrayOfByte = this.mValue;
      paramInt3 = paramInt4 + 1;
      arrayOfByte[paramInt4] = ((byte)(paramInt1 >> 16 & 0xFF));
      arrayOfByte = this.mValue;
      arrayOfByte[paramInt3] = ((byte)(arrayOfByte[paramInt3] + (byte)(paramInt2 & 0xFF)));
    }
  }
  
  public boolean setValue(String paramString)
  {
    this.mValue = paramString.getBytes();
    return true;
  }
  
  public boolean setValue(byte[] paramArrayOfByte)
  {
    this.mValue = paramArrayOfByte;
    return true;
  }
  
  public void setWriteType(int paramInt)
  {
    this.mWriteType = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(new ParcelUuid(this.mUuid), 0);
    paramParcel.writeInt(this.mInstance);
    paramParcel.writeInt(this.mProperties);
    paramParcel.writeInt(this.mPermissions);
    paramParcel.writeInt(this.mKeySize);
    paramParcel.writeInt(this.mWriteType);
    paramParcel.writeTypedList(this.mDescriptors);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattCharacteristic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */