package android.hardware.camera2.params;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Size;
import com.android.internal.util.Preconditions;

public final class MeteringRectangle
{
  public static final int METERING_WEIGHT_DONT_CARE = 0;
  public static final int METERING_WEIGHT_MAX = 1000;
  public static final int METERING_WEIGHT_MIN = 0;
  private final int mHeight;
  private final int mWeight;
  private final int mWidth;
  private final int mX;
  private final int mY;
  
  public MeteringRectangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mX = Preconditions.checkArgumentNonnegative(paramInt1, "x must be nonnegative");
    this.mY = Preconditions.checkArgumentNonnegative(paramInt2, "y must be nonnegative");
    this.mWidth = Preconditions.checkArgumentNonnegative(paramInt3, "width must be nonnegative");
    this.mHeight = Preconditions.checkArgumentNonnegative(paramInt4, "height must be nonnegative");
    this.mWeight = Preconditions.checkArgumentInRange(paramInt5, 0, 1000, "meteringWeight");
  }
  
  public MeteringRectangle(Point paramPoint, Size paramSize, int paramInt)
  {
    Preconditions.checkNotNull(paramPoint, "xy must not be null");
    Preconditions.checkNotNull(paramSize, "dimensions must not be null");
    this.mX = Preconditions.checkArgumentNonnegative(paramPoint.x, "x must be nonnegative");
    this.mY = Preconditions.checkArgumentNonnegative(paramPoint.y, "y must be nonnegative");
    this.mWidth = Preconditions.checkArgumentNonnegative(paramSize.getWidth(), "width must be nonnegative");
    this.mHeight = Preconditions.checkArgumentNonnegative(paramSize.getHeight(), "height must be nonnegative");
    this.mWeight = Preconditions.checkArgumentNonnegative(paramInt, "meteringWeight must be nonnegative");
  }
  
  public MeteringRectangle(Rect paramRect, int paramInt)
  {
    Preconditions.checkNotNull(paramRect, "rect must not be null");
    this.mX = Preconditions.checkArgumentNonnegative(paramRect.left, "rect.left must be nonnegative");
    this.mY = Preconditions.checkArgumentNonnegative(paramRect.top, "rect.top must be nonnegative");
    this.mWidth = Preconditions.checkArgumentNonnegative(paramRect.width(), "rect.width must be nonnegative");
    this.mHeight = Preconditions.checkArgumentNonnegative(paramRect.height(), "rect.height must be nonnegative");
    this.mWeight = Preconditions.checkArgumentNonnegative(paramInt, "meteringWeight must be nonnegative");
  }
  
  public boolean equals(MeteringRectangle paramMeteringRectangle)
  {
    boolean bool2 = false;
    if (paramMeteringRectangle == null) {
      return false;
    }
    boolean bool1 = bool2;
    if (this.mX == paramMeteringRectangle.mX)
    {
      bool1 = bool2;
      if (this.mY == paramMeteringRectangle.mY)
      {
        bool1 = bool2;
        if (this.mWidth == paramMeteringRectangle.mWidth)
        {
          bool1 = bool2;
          if (this.mHeight == paramMeteringRectangle.mHeight)
          {
            bool1 = bool2;
            if (this.mWeight == paramMeteringRectangle.mWeight) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MeteringRectangle)) {
      return equals((MeteringRectangle)paramObject);
    }
    return false;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getMeteringWeight()
  {
    return this.mWeight;
  }
  
  public Rect getRect()
  {
    return new Rect(this.mX, this.mY, this.mX + this.mWidth, this.mY + this.mHeight);
  }
  
  public Size getSize()
  {
    return new Size(this.mWidth, this.mHeight);
  }
  
  public Point getUpperLeftPoint()
  {
    return new Point(this.mX, this.mY);
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public int getX()
  {
    return this.mX;
  }
  
  public int getY()
  {
    return this.mY;
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCode(new int[] { this.mX, this.mY, this.mWidth, this.mHeight, this.mWeight });
  }
  
  public String toString()
  {
    return String.format("(x:%d, y:%d, w:%d, h:%d, wt:%d)", new Object[] { Integer.valueOf(this.mX), Integer.valueOf(this.mY), Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mWeight) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/MeteringRectangle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */