package android.bluetooth.le;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class ScanResult
  implements Parcelable
{
  public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator()
  {
    public ScanResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ScanResult(paramAnonymousParcel, null);
    }
    
    public ScanResult[] newArray(int paramAnonymousInt)
    {
      return new ScanResult[paramAnonymousInt];
    }
  };
  private BluetoothDevice mDevice;
  private int mRssi;
  private ScanRecord mScanRecord;
  private long mTimestampNanos;
  
  public ScanResult(BluetoothDevice paramBluetoothDevice, ScanRecord paramScanRecord, int paramInt, long paramLong)
  {
    this.mDevice = paramBluetoothDevice;
    this.mScanRecord = paramScanRecord;
    this.mRssi = paramInt;
    this.mTimestampNanos = paramLong;
  }
  
  private ScanResult(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    if (paramParcel.readInt() == 1) {
      this.mDevice = ((BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() == 1) {
      this.mScanRecord = ScanRecord.parseFromBytes(paramParcel.createByteArray());
    }
    this.mRssi = paramParcel.readInt();
    this.mTimestampNanos = paramParcel.readLong();
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
    paramObject = (ScanResult)paramObject;
    if ((Objects.equals(this.mDevice, ((ScanResult)paramObject).mDevice)) && (this.mRssi == ((ScanResult)paramObject).mRssi) && (Objects.equals(this.mScanRecord, ((ScanResult)paramObject).mScanRecord))) {
      return this.mTimestampNanos == ((ScanResult)paramObject).mTimestampNanos;
    }
    return false;
  }
  
  public BluetoothDevice getDevice()
  {
    return this.mDevice;
  }
  
  public int getRssi()
  {
    return this.mRssi;
  }
  
  public ScanRecord getScanRecord()
  {
    return this.mScanRecord;
  }
  
  public long getTimestampNanos()
  {
    return this.mTimestampNanos;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mDevice, Integer.valueOf(this.mRssi), this.mScanRecord, Long.valueOf(this.mTimestampNanos) });
  }
  
  public String toString()
  {
    return "ScanResult{mDevice=" + this.mDevice + ", mScanRecord=" + Objects.toString(this.mScanRecord) + ", mRssi=" + this.mRssi + ", mTimestampNanos=" + this.mTimestampNanos + '}';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mDevice != null)
    {
      paramParcel.writeInt(1);
      this.mDevice.writeToParcel(paramParcel, paramInt);
      if (this.mScanRecord == null) {
        break label69;
      }
      paramParcel.writeInt(1);
      paramParcel.writeByteArray(this.mScanRecord.getBytes());
    }
    for (;;)
    {
      paramParcel.writeInt(this.mRssi);
      paramParcel.writeLong(this.mTimestampNanos);
      return;
      paramParcel.writeInt(0);
      break;
      label69:
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ScanResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */