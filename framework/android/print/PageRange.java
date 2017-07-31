package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class PageRange
  implements Parcelable
{
  public static final PageRange ALL_PAGES = new PageRange(0, Integer.MAX_VALUE);
  public static final PageRange[] ALL_PAGES_ARRAY = { ALL_PAGES };
  public static final Parcelable.Creator<PageRange> CREATOR = new Parcelable.Creator()
  {
    public PageRange createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PageRange(paramAnonymousParcel, null);
    }
    
    public PageRange[] newArray(int paramAnonymousInt)
    {
      return new PageRange[paramAnonymousInt];
    }
  };
  private final int mEnd;
  private final int mStart;
  
  public PageRange(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("start cannot be less than zero.");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("end cannot be less than zero.");
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("start must be lesser than end.");
    }
    this.mStart = paramInt1;
    this.mEnd = paramInt2;
  }
  
  private PageRange(Parcel paramParcel)
  {
    this(paramParcel.readInt(), paramParcel.readInt());
  }
  
  public boolean contains(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= this.mStart)
    {
      bool1 = bool2;
      if (paramInt <= this.mEnd) {
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
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (PageRange)paramObject;
    if (this.mEnd != ((PageRange)paramObject).mEnd) {
      return false;
    }
    return this.mStart == ((PageRange)paramObject).mStart;
  }
  
  public int getEnd()
  {
    return this.mEnd;
  }
  
  public int getSize()
  {
    return this.mEnd - this.mStart + 1;
  }
  
  public int getStart()
  {
    return this.mStart;
  }
  
  public int hashCode()
  {
    return (this.mEnd + 31) * 31 + this.mStart;
  }
  
  public String toString()
  {
    if ((this.mStart == 0) && (this.mEnd == Integer.MAX_VALUE)) {
      return "PageRange[<all pages>]";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PageRange[").append(this.mStart).append(" - ").append(this.mEnd).append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mStart);
    paramParcel.writeInt(this.mEnd);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PageRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */