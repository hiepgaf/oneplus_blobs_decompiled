package android.renderscript;

public class Double3
{
  public double x;
  public double y;
  public double z;
  
  public Double3() {}
  
  public Double3(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
  }
  
  public Double3(Double3 paramDouble3)
  {
    this.x = paramDouble3.x;
    this.y = paramDouble3.y;
    this.z = paramDouble3.z;
  }
  
  public static Double3 add(Double3 paramDouble3, double paramDouble)
  {
    Double3 localDouble3 = new Double3();
    paramDouble3.x += paramDouble;
    paramDouble3.y += paramDouble;
    paramDouble3.z += paramDouble;
    return localDouble3;
  }
  
  public static Double3 add(Double3 paramDouble31, Double3 paramDouble32)
  {
    Double3 localDouble3 = new Double3();
    paramDouble31.x += paramDouble32.x;
    paramDouble31.y += paramDouble32.y;
    paramDouble31.z += paramDouble32.z;
    return localDouble3;
  }
  
  public static Double3 div(Double3 paramDouble3, double paramDouble)
  {
    Double3 localDouble3 = new Double3();
    paramDouble3.x /= paramDouble;
    paramDouble3.y /= paramDouble;
    paramDouble3.z /= paramDouble;
    return localDouble3;
  }
  
  public static Double3 div(Double3 paramDouble31, Double3 paramDouble32)
  {
    Double3 localDouble3 = new Double3();
    paramDouble31.x /= paramDouble32.x;
    paramDouble31.y /= paramDouble32.y;
    paramDouble31.z /= paramDouble32.z;
    return localDouble3;
  }
  
  public static double dotProduct(Double3 paramDouble31, Double3 paramDouble32)
  {
    return paramDouble32.x * paramDouble31.x + paramDouble32.y * paramDouble31.y + paramDouble32.z * paramDouble31.z;
  }
  
  public static Double3 mul(Double3 paramDouble3, double paramDouble)
  {
    Double3 localDouble3 = new Double3();
    paramDouble3.x *= paramDouble;
    paramDouble3.y *= paramDouble;
    paramDouble3.z *= paramDouble;
    return localDouble3;
  }
  
  public static Double3 mul(Double3 paramDouble31, Double3 paramDouble32)
  {
    Double3 localDouble3 = new Double3();
    paramDouble31.x *= paramDouble32.x;
    paramDouble31.y *= paramDouble32.y;
    paramDouble31.z *= paramDouble32.z;
    return localDouble3;
  }
  
  public static Double3 sub(Double3 paramDouble3, double paramDouble)
  {
    Double3 localDouble3 = new Double3();
    paramDouble3.x -= paramDouble;
    paramDouble3.y -= paramDouble;
    paramDouble3.z -= paramDouble;
    return localDouble3;
  }
  
  public static Double3 sub(Double3 paramDouble31, Double3 paramDouble32)
  {
    Double3 localDouble3 = new Double3();
    paramDouble31.x -= paramDouble32.x;
    paramDouble31.y -= paramDouble32.y;
    paramDouble31.z -= paramDouble32.z;
    return localDouble3;
  }
  
  public void add(double paramDouble)
  {
    this.x += paramDouble;
    this.y += paramDouble;
    this.z += paramDouble;
  }
  
  public void add(Double3 paramDouble3)
  {
    this.x += paramDouble3.x;
    this.y += paramDouble3.y;
    this.z += paramDouble3.z;
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
    case 1: 
      this.y += paramDouble;
      return;
    }
    this.z += paramDouble;
  }
  
  public void addMultiple(Double3 paramDouble3, double paramDouble)
  {
    this.x += paramDouble3.x * paramDouble;
    this.y += paramDouble3.y * paramDouble;
    this.z += paramDouble3.z * paramDouble;
  }
  
  public void copyTo(double[] paramArrayOfDouble, int paramInt)
  {
    paramArrayOfDouble[paramInt] = this.x;
    paramArrayOfDouble[(paramInt + 1)] = this.y;
    paramArrayOfDouble[(paramInt + 2)] = this.z;
  }
  
  public void div(double paramDouble)
  {
    this.x /= paramDouble;
    this.y /= paramDouble;
    this.z /= paramDouble;
  }
  
  public void div(Double3 paramDouble3)
  {
    this.x /= paramDouble3.x;
    this.y /= paramDouble3.y;
    this.z /= paramDouble3.z;
  }
  
  public double dotProduct(Double3 paramDouble3)
  {
    return this.x * paramDouble3.x + this.y * paramDouble3.y + this.z * paramDouble3.z;
  }
  
  public double elementSum()
  {
    return this.x + this.y + this.z;
  }
  
  public double get(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      return this.x;
    case 1: 
      return this.y;
    }
    return this.z;
  }
  
  public int length()
  {
    return 3;
  }
  
  public void mul(double paramDouble)
  {
    this.x *= paramDouble;
    this.y *= paramDouble;
    this.z *= paramDouble;
  }
  
  public void mul(Double3 paramDouble3)
  {
    this.x *= paramDouble3.x;
    this.y *= paramDouble3.y;
    this.z *= paramDouble3.z;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
  }
  
  public void set(Double3 paramDouble3)
  {
    this.x = paramDouble3.x;
    this.y = paramDouble3.y;
    this.z = paramDouble3.z;
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
    case 1: 
      this.y = paramDouble;
      return;
    }
    this.z = paramDouble;
  }
  
  public void setValues(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
  }
  
  public void sub(double paramDouble)
  {
    this.x -= paramDouble;
    this.y -= paramDouble;
    this.z -= paramDouble;
  }
  
  public void sub(Double3 paramDouble3)
  {
    this.x -= paramDouble3.x;
    this.y -= paramDouble3.y;
    this.z -= paramDouble3.z;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Double3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */