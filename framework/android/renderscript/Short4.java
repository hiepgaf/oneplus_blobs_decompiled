package android.renderscript;

public class Short4
{
  public short w;
  public short x;
  public short y;
  public short z;
  
  public Short4() {}
  
  public Short4(Short4 paramShort4)
  {
    this.x = paramShort4.x;
    this.y = paramShort4.y;
    this.z = paramShort4.z;
    this.w = paramShort4.w;
  }
  
  public Short4(short paramShort)
  {
    this.w = paramShort;
    this.z = paramShort;
    this.y = paramShort;
    this.x = paramShort;
  }
  
  public Short4(short paramShort1, short paramShort2, short paramShort3, short paramShort4)
  {
    this.x = paramShort1;
    this.y = paramShort2;
    this.z = paramShort3;
    this.w = paramShort4;
  }
  
  public static Short4 add(Short4 paramShort41, Short4 paramShort42)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort41.x + paramShort42.x));
    localShort4.y = ((short)(paramShort41.y + paramShort42.y));
    localShort4.z = ((short)(paramShort41.z + paramShort42.z));
    localShort4.w = ((short)(paramShort41.w + paramShort42.w));
    return localShort4;
  }
  
  public static Short4 add(Short4 paramShort4, short paramShort)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort4.x + paramShort));
    localShort4.y = ((short)(paramShort4.y + paramShort));
    localShort4.z = ((short)(paramShort4.z + paramShort));
    localShort4.w = ((short)(paramShort4.w + paramShort));
    return localShort4;
  }
  
  public static Short4 div(Short4 paramShort41, Short4 paramShort42)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort41.x / paramShort42.x));
    localShort4.y = ((short)(paramShort41.y / paramShort42.y));
    localShort4.z = ((short)(paramShort41.z / paramShort42.z));
    localShort4.w = ((short)(paramShort41.w / paramShort42.w));
    return localShort4;
  }
  
  public static Short4 div(Short4 paramShort4, short paramShort)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort4.x / paramShort));
    localShort4.y = ((short)(paramShort4.y / paramShort));
    localShort4.z = ((short)(paramShort4.z / paramShort));
    localShort4.w = ((short)(paramShort4.w / paramShort));
    return localShort4;
  }
  
  public static short dotProduct(Short4 paramShort41, Short4 paramShort42)
  {
    return (short)(paramShort42.x * paramShort41.x + paramShort42.y * paramShort41.y + paramShort42.z * paramShort41.z + paramShort42.w * paramShort41.w);
  }
  
  public static Short4 mod(Short4 paramShort41, Short4 paramShort42)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort41.x % paramShort42.x));
    localShort4.y = ((short)(paramShort41.y % paramShort42.y));
    localShort4.z = ((short)(paramShort41.z % paramShort42.z));
    localShort4.w = ((short)(paramShort41.w % paramShort42.w));
    return localShort4;
  }
  
  public static Short4 mod(Short4 paramShort4, short paramShort)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort4.x % paramShort));
    localShort4.y = ((short)(paramShort4.y % paramShort));
    localShort4.z = ((short)(paramShort4.z % paramShort));
    localShort4.w = ((short)(paramShort4.w % paramShort));
    return localShort4;
  }
  
  public static Short4 mul(Short4 paramShort41, Short4 paramShort42)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort41.x * paramShort42.x));
    localShort4.y = ((short)(paramShort41.y * paramShort42.y));
    localShort4.z = ((short)(paramShort41.z * paramShort42.z));
    localShort4.w = ((short)(paramShort41.w * paramShort42.w));
    return localShort4;
  }
  
  public static Short4 mul(Short4 paramShort4, short paramShort)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort4.x * paramShort));
    localShort4.y = ((short)(paramShort4.y * paramShort));
    localShort4.z = ((short)(paramShort4.z * paramShort));
    localShort4.w = ((short)(paramShort4.w * paramShort));
    return localShort4;
  }
  
  public static Short4 sub(Short4 paramShort41, Short4 paramShort42)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort41.x - paramShort42.x));
    localShort4.y = ((short)(paramShort41.y - paramShort42.y));
    localShort4.z = ((short)(paramShort41.z - paramShort42.z));
    localShort4.w = ((short)(paramShort41.w - paramShort42.w));
    return localShort4;
  }
  
  public static Short4 sub(Short4 paramShort4, short paramShort)
  {
    Short4 localShort4 = new Short4();
    localShort4.x = ((short)(paramShort4.x - paramShort));
    localShort4.y = ((short)(paramShort4.y - paramShort));
    localShort4.z = ((short)(paramShort4.z - paramShort));
    localShort4.w = ((short)(paramShort4.w - paramShort));
    return localShort4;
  }
  
  public void add(Short4 paramShort4)
  {
    this.x = ((short)(this.x + paramShort4.x));
    this.y = ((short)(this.y + paramShort4.y));
    this.z = ((short)(this.z + paramShort4.z));
    this.w = ((short)(this.w + paramShort4.w));
  }
  
  public void add(short paramShort)
  {
    this.x = ((short)(this.x + paramShort));
    this.y = ((short)(this.y + paramShort));
    this.z = ((short)(this.z + paramShort));
    this.w = ((short)(this.w + paramShort));
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
    case 1: 
      this.y = ((short)(this.y + paramShort));
      return;
    case 2: 
      this.z = ((short)(this.z + paramShort));
      return;
    }
    this.w = ((short)(this.w + paramShort));
  }
  
  public void addMultiple(Short4 paramShort4, short paramShort)
  {
    this.x = ((short)(this.x + paramShort4.x * paramShort));
    this.y = ((short)(this.y + paramShort4.y * paramShort));
    this.z = ((short)(this.z + paramShort4.z * paramShort));
    this.w = ((short)(this.w + paramShort4.w * paramShort));
  }
  
  public void copyTo(short[] paramArrayOfShort, int paramInt)
  {
    paramArrayOfShort[paramInt] = this.x;
    paramArrayOfShort[(paramInt + 1)] = this.y;
    paramArrayOfShort[(paramInt + 2)] = this.z;
    paramArrayOfShort[(paramInt + 3)] = this.w;
  }
  
  public void div(Short4 paramShort4)
  {
    this.x = ((short)(this.x / paramShort4.x));
    this.y = ((short)(this.y / paramShort4.y));
    this.z = ((short)(this.z / paramShort4.z));
    this.w = ((short)(this.w / paramShort4.w));
  }
  
  public void div(short paramShort)
  {
    this.x = ((short)(this.x / paramShort));
    this.y = ((short)(this.y / paramShort));
    this.z = ((short)(this.z / paramShort));
    this.w = ((short)(this.w / paramShort));
  }
  
  public short dotProduct(Short4 paramShort4)
  {
    return (short)(this.x * paramShort4.x + this.y * paramShort4.y + this.z * paramShort4.z + this.w * paramShort4.w);
  }
  
  public short elementSum()
  {
    return (short)(this.x + this.y + this.z + this.w);
  }
  
  public short get(int paramInt)
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
  
  public short length()
  {
    return 4;
  }
  
  public void mod(Short4 paramShort4)
  {
    this.x = ((short)(this.x % paramShort4.x));
    this.y = ((short)(this.y % paramShort4.y));
    this.z = ((short)(this.z % paramShort4.z));
    this.w = ((short)(this.w % paramShort4.w));
  }
  
  public void mod(short paramShort)
  {
    this.x = ((short)(this.x % paramShort));
    this.y = ((short)(this.y % paramShort));
    this.z = ((short)(this.z % paramShort));
    this.w = ((short)(this.w % paramShort));
  }
  
  public void mul(Short4 paramShort4)
  {
    this.x = ((short)(this.x * paramShort4.x));
    this.y = ((short)(this.y * paramShort4.y));
    this.z = ((short)(this.z * paramShort4.z));
    this.w = ((short)(this.w * paramShort4.w));
  }
  
  public void mul(short paramShort)
  {
    this.x = ((short)(this.x * paramShort));
    this.y = ((short)(this.y * paramShort));
    this.z = ((short)(this.z * paramShort));
    this.w = ((short)(this.w * paramShort));
  }
  
  public void negate()
  {
    this.x = ((short)-this.x);
    this.y = ((short)-this.y);
    this.z = ((short)-this.z);
    this.w = ((short)-this.w);
  }
  
  public void set(Short4 paramShort4)
  {
    this.x = paramShort4.x;
    this.y = paramShort4.y;
    this.z = paramShort4.z;
    this.w = paramShort4.w;
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
    case 1: 
      this.y = paramShort;
      return;
    case 2: 
      this.z = paramShort;
      return;
    }
    this.w = paramShort;
  }
  
  public void setValues(short paramShort1, short paramShort2, short paramShort3, short paramShort4)
  {
    this.x = paramShort1;
    this.y = paramShort2;
    this.z = paramShort3;
    this.w = paramShort4;
  }
  
  public void sub(Short4 paramShort4)
  {
    this.x = ((short)(this.x - paramShort4.x));
    this.y = ((short)(this.y - paramShort4.y));
    this.z = ((short)(this.z - paramShort4.z));
    this.w = ((short)(this.w - paramShort4.w));
  }
  
  public void sub(short paramShort)
  {
    this.x = ((short)(this.x - paramShort));
    this.y = ((short)(this.y - paramShort));
    this.z = ((short)(this.z - paramShort));
    this.w = ((short)(this.w - paramShort));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Short4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */