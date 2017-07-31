package android.renderscript;

public class Int2
{
  public int x;
  public int y;
  
  public Int2() {}
  
  public Int2(int paramInt)
  {
    this.y = paramInt;
    this.x = paramInt;
  }
  
  public Int2(int paramInt1, int paramInt2)
  {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public Int2(Int2 paramInt2)
  {
    this.x = paramInt2.x;
    this.y = paramInt2.y;
  }
  
  public static Int2 add(Int2 paramInt2, int paramInt)
  {
    Int2 localInt2 = new Int2();
    paramInt2.x += paramInt;
    paramInt2.y += paramInt;
    return localInt2;
  }
  
  public static Int2 add(Int2 paramInt21, Int2 paramInt22)
  {
    Int2 localInt2 = new Int2();
    paramInt21.x += paramInt22.x;
    paramInt21.y += paramInt22.y;
    return localInt2;
  }
  
  public static Int2 div(Int2 paramInt2, int paramInt)
  {
    Int2 localInt2 = new Int2();
    paramInt2.x /= paramInt;
    paramInt2.y /= paramInt;
    return localInt2;
  }
  
  public static Int2 div(Int2 paramInt21, Int2 paramInt22)
  {
    Int2 localInt2 = new Int2();
    paramInt21.x /= paramInt22.x;
    paramInt21.y /= paramInt22.y;
    return localInt2;
  }
  
  public static int dotProduct(Int2 paramInt21, Int2 paramInt22)
  {
    return paramInt22.x * paramInt21.x + paramInt22.y * paramInt21.y;
  }
  
  public static Int2 mod(Int2 paramInt2, int paramInt)
  {
    Int2 localInt2 = new Int2();
    paramInt2.x %= paramInt;
    paramInt2.y %= paramInt;
    return localInt2;
  }
  
  public static Int2 mod(Int2 paramInt21, Int2 paramInt22)
  {
    Int2 localInt2 = new Int2();
    paramInt21.x %= paramInt22.x;
    paramInt21.y %= paramInt22.y;
    return localInt2;
  }
  
  public static Int2 mul(Int2 paramInt2, int paramInt)
  {
    Int2 localInt2 = new Int2();
    paramInt2.x *= paramInt;
    paramInt2.y *= paramInt;
    return localInt2;
  }
  
  public static Int2 mul(Int2 paramInt21, Int2 paramInt22)
  {
    Int2 localInt2 = new Int2();
    paramInt21.x *= paramInt22.x;
    paramInt21.y *= paramInt22.y;
    return localInt2;
  }
  
  public static Int2 sub(Int2 paramInt2, int paramInt)
  {
    Int2 localInt2 = new Int2();
    paramInt2.x -= paramInt;
    paramInt2.y -= paramInt;
    return localInt2;
  }
  
  public static Int2 sub(Int2 paramInt21, Int2 paramInt22)
  {
    Int2 localInt2 = new Int2();
    paramInt21.x -= paramInt22.x;
    paramInt21.y -= paramInt22.y;
    return localInt2;
  }
  
  public void add(int paramInt)
  {
    this.x += paramInt;
    this.y += paramInt;
  }
  
  public void add(Int2 paramInt2)
  {
    this.x += paramInt2.x;
    this.y += paramInt2.y;
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
    }
    this.y += paramInt2;
  }
  
  public void addMultiple(Int2 paramInt2, int paramInt)
  {
    this.x += paramInt2.x * paramInt;
    this.y += paramInt2.y * paramInt;
  }
  
  public void copyTo(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt[paramInt] = this.x;
    paramArrayOfInt[(paramInt + 1)] = this.y;
  }
  
  public void div(int paramInt)
  {
    this.x /= paramInt;
    this.y /= paramInt;
  }
  
  public void div(Int2 paramInt2)
  {
    this.x /= paramInt2.x;
    this.y /= paramInt2.y;
  }
  
  public int dotProduct(Int2 paramInt2)
  {
    return this.x * paramInt2.x + this.y * paramInt2.y;
  }
  
  public int elementSum()
  {
    return this.x + this.y;
  }
  
  public int get(int paramInt)
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
  
  public void mod(int paramInt)
  {
    this.x %= paramInt;
    this.y %= paramInt;
  }
  
  public void mod(Int2 paramInt2)
  {
    this.x %= paramInt2.x;
    this.y %= paramInt2.y;
  }
  
  public void mul(int paramInt)
  {
    this.x *= paramInt;
    this.y *= paramInt;
  }
  
  public void mul(Int2 paramInt2)
  {
    this.x *= paramInt2.x;
    this.y *= paramInt2.y;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public void set(Int2 paramInt2)
  {
    this.x = paramInt2.x;
    this.y = paramInt2.y;
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
    }
    this.y = paramInt2;
  }
  
  public void setValues(int paramInt1, int paramInt2)
  {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public void sub(int paramInt)
  {
    this.x -= paramInt;
    this.y -= paramInt;
  }
  
  public void sub(Int2 paramInt2)
  {
    this.x -= paramInt2.x;
    this.y -= paramInt2.y;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Int2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */