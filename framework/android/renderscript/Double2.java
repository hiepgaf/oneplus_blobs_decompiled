package android.renderscript;

public class Double2
{
  public double x;
  public double y;
  
  public Double2() {}
  
  public Double2(double paramDouble1, double paramDouble2)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
  }
  
  public Double2(Double2 paramDouble2)
  {
    this.x = paramDouble2.x;
    this.y = paramDouble2.y;
  }
  
  public static Double2 add(Double2 paramDouble2, double paramDouble)
  {
    Double2 localDouble2 = new Double2();
    paramDouble2.x += paramDouble;
    paramDouble2.y += paramDouble;
    return localDouble2;
  }
  
  public static Double2 add(Double2 paramDouble21, Double2 paramDouble22)
  {
    Double2 localDouble2 = new Double2();
    paramDouble21.x += paramDouble22.x;
    paramDouble21.y += paramDouble22.y;
    return localDouble2;
  }
  
  public static Double2 div(Double2 paramDouble2, double paramDouble)
  {
    Double2 localDouble2 = new Double2();
    paramDouble2.x /= paramDouble;
    paramDouble2.y /= paramDouble;
    return localDouble2;
  }
  
  public static Double2 div(Double2 paramDouble21, Double2 paramDouble22)
  {
    Double2 localDouble2 = new Double2();
    paramDouble21.x /= paramDouble22.x;
    paramDouble21.y /= paramDouble22.y;
    return localDouble2;
  }
  
  public static Double dotProduct(Double2 paramDouble21, Double2 paramDouble22)
  {
    return Double.valueOf(paramDouble22.x * paramDouble21.x + paramDouble22.y * paramDouble21.y);
  }
  
  public static Double2 mul(Double2 paramDouble2, double paramDouble)
  {
    Double2 localDouble2 = new Double2();
    paramDouble2.x *= paramDouble;
    paramDouble2.y *= paramDouble;
    return localDouble2;
  }
  
  public static Double2 mul(Double2 paramDouble21, Double2 paramDouble22)
  {
    Double2 localDouble2 = new Double2();
    paramDouble21.x *= paramDouble22.x;
    paramDouble21.y *= paramDouble22.y;
    return localDouble2;
  }
  
  public static Double2 sub(Double2 paramDouble2, double paramDouble)
  {
    Double2 localDouble2 = new Double2();
    paramDouble2.x -= paramDouble;
    paramDouble2.y -= paramDouble;
    return localDouble2;
  }
  
  public static Double2 sub(Double2 paramDouble21, Double2 paramDouble22)
  {
    Double2 localDouble2 = new Double2();
    paramDouble21.x -= paramDouble22.x;
    paramDouble21.y -= paramDouble22.y;
    return localDouble2;
  }
  
  public void add(double paramDouble)
  {
    this.x += paramDouble;
    this.y += paramDouble;
  }
  
  public void add(Double2 paramDouble2)
  {
    this.x += paramDouble2.x;
    this.y += paramDouble2.y;
  }
  
  public void addAt(int paramInt, double paramDouble)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x += paramDouble;
      return;
    }
    this.y += paramDouble;
  }
  
  public void addMultiple(Double2 paramDouble2, double paramDouble)
  {
    this.x += paramDouble2.x * paramDouble;
    this.y += paramDouble2.y * paramDouble;
  }
  
  public void copyTo(double[] paramArrayOfDouble, int paramInt)
  {
    paramArrayOfDouble[paramInt] = this.x;
    paramArrayOfDouble[(paramInt + 1)] = this.y;
  }
  
  public void div(double paramDouble)
  {
    this.x /= paramDouble;
    this.y /= paramDouble;
  }
  
  public void div(Double2 paramDouble2)
  {
    this.x /= paramDouble2.x;
    this.y /= paramDouble2.y;
  }
  
  public double dotProduct(Double2 paramDouble2)
  {
    return this.x * paramDouble2.x + this.y * paramDouble2.y;
  }
  
  public double elementSum()
  {
    return this.x + this.y;
  }
  
  public double get(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      return this.x;
    }
    return this.y;
  }
  
  public int length()
  {
    return 2;
  }
  
  public void mul(double paramDouble)
  {
    this.x *= paramDouble;
    this.y *= paramDouble;
  }
  
  public void mul(Double2 paramDouble2)
  {
    this.x *= paramDouble2.x;
    this.y *= paramDouble2.y;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public void set(Double2 paramDouble2)
  {
    this.x = paramDouble2.x;
    this.y = paramDouble2.y;
  }
  
  public void setAt(int paramInt, double paramDouble)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramDouble;
      return;
    }
    this.y = paramDouble;
  }
  
  public void setValues(double paramDouble1, double paramDouble2)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
  }
  
  public void sub(double paramDouble)
  {
    this.x -= paramDouble;
    this.y -= paramDouble;
  }
  
  public void sub(Double2 paramDouble2)
  {
    this.x -= paramDouble2.x;
    this.y -= paramDouble2.y;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Double2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */