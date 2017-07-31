package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PointF
  implements Parcelable
{
  public static final Parcelable.Creator<PointF> CREATOR = new Parcelable.Creator()
  {
    public PointF createFromParcel(Parcel paramAnonymousParcel)
    {
      PointF localPointF = new PointF();
      localPointF.readFromParcel(paramAnonymousParcel);
      return localPointF;
    }
    
    public PointF[] newArray(int paramAnonymousInt)
    {
      return new PointF[paramAnonymousInt];
    }
  };
  public float x;
  public float y;
  
  public PointF() {}
  
  public PointF(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public PointF(Point paramPoint)
  {
    this.x = paramPoint.x;
    this.y = paramPoint.y;
  }
  
  public static float length(float paramFloat1, float paramFloat2)
  {
    return (float)Math.hypot(paramFloat1, paramFloat2);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public final boolean equals(float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.x == paramFloat1)
    {
      bool1 = bool2;
      if (this.y == paramFloat2) {
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
    paramObject = (PointF)paramObject;
    if (Float.compare(((PointF)paramObject).x, this.x) != 0) {
      return false;
    }
    return Float.compare(((PointF)paramObject).y, this.y) == 0;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.x != 0.0F) {}
    for (int i = Float.floatToIntBits(this.x);; i = 0)
    {
      if (this.y != 0.0F) {
        j = Float.floatToIntBits(this.y);
      }
      return i * 31 + j;
    }
  }
  
  public final float length()
  {
    return length(this.x, this.y);
  }
  
  public final void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public final void offset(float paramFloat1, float paramFloat2)
  {
    this.x += paramFloat1;
    this.y += paramFloat2;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.x = paramParcel.readFloat();
    this.y = paramParcel.readFloat();
  }
  
  public final void set(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public final void set(PointF paramPointF)
  {
    this.x = paramPointF.x;
    this.y = paramPointF.y;
  }
  
  public String toString()
  {
    return "PointF(" + this.x + ", " + this.y + ")";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.x);
    paramParcel.writeFloat(this.y);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PointF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */