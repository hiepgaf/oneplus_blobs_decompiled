package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ScanFilter
  implements Parcelable
{
  public static final Parcelable.Creator<ScanFilter> CREATOR = new Parcelable.Creator()
  {
    public ScanFilter createFromParcel(Parcel paramAnonymousParcel)
    {
      ScanFilter.Builder localBuilder = new ScanFilter.Builder();
      if (paramAnonymousParcel.readInt() == 1) {
        localBuilder.setDeviceName(paramAnonymousParcel.readString());
      }
      if (paramAnonymousParcel.readInt() == 1) {
        localBuilder.setDeviceAddress(paramAnonymousParcel.readString());
      }
      Object localObject;
      if (paramAnonymousParcel.readInt() == 1)
      {
        localObject = (ParcelUuid)paramAnonymousParcel.readParcelable(ParcelUuid.class.getClassLoader());
        localBuilder.setServiceUuid((ParcelUuid)localObject);
        if (paramAnonymousParcel.readInt() == 1) {
          localBuilder.setServiceUuid((ParcelUuid)localObject, (ParcelUuid)paramAnonymousParcel.readParcelable(ParcelUuid.class.getClassLoader()));
        }
      }
      byte[] arrayOfByte1;
      if (paramAnonymousParcel.readInt() == 1)
      {
        localObject = (ParcelUuid)paramAnonymousParcel.readParcelable(ParcelUuid.class.getClassLoader());
        if (paramAnonymousParcel.readInt() == 1)
        {
          arrayOfByte1 = new byte[paramAnonymousParcel.readInt()];
          paramAnonymousParcel.readByteArray(arrayOfByte1);
          if (paramAnonymousParcel.readInt() != 0) {
            break label205;
          }
          localBuilder.setServiceData((ParcelUuid)localObject, arrayOfByte1);
        }
      }
      int i = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readInt() == 1)
      {
        localObject = new byte[paramAnonymousParcel.readInt()];
        paramAnonymousParcel.readByteArray((byte[])localObject);
        if (paramAnonymousParcel.readInt() != 0) {
          break label233;
        }
        localBuilder.setManufacturerData(i, (byte[])localObject);
      }
      for (;;)
      {
        return localBuilder.build();
        label205:
        byte[] arrayOfByte2 = new byte[paramAnonymousParcel.readInt()];
        paramAnonymousParcel.readByteArray(arrayOfByte2);
        localBuilder.setServiceData((ParcelUuid)localObject, arrayOfByte1, arrayOfByte2);
        break;
        label233:
        arrayOfByte1 = new byte[paramAnonymousParcel.readInt()];
        paramAnonymousParcel.readByteArray(arrayOfByte1);
        localBuilder.setManufacturerData(i, (byte[])localObject, arrayOfByte1);
      }
    }
    
    public ScanFilter[] newArray(int paramAnonymousInt)
    {
      return new ScanFilter[paramAnonymousInt];
    }
  };
  private static final ScanFilter EMPTY = new Builder().build();
  private final String mDeviceAddress;
  private final String mDeviceName;
  private final byte[] mManufacturerData;
  private final byte[] mManufacturerDataMask;
  private final int mManufacturerId;
  private final byte[] mServiceData;
  private final byte[] mServiceDataMask;
  private final ParcelUuid mServiceDataUuid;
  private final ParcelUuid mServiceUuid;
  private final ParcelUuid mServiceUuidMask;
  
  private ScanFilter(String paramString1, String paramString2, ParcelUuid paramParcelUuid1, ParcelUuid paramParcelUuid2, ParcelUuid paramParcelUuid3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4)
  {
    this.mDeviceName = paramString1;
    this.mServiceUuid = paramParcelUuid1;
    this.mServiceUuidMask = paramParcelUuid2;
    this.mDeviceAddress = paramString2;
    this.mServiceDataUuid = paramParcelUuid3;
    this.mServiceData = paramArrayOfByte1;
    this.mServiceDataMask = paramArrayOfByte2;
    this.mManufacturerId = paramInt;
    this.mManufacturerData = paramArrayOfByte3;
    this.mManufacturerDataMask = paramArrayOfByte4;
  }
  
  private boolean matchesPartialData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if ((paramArrayOfByte3 == null) || (paramArrayOfByte3.length < paramArrayOfByte1.length)) {
      return false;
    }
    if (paramArrayOfByte2 == null)
    {
      i = 0;
      while (i < paramArrayOfByte1.length)
      {
        if (paramArrayOfByte3[i] != paramArrayOfByte1[i]) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    int i = 0;
    while (i < paramArrayOfByte1.length)
    {
      if ((paramArrayOfByte2[i] & paramArrayOfByte3[i]) != (paramArrayOfByte2[i] & paramArrayOfByte1[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean matchesServiceUuid(UUID paramUUID1, UUID paramUUID2, UUID paramUUID3)
  {
    boolean bool = false;
    if (paramUUID2 == null) {
      return paramUUID1.equals(paramUUID3);
    }
    if ((paramUUID1.getLeastSignificantBits() & paramUUID2.getLeastSignificantBits()) != (paramUUID3.getLeastSignificantBits() & paramUUID2.getLeastSignificantBits())) {
      return false;
    }
    if ((paramUUID1.getMostSignificantBits() & paramUUID2.getMostSignificantBits()) == (paramUUID3.getMostSignificantBits() & paramUUID2.getMostSignificantBits())) {
      bool = true;
    }
    return bool;
  }
  
  private boolean matchesServiceUuids(ParcelUuid paramParcelUuid1, ParcelUuid paramParcelUuid2, List<ParcelUuid> paramList)
  {
    if (paramParcelUuid1 == null) {
      return true;
    }
    if (paramList == null) {
      return false;
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ParcelUuid localParcelUuid = (ParcelUuid)localIterator.next();
      if (paramParcelUuid2 == null) {}
      for (paramList = null; matchesServiceUuid(paramParcelUuid1.getUuid(), paramList, localParcelUuid.getUuid()); paramList = paramParcelUuid2.getUuid()) {
        return true;
      }
    }
    return false;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (ScanFilter)paramObject;
    boolean bool1 = bool2;
    if (Objects.equals(this.mDeviceName, ((ScanFilter)paramObject).mDeviceName))
    {
      bool1 = bool2;
      if (Objects.equals(this.mDeviceAddress, ((ScanFilter)paramObject).mDeviceAddress))
      {
        bool1 = bool2;
        if (this.mManufacturerId == ((ScanFilter)paramObject).mManufacturerId)
        {
          bool1 = bool2;
          if (Objects.deepEquals(this.mManufacturerData, ((ScanFilter)paramObject).mManufacturerData))
          {
            bool1 = bool2;
            if (Objects.deepEquals(this.mManufacturerDataMask, ((ScanFilter)paramObject).mManufacturerDataMask))
            {
              bool1 = bool2;
              if (Objects.equals(this.mServiceDataUuid, ((ScanFilter)paramObject).mServiceDataUuid))
              {
                bool1 = bool2;
                if (Objects.deepEquals(this.mServiceData, ((ScanFilter)paramObject).mServiceData))
                {
                  bool1 = bool2;
                  if (Objects.deepEquals(this.mServiceDataMask, ((ScanFilter)paramObject).mServiceDataMask))
                  {
                    bool1 = bool2;
                    if (Objects.equals(this.mServiceUuid, ((ScanFilter)paramObject).mServiceUuid)) {
                      bool1 = Objects.equals(this.mServiceUuidMask, ((ScanFilter)paramObject).mServiceUuidMask);
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
  
  public String getDeviceAddress()
  {
    return this.mDeviceAddress;
  }
  
  public String getDeviceName()
  {
    return this.mDeviceName;
  }
  
  public byte[] getManufacturerData()
  {
    return this.mManufacturerData;
  }
  
  public byte[] getManufacturerDataMask()
  {
    return this.mManufacturerDataMask;
  }
  
  public int getManufacturerId()
  {
    return this.mManufacturerId;
  }
  
  public byte[] getServiceData()
  {
    return this.mServiceData;
  }
  
  public byte[] getServiceDataMask()
  {
    return this.mServiceDataMask;
  }
  
  public ParcelUuid getServiceDataUuid()
  {
    return this.mServiceDataUuid;
  }
  
  public ParcelUuid getServiceUuid()
  {
    return this.mServiceUuid;
  }
  
  public ParcelUuid getServiceUuidMask()
  {
    return this.mServiceUuidMask;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mDeviceName, this.mDeviceAddress, Integer.valueOf(this.mManufacturerId), Integer.valueOf(Arrays.hashCode(this.mManufacturerData)), Integer.valueOf(Arrays.hashCode(this.mManufacturerDataMask)), this.mServiceDataUuid, Integer.valueOf(Arrays.hashCode(this.mServiceData)), Integer.valueOf(Arrays.hashCode(this.mServiceDataMask)), this.mServiceUuid, this.mServiceUuidMask });
  }
  
  public boolean isAllFieldsEmpty()
  {
    return EMPTY.equals(this);
  }
  
  public boolean matches(ScanResult paramScanResult)
  {
    if (paramScanResult == null) {
      return false;
    }
    BluetoothDevice localBluetoothDevice = paramScanResult.getDevice();
    if ((this.mDeviceAddress == null) || ((localBluetoothDevice != null) && (this.mDeviceAddress.equals(localBluetoothDevice.getAddress()))))
    {
      paramScanResult = paramScanResult.getScanRecord();
      if (paramScanResult != null) {
        break label77;
      }
      if ((this.mDeviceName == null) && (this.mServiceUuid == null)) {
        break label63;
      }
    }
    label63:
    while ((this.mManufacturerData != null) || (this.mServiceData != null))
    {
      return false;
      return false;
    }
    label77:
    if ((this.mDeviceName == null) || (this.mDeviceName.equals(paramScanResult.getDeviceName())))
    {
      if ((this.mServiceUuid == null) || (matchesServiceUuids(this.mServiceUuid, this.mServiceUuidMask, paramScanResult.getServiceUuids())))
      {
        if ((this.mServiceDataUuid == null) || (matchesPartialData(this.mServiceData, this.mServiceDataMask, paramScanResult.getServiceData(this.mServiceDataUuid)))) {
          break label160;
        }
        return false;
      }
    }
    else {
      return false;
    }
    return false;
    label160:
    return (this.mManufacturerId < 0) || (matchesPartialData(this.mManufacturerData, this.mManufacturerDataMask, paramScanResult.getManufacturerSpecificData(this.mManufacturerId)));
  }
  
  public String toString()
  {
    return "BluetoothLeScanFilter [mDeviceName=" + this.mDeviceName + ", mDeviceAddress=" + this.mDeviceAddress + ", mUuid=" + this.mServiceUuid + ", mUuidMask=" + this.mServiceUuidMask + ", mServiceDataUuid=" + Objects.toString(this.mServiceDataUuid) + ", mServiceData=" + Arrays.toString(this.mServiceData) + ", mServiceDataMask=" + Arrays.toString(this.mServiceDataMask) + ", mManufacturerId=" + this.mManufacturerId + ", mManufacturerData=" + Arrays.toString(this.mManufacturerData) + ", mManufacturerDataMask=" + Arrays.toString(this.mManufacturerDataMask) + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 0;
    int i;
    if (this.mDeviceName == null)
    {
      i = 0;
      paramParcel.writeInt(i);
      if (this.mDeviceName != null) {
        paramParcel.writeString(this.mDeviceName);
      }
      if (this.mDeviceAddress != null) {
        break label318;
      }
      i = 0;
      label41:
      paramParcel.writeInt(i);
      if (this.mDeviceAddress != null) {
        paramParcel.writeString(this.mDeviceAddress);
      }
      if (this.mServiceUuid != null) {
        break label323;
      }
      i = 0;
      label70:
      paramParcel.writeInt(i);
      if (this.mServiceUuid != null)
      {
        paramParcel.writeParcelable(this.mServiceUuid, paramInt);
        if (this.mServiceUuidMask != null) {
          break label328;
        }
        i = 0;
        label100:
        paramParcel.writeInt(i);
        if (this.mServiceUuidMask != null) {
          paramParcel.writeParcelable(this.mServiceUuidMask, paramInt);
        }
      }
      if (this.mServiceDataUuid != null) {
        break label333;
      }
      i = 0;
      label130:
      paramParcel.writeInt(i);
      if (this.mServiceDataUuid != null)
      {
        paramParcel.writeParcelable(this.mServiceDataUuid, paramInt);
        if (this.mServiceData != null) {
          break label338;
        }
        paramInt = 0;
        label160:
        paramParcel.writeInt(paramInt);
        if (this.mServiceData != null)
        {
          paramParcel.writeInt(this.mServiceData.length);
          paramParcel.writeByteArray(this.mServiceData);
          if (this.mServiceDataMask != null) {
            break label343;
          }
          paramInt = 0;
          label198:
          paramParcel.writeInt(paramInt);
          if (this.mServiceDataMask != null)
          {
            paramParcel.writeInt(this.mServiceDataMask.length);
            paramParcel.writeByteArray(this.mServiceDataMask);
          }
        }
      }
      paramParcel.writeInt(this.mManufacturerId);
      if (this.mManufacturerData != null) {
        break label348;
      }
      paramInt = 0;
      label244:
      paramParcel.writeInt(paramInt);
      if (this.mManufacturerData != null)
      {
        paramParcel.writeInt(this.mManufacturerData.length);
        paramParcel.writeByteArray(this.mManufacturerData);
        if (this.mManufacturerDataMask != null) {
          break label353;
        }
      }
    }
    label318:
    label323:
    label328:
    label333:
    label338:
    label343:
    label348:
    label353:
    for (paramInt = j;; paramInt = 1)
    {
      paramParcel.writeInt(paramInt);
      if (this.mManufacturerDataMask != null)
      {
        paramParcel.writeInt(this.mManufacturerDataMask.length);
        paramParcel.writeByteArray(this.mManufacturerDataMask);
      }
      return;
      i = 1;
      break;
      i = 1;
      break label41;
      i = 1;
      break label70;
      i = 1;
      break label100;
      i = 1;
      break label130;
      paramInt = 1;
      break label160;
      paramInt = 1;
      break label198;
      paramInt = 1;
      break label244;
    }
  }
  
  public static final class Builder
  {
    private String mDeviceAddress;
    private String mDeviceName;
    private byte[] mManufacturerData;
    private byte[] mManufacturerDataMask;
    private int mManufacturerId = -1;
    private byte[] mServiceData;
    private byte[] mServiceDataMask;
    private ParcelUuid mServiceDataUuid;
    private ParcelUuid mServiceUuid;
    private ParcelUuid mUuidMask;
    
    public ScanFilter build()
    {
      return new ScanFilter(this.mDeviceName, this.mDeviceAddress, this.mServiceUuid, this.mUuidMask, this.mServiceDataUuid, this.mServiceData, this.mServiceDataMask, this.mManufacturerId, this.mManufacturerData, this.mManufacturerDataMask, null);
    }
    
    public Builder setDeviceAddress(String paramString)
    {
      if ((paramString == null) || (BluetoothAdapter.checkBluetoothAddress(paramString)))
      {
        this.mDeviceAddress = paramString;
        return this;
      }
      throw new IllegalArgumentException("invalid device address " + paramString);
    }
    
    public Builder setDeviceName(String paramString)
    {
      this.mDeviceName = paramString;
      return this;
    }
    
    public Builder setManufacturerData(int paramInt, byte[] paramArrayOfByte)
    {
      if ((paramArrayOfByte != null) && (paramInt < 0)) {
        throw new IllegalArgumentException("invalid manufacture id");
      }
      this.mManufacturerId = paramInt;
      this.mManufacturerData = paramArrayOfByte;
      this.mManufacturerDataMask = null;
      return this;
    }
    
    public Builder setManufacturerData(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      if ((paramArrayOfByte1 != null) && (paramInt < 0)) {
        throw new IllegalArgumentException("invalid manufacture id");
      }
      if (this.mManufacturerDataMask != null)
      {
        if (this.mManufacturerData == null) {
          throw new IllegalArgumentException("manufacturerData is null while manufacturerDataMask is not null");
        }
        if (this.mManufacturerData.length != this.mManufacturerDataMask.length) {
          throw new IllegalArgumentException("size mismatch for manufacturerData and manufacturerDataMask");
        }
      }
      this.mManufacturerId = paramInt;
      this.mManufacturerData = paramArrayOfByte1;
      this.mManufacturerDataMask = paramArrayOfByte2;
      return this;
    }
    
    public Builder setServiceData(ParcelUuid paramParcelUuid, byte[] paramArrayOfByte)
    {
      if (paramParcelUuid == null) {
        throw new IllegalArgumentException("serviceDataUuid is null");
      }
      this.mServiceDataUuid = paramParcelUuid;
      this.mServiceData = paramArrayOfByte;
      this.mServiceDataMask = null;
      return this;
    }
    
    public Builder setServiceData(ParcelUuid paramParcelUuid, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      if (paramParcelUuid == null) {
        throw new IllegalArgumentException("serviceDataUuid is null");
      }
      if (this.mServiceDataMask != null)
      {
        if (this.mServiceData == null) {
          throw new IllegalArgumentException("serviceData is null while serviceDataMask is not null");
        }
        if (this.mServiceData.length != this.mServiceDataMask.length) {
          throw new IllegalArgumentException("size mismatch for service data and service data mask");
        }
      }
      this.mServiceDataUuid = paramParcelUuid;
      this.mServiceData = paramArrayOfByte1;
      this.mServiceDataMask = paramArrayOfByte2;
      return this;
    }
    
    public Builder setServiceUuid(ParcelUuid paramParcelUuid)
    {
      this.mServiceUuid = paramParcelUuid;
      this.mUuidMask = null;
      return this;
    }
    
    public Builder setServiceUuid(ParcelUuid paramParcelUuid1, ParcelUuid paramParcelUuid2)
    {
      if ((this.mUuidMask != null) && (this.mServiceUuid == null)) {
        throw new IllegalArgumentException("uuid is null while uuidMask is not null!");
      }
      this.mServiceUuid = paramParcelUuid1;
      this.mUuidMask = paramParcelUuid2;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ScanFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */