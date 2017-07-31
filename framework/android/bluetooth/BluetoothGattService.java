package android.bluetooth;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BluetoothGattService
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothGattService> CREATOR = new Parcelable.Creator()
  {
    public BluetoothGattService createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothGattService(paramAnonymousParcel, null);
    }
    
    public BluetoothGattService[] newArray(int paramAnonymousInt)
    {
      return new BluetoothGattService[paramAnonymousInt];
    }
  };
  public static final int SERVICE_TYPE_PRIMARY = 0;
  public static final int SERVICE_TYPE_SECONDARY = 1;
  private boolean mAdvertisePreferred;
  protected List<BluetoothGattCharacteristic> mCharacteristics;
  protected BluetoothDevice mDevice;
  protected int mHandles = 0;
  protected List<BluetoothGattService> mIncludedServices;
  protected int mInstanceId;
  protected int mServiceType;
  protected UUID mUuid;
  
  BluetoothGattService(BluetoothDevice paramBluetoothDevice, UUID paramUUID, int paramInt1, int paramInt2)
  {
    this.mDevice = paramBluetoothDevice;
    this.mUuid = paramUUID;
    this.mInstanceId = paramInt1;
    this.mServiceType = paramInt2;
    this.mCharacteristics = new ArrayList();
    this.mIncludedServices = new ArrayList();
  }
  
  private BluetoothGattService(Parcel paramParcel)
  {
    this.mUuid = ((ParcelUuid)paramParcel.readParcelable(null)).getUuid();
    this.mInstanceId = paramParcel.readInt();
    this.mServiceType = paramParcel.readInt();
    this.mCharacteristics = new ArrayList();
    Object localObject = paramParcel.createTypedArrayList(BluetoothGattCharacteristic.CREATOR);
    if (localObject != null)
    {
      Iterator localIterator = ((Iterable)localObject).iterator();
      while (localIterator.hasNext())
      {
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic)localIterator.next();
        localBluetoothGattCharacteristic.setService(this);
        this.mCharacteristics.add(localBluetoothGattCharacteristic);
      }
    }
    this.mIncludedServices = new ArrayList();
    paramParcel = paramParcel.createTypedArrayList(BluetoothGattIncludedService.CREATOR);
    if (localObject != null)
    {
      paramParcel = paramParcel.iterator();
      while (paramParcel.hasNext())
      {
        localObject = (BluetoothGattIncludedService)paramParcel.next();
        this.mIncludedServices.add(new BluetoothGattService(null, ((BluetoothGattIncludedService)localObject).getUuid(), ((BluetoothGattIncludedService)localObject).getInstanceId(), ((BluetoothGattIncludedService)localObject).getType()));
      }
    }
  }
  
  public BluetoothGattService(UUID paramUUID, int paramInt)
  {
    this.mDevice = null;
    this.mUuid = paramUUID;
    this.mInstanceId = 0;
    this.mServiceType = paramInt;
    this.mCharacteristics = new ArrayList();
    this.mIncludedServices = new ArrayList();
  }
  
  public BluetoothGattService(UUID paramUUID, int paramInt1, int paramInt2)
  {
    this.mDevice = null;
    this.mUuid = paramUUID;
    this.mInstanceId = paramInt1;
    this.mServiceType = paramInt2;
    this.mCharacteristics = new ArrayList();
    this.mIncludedServices = new ArrayList();
  }
  
  public boolean addCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
  {
    this.mCharacteristics.add(paramBluetoothGattCharacteristic);
    paramBluetoothGattCharacteristic.setService(this);
    return true;
  }
  
  public void addIncludedService(BluetoothGattService paramBluetoothGattService)
  {
    this.mIncludedServices.add(paramBluetoothGattService);
  }
  
  public boolean addService(BluetoothGattService paramBluetoothGattService)
  {
    this.mIncludedServices.add(paramBluetoothGattService);
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BluetoothGattCharacteristic getCharacteristic(UUID paramUUID)
  {
    Iterator localIterator = this.mCharacteristics.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic)localIterator.next();
      if (paramUUID.equals(localBluetoothGattCharacteristic.getUuid())) {
        return localBluetoothGattCharacteristic;
      }
    }
    return null;
  }
  
  BluetoothGattCharacteristic getCharacteristic(UUID paramUUID, int paramInt)
  {
    Iterator localIterator = this.mCharacteristics.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic)localIterator.next();
      if ((paramUUID.equals(localBluetoothGattCharacteristic.getUuid())) && (localBluetoothGattCharacteristic.getInstanceId() == paramInt)) {
        return localBluetoothGattCharacteristic;
      }
    }
    return null;
  }
  
  public List<BluetoothGattCharacteristic> getCharacteristics()
  {
    return this.mCharacteristics;
  }
  
  BluetoothDevice getDevice()
  {
    return this.mDevice;
  }
  
  int getHandles()
  {
    return this.mHandles;
  }
  
  public List<BluetoothGattService> getIncludedServices()
  {
    return this.mIncludedServices;
  }
  
  public int getInstanceId()
  {
    return this.mInstanceId;
  }
  
  public int getType()
  {
    return this.mServiceType;
  }
  
  public UUID getUuid()
  {
    return this.mUuid;
  }
  
  public boolean isAdvertisePreferred()
  {
    return this.mAdvertisePreferred;
  }
  
  public void setAdvertisePreferred(boolean paramBoolean)
  {
    this.mAdvertisePreferred = paramBoolean;
  }
  
  void setDevice(BluetoothDevice paramBluetoothDevice)
  {
    this.mDevice = paramBluetoothDevice;
  }
  
  public void setHandles(int paramInt)
  {
    this.mHandles = paramInt;
  }
  
  public void setInstanceId(int paramInt)
  {
    this.mInstanceId = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(new ParcelUuid(this.mUuid), 0);
    paramParcel.writeInt(this.mInstanceId);
    paramParcel.writeInt(this.mServiceType);
    paramParcel.writeTypedList(this.mCharacteristics);
    ArrayList localArrayList = new ArrayList(this.mIncludedServices.size());
    Iterator localIterator = this.mIncludedServices.iterator();
    while (localIterator.hasNext())
    {
      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
      localArrayList.add(new BluetoothGattIncludedService(localBluetoothGattService.getUuid(), localBluetoothGattService.getInstanceId(), localBluetoothGattService.getType()));
    }
    paramParcel.writeTypedList(localArrayList);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */