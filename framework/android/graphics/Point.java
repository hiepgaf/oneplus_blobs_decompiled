package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.PrintWriter;

public class Point
  implements Parcelable
{
  public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator()
  {
    public Point createFromParcel(Parcel paramAnonymousParcel)
    {
      Point localPoint = new Point();
      localPoint.readFromParcel(paramAnonymousParcel);
      return localPoint;
    }
    
    public Point[] newArray(int paramAnonymousInt)
    {
      return new Point[paramAnonymousInt];
    }
  };
  public int x;
  public int y;
  
  public Point() {}
  
  public Point(int paramInt1, int paramInt2)
  {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public Point(Point paramPoint)
  {
    this.x = paramPoint.x;
    this.y = paramPoint.y;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public final boolean equals(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.x == paramInt1)
    {
      bool1 = bool2;
      if (this.y == paramInt2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (Point)paramObject;
    if (this.x != ((Point)paramObject).x) {
      return false;
    }
    return this.y == ((Point)paramObject).y;
  }
  
  public int hashCode()
  {
    return this.x * 31 + this.y;
  }
  
  public final void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public final void offset(int paramInt1, int paramInt2)
  {
    this.x += paramInt1;
    this.y += paramInt2;
  }
  
  public void printShortString(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("[");
    paramPrintWriter.print(this.x);
    paramPrintWriter.print(",");
    paramPrintWriter.print(this.y);
    paramPrintWriter.print("]");
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.x = paramParcel.readInt();
    this.y = paramParcel.readInt();
  }
  
  public void set(int paramInt1, int paramInt2)
  {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public String toString()
  {
    return "Point(" + this.x + ", " + this.y + ")";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.x);
    paramParcel.writeInt(this.y);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Point.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */