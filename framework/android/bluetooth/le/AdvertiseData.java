package android.bluetooth.le;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AdvertiseData
  implements Parcelable
{
  public static final Parcelable.Creator<AdvertiseData> CREATOR = new Parcelable.Creator()
  {
    public AdvertiseData createFromParcel(Parcel paramAnonymousParcel)
    {
      AdvertiseData.Builder localBuilder = new AdvertiseData.Builder();
      Object localObject = paramAnonymousParcel.readArrayList(ParcelUuid.class.getClassLoader());
      if (localObject != null)
      {
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          localBuilder.addServiceUuid((ParcelUuid)((Iterator)localObject).next());
        }
      }
      int j = paramAnonymousParcel.readInt();
      int i = 0;
      while (i < j)
      {
        int k = paramAnonymousParcel.readInt();
        if (paramAnonymousParcel.readInt() == 1)
        {
          localObject = new byte[paramAnonymousParcel.readInt()];
          paramAnonymousParcel.readByteArray((byte[])localObject);
          localBuilder.addManufacturerData(k, (byte[])localObject);
        }
        i += 1;
      }
      j = paramAnonymousParcel.readInt();
      i = 0;
      while (i < j)
      {
        localObject = (ParcelUuid)paramAnonymousParcel.readParcelable(ParcelUuid.class.getClassLoader());
        if (paramAnonymousParcel.readInt() == 1)
        {
          byte[] arrayOfByte = new byte[paramAnonymousParcel.readInt()];
          paramAnonymousParcel.readByteArray(arrayOfByte);
          localBuilder.addServiceData((ParcelUuid)localObject, arrayOfByte);
        }
        i += 1;
      }
      if (paramAnonymousParcel.readByte() == 1)
      {
        bool = true;
        localBuilder.setIncludeTxPowerLevel(bool);
        if (paramAnonymousParcel.readByte() != 1) {
          break label235;
        }
      }
      label235:
      for (boolean bool = true;; bool = false)
      {
        localBuilder.setIncludeDeviceName(bool);
        return localBuilder.build();
        bool = false;
        break;
      }
    }
    
    public AdvertiseData[] newArray(int paramAnonymousInt)
    {
      return new AdvertiseData[paramAnonymousInt];
    }
  };
  private final boolean mIncludeDeviceName;
  private final boolean mIncludeTxPowerLevel;
  private final SparseArray<byte[]> mManufacturerSpecificData;
  private final Map<ParcelUuid, byte[]> mServiceData;
  private final List<ParcelUuid> mServiceUuids;
  
  private AdvertiseData(List<ParcelUuid> paramList, SparseArray<byte[]> paramSparseArray, Map<ParcelUuid, byte[]> paramMap, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mServiceUuids = paramList;
    this.mManufacturerSpecificData = paramSparseArray;
    this.mServiceData = paramMap;
    this.mIncludeTxPowerLevel = paramBoolean1;
    this.mIncludeDeviceName = paramBoolean2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (AdvertiseData)paramObject;
    if ((Objects.equals(this.mServiceUuids, ((AdvertiseData)paramObject).mServiceUuids)) && (BluetoothLeUtils.equals(this.mManufacturerSpecificData, ((AdvertiseData)paramObject).mManufacturerSpecificData)) && (BluetoothLeUtils.equals(this.mServiceData, ((AdvertiseData)paramObject).mServiceData)) && (this.mIncludeDeviceName == ((AdvertiseData)paramObject).mIncludeDeviceName)) {
      return this.mIncludeTxPowerLevel == ((AdvertiseData)paramObject).mIncludeTxPowerLevel;
    }
    return false;
  }
  
  public boolean getIncludeDeviceName()
  {
    return this.mIncludeDeviceName;
  }
  
  public boolean getIncludeTxPowerLevel()
  {
    return this.mIncludeTxPowerLevel;
  }
  
  public SparseArray<byte[]> getManufacturerSpecificData()
  {
    return this.mManufacturerSpecificData;
  }
  
  public Map<ParcelUuid, byte[]> getServiceData()
  {
    return this.mServiceData;
  }
  
  public List<ParcelUuid> getServiceUuids()
  {
    return this.mServiceUuids;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mServiceUuids, this.mManufacturerSpecificData, this.mServiceData, Boolean.valueOf(this.mIncludeDeviceName), Boolean.valueOf(this.mIncludeTxPowerLevel) });
  }
  
  public String toString()
  {
    return "AdvertiseData [mServiceUuids=" + this.mServiceUuids + ", mManufacturerSpecificData=" + BluetoothLeUtils.toString(this.mManufacturerSpecificData) + ", mServiceData=" + BluetoothLeUtils.toString(this.mServiceData) + ", mIncludeTxPowerLevel=" + this.mIncludeTxPowerLevel + ", mIncludeDeviceName=" + this.mIncludeDeviceName + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeList(this.mServiceUuids);
    paramParcel.writeInt(this.mManufacturerSpecificData.size());
    int i = 0;
    if (i < this.mManufacturerSpecificData.size())
    {
      paramParcel.writeInt(this.mManufacturerSpecificData.keyAt(i));
      localObject1 = (byte[])this.mManufacturerSpecificData.valueAt(i);
      if (localObject1 == null) {
        paramParcel.writeInt(0);
      }
      for (;;)
      {
        i += 1;
        break;
        paramParcel.writeInt(1);
        paramParcel.writeInt(localObject1.length);
        paramParcel.writeByteArray((byte[])localObject1);
      }
    }
    paramParcel.writeInt(this.mServiceData.size());
    Object localObject1 = this.mServiceData.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (ParcelUuid)((Iterator)localObject1).next();
      paramParcel.writeParcelable((Parcelable)localObject2, paramInt);
      localObject2 = (byte[])this.mServiceData.get(localObject2);
      if (localObject2 == null)
      {
        paramParcel.writeInt(0);
      }
      else
      {
        paramParcel.writeInt(1);
        paramParcel.writeInt(localObject2.length);
        paramParcel.writeByteArray((byte[])localObject2);
      }
    }
    if (getIncludeTxPowerLevel())
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (!getIncludeDeviceName()) {
        break label243;
      }
    }
    label243:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
      paramInt = 0;
      break;
    }
  }
  
  public static final class Builder
  {
    private boolean mIncludeDeviceName;
    private boolean mIncludeTxPowerLevel;
    private SparseArray<byte[]> mManufacturerSpecificData = new SparseArray();
    private Map<ParcelUuid, byte[]> mServiceData = new ArrayMap();
    private List<ParcelUuid> mServiceUuids = new ArrayList();
    
    public Builder addManufacturerData(int paramInt, byte[] paramArrayOfByte)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("invalid manufacturerId - " + paramInt);
      }
      if (paramArrayOfByte == null) {
        throw new IllegalArgumentException("manufacturerSpecificData is null");
      }
      this.mManufacturerSpecificData.put(paramInt, paramArrayOfByte);
      return this;
    }
    
    public Builder addServiceData(ParcelUuid paramParcelUuid, byte[] paramArrayOfByte)
    {
      if ((paramParcelUuid == null) || (paramArrayOfByte == null)) {
        throw new IllegalArgumentException("serviceDataUuid or serviceDataUuid is null");
      }
      this.mServiceData.put(paramParcelUuid, paramArrayOfByte);
      return this;
    }
    
    public Builder addServiceUuid(ParcelUuid paramParcelUuid)
    {
      if (paramParcelUuid == null) {
        throw new IllegalArgumentException("serivceUuids are null");
      }
      this.mServiceUuids.add(paramParcelUuid);
      return this;
    }
    
    public AdvertiseData build()
    {
      return new AdvertiseData(this.mServiceUuids, this.mManufacturerSpecificData, this.mServiceData, this.mIncludeTxPowerLevel, this.mIncludeDeviceName, null);
    }
    
    public Builder setIncludeDeviceName(boolean paramBoolean)
    {
      this.mIncludeDeviceName = paramBoolean;
      return this;
    }
    
    public Builder setIncludeTxPowerLevel(boolean paramBoolean)
    {
      this.mIncludeTxPowerLevel = paramBoolean;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/AdvertiseData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */