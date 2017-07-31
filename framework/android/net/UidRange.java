package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class UidRange
  implements Parcelable
{
  public static final Parcelable.Creator<UidRange> CREATOR = new Parcelable.Creator()
  {
    public UidRange createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UidRange(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public UidRange[] newArray(int paramAnonymousInt)
    {
      return new UidRange[paramAnonymousInt];
    }
  };
  public final int start;
  public final int stop;
  
  public UidRange(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Invalid start UID.");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("Invalid stop UID.");
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("Invalid UID range.");
    }
    this.start = paramInt1;
    this.stop = paramInt2;
  }
  
  public static UidRange createForUser(int paramInt)
  {
    return new UidRange(paramInt * 100000, (paramInt + 1) * 100000 - 1);
  }
  
  public boolean contains(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.start <= paramInt)
    {
      bool1 = bool2;
      if (paramInt <= this.stop) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean containsRange(UidRange paramUidRange)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.start <= paramUidRange.start)
    {
      bool1 = bool2;
      if (paramUidRange.stop <= this.stop) {
        bool1 = true;
      }
    }
    return bool1;
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
    if ((paramObject instanceof UidRange))
    {
      paramObject = (UidRange)paramObject;
      return (this.start == ((UidRange)paramObject).start) && (this.stop == ((UidRange)paramObject).stop);
    }
    return false;
  }
  
  public int getStartUser()
  {
    return this.start / 100000;
  }
  
  public int hashCode()
  {
    return (this.start + 527) * 31 + this.stop;
  }
  
  public String toString()
  {
    return this.start + "-" + this.stop;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.start);
    paramParcel.writeInt(this.stop);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/UidRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */