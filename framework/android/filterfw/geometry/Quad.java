package android.filterfw.geometry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Quad
{
  public Point p0;
  public Point p1;
  public Point p2;
  public Point p3;
  
  public Quad() {}
  
  public Quad(Point paramPoint1, Point paramPoint2, Point paramPoint3, Point paramPoint4)
  {
    this.p0 = paramPoint1;
    this.p1 = paramPoint2;
    this.p2 = paramPoint3;
    this.p3 = paramPoint4;
  }
  
  public boolean IsInUnitRange()
  {
    if ((this.p0.IsInUnitRange()) && (this.p1.IsInUnitRange()) && (this.p2.IsInUnitRange())) {
      return this.p3.IsInUnitRange();
    }
    return false;
  }
  
  public Rectangle boundingBox()
  {
    List localList1 = Arrays.asList(new Float[] { Float.valueOf(this.p0.x), Float.valueOf(this.p1.x), Float.valueOf(this.p2.x), Float.valueOf(this.p3.x) });
    List localList2 = Arrays.asList(new Float[] { Float.valueOf(this.p0.y), Float.valueOf(this.p1.y), Float.valueOf(this.p2.y), Float.valueOf(this.p3.y) });
    float f1 = ((Float)Collections.min(localList1)).floatValue();
    float f2 = ((Float)Collections.min(localList2)).floatValue();
    return new Rectangle(f1, f2, ((Float)Collections.max(localList1)).floatValue() - f1, ((Float)Collections.max(localList2)).floatValue() - f2);
  }
  
  public float getBoundingHeight()
  {
    List localList = Arrays.asList(new Float[] { Float.valueOf(this.p0.y), Float.valueOf(this.p1.y), Float.valueOf(this.p2.y), Float.valueOf(this.p3.y) });
    return ((Float)Collections.max(localList)).floatValue() - ((Float)Collections.min(localList)).floatValue();
  }
  
  public float getBoundingWidth()
  {
    List localList = Arrays.asList(new Float[] { Float.valueOf(this.p0.x), Float.valueOf(this.p1.x), Float.valueOf(this.p2.x), Float.valueOf(this.p3.x) });
    return ((Float)Collections.max(localList)).floatValue() - ((Float)Collections.min(localList)).floatValue();
  }
  
  public Quad scaled(float paramFloat)
  {
    return new Quad(this.p0.times(paramFloat), this.p1.times(paramFloat), this.p2.times(paramFloat), this.p3.times(paramFloat));
  }
  
  public Quad scaled(float paramFloat1, float paramFloat2)
  {
    return new Quad(this.p0.mult(paramFloat1, paramFloat2), this.p1.mult(paramFloat1, paramFloat2), this.p2.mult(paramFloat1, paramFloat2), this.p3.mult(paramFloat1, paramFloat2));
  }
  
  public String toString()
  {
    return "{" + this.p0 + ", " + this.p1 + ", " + this.p2 + ", " + this.p3 + "}";
  }
  
  public Quad translated(float paramFloat1, float paramFloat2)
  {
    return new Quad(this.p0.plus(paramFloat1, paramFloat2), this.p1.plus(paramFloat1, paramFloat2), this.p2.plus(paramFloat1, paramFloat2), this.p3.plus(paramFloat1, paramFloat2));
  }
  
  public Quad translated(Point paramPoint)
  {
    return new Quad(this.p0.plus(paramPoint), this.p1.plus(paramPoint), this.p2.plus(paramPoint), this.p3.plus(paramPoint));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/geometry/Quad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */