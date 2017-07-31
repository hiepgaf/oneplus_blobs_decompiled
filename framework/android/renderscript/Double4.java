package android.renderscript;

public class Double4
{
  public double w;
  public double x;
  public double y;
  public double z;
  
  public Double4() {}
  
  public Double4(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
    this.w = paramDouble4;
  }
  
  public Double4(Double4 paramDouble4)
  {
    this.x = paramDouble4.x;
    this.y = paramDouble4.y;
    this.z = paramDouble4.z;
    this.w = paramDouble4.w;
  }
  
  public static Double4 add(Double4 paramDouble4, double paramDouble)
  {
    Double4 localDouble4 = new Double4();
    paramDouble4.x += paramDouble;
    paramDouble4.y += paramDouble;
    paramDouble4.z += paramDouble;
    paramDouble4.w += paramDouble;
    return localDouble4;
  }
  
  public static Double4 add(Double4 paramDouble41, Double4 paramDouble42)
  {
    Double4 localDouble4 = new Double4();
    paramDouble41.x += paramDouble42.x;
    paramDouble41.y += paramDouble42.y;
    paramDouble41.z += paramDouble42.z;
    paramDouble41.w += paramDouble42.w;
    return localDouble4;
  }
  
  public static Double4 div(Double4 paramDouble4, double paramDouble)
  {
    Double4 localDouble4 = new Double4();
    paramDouble4.x /= paramDouble;
    paramDouble4.y /= paramDouble;
    paramDouble4.z /= paramDouble;
    paramDouble4.w /= paramDouble;
    return localDouble4;
  }
  
  public static Double4 div(Double4 paramDouble41, Double4 paramDouble42)
  {
    Double4 localDouble4 = new Double4();
    paramDouble41.x /= paramDouble42.x;
    paramDouble41.y /= paramDouble42.y;
    paramDouble41.z /= paramDouble42.z;
    paramDouble41.w /= paramDouble42.w;
    return localDouble4;
  }
  
  public static double dotProduct(Double4 paramDouble41, Double4 paramDouble42)
  {
    return paramDouble42.x * paramDouble41.x + paramDouble42.y * paramDouble41.y + paramDouble42.z * paramDouble41.z + paramDouble42.w * paramDouble41.w;
  }
  
  public static Double4 mul(Double4 paramDouble4, double paramDouble)
  {
    Double4 localDouble4 = new Double4();
    paramDouble4.x *= paramDouble;
    paramDouble4.y *= paramDouble;
    paramDouble4.z *= paramDouble;
    paramDouble4.w *= paramDouble;
    return localDouble4;
  }
  
  public static Double4 mul(Double4 paramDouble41, Double4 paramDouble42)
  {
    Double4 localDouble4 = new Double4();
    paramDouble41.x *= paramDouble42.x;
    paramDouble41.y *= paramDouble42.y;
    paramDouble41.z *= paramDouble42.z;
    paramDouble41.w *= paramDouble42.w;
    return localDouble4;
  }
  
  public static Double4 sub(Double4 paramDouble4, double paramDouble)
  {
    Double4 localDouble4 = new Double4();
    paramDouble4.x -= paramDouble;
    paramDouble4.y -= paramDouble;
    paramDouble4.z -= paramDouble;
    paramDouble4.w -= paramDouble;
    return localDouble4;
  }
  
  public static Double4 sub(Double4 paramDouble41, Double4 paramDouble42)
  {
    Double4 localDouble4 = new Double4();
    paramDouble41.x -= paramDouble42.x;
    paramDouble41.y -= paramDouble42.y;
    paramDouble41.z -= paramDouble42.z;
    paramDouble41.w -= paramDouble42.w;
    return localDouble4;
  }
  
  public void add(double paramDouble)
  {
    this.x += paramDouble;
    this.y += paramDouble;
    this.z += paramDouble;
    this.w += paramDouble;
  }
  
  public void add(Double4 paramDouble4)
  {
    this.x += paramDouble4.x;
    this.y += paramDouble4.y;
    this.z += paramDouble4.z;
    this.w += paramDouble4.w;
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
    case 2: 
      this.z += paramDouble;
      return;
    }
    this.w += paramDouble;
  }
  
  public void addMultiple(Double4 paramDouble4, double paramDouble)
  {
    this.x += paramDouble4.x * paramDouble;
    this.y += paramDouble4.y * paramDouble;
    this.z += paramDouble4.z * paramDouble;
    this.w += paramDouble4.w * paramDouble;
  }
  
  public void copyTo(double[] paramArrayOfDouble, int paramInt)
  {
    paramArrayOfDouble[paramInt] = this.x;
    paramArrayOfDouble[(paramInt + 1)] = this.y;
    paramArrayOfDouble[(paramInt + 2)] = this.z;
    paramArrayOfDouble[(paramInt + 3)] = this.w;
  }
  
  public void div(double paramDouble)
  {
    this.x /= paramDouble;
    this.y /= paramDouble;
    this.z /= paramDouble;
    this.w /= paramDouble;
  }
  
  public void div(Double4 paramDouble4)
  {
    this.x /= paramDouble4.x;
    this.y /= paramDouble4.y;
    this.z /= paramDouble4.z;
    this.w /= paramDouble4.w;
  }
  
  public double dotProduct(Double4 paramDouble4)
  {
    return this.x * paramDouble4.x + this.y * paramDouble4.y + this.z * paramDouble4.z + this.w * paramDouble4.w;
  }
  
  public double elementSum()
  {
    return this.x + this.y + this.z + this.w;
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
    case 2: 
      return this.z;
    }
    return this.w;
  }
  
  public int length()
  {
    return 4;
  }
  
  public void mul(double paramDouble)
  {
    this.x *= paramDouble;
    this.y *= paramDouble;
    this.z *= paramDouble;
    this.w *= paramDouble;
  }
  
  public void mul(Double4 paramDouble4)
  {
    this.x *= paramDouble4.x;
    this.y *= paramDouble4.y;
    this.z *= paramDouble4.z;
    this.w *= paramDouble4.w;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
    this.w = (-this.w);
  }
  
  public void set(Double4 paramDouble4)
  {
    this.x = paramDouble4.x;
    this.y = paramDouble4.y;
    this.z = paramDouble4.z;
    this.w = paramDouble4.w;
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
    case 2: 
      this.z = paramDouble;
      return;
    }
    this.w = paramDouble;
  }
  
  public void setValues(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
    this.w = paramDouble4;
  }
  
  public void sub(double paramDouble)
  {
    this.x -= paramDouble;
    this.y -= paramDouble;
    this.z -= paramDouble;
    this.w -= paramDouble;
  }
  
  public void sub(Double4 paramDouble4)
  {
    this.x -= paramDouble4.x;
    this.y -= paramDouble4.y;
    this.z -= paramDouble4.z;
    this.w -= paramDouble4.w;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Double4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */