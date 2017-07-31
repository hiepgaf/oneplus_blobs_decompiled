package android.renderscript;

public class Int4
{
  public int w;
  public int x;
  public int y;
  public int z;
  
  public Int4() {}
  
  public Int4(int paramInt)
  {
    this.w = paramInt;
    this.z = paramInt;
    this.y = paramInt;
    this.x = paramInt;
  }
  
  public Int4(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.x = paramInt1;
    this.y = paramInt2;
    this.z = paramInt3;
    this.w = paramInt4;
  }
  
  public Int4(Int4 paramInt4)
  {
    this.x = paramInt4.x;
    this.y = paramInt4.y;
    this.z = paramInt4.z;
    this.w = paramInt4.w;
  }
  
  public static Int4 add(Int4 paramInt4, int paramInt)
  {
    Int4 localInt4 = new Int4();
    paramInt4.x += paramInt;
    paramInt4.y += paramInt;
    paramInt4.z += paramInt;
    paramInt4.w += paramInt;
    return localInt4;
  }
  
  public static Int4 add(Int4 paramInt41, Int4 paramInt42)
  {
    Int4 localInt4 = new Int4();
    paramInt41.x += paramInt42.x;
    paramInt41.y += paramInt42.y;
    paramInt41.z += paramInt42.z;
    paramInt41.w += paramInt42.w;
    return localInt4;
  }
  
  public static Int4 div(Int4 paramInt4, int paramInt)
  {
    Int4 localInt4 = new Int4();
    paramInt4.x /= paramInt;
    paramInt4.y /= paramInt;
    paramInt4.z /= paramInt;
    paramInt4.w /= paramInt;
    return localInt4;
  }
  
  public static Int4 div(Int4 paramInt41, Int4 paramInt42)
  {
    Int4 localInt4 = new Int4();
    paramInt41.x /= paramInt42.x;
    paramInt41.y /= paramInt42.y;
    paramInt41.z /= paramInt42.z;
    paramInt41.w /= paramInt42.w;
    return localInt4;
  }
  
  public static int dotProduct(Int4 paramInt41, Int4 paramInt42)
  {
    return paramInt42.x * paramInt41.x + paramInt42.y * paramInt41.y + paramInt42.z * paramInt41.z + paramInt42.w * paramInt41.w;
  }
  
  public static Int4 mod(Int4 paramInt4, int paramInt)
  {
    Int4 localInt4 = new Int4();
    paramInt4.x %= paramInt;
    paramInt4.y %= paramInt;
    paramInt4.z %= paramInt;
    paramInt4.w %= paramInt;
    return localInt4;
  }
  
  public static Int4 mod(Int4 paramInt41, Int4 paramInt42)
  {
    Int4 localInt4 = new Int4();
    paramInt41.x %= paramInt42.x;
    paramInt41.y %= paramInt42.y;
    paramInt41.z %= paramInt42.z;
    paramInt41.w %= paramInt42.w;
    return localInt4;
  }
  
  public static Int4 mul(Int4 paramInt4, int paramInt)
  {
    Int4 localInt4 = new Int4();
    paramInt4.x *= paramInt;
    paramInt4.y *= paramInt;
    paramInt4.z *= paramInt;
    paramInt4.w *= paramInt;
    return localInt4;
  }
  
  public static Int4 mul(Int4 paramInt41, Int4 paramInt42)
  {
    Int4 localInt4 = new Int4();
    paramInt41.x *= paramInt42.x;
    paramInt41.y *= paramInt42.y;
    paramInt41.z *= paramInt42.z;
    paramInt41.w *= paramInt42.w;
    return localInt4;
  }
  
  public static Int4 sub(Int4 paramInt4, int paramInt)
  {
    Int4 localInt4 = new Int4();
    paramInt4.x -= paramInt;
    paramInt4.y -= paramInt;
    paramInt4.z -= paramInt;
    paramInt4.w -= paramInt;
    return localInt4;
  }
  
  public static Int4 sub(Int4 paramInt41, Int4 paramInt42)
  {
    Int4 localInt4 = new Int4();
    paramInt41.x -= paramInt42.x;
    paramInt41.y -= paramInt42.y;
    paramInt41.z -= paramInt42.z;
    paramInt41.w -= paramInt42.w;
    return localInt4;
  }
  
  public void add(int paramInt)
  {
    this.x += paramInt;
    this.y += paramInt;
    this.z += paramInt;
    this.w += paramInt;
  }
  
  public void add(Int4 paramInt4)
  {
    this.x += paramInt4.x;
    this.y += paramInt4.y;
    this.z += paramInt4.z;
    this.w += paramInt4.w;
  }
  
  public void addAt(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x += paramInt2;
      return;
    case 1: 
      this.y += paramInt2;
      return;
    case 2: 
      this.z += paramInt2;
      return;
    }
    this.w += paramInt2;
  }
  
  public void addMultiple(Int4 paramInt4, int paramInt)
  {
    this.x += paramInt4.x * paramInt;
    this.y += paramInt4.y * paramInt;
    this.z += paramInt4.z * paramInt;
    this.w += paramInt4.w * paramInt;
  }
  
  public void copyTo(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt[paramInt] = this.x;
    paramArrayOfInt[(paramInt + 1)] = this.y;
    paramArrayOfInt[(paramInt + 2)] = this.z;
    paramArrayOfInt[(paramInt + 3)] = this.w;
  }
  
  public void div(int paramInt)
  {
    this.x /= paramInt;
    this.y /= paramInt;
    this.z /= paramInt;
    this.w /= paramInt;
  }
  
  public void div(Int4 paramInt4)
  {
    this.x /= paramInt4.x;
    this.y /= paramInt4.y;
    this.z /= paramInt4.z;
    this.w /= paramInt4.w;
  }
  
  public int dotProduct(Int4 paramInt4)
  {
    return this.x * paramInt4.x + this.y * paramInt4.y + this.z * paramInt4.z + this.w * paramInt4.w;
  }
  
  public int elementSum()
  {
    return this.x + this.y + this.z + this.w;
  }
  
  public int get(int paramInt)
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
  
  public void mod(int paramInt)
  {
    this.x %= paramInt;
    this.y %= paramInt;
    this.z %= paramInt;
    this.w %= paramInt;
  }
  
  public void mod(Int4 paramInt4)
  {
    this.x %= paramInt4.x;
    this.y %= paramInt4.y;
    this.z %= paramInt4.z;
    this.w %= paramInt4.w;
  }
  
  public void mul(int paramInt)
  {
    this.x *= paramInt;
    this.y *= paramInt;
    this.z *= paramInt;
    this.w *= paramInt;
  }
  
  public void mul(Int4 paramInt4)
  {
    this.x *= paramInt4.x;
    this.y *= paramInt4.y;
    this.z *= paramInt4.z;
    this.w *= paramInt4.w;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
    this.w = (-this.w);
  }
  
  public void set(Int4 paramInt4)
  {
    this.x = paramInt4.x;
    this.y = paramInt4.y;
    this.z = paramInt4.z;
    this.w = paramInt4.w;
  }
  
  public void setAt(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramInt2;
      return;
    case 1: 
      this.y = paramInt2;
      return;
    case 2: 
      this.z = paramInt2;
      return;
    }
    this.w = paramInt2;
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.x = paramInt1;
    this.y = paramInt2;
    this.z = paramInt3;
    this.w = paramInt4;
  }
  
  public void sub(int paramInt)
  {
    this.x -= paramInt;
    this.y -= paramInt;
    this.z -= paramInt;
    this.w -= paramInt;
  }
  
  public void sub(Int4 paramInt4)
  {
    this.x -= paramInt4.x;
    this.y -= paramInt4.y;
    this.z -= paramInt4.z;
    this.w -= paramInt4.w;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Int4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */