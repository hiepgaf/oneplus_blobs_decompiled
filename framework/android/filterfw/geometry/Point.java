package android.filterfw.geometry;

public class Point
{
  public float x;
  public float y;
  
  public Point() {}
  
  public Point(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public boolean IsInUnitRange()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.x >= 0.0F)
    {
      bool1 = bool2;
      if (this.x <= 1.0F)
      {
        bool1 = bool2;
        if (this.y >= 0.0F)
        {
          bool1 = bool2;
          if (this.y <= 1.0F) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public float distanceTo(Point paramPoint)
  {
    return paramPoint.minus(this).length();
  }
  
  public float length()
  {
    return (float)Math.hypot(this.x, this.y);
  }
  
  public Point minus(float paramFloat1, float paramFloat2)
  {
    return new Point(this.x - paramFloat1, this.y - paramFloat2);
  }
  
  public Point minus(Point paramPoint)
  {
    return minus(paramPoint.x, paramPoint.y);
  }
  
  public Point mult(float paramFloat1, float paramFloat2)
  {
    return new Point(this.x * paramFloat1, this.y * paramFloat2);
  }
  
  public Point normalize()
  {
    return scaledTo(1.0F);
  }
  
  public Point plus(float paramFloat1, float paramFloat2)
  {
    return new Point(this.x + paramFloat1, this.y + paramFloat2);
  }
  
  public Point plus(Point paramPoint)
  {
    return plus(paramPoint.x, paramPoint.y);
  }
  
  public Point rotated(float paramFloat)
  {
    return new Point((float)(Math.cos(paramFloat) * this.x - Math.sin(paramFloat) * this.y), (float)(Math.sin(paramFloat) * this.x + Math.cos(paramFloat) * this.y));
  }
  
  public Point rotated90(int paramInt)
  {
    float f2 = this.x;
    float f1 = this.y;
    int i = 0;
    while (i < paramInt)
    {
      float f3 = -f2;
      i += 1;
      f2 = f1;
      f1 = f3;
    }
    return new Point(f2, f1);
  }
  
  public Point rotatedAround(Point paramPoint, float paramFloat)
  {
    return minus(paramPoint).rotated(paramFloat).plus(paramPoint);
  }
  
  public Point scaledTo(float paramFloat)
  {
    return times(paramFloat / length());
  }
  
  public void set(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }
  
  public Point times(float paramFloat)
  {
    return new Point(this.x * paramFloat, this.y * paramFloat);
  }
  
  public String toString()
  {
    return "(" + this.x + ", " + this.y + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/geometry/Point.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */