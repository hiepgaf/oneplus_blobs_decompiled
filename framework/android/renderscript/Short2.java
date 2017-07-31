package android.renderscript;

public class Short2
{
  public short x;
  public short y;
  
  public Short2() {}
  
  public Short2(Short2 paramShort2)
  {
    this.x = paramShort2.x;
    this.y = paramShort2.y;
  }
  
  public Short2(short paramShort)
  {
    this.y = paramShort;
    this.x = paramShort;
  }
  
  public Short2(short paramShort1, short paramShort2)
  {
    this.x = paramShort1;
    this.y = paramShort2;
  }
  
  public static Short2 add(Short2 paramShort21, Short2 paramShort22)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort21.x + paramShort22.x));
    localShort2.y = ((short)(paramShort21.y + paramShort22.y));
    return localShort2;
  }
  
  public static Short2 add(Short2 paramShort2, short paramShort)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort2.x + paramShort));
    localShort2.y = ((short)(paramShort2.y + paramShort));
    return localShort2;
  }
  
  public static Short2 div(Short2 paramShort21, Short2 paramShort22)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort21.x / paramShort22.x));
    localShort2.y = ((short)(paramShort21.y / paramShort22.y));
    return localShort2;
  }
  
  public static Short2 div(Short2 paramShort2, short paramShort)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort2.x / paramShort));
    localShort2.y = ((short)(paramShort2.y / paramShort));
    return localShort2;
  }
  
  public static short dotProduct(Short2 paramShort21, Short2 paramShort22)
  {
    return (short)(paramShort22.x * paramShort21.x + paramShort22.y * paramShort21.y);
  }
  
  public static Short2 mod(Short2 paramShort21, Short2 paramShort22)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort21.x % paramShort22.x));
    localShort2.y = ((short)(paramShort21.y % paramShort22.y));
    return localShort2;
  }
  
  public static Short2 mod(Short2 paramShort2, short paramShort)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort2.x % paramShort));
    localShort2.y = ((short)(paramShort2.y % paramShort));
    return localShort2;
  }
  
  public static Short2 mul(Short2 paramShort21, Short2 paramShort22)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort21.x * paramShort22.x));
    localShort2.y = ((short)(paramShort21.y * paramShort22.y));
    return localShort2;
  }
  
  public static Short2 mul(Short2 paramShort2, short paramShort)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort2.x * paramShort));
    localShort2.y = ((short)(paramShort2.y * paramShort));
    return localShort2;
  }
  
  public static Short2 sub(Short2 paramShort21, Short2 paramShort22)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort21.x - paramShort22.x));
    localShort2.y = ((short)(paramShort21.y - paramShort22.y));
    return localShort2;
  }
  
  public static Short2 sub(Short2 paramShort2, short paramShort)
  {
    Short2 localShort2 = new Short2();
    localShort2.x = ((short)(paramShort2.x - paramShort));
    localShort2.y = ((short)(paramShort2.y - paramShort));
    return localShort2;
  }
  
  public void add(Short2 paramShort2)
  {
    this.x = ((short)(this.x + paramShort2.x));
    this.y = ((short)(this.y + paramShort2.y));
  }
  
  public void add(short paramShort)
  {
    this.x = ((short)(this.x + paramShort));
    this.y = ((short)(this.y + paramShort));
  }
  
  public void addAt(int paramInt, short paramShort)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = ((short)(this.x + paramShort));
      return;
    }
    this.y = ((short)(this.y + paramShort));
  }
  
  public void addMultiple(Short2 paramShort2, short paramShort)
  {
    this.x = ((short)(this.x + paramShort2.x * paramShort));
    this.y = ((short)(this.y + paramShort2.y * paramShort));
  }
  
  public void copyTo(short[] paramArrayOfShort, int paramInt)
  {
    paramArrayOfShort[paramInt] = this.x;
    paramArrayOfShort[(paramInt + 1)] = this.y;
  }
  
  public void div(Short2 paramShort2)
  {
    this.x = ((short)(this.x / paramShort2.x));
    this.y = ((short)(this.y / paramShort2.y));
  }
  
  public void div(short paramShort)
  {
    this.x = ((short)(this.x / paramShort));
    this.y = ((short)(this.y / paramShort));
  }
  
  public short dotProduct(Short2 paramShort2)
  {
    return (short)(this.x * paramShort2.x + this.y * paramShort2.y);
  }
  
  public short elementSum()
  {
    return (short)(this.x + this.y);
  }
  
  public short get(int paramInt)
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
  
  public short length()
  {
    return 2;
  }
  
  public void mod(Short2 paramShort2)
  {
    this.x = ((short)(this.x % paramShort2.x));
    this.y = ((short)(this.y % paramShort2.y));
  }
  
  public void mod(short paramShort)
  {
    this.x = ((short)(this.x % paramShort));
    this.y = ((short)(this.y % paramShort));
  }
  
  public void mul(Short2 paramShort2)
  {
    this.x = ((short)(this.x * paramShort2.x));
    this.y = ((short)(this.y * paramShort2.y));
  }
  
  public void mul(short paramShort)
  {
    this.x = ((short)(this.x * paramShort));
    this.y = ((short)(this.y * paramShort));
  }
  
  public void negate()
  {
    this.x = ((short)-this.x);
    this.y = ((short)-this.y);
  }
  
  public void set(Short2 paramShort2)
  {
    this.x = paramShort2.x;
    this.y = paramShort2.y;
  }
  
  public void setAt(int paramInt, short paramShort)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramShort;
      return;
    }
    this.y = paramShort;
  }
  
  public void setValues(short paramShort1, short paramShort2)
  {
    this.x = paramShort1;
    this.y = paramShort2;
  }
  
  public void sub(Short2 paramShort2)
  {
    this.x = ((short)(this.x - paramShort2.x));
    this.y = ((short)(this.y - paramShort2.y));
  }
  
  public void sub(short paramShort)
  {
    this.x = ((short)(this.x - paramShort));
    this.y = ((short)(this.y - paramShort));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Short2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */