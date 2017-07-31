package android.renderscript;

public class Byte3
{
  public byte x;
  public byte y;
  public byte z;
  
  public Byte3() {}
  
  public Byte3(byte paramByte1, byte paramByte2, byte paramByte3)
  {
    this.x = paramByte1;
    this.y = paramByte2;
    this.z = paramByte3;
  }
  
  public Byte3(Byte3 paramByte3)
  {
    this.x = paramByte3.x;
    this.y = paramByte3.y;
    this.z = paramByte3.z;
  }
  
  public static Byte3 add(Byte3 paramByte3, byte paramByte)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte3.x + paramByte));
    localByte3.y = ((byte)(paramByte3.y + paramByte));
    localByte3.z = ((byte)(paramByte3.z + paramByte));
    return localByte3;
  }
  
  public static Byte3 add(Byte3 paramByte31, Byte3 paramByte32)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte31.x + paramByte32.x));
    localByte3.y = ((byte)(paramByte31.y + paramByte32.y));
    localByte3.z = ((byte)(paramByte31.z + paramByte32.z));
    return localByte3;
  }
  
  public static Byte3 div(Byte3 paramByte3, byte paramByte)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte3.x / paramByte));
    localByte3.y = ((byte)(paramByte3.y / paramByte));
    localByte3.z = ((byte)(paramByte3.z / paramByte));
    return localByte3;
  }
  
  public static Byte3 div(Byte3 paramByte31, Byte3 paramByte32)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte31.x / paramByte32.x));
    localByte3.y = ((byte)(paramByte31.y / paramByte32.y));
    localByte3.z = ((byte)(paramByte31.z / paramByte32.z));
    return localByte3;
  }
  
  public static byte dotProduct(Byte3 paramByte31, Byte3 paramByte32)
  {
    return (byte)((byte)((byte)(paramByte32.x * paramByte31.x) + (byte)(paramByte32.y * paramByte31.y)) + (byte)(paramByte32.z * paramByte31.z));
  }
  
  public static Byte3 mul(Byte3 paramByte3, byte paramByte)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte3.x * paramByte));
    localByte3.y = ((byte)(paramByte3.y * paramByte));
    localByte3.z = ((byte)(paramByte3.z * paramByte));
    return localByte3;
  }
  
  public static Byte3 mul(Byte3 paramByte31, Byte3 paramByte32)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte31.x * paramByte32.x));
    localByte3.y = ((byte)(paramByte31.y * paramByte32.y));
    localByte3.z = ((byte)(paramByte31.z * paramByte32.z));
    return localByte3;
  }
  
  public static Byte3 sub(Byte3 paramByte3, byte paramByte)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte3.x - paramByte));
    localByte3.y = ((byte)(paramByte3.y - paramByte));
    localByte3.z = ((byte)(paramByte3.z - paramByte));
    return localByte3;
  }
  
  public static Byte3 sub(Byte3 paramByte31, Byte3 paramByte32)
  {
    Byte3 localByte3 = new Byte3();
    localByte3.x = ((byte)(paramByte31.x - paramByte32.x));
    localByte3.y = ((byte)(paramByte31.y - paramByte32.y));
    localByte3.z = ((byte)(paramByte31.z - paramByte32.z));
    return localByte3;
  }
  
  public void add(byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte));
    this.y = ((byte)(this.y + paramByte));
    this.z = ((byte)(this.z + paramByte));
  }
  
  public void add(Byte3 paramByte3)
  {
    this.x = ((byte)(this.x + paramByte3.x));
    this.y = ((byte)(this.y + paramByte3.y));
    this.z = ((byte)(this.z + paramByte3.z));
  }
  
  public void addAt(int paramInt, byte paramByte)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = ((byte)(this.x + paramByte));
      return;
    case 1: 
      this.y = ((byte)(this.y + paramByte));
      return;
    }
    this.z = ((byte)(this.z + paramByte));
  }
  
  public void addMultiple(Byte3 paramByte3, byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte3.x * paramByte));
    this.y = ((byte)(this.y + paramByte3.y * paramByte));
    this.z = ((byte)(this.z + paramByte3.z * paramByte));
  }
  
  public void copyTo(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = this.x;
    paramArrayOfByte[(paramInt + 1)] = this.y;
    paramArrayOfByte[(paramInt + 2)] = this.z;
  }
  
  public void div(byte paramByte)
  {
    this.x = ((byte)(this.x / paramByte));
    this.y = ((byte)(this.y / paramByte));
    this.z = ((byte)(this.z / paramByte));
  }
  
  public void div(Byte3 paramByte3)
  {
    this.x = ((byte)(this.x / paramByte3.x));
    this.y = ((byte)(this.y / paramByte3.y));
    this.z = ((byte)(this.z / paramByte3.z));
  }
  
  public byte dotProduct(Byte3 paramByte3)
  {
    return (byte)((byte)((byte)(this.x * paramByte3.x) + (byte)(this.y * paramByte3.y)) + (byte)(this.z * paramByte3.z));
  }
  
  public byte elementSum()
  {
    return (byte)(this.x + this.y + this.z);
  }
  
  public byte get(int paramInt)
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
  
  public byte length()
  {
    return 3;
  }
  
  public void mul(byte paramByte)
  {
    this.x = ((byte)(this.x * paramByte));
    this.y = ((byte)(this.y * paramByte));
    this.z = ((byte)(this.z * paramByte));
  }
  
  public void mul(Byte3 paramByte3)
  {
    this.x = ((byte)(this.x * paramByte3.x));
    this.y = ((byte)(this.y * paramByte3.y));
    this.z = ((byte)(this.z * paramByte3.z));
  }
  
  public void negate()
  {
    this.x = ((byte)-this.x);
    this.y = ((byte)-this.y);
    this.z = ((byte)-this.z);
  }
  
  public void set(Byte3 paramByte3)
  {
    this.x = paramByte3.x;
    this.y = paramByte3.y;
    this.z = paramByte3.z;
  }
  
  public void setAt(int paramInt, byte paramByte)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramByte;
      return;
    case 1: 
      this.y = paramByte;
      return;
    }
    this.z = paramByte;
  }
  
  public void setValues(byte paramByte1, byte paramByte2, byte paramByte3)
  {
    this.x = paramByte1;
    this.y = paramByte2;
    this.z = paramByte3;
  }
  
  public void sub(byte paramByte)
  {
    this.x = ((byte)(this.x - paramByte));
    this.y = ((byte)(this.y - paramByte));
    this.z = ((byte)(this.z - paramByte));
  }
  
  public void sub(Byte3 paramByte3)
  {
    this.x = ((byte)(this.x - paramByte3.x));
    this.y = ((byte)(this.y - paramByte3.y));
    this.z = ((byte)(this.z - paramByte3.z));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Byte3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */