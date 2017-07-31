package android.bluetooth.le;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ResultStorageDescriptor
  implements Parcelable
{
  public static final Parcelable.Creator<ResultStorageDescriptor> CREATOR = new Parcelable.Creator()
  {
    public ResultStorageDescriptor createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ResultStorageDescriptor(paramAnonymousParcel, null);
    }
    
    public ResultStorageDescriptor[] newArray(int paramAnonymousInt)
    {
      return new ResultStorageDescriptor[paramAnonymousInt];
    }
  };
  private int mLength;
  private int mOffset;
  private int mType;
  
  public ResultStorageDescriptor(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mType = paramInt1;
    this.mOffset = paramInt2;
    this.mLength = paramInt3;
  }
  
  private ResultStorageDescriptor(Parcel paramParcel)
  {
    ReadFromParcel(paramParcel);
  }
  
  private void ReadFromParcel(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mOffset = paramParcel.readInt();
    this.mLength = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getLength()
  {
    return this.mLength;
  }
  
  public int getOffset()
  {
    return this.mOffset;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mOffset);
    paramParcel.writeInt(this.mLength);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ResultStorageDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */