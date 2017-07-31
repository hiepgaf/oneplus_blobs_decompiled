package android.renderscript;

public class Byte2
{
  public byte x;
  public byte y;
  
  public Byte2() {}
  
  public Byte2(byte paramByte1, byte paramByte2)
  {
    this.x = paramByte1;
    this.y = paramByte2;
  }
  
  public Byte2(Byte2 paramByte2)
  {
    this.x = paramByte2.x;
    this.y = paramByte2.y;
  }
  
  public static Byte2 add(Byte2 paramByte2, byte paramByte)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte2.x + paramByte));
    localByte2.y = ((byte)(paramByte2.y + paramByte));
    return localByte2;
  }
  
  public static Byte2 add(Byte2 paramByte21, Byte2 paramByte22)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte21.x + paramByte22.x));
    localByte2.y = ((byte)(paramByte21.y + paramByte22.y));
    return localByte2;
  }
  
  public static Byte2 div(Byte2 paramByte2, byte paramByte)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte2.x / paramByte));
    localByte2.y = ((byte)(paramByte2.y / paramByte));
    return localByte2;
  }
  
  public static Byte2 div(Byte2 paramByte21, Byte2 paramByte22)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte21.x / paramByte22.x));
    localByte2.y = ((byte)(paramByte21.y / paramByte22.y));
    return localByte2;
  }
  
  public static byte dotProduct(Byte2 paramByte21, Byte2 paramByte22)
  {
    return (byte)(paramByte22.x * paramByte21.x + paramByte22.y * paramByte21.y);
  }
  
  public static Byte2 mul(Byte2 paramByte2, byte paramByte)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte2.x * paramByte));
    localByte2.y = ((byte)(paramByte2.y * paramByte));
    return localByte2;
  }
  
  public static Byte2 mul(Byte2 paramByte21, Byte2 paramByte22)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte21.x * paramByte22.x));
    localByte2.y = ((byte)(paramByte21.y * paramByte22.y));
    return localByte2;
  }
  
  public static Byte2 sub(Byte2 paramByte2, byte paramByte)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte2.x - paramByte));
    localByte2.y = ((byte)(paramByte2.y - paramByte));
    return localByte2;
  }
  
  public static Byte2 sub(Byte2 paramByte21, Byte2 paramByte22)
  {
    Byte2 localByte2 = new Byte2();
    localByte2.x = ((byte)(paramByte21.x - paramByte22.x));
    localByte2.y = ((byte)(paramByte21.y - paramByte22.y));
    return localByte2;
  }
  
  public void add(byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte));
    this.y = ((byte)(this.y + paramByte));
  }
  
  public void add(Byte2 paramByte2)
  {
    this.x = ((byte)(this.x + paramByte2.x));
    this.y = ((byte)(this.y + paramByte2.y));
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
    }
    this.y = ((byte)(this.y + paramByte));
  }
  
  public void addMultiple(Byte2 paramByte2, byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte2.x * paramByte));
    this.y = ((byte)(this.y + paramByte2.y * paramByte));
  }
  
  public void copyTo(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = this.x;
    paramArrayOfByte[(paramInt + 1)] = this.y;
  }
  
  public void div(byte paramByte)
  {
    this.x = ((byte)(this.x / paramByte));
    this.y = ((byte)(this.y / paramByte));
  }
  
  public void div(Byte2 paramByte2)
  {
    this.x = ((byte)(this.x / paramByte2.x));
    this.y = ((byte)(this.y / paramByte2.y));
  }
  
  public byte dotProduct(Byte2 paramByte2)
  {
    return (byte)(this.x * paramByte2.x + this.y * paramByte2.y);
  }
  
  public byte elementSum()
  {
    return (byte)(this.x + this.y);
  }
  
  public byte get(int paramInt)
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
  
  public byte length()
  {
    return 2;
  }
  
  public void mul(byte paramByte)
  {
    this.x = ((byte)(this.x * paramByte));
    this.y = ((byte)(this.y * paramByte));
  }
  
  public void mul(Byte2 paramByte2)
  {
    this.x = ((byte)(this.x * paramByte2.x));
    this.y = ((byte)(this.y * paramByte2.y));
  }
  
  public void negate()
  {
    this.x = ((byte)-this.x);
    this.y = ((byte)-this.y);
  }
  
  public void set(Byte2 paramByte2)
  {
    this.x = paramByte2.x;
    this.y = paramByte2.y;
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
    }
    this.y = paramByte;
  }
  
  public void setValues(byte paramByte1, byte paramByte2)
  {
    this.x = paramByte1;
    this.y = paramByte2;
  }
  
  public void sub(byte paramByte)
  {
    this.x = ((byte)(this.x - paramByte));
    this.y = ((byte)(this.y - paramByte));
  }
  
  public void sub(Byte2 paramByte2)
  {
    this.x = ((byte)(this.x - paramByte2.x));
    this.y = ((byte)(this.y - paramByte2.y));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Byte2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */