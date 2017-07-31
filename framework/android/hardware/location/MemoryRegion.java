package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class MemoryRegion
  implements Parcelable
{
  public static final Parcelable.Creator<MemoryRegion> CREATOR = new Parcelable.Creator()
  {
    public MemoryRegion createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MemoryRegion(paramAnonymousParcel);
    }
    
    public MemoryRegion[] newArray(int paramAnonymousInt)
    {
      return new MemoryRegion[paramAnonymousInt];
    }
  };
  private boolean mIsExecutable;
  private boolean mIsReadable;
  private boolean mIsWritable;
  private int mSizeBytes;
  private int mSizeBytesFree;
  
  public MemoryRegion(Parcel paramParcel)
  {
    this.mSizeBytes = paramParcel.readInt();
    this.mSizeBytesFree = paramParcel.readInt();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.mIsReadable = bool1;
      if (paramParcel.readInt() == 0) {
        break label70;
      }
      bool1 = true;
      label45:
      this.mIsWritable = bool1;
      if (paramParcel.readInt() == 0) {
        break label75;
      }
    }
    label70:
    label75:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsExecutable = bool1;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label45;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCapacityBytes()
  {
    return this.mSizeBytes;
  }
  
  public int getFreeCapacityBytes()
  {
    return this.mSizeBytesFree;
  }
  
  public boolean isExecutable()
  {
    return this.mIsExecutable;
  }
  
  public boolean isReadable()
  {
    return this.mIsReadable;
  }
  
  public boolean isWritable()
  {
    return this.mIsWritable;
  }
  
  public String toString()
  {
    if (isReadable())
    {
      str = "" + "r";
      if (!isWritable()) {
        break label150;
      }
      str = str + "w";
      label55:
      if (!isExecutable()) {
        break label173;
      }
    }
    label150:
    label173:
    for (String str = str + "x";; str = str + "-")
    {
      return "[ " + this.mSizeBytesFree + "/ " + this.mSizeBytes + " ] : " + str;
      str = "" + "-";
      break;
      str = str + "-";
      break label55;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(this.mSizeBytes);
    paramParcel.writeInt(this.mSizeBytesFree);
    if (this.mIsReadable)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mIsWritable) {
        break label66;
      }
      paramInt = 1;
      label41:
      paramParcel.writeInt(paramInt);
      if (!this.mIsExecutable) {
        break label71;
      }
    }
    label66:
    label71:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label41;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/MemoryRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */