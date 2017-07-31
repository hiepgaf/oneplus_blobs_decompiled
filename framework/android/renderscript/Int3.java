package android.renderscript;

public class Int3
{
  public int x;
  public int y;
  public int z;
  
  public Int3() {}
  
  public Int3(int paramInt)
  {
    this.z = paramInt;
    this.y = paramInt;
    this.x = paramInt;
  }
  
  public Int3(int paramInt1, int paramInt2, int paramInt3)
  {
    this.x = paramInt1;
    this.y = paramInt2;
    this.z = paramInt3;
  }
  
  public Int3(Int3 paramInt3)
  {
    this.x = paramInt3.x;
    this.y = paramInt3.y;
    this.z = paramInt3.z;
  }
  
  public static Int3 add(Int3 paramInt3, int paramInt)
  {
    Int3 localInt3 = new Int3();
    paramInt3.x += paramInt;
    paramInt3.y += paramInt;
    paramInt3.z += paramInt;
    return localInt3;
  }
  
  public static Int3 add(Int3 paramInt31, Int3 paramInt32)
  {
    Int3 localInt3 = new Int3();
    paramInt31.x += paramInt32.x;
    paramInt31.y += paramInt32.y;
    paramInt31.z += paramInt32.z;
    return localInt3;
  }
  
  public static Int3 div(Int3 paramInt3, int paramInt)
  {
    Int3 localInt3 = new Int3();
    paramInt3.x /= paramInt;
    paramInt3.y /= paramInt;
    paramInt3.z /= paramInt;
    return localInt3;
  }
  
  public static Int3 div(Int3 paramInt31, Int3 paramInt32)
  {
    Int3 localInt3 = new Int3();
    paramInt31.x /= paramInt32.x;
    paramInt31.y /= paramInt32.y;
    paramInt31.z /= paramInt32.z;
    return localInt3;
  }
  
  public static int dotProduct(Int3 paramInt31, Int3 paramInt32)
  {
    return paramInt32.x * paramInt31.x + paramInt32.y * paramInt31.y + paramInt32.z * paramInt31.z;
  }
  
  public static Int3 mod(Int3 paramInt3, int paramInt)
  {
    Int3 localInt3 = new Int3();
    paramInt3.x %= paramInt;
    paramInt3.y %= paramInt;
    paramInt3.z %= paramInt;
    return localInt3;
  }
  
  public static Int3 mod(Int3 paramInt31, Int3 paramInt32)
  {
    Int3 localInt3 = new Int3();
    paramInt31.x %= paramInt32.x;
    paramInt31.y %= paramInt32.y;
    paramInt31.z %= paramInt32.z;
    return localInt3;
  }
  
  public static Int3 mul(Int3 paramInt3, int paramInt)
  {
    Int3 localInt3 = new Int3();
    paramInt3.x *= paramInt;
    paramInt3.y *= paramInt;
    paramInt3.z *= paramInt;
    return localInt3;
  }
  
  public static Int3 mul(Int3 paramInt31, Int3 paramInt32)
  {
    Int3 localInt3 = new Int3();
    paramInt31.x *= paramInt32.x;
    paramInt31.y *= paramInt32.y;
    paramInt31.z *= paramInt32.z;
    return localInt3;
  }
  
  public static Int3 sub(Int3 paramInt3, int paramInt)
  {
    Int3 localInt3 = new Int3();
    paramInt3.x -= paramInt;
    paramInt3.y -= paramInt;
    paramInt3.z -= paramInt;
    return localInt3;
  }
  
  public static Int3 sub(Int3 paramInt31, Int3 paramInt32)
  {
    Int3 localInt3 = new Int3();
    paramInt31.x -= paramInt32.x;
    paramInt31.y -= paramInt32.y;
    paramInt31.z -= paramInt32.z;
    return localInt3;
  }
  
  public void add(int paramInt)
  {
    this.x += paramInt;
    this.y += paramInt;
    this.z += paramInt;
  }
  
  public void add(Int3 paramInt3)
  {
    this.x += paramInt3.x;
    this.y += paramInt3.y;
    this.z += paramInt3.z;
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
    }
    this.z += paramInt2;
  }
  
  public void addMultiple(Int3 paramInt3, int paramInt)
  {
    this.x += paramInt3.x * paramInt;
    this.y += paramInt3.y * paramInt;
    this.z += paramInt3.z * paramInt;
  }
  
  public void copyTo(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt[paramInt] = this.x;
    paramArrayOfInt[(paramInt + 1)] = this.y;
    paramArrayOfInt[(paramInt + 2)] = this.z;
  }
  
  public void div(int paramInt)
  {
    this.x /= paramInt;
    this.y /= paramInt;
    this.z /= paramInt;
  }
  
  public void div(Int3 paramInt3)
  {
    this.x /= paramInt3.x;
    this.y /= paramInt3.y;
    this.z /= paramInt3.z;
  }
  
  public int dotProduct(Int3 paramInt3)
  {
    return this.x * paramInt3.x + this.y * paramInt3.y + this.z * paramInt3.z;
  }
  
  public int elementSum()
  {
    return this.x + this.y + this.z;
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
    }
    return this.z;
  }
  
  public int length()
  {
    return 3;
  }
  
  public void mod(int paramInt)
  {
    this.x %= paramInt;
    this.y %= paramInt;
    this.z %= paramInt;
  }
  
  public void mod(Int3 paramInt3)
  {
    this.x %= paramInt3.x;
    this.y %= paramInt3.y;
    this.z %= paramInt3.z;
  }
  
  public void mul(int paramInt)
  {
    this.x *= paramInt;
    this.y *= paramInt;
    this.z *= paramInt;
  }
  
  public void mul(Int3 paramInt3)
  {
    this.x *= paramInt3.x;
    this.y *= paramInt3.y;
    this.z *= paramInt3.z;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
  }
  
  public void set(Int3 paramInt3)
  {
    this.x = paramInt3.x;
    this.y = paramInt3.y;
    this.z = paramInt3.z;
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
    }
    this.z = paramInt2;
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3)
  {
    this.x = paramInt1;
    this.y = paramInt2;
    this.z = paramInt3;
  }
  
  public void sub(int paramInt)
  {
    this.x -= paramInt;
    this.y -= paramInt;
    this.z -= paramInt;
  }
  
  public void sub(Int3 paramInt3)
  {
    this.x -= paramInt3.x;
    this.y -= paramInt3.y;
    this.z -= paramInt3.z;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Int3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */