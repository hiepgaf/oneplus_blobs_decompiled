package android.bluetooth;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.UUID;

public class BluetoothGattDescriptor
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothGattDescriptor> CREATOR = new Parcelable.Creator()
  {
    public BluetoothGattDescriptor createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothGattDescriptor(paramAnonymousParcel, null);
    }
    
    public BluetoothGattDescriptor[] newArray(int paramAnonymousInt)
    {
      return new BluetoothGattDescriptor[paramAnonymousInt];
    }
  };
  public static final byte[] DISABLE_NOTIFICATION_VALUE;
  public static final byte[] ENABLE_INDICATION_VALUE;
  public static final byte[] ENABLE_NOTIFICATION_VALUE = { 1, 0 };
  public static final int PERMISSION_READ = 1;
  public static final int PERMISSION_READ_ENCRYPTED = 2;
  public static final int PERMISSION_READ_ENCRYPTED_MITM = 4;
  public static final int PERMISSION_WRITE = 16;
  public static final int PERMISSION_WRITE_ENCRYPTED = 32;
  public static final int PERMISSION_WRITE_ENCRYPTED_MITM = 64;
  public static final int PERMISSION_WRITE_SIGNED = 128;
  public static final int PERMISSION_WRITE_SIGNED_MITM = 256;
  protected BluetoothGattCharacteristic mCharacteristic;
  protected int mInstance;
  protected int mPermissions;
  protected UUID mUuid;
  protected byte[] mValue;
  
  static
  {
    ENABLE_INDICATION_VALUE = new byte[] { 2, 0 };
    DISABLE_NOTIFICATION_VALUE = new byte[] { 0, 0 };
  }
  
  BluetoothGattDescriptor(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, UUID paramUUID, int paramInt1, int paramInt2)
  {
    initDescriptor(paramBluetoothGattCharacteristic, paramUUID, paramInt1, paramInt2);
  }
  
  private BluetoothGattDescriptor(Parcel paramParcel)
  {
    this.mUuid = ((ParcelUuid)paramParcel.readParcelable(null)).getUuid();
    this.mInstance = paramParcel.readInt();
    this.mPermissions = paramParcel.readInt();
  }
  
  public BluetoothGattDescriptor(UUID paramUUID, int paramInt)
  {
    initDescriptor(null, paramUUID, 0, paramInt);
  }
  
  public BluetoothGattDescriptor(UUID paramUUID, int paramInt1, int paramInt2)
  {
    initDescriptor(null, paramUUID, paramInt1, paramInt2);
  }
  
  private void initDescriptor(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, UUID paramUUID, int paramInt1, int paramInt2)
  {
    this.mCharacteristic = paramBluetoothGattCharacteristic;
    this.mUuid = paramUUID;
    this.mInstance = paramInt1;
    this.mPermissions = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BluetoothGattCharacteristic getCharacteristic()
  {
    return this.mCharacteristic;
  }
  
  public int getInstanceId()
  {
    return this.mInstance;
  }
  
  public int getPermissions()
  {
    return this.mPermissions;
  }
  
  public UUID getUuid()
  {
    return this.mUuid;
  }
  
  public byte[] getValue()
  {
    return this.mValue;
  }
  
  void setCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    this.mCharacteristic = paramBluetoothGattCharacteristic;
  }
  
  public boolean setValue(byte[] paramArrayOfByte)
  {
    this.mValue = paramArrayOfByte;
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(new ParcelUuid(this.mUuid), 0);
    paramParcel.writeInt(this.mInstance);
    paramParcel.writeInt(this.mPermissions);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */