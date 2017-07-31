package android.filterfw.geometry;

public class Rectangle
  extends Quad
{
  public Rectangle() {}
  
  public Rectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    super(new Point(paramFloat1, paramFloat2), new Point(paramFloat1 + paramFloat3, paramFloat2), new Point(paramFloat1, paramFloat2 + paramFloat4), new Point(paramFloat1 + paramFloat3, paramFloat2 + paramFloat4));
  }
  
  public Rectangle(Point paramPoint1, Point paramPoint2)
  {
    super(paramPoint1, paramPoint1.plus(paramPoint2.x, 0.0F), paramPoint1.plus(0.0F, paramPoint2.y), paramPoint1.plus(paramPoint2.x, paramPoint2.y));
  }
  
  private Rectangle(Point paramPoint1, Point paramPoint2, Point paramPoint3, Point paramPoint4)
  {
    super(paramPoint1, paramPoint2, paramPoint3, paramPoint4);
  }
  
  public static Rectangle fromCenterVerticalAxis(Point paramPoint1, Point paramPoint2, Point paramPoint3)
  {
    Point localPoint = paramPoint2.scaledTo(paramPoint3.y / 2.0F);
    paramPoint2 = paramPoint2.rotated90(1).scaledTo(paramPoint3.x / 2.0F);
    return new Rectangle(paramPoint1.minus(paramPoint2).minus(localPoint), paramPoint1.plus(paramPoint2).minus(localPoint), paramPoint1.minus(paramPoint2).plus(localPoint), paramPoint1.plus(paramPoint2).plus(localPoint));
  }
  
  public static Rectangle fromRotatedRect(Point paramPoint1, Point paramPoint2, float paramFloat)
  {
    Point localPoint1 = new Point(paramPoint1.x - paramPoint2.x / 2.0F, paramPoint1.y - paramPoint2.y / 2.0F);
    Point localPoint2 = new Point(paramPoint1.x + paramPoint2.x / 2.0F, paramPoint1.y - paramPoint2.y / 2.0F);
    Point localPoint3 = new Point(paramPoint1.x - paramPoint2.x / 2.0F, paramPoint1.y + paramPoint2.y / 2.0F);
    paramPoint2 = new Point(paramPoint1.x + paramPoint2.x / 2.0F, paramPoint1.y + paramPoint2.y / 2.0F);
    return new Rectangle(localPoint1.rotatedAround(paramPoint1, paramFloat), localPoint2.rotatedAround(paramPoint1, paramFloat), localPoint3.rotatedAround(paramPoint1, paramFloat), paramPoint2.rotatedAround(paramPoint1, paramFloat));
  }
  
  public Point center()
  {
    return this.p0.plus(this.p1).plus(this.p2).plus(this.p3).times(0.25F);
  }
  
  public float getHeight()
  {
    return this.p2.minus(this.p0).length();
  }
  
  public float getWidth()
  {
    return this.p1.minus(this.p0).length();
  }
  
  public Rectangle scaled(float paramFloat)
  {
    return new Rectangle(this.p0.times(paramFloat), this.p1.times(paramFloat), this.p2.times(paramFloat), this.p3.times(paramFloat));
  }
  
  public Rectangle scaled(float paramFloat1, float paramFloat2)
  {
    return new Rectangle(this.p0.mult(paramFloat1, paramFloat2), this.p1.mult(paramFloat1, paramFloat2), this.p2.mult(paramFloat1, paramFloat2), this.p3.mult(paramFloat1, paramFloat2));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/geometry/Rectangle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */