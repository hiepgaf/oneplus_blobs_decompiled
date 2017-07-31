package android.bluetooth;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.UUID;

public class BluetoothGattIncludedService
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothGattIncludedService> CREATOR = new Parcelable.Creator()
  {
    public BluetoothGattIncludedService createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothGattIncludedService(paramAnonymousParcel, null);
    }
    
    public BluetoothGattIncludedService[] newArray(int paramAnonymousInt)
    {
      return new BluetoothGattIncludedService[paramAnonymousInt];
    }
  };
  protected int mInstanceId;
  protected int mServiceType;
  protected UUID mUuid;
  
  private BluetoothGattIncludedService(Parcel paramParcel)
  {
    this.mUuid = ((ParcelUuid)paramParcel.readParcelable(null)).getUuid();
    this.mInstanceId = paramParcel.readInt();
    this.mServiceType = paramParcel.readInt();
  }
  
  public BluetoothGattIncludedService(UUID paramUUID, int paramInt1, int paramInt2)
  {
    this.mUuid = paramUUID;
    this.mInstanceId = paramInt1;
    this.mServiceType = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
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
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(new ParcelUuid(this.mUuid), 0);
    paramParcel.writeInt(this.mInstanceId);
    paramParcel.writeInt(this.mServiceType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattIncludedService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */