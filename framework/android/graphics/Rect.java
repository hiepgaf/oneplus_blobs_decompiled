package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Rect
  implements Parcelable
{
  public static final Parcelable.Creator<Rect> CREATOR = new Parcelable.Creator()
  {
    public Rect createFromParcel(Parcel paramAnonymousParcel)
    {
      Rect localRect = new Rect();
      localRect.readFromParcel(paramAnonymousParcel);
      return localRect;
    }
    
    public Rect[] newArray(int paramAnonymousInt)
    {
      return new Rect[paramAnonymousInt];
    }
  };
  public int bottom;
  public int left;
  public int right;
  public int top;
  
  public Rect() {}
  
  public Rect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.left = paramInt1;
    this.top = paramInt2;
    this.right = paramInt3;
    this.bottom = paramInt4;
  }
  
  public Rect(Rect paramRect)
  {
    if (paramRect == null)
    {
      this.bottom = 0;
      this.right = 0;
      this.top = 0;
      this.left = 0;
      return;
    }
    this.left = paramRect.left;
    this.top = paramRect.top;
    this.right = paramRect.right;
    this.bottom = paramRect.bottom;
  }
  
  public static boolean intersects(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramRect1.left < paramRect2.right)
    {
      bool1 = bool2;
      if (paramRect2.left < paramRect1.right)
      {
        bool1 = bool2;
        if (paramRect1.top < paramRect2.bottom)
        {
          bool1 = bool2;
          if (paramRect2.top < paramRect1.bottom) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public static Rect unflattenFromString(String paramString)
  {
    paramString = UnflattenHelper.getMatcher(paramString);
    if (!paramString.matches()) {
      return null;
    }
    return new Rect(Integer.parseInt(paramString.group(1)), Integer.parseInt(paramString.group(2)), Integer.parseInt(paramString.group(3)), Integer.parseInt(paramString.group(4)));
  }
  
  public final int centerX()
  {
    return this.left + this.right >> 1;
  }
  
  public final int centerY()
  {
    return this.top + this.bottom >> 1;
  }
  
  public boolean contains(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (paramInt1 >= this.left)
        {
          bool1 = bool2;
          if (paramInt1 < this.right)
          {
            bool1 = bool2;
            if (paramInt2 >= this.top)
            {
              bool1 = bool2;
              if (paramInt2 < this.bottom) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (this.left <= paramInt1)
        {
          bool1 = bool2;
          if (this.top <= paramInt2)
          {
            bool1 = bool2;
            if (this.right >= paramInt3)
            {
              bool1 = bool2;
              if (this.bottom >= paramInt4) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean contains(Rect paramRect)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < this.right)
    {
      bool1 = bool2;
      if (this.top < this.bottom)
      {
        bool1 = bool2;
        if (this.left <= paramRect.left)
        {
          bool1 = bool2;
          if (this.top <= paramRect.top)
          {
            bool1 = bool2;
            if (this.right >= paramRect.right)
            {
              bool1 = bool2;
              if (this.bottom >= paramRect.bottom) {
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
    paramObject = (Rect)paramObject;
    return (this.left == ((Rect)paramObject).left) && (this.top == ((Rect)paramObject).top) && (this.right == ((Rect)paramObject).right) && (this.bottom == ((Rect)paramObject).bottom);
  }
  
  public final float exactCenterX()
  {
    return (this.left + this.right) * 0.5F;
  }
  
  public final float exactCenterY()
  {
    return (this.top + this.bottom) * 0.5F;
  }
  
  public String flattenToString()
  {
    StringBuilder localStringBuilder = new StringBuilder(32);
    localStringBuilder.append(this.left);
    localStringBuilder.append(' ');
    localStringBuilder.append(this.top);
    localStringBuilder.append(' ');
    localStringBuilder.append(this.right);
    localStringBuilder.append(' ');
    localStringBuilder.append(this.bottom);
    return localStringBuilder.toString();
  }
  
  public int hashCode()
  {
    return ((this.left * 31 + this.top) * 31 + this.right) * 31 + this.bottom;
  }
  
  public final int height()
  {
    return this.bottom - this.top;
  }
  
  public void inset(int paramInt1, int paramInt2)
  {
    this.left += paramInt1;
    this.top += paramInt2;
    this.right -= paramInt1;
    this.bottom -= paramInt2;
  }
  
  public void inset(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.left += paramInt1;
    this.top += paramInt2;
    this.right -= paramInt3;
    this.bottom -= paramInt4;
  }
  
  public void inset(Rect paramRect)
  {
    this.left += paramRect.left;
    this.top += paramRect.top;
    this.right -= paramRect.right;
    this.bottom -= paramRect.bottom;
  }
  
  public boolean intersect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.left < paramInt3) && (paramInt1 < this.right) && (this.top < paramInt4) && (paramInt2 < this.bottom))
    {
      if (this.left < paramInt1) {
        this.left = paramInt1;
      }
      if (this.top < paramInt2) {
        this.top = paramInt2;
      }
      if (this.right > paramInt3) {
        this.right = paramInt3;
      }
      if (this.bottom > paramInt4) {
        this.bottom = paramInt4;
      }
      return true;
    }
    return false;
  }
  
  public boolean intersect(Rect paramRect)
  {
    return intersect(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public boolean intersects(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.left < paramInt3)
    {
      bool1 = bool2;
      if (paramInt1 < this.right)
      {
        bool1 = bool2;
        if (this.top < paramInt4)
        {
          bool1 = bool2;
          if (paramInt2 < this.bottom) {
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
  
  public void offset(int paramInt1, int paramInt2)
  {
    this.left += paramInt1;
    this.top += paramInt2;
    this.right += paramInt1;
    this.bottom += paramInt2;
  }
  
  public void offsetTo(int paramInt1, int paramInt2)
  {
    this.right += paramInt1 - this.left;
    this.bottom += paramInt2 - this.top;
    this.left = paramInt1;
    this.top = paramInt2;
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
    this.left = paramParcel.readInt();
    this.top = paramParcel.readInt();
    this.right = paramParcel.readInt();
    this.bottom = paramParcel.readInt();
  }
  
  public void scale(float paramFloat)
  {
    if (paramFloat != 1.0F)
    {
      this.left = ((int)(this.left * paramFloat + 0.5F));
      this.top = ((int)(this.top * paramFloat + 0.5F));
      this.right = ((int)(this.right * paramFloat + 0.5F));
      this.bottom = ((int)(this.bottom * paramFloat + 0.5F));
    }
  }
  
  public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.left = paramInt1;
    this.top = paramInt2;
    this.right = paramInt3;
    this.bottom = paramInt4;
  }
  
  public void set(Rect paramRect)
  {
    this.left = paramRect.left;
    this.top = paramRect.top;
    this.right = paramRect.right;
    this.bottom = paramRect.bottom;
  }
  
  public void setEmpty()
  {
    this.bottom = 0;
    this.top = 0;
    this.right = 0;
    this.left = 0;
  }
  
  public boolean setIntersect(Rect paramRect1, Rect paramRect2)
  {
    if ((paramRect1.left < paramRect2.right) && (paramRect2.left < paramRect1.right) && (paramRect1.top < paramRect2.bottom) && (paramRect2.top < paramRect1.bottom))
    {
      this.left = Math.max(paramRect1.left, paramRect2.left);
      this.top = Math.max(paramRect1.top, paramRect2.top);
      this.right = Math.min(paramRect1.right, paramRect2.right);
      this.bottom = Math.min(paramRect1.bottom, paramRect2.bottom);
      return true;
    }
    return false;
  }
  
  public void sort()
  {
    int i;
    if (this.left > this.right)
    {
      i = this.left;
      this.left = this.right;
      this.right = i;
    }
    if (this.top > this.bottom)
    {
      i = this.top;
      this.top = this.bottom;
      this.bottom = i;
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
    StringBuilder localStringBuilder = new StringBuilder(32);
    localStringBuilder.append("Rect(");
    localStringBuilder.append(this.left);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.top);
    localStringBuilder.append(" - ");
    localStringBuilder.append(this.right);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.bottom);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void union(int paramInt1, int paramInt2)
  {
    if (paramInt1 < this.left) {
      this.left = paramInt1;
    }
    do
    {
      while (paramInt2 < this.top)
      {
        this.top = paramInt2;
        return;
        if (paramInt1 > this.right) {
          this.right = paramInt1;
        }
      }
    } while (paramInt2 <= this.bottom);
    this.bottom = paramInt2;
  }
  
  public void union(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 < paramInt3) && (paramInt2 < paramInt4))
    {
      if ((this.left >= this.right) || (this.top >= this.bottom)) {
        break label88;
      }
      if (this.left > paramInt1) {
        this.left = paramInt1;
      }
      if (this.top > paramInt2) {
        this.top = paramInt2;
      }
      if (this.right < paramInt3) {
        this.right = paramInt3;
      }
      if (this.bottom < paramInt4) {
        this.bottom = paramInt4;
      }
    }
    return;
    label88:
    this.left = paramInt1;
    this.top = paramInt2;
    this.right = paramInt3;
    this.bottom = paramInt4;
  }
  
  public void union(Rect paramRect)
  {
    union(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public final int width()
  {
    return this.right - this.left;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.left);
    paramParcel.writeInt(this.top);
    paramParcel.writeInt(this.right);
    paramParcel.writeInt(this.bottom);
  }
  
  private static final class UnflattenHelper
  {
    private static final Pattern FLATTENED_PATTERN = Pattern.compile("(-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)");
    
    static Matcher getMatcher(String paramString)
    {
      return FLATTENED_PATTERN.matcher(paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Rect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */