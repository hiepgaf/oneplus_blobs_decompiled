package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.FastMath;
import java.io.PrintWriter;

public class RectF
  implements Parcelable
{
  public static final Parcelable.Creator<RectF> CREATOR = new Parcelable.Creator()
  {
    public RectF createFromParcel(Parcel paramAnonymousParcel)
    {
      RectF localRectF = new RectF();
      localRectF.readFromParcel(paramAnonymousParcel);
      return localRectF;
    }
    
    public RectF[] newArray(int paramAnonymousInt)
    {
      return new RectF[paramAnonymousInt];
    }
  };
  public float bottom;
  public float left;
  public float right;
  public float top;
  
  public RectF() {}
  
  public RectF(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.left = paramFloat1;
    this.top = paramFloat2;
    this.right = paramFloat3;
    this.bottom = paramFloat4;
  }
  
  public RectF(Rect paramRect)
  {
    if (paramRect == null)
    {
      this.bottom = 0.0F;
      this.right = 0.0F;
      this.top = 0.0F;
      this.left = 0.0F;
      return;
    }
    this.left = paramRect.left;
    this.top = paramRect.top;
    this.right = paramRect.right;
    this.bottom = paramRect.bottom;
  }
  
  public RectF(RectF paramRectF)
  {
    if (paramRectF == null)
    {
      this.bottom = 0.0F;
      this.right = 0.0F;
      this.top = 0.0F;
      this.left = 0.0F;
      return;
    }
    this.left = paramRectF.left;
    this.top = paramRectF.top;
    this.right = paramRectF.right;
    this.bottom = paramRectF.bottom;
  }
  
  public static boolean intersects(RectF paramRectF1, RectF paramRectF2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramRectF1.left < paramRectF2.right)
    {
      bool1 = bool2;
      if (paramRectF2.left < paramRectF1.right)
      {
        bool1 = bool2;
        if (paramRectF1.top < paramRectF2.bottom)
        {
          bool1 = bool2;
          if (paramRectF2.top < paramRectF1.bottom) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public final float centerX()
  {
    return (this.left + this.right) * 0.5F;
  }
  
  public final float centerY()
  {
    return (this.top + this.bottom) * 0.5F;
  }
  
  public boolean contains(float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (paramFloat1 >= this.left)
        {
          bool1 = bool2;
          if (paramFloat1 < this.right)
          {
            bool1 = bool2;
            if (paramFloat2 >= this.top)
            {
              bool1 = bool2;
              if (paramFloat2 < this.bottom) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean contains(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (this.left <= paramFloat1)
        {
          bool1 = bool2;
          if (this.top <= paramFloat2)
          {
            bool1 = bool2;
            if (this.right >= paramFloat3)
            {
              bool1 = bool2;
              if (this.bottom >= paramFloat4) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean contains(RectF paramRectF)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (this.left <= paramRectF.left)
        {
          bool1 = bool2;
          if (this.top <= paramRectF.top)
          {
            bool1 = bool2;
            if (this.right >= paramRectF.right)
            {
              bool1 = bool2;
              if (this.bottom >= paramRectF.bottom) {
                bool1 = true;
              }
            }
          }
        }
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
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (RectF)paramObject;
    return (this.left == ((RectF)paramObject).left) && (this.top == ((RectF)paramObject).top) && (this.right == ((RectF)paramObject).right) && (this.bottom == ((RectF)paramObject).bottom);
  }
  
  public int hashCode()
  {
    int m = 0;
    int i;
    int j;
    if (this.left != 0.0F)
    {
      i = Float.floatToIntBits(this.left);
      if (this.top == 0.0F) {
        break label95;
      }
      j = Float.floatToIntBits(this.top);
      label37:
      if (this.right == 0.0F) {
        break label100;
      }
    }
    label95:
    label100:
    for (int k = Float.floatToIntBits(this.right);; k = 0)
    {
      if (this.bottom != 0.0F) {
        m = Float.floatToIntBits(this.bottom);
      }
      return ((i * 31 + j) * 31 + k) * 31 + m;
      i = 0;
      break;
      j = 0;
      break label37;
    }
  }
  
  public final float height()
  {
    return this.bottom - this.top;
  }
  
  public void inset(float paramFloat1, float paramFloat2)
  {
    this.left += paramFloat1;
    this.top += paramFloat2;
    this.right -= paramFloat1;
    this.bottom -= paramFloat2;
  }
  
  public boolean intersect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((this.left < paramFloat3) && (paramFloat1 < this.right) && (this.top < paramFloat4) && (paramFloat2 < this.bottom))
    {
      if (this.left < paramFloat1) {
        this.left = paramFloat1;
      }
      if (this.top < paramFloat2) {
        this.top = paramFloat2;
      }
      if (this.right > paramFloat3) {
        this.right = paramFloat3;
      }
      if (this.bottom > paramFloat4) {
        this.bottom = paramFloat4;
      }
      return true;
    }
    return false;
  }
  
  public boolean intersect(RectF paramRectF)
  {
    return intersect(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom);
  }
  
  public boolean intersects(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < paramFloat3)
    {
      bool1 = bool2;
      if (paramFloat1 < this.right)
      {
        bool1 = bool2;
        if (this.top < paramFloat4)
        {
          bool1 = bool2;
          if (paramFloat2 < this.bottom) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public final boolean isEmpty()
  {
    return (this.left >= this.right) || (this.top >= this.bottom);
  }
  
  public void offset(float paramFloat1, float paramFloat2)
  {
    this.left += paramFloat1;
    this.top += paramFloat2;
    this.right += paramFloat1;
    this.bottom += paramFloat2;
  }
  
  public void offsetTo(float paramFloat1, float paramFloat2)
  {
    this.right += paramFloat1 - this.left;
    this.bottom += paramFloat2 - this.top;
    this.left = paramFloat1;
    this.top = paramFloat2;
  }
  
  public void printShortString(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print('[');
    paramPrintWriter.print(this.left);
    paramPrintWriter.print(',');
    paramPrintWriter.print(this.top);
    paramPrintWriter.print("][");
    paramPrintWriter.print(this.right);
    paramPrintWriter.print(',');
    paramPrintWriter.print(this.bottom);
    paramPrintWriter.print(']');
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.left = paramParcel.readFloat();
    this.top = paramParcel.readFloat();
    this.right = paramParcel.readFloat();
    this.bottom = paramParcel.readFloat();
  }
  
  public void round(Rect paramRect)
  {
    paramRect.set(FastMath.round(this.left), FastMath.round(this.top), FastMath.round(this.right), FastMath.round(this.bottom));
  }
  
  public void roundOut(Rect paramRect)
  {
    paramRect.set((int)Math.floor(this.left), (int)Math.floor(this.top), (int)Math.ceil(this.right), (int)Math.ceil(this.bottom));
  }
  
  public void set(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.left = paramFloat1;
    this.top = paramFloat2;
    this.right = paramFloat3;
    this.bottom = paramFloat4;
  }
  
  public void set(Rect paramRect)
  {
    this.left = paramRect.left;
    this.top = paramRect.top;
    this.right = paramRect.right;
    this.bottom = paramRect.bottom;
  }
  
  public void set(RectF paramRectF)
  {
    this.left = paramRectF.left;
    this.top = paramRectF.top;
    this.right = paramRectF.right;
    this.bottom = paramRectF.bottom;
  }
  
  public void setEmpty()
  {
    this.bottom = 0.0F;
    this.top = 0.0F;
    this.right = 0.0F;
    this.left = 0.0F;
  }
  
  public boolean setIntersect(RectF paramRectF1, RectF paramRectF2)
  {
    if ((paramRectF1.left < paramRectF2.right) && (paramRectF2.left < paramRectF1.right) && (paramRectF1.top < paramRectF2.bottom) && (paramRectF2.top < paramRectF1.bottom))
    {
      this.left = Math.max(paramRectF1.left, paramRectF2.left);
      this.top = Math.max(paramRectF1.top, paramRectF2.top);
      this.right = Math.min(paramRectF1.right, paramRectF2.right);
      this.bottom = Math.min(paramRectF1.bottom, paramRectF2.bottom);
      return true;
    }
    return false;
  }
  
  public void sort()
  {
    float f;
    if (this.left > this.right)
    {
      f = this.left;
      this.left = this.right;
      this.right = f;
    }
    if (this.top > this.bottom)
    {
      f = this.top;
      this.top = this.bottom;
      this.bottom = f;
    }
  }
  
  public String toShortString()
  {
    return toShortString(new StringBuilder(32));
  }
  
  public String toShortString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.setLength(0);
    paramStringBuilder.append('[');
    paramStringBuilder.append(this.left);
    paramStringBuilder.append(',');
    paramStringBuilder.append(this.top);
    paramStringBuilder.append("][");
    paramStringBuilder.append(this.right);
    paramStringBuilder.append(',');
    paramStringBuilder.append(this.bottom);
    paramStringBuilder.append(']');
    return paramStringBuilder.toString();
  }
  
  public String toString()
  {
    return "RectF(" + this.left + ", " + this.top + ", " + this.right + ", " + this.bottom + ")";
  }
  
  public void union(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 < this.left) {
      this.left = paramFloat1;
    }
    do
    {
      while (paramFloat2 < this.top)
      {
        this.top = paramFloat2;
        return;
        if (paramFloat1 > this.right) {
          this.right = paramFloat1;
        }
      }
    } while (paramFloat2 <= this.bottom);
    this.bottom = paramFloat2;
  }
  
  public void union(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((paramFloat1 < paramFloat3) && (paramFloat2 < paramFloat4))
    {
      if ((this.left >= this.right) || (this.top >= this.bottom)) {
        break label96;
      }
      if (this.left > paramFloat1) {
        this.left = paramFloat1;
      }
      if (this.top > paramFloat2) {
        this.top = paramFloat2;
      }
      if (this.right < paramFloat3) {
        this.right = paramFloat3;
      }
      if (this.bottom < paramFloat4) {
        this.bottom = paramFloat4;
      }
    }
    return;
    label96:
    this.left = paramFloat1;
    this.top = paramFloat2;
    this.right = paramFloat3;
    this.bottom = paramFloat4;
  }
  
  public void union(RectF paramRectF)
  {
    union(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom);
  }
  
  public final float width()
  {
    return this.right - this.left;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.left);
    paramParcel.writeFloat(this.top);
    paramParcel.writeFloat(this.right);
    paramParcel.writeFloat(this.bottom);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/RectF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */