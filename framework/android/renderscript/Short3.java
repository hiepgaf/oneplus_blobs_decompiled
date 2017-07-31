package android.renderscript;

public class Short3
{
  public short x;
  public short y;
  public short z;
  
  public Short3() {}
  
  public Short3(Short3 paramShort3)
  {
    this.x = paramShort3.x;
    this.y = paramShort3.y;
    this.z = paramShort3.z;
  }
  
  public Short3(short paramShort)
  {
    this.z = paramShort;
    this.y = paramShort;
    this.x = paramShort;
  }
  
  public Short3(short paramShort1, short paramShort2, short paramShort3)
  {
    this.x = paramShort1;
    this.y = paramShort2;
    this.z = paramShort3;
  }
  
  public static Short3 add(Short3 paramShort31, Short3 paramShort32)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort31.x + paramShort32.x));
    localShort3.y = ((short)(paramShort31.y + paramShort32.y));
    localShort3.z = ((short)(paramShort31.z + paramShort32.z));
    return localShort3;
  }
  
  public static Short3 add(Short3 paramShort3, short paramShort)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort3.x + paramShort));
    localShort3.y = ((short)(paramShort3.y + paramShort));
    localShort3.z = ((short)(paramShort3.z + paramShort));
    return localShort3;
  }
  
  public static Short3 div(Short3 paramShort31, Short3 paramShort32)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort31.x / paramShort32.x));
    localShort3.y = ((short)(paramShort31.y / paramShort32.y));
    localShort3.z = ((short)(paramShort31.z / paramShort32.z));
    return localShort3;
  }
  
  public static Short3 div(Short3 paramShort3, short paramShort)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort3.x / paramShort));
    localShort3.y = ((short)(paramShort3.y / paramShort));
    localShort3.z = ((short)(paramShort3.z / paramShort));
    return localShort3;
  }
  
  public static short dotProduct(Short3 paramShort31, Short3 paramShort32)
  {
    return (short)(paramShort32.x * paramShort31.x + paramShort32.y * paramShort31.y + paramShort32.z * paramShort31.z);
  }
  
  public static Short3 mod(Short3 paramShort31, Short3 paramShort32)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort31.x % paramShort32.x));
    localShort3.y = ((short)(paramShort31.y % paramShort32.y));
    localShort3.z = ((short)(paramShort31.z % paramShort32.z));
    return localShort3;
  }
  
  public static Short3 mod(Short3 paramShort3, short paramShort)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort3.x % paramShort));
    localShort3.y = ((short)(paramShort3.y % paramShort));
    localShort3.z = ((short)(paramShort3.z % paramShort));
    return localShort3;
  }
  
  public static Short3 mul(Short3 paramShort31, Short3 paramShort32)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort31.x * paramShort32.x));
    localShort3.y = ((short)(paramShort31.y * paramShort32.y));
    localShort3.z = ((short)(paramShort31.z * paramShort32.z));
    return localShort3;
  }
  
  public static Short3 mul(Short3 paramShort3, short paramShort)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort3.x * paramShort));
    localShort3.y = ((short)(paramShort3.y * paramShort));
    localShort3.z = ((short)(paramShort3.z * paramShort));
    return localShort3;
  }
  
  public static Short3 sub(Short3 paramShort31, Short3 paramShort32)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort31.x - paramShort32.x));
    localShort3.y = ((short)(paramShort31.y - paramShort32.y));
    localShort3.z = ((short)(paramShort31.z - paramShort32.z));
    return localShort3;
  }
  
  public static Short3 sub(Short3 paramShort3, short paramShort)
  {
    Short3 localShort3 = new Short3();
    localShort3.x = ((short)(paramShort3.x - paramShort));
    localShort3.y = ((short)(paramShort3.y - paramShort));
    localShort3.z = ((short)(paramShort3.z - paramShort));
    return localShort3;
  }
  
  public void add(Short3 paramShort3)
  {
    this.x = ((short)(this.x + paramShort3.x));
    this.y = ((short)(this.y + paramShort3.y));
    this.z = ((short)(this.z + paramShort3.z));
  }
  
  public void add(short paramShort)
  {
    this.x = ((short)(this.x + paramShort));
    this.y = ((short)(this.y + paramShort));
    this.z = ((short)(this.z + paramShort));
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
    }
    this.z = ((short)(this.z + paramShort));
  }
  
  public void addMultiple(Short3 paramShort3, short paramShort)
  {
    this.x = ((short)(this.x + paramShort3.x * paramShort));
    this.y = ((short)(this.y + paramShort3.y * paramShort));
    this.z = ((short)(this.z + paramShort3.z * paramShort));
  }
  
  public void copyTo(short[] paramArrayOfShort, int paramInt)
  {
    paramArrayOfShort[paramInt] = this.x;
    paramArrayOfShort[(paramInt + 1)] = this.y;
    paramArrayOfShort[(paramInt + 2)] = this.z;
  }
  
  public void div(Short3 paramShort3)
  {
    this.x = ((short)(this.x / paramShort3.x));
    this.y = ((short)(this.y / paramShort3.y));
    this.z = ((short)(this.z / paramShort3.z));
  }
  
  public void div(short paramShort)
  {
    this.x = ((short)(this.x / paramShort));
    this.y = ((short)(this.y / paramShort));
    this.z = ((short)(this.z / paramShort));
  }
  
  public short dotProduct(Short3 paramShort3)
  {
    return (short)(this.x * paramShort3.x + this.y * paramShort3.y + this.z * paramShort3.z);
  }
  
  public short elementSum()
  {
    return (short)(this.x + this.y + this.z);
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
    }
    return this.z;
  }
  
  public short length()
  {
    return 3;
  }
  
  public void mod(Short3 paramShort3)
  {
    this.x = ((short)(this.x % paramShort3.x));
    this.y = ((short)(this.y % paramShort3.y));
    this.z = ((short)(this.z % paramShort3.z));
  }
  
  public void mod(short paramShort)
  {
    this.x = ((short)(this.x % paramShort));
    this.y = ((short)(this.y % paramShort));
    this.z = ((short)(this.z % paramShort));
  }
  
  public void mul(Short3 paramShort3)
  {
    this.x = ((short)(this.x * paramShort3.x));
    this.y = ((short)(this.y * paramShort3.y));
    this.z = ((short)(this.z * paramShort3.z));
  }
  
  public void mul(short paramShort)
  {
    this.x = ((short)(this.x * paramShort));
    this.y = ((short)(this.y * paramShort));
    this.z = ((short)(this.z * paramShort));
  }
  
  public void negate()
  {
    this.x = ((short)-this.x);
    this.y = ((short)-this.y);
    this.z = ((short)-this.z);
  }
  
  public void set(Short3 paramShort3)
  {
    this.x = paramShort3.x;
    this.y = paramShort3.y;
    this.z = paramShort3.z;
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
    }
    this.z = paramShort;
  }
  
  public void setValues(short paramShort1, short paramShort2, short paramShort3)
  {
    this.x = paramShort1;
    this.y = paramShort2;
    this.z = paramShort3;
  }
  
  public void sub(Short3 paramShort3)
  {
    this.x = ((short)(this.x - paramShort3.x));
    this.y = ((short)(this.y - paramShort3.y));
    this.z = ((short)(this.z - paramShort3.z));
  }
  
  public void sub(short paramShort)
  {
    this.x = ((short)(this.x - paramShort));
    this.y = ((short)(this.y - paramShort));
    this.z = ((short)(this.z - paramShort));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Short3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */