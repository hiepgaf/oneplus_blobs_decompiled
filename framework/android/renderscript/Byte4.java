package android.renderscript;

public class Byte4
{
  public byte w;
  public byte x;
  public byte y;
  public byte z;
  
  public Byte4() {}
  
  public Byte4(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    this.x = paramByte1;
    this.y = paramByte2;
    this.z = paramByte3;
    this.w = paramByte4;
  }
  
  public Byte4(Byte4 paramByte4)
  {
    this.x = paramByte4.x;
    this.y = paramByte4.y;
    this.z = paramByte4.z;
    this.w = paramByte4.w;
  }
  
  public static Byte4 add(Byte4 paramByte4, byte paramByte)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte4.x + paramByte));
    localByte4.y = ((byte)(paramByte4.y + paramByte));
    localByte4.z = ((byte)(paramByte4.z + paramByte));
    localByte4.w = ((byte)(paramByte4.w + paramByte));
    return localByte4;
  }
  
  public static Byte4 add(Byte4 paramByte41, Byte4 paramByte42)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte41.x + paramByte42.x));
    localByte4.y = ((byte)(paramByte41.y + paramByte42.y));
    localByte4.z = ((byte)(paramByte41.z + paramByte42.z));
    localByte4.w = ((byte)(paramByte41.w + paramByte42.w));
    return localByte4;
  }
  
  public static Byte4 div(Byte4 paramByte4, byte paramByte)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte4.x / paramByte));
    localByte4.y = ((byte)(paramByte4.y / paramByte));
    localByte4.z = ((byte)(paramByte4.z / paramByte));
    localByte4.w = ((byte)(paramByte4.w / paramByte));
    return localByte4;
  }
  
  public static Byte4 div(Byte4 paramByte41, Byte4 paramByte42)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte41.x / paramByte42.x));
    localByte4.y = ((byte)(paramByte41.y / paramByte42.y));
    localByte4.z = ((byte)(paramByte41.z / paramByte42.z));
    localByte4.w = ((byte)(paramByte41.w / paramByte42.w));
    return localByte4;
  }
  
  public static byte dotProduct(Byte4 paramByte41, Byte4 paramByte42)
  {
    return (byte)(paramByte42.x * paramByte41.x + paramByte42.y * paramByte41.y + paramByte42.z * paramByte41.z + paramByte42.w * paramByte41.w);
  }
  
  public static Byte4 mul(Byte4 paramByte4, byte paramByte)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte4.x * paramByte));
    localByte4.y = ((byte)(paramByte4.y * paramByte));
    localByte4.z = ((byte)(paramByte4.z * paramByte));
    localByte4.w = ((byte)(paramByte4.w * paramByte));
    return localByte4;
  }
  
  public static Byte4 mul(Byte4 paramByte41, Byte4 paramByte42)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte41.x * paramByte42.x));
    localByte4.y = ((byte)(paramByte41.y * paramByte42.y));
    localByte4.z = ((byte)(paramByte41.z * paramByte42.z));
    localByte4.w = ((byte)(paramByte41.w * paramByte42.w));
    return localByte4;
  }
  
  public static Byte4 sub(Byte4 paramByte4, byte paramByte)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte4.x - paramByte));
    localByte4.y = ((byte)(paramByte4.y - paramByte));
    localByte4.z = ((byte)(paramByte4.z - paramByte));
    localByte4.w = ((byte)(paramByte4.w - paramByte));
    return localByte4;
  }
  
  public static Byte4 sub(Byte4 paramByte41, Byte4 paramByte42)
  {
    Byte4 localByte4 = new Byte4();
    localByte4.x = ((byte)(paramByte41.x - paramByte42.x));
    localByte4.y = ((byte)(paramByte41.y - paramByte42.y));
    localByte4.z = ((byte)(paramByte41.z - paramByte42.z));
    localByte4.w = ((byte)(paramByte41.w - paramByte42.w));
    return localByte4;
  }
  
  public void add(byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte));
    this.y = ((byte)(this.y + paramByte));
    this.z = ((byte)(this.z + paramByte));
    this.w = ((byte)(this.w + paramByte));
  }
  
  public void add(Byte4 paramByte4)
  {
    this.x = ((byte)(this.x + paramByte4.x));
    this.y = ((byte)(this.y + paramByte4.y));
    this.z = ((byte)(this.z + paramByte4.z));
    this.w = ((byte)(this.w + paramByte4.w));
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
    case 2: 
      this.z = ((byte)(this.z + paramByte));
      return;
    }
    this.w = ((byte)(this.w + paramByte));
  }
  
  public void addMultiple(Byte4 paramByte4, byte paramByte)
  {
    this.x = ((byte)(this.x + paramByte4.x * paramByte));
    this.y = ((byte)(this.y + paramByte4.y * paramByte));
    this.z = ((byte)(this.z + paramByte4.z * paramByte));
    this.w = ((byte)(this.w + paramByte4.w * paramByte));
  }
  
  public void copyTo(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[paramInt] = this.x;
    paramArrayOfByte[(paramInt + 1)] = this.y;
    paramArrayOfByte[(paramInt + 2)] = this.z;
    paramArrayOfByte[(paramInt + 3)] = this.w;
  }
  
  public void div(byte paramByte)
  {
    this.x = ((byte)(this.x / paramByte));
    this.y = ((byte)(this.y / paramByte));
    this.z = ((byte)(this.z / paramByte));
    this.w = ((byte)(this.w / paramByte));
  }
  
  public void div(Byte4 paramByte4)
  {
    this.x = ((byte)(this.x / paramByte4.x));
    this.y = ((byte)(this.y / paramByte4.y));
    this.z = ((byte)(this.z / paramByte4.z));
    this.w = ((byte)(this.w / paramByte4.w));
  }
  
  public byte dotProduct(Byte4 paramByte4)
  {
    return (byte)(this.x * paramByte4.x + this.y * paramByte4.y + this.z * paramByte4.z + this.w * paramByte4.w);
  }
  
  public byte elementSum()
  {
    return (byte)(this.x + this.y + this.z + this.w);
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
    case 2: 
      return this.z;
    }
    return this.w;
  }
  
  public byte length()
  {
    return 4;
  }
  
  public void mul(byte paramByte)
  {
    this.x = ((byte)(this.x * paramByte));
    this.y = ((byte)(this.y * paramByte));
    this.z = ((byte)(this.z * paramByte));
    this.w = ((byte)(this.w * paramByte));
  }
  
  public void mul(Byte4 paramByte4)
  {
    this.x = ((byte)(this.x * paramByte4.x));
    this.y = ((byte)(this.y * paramByte4.y));
    this.z = ((byte)(this.z * paramByte4.z));
    this.w = ((byte)(this.w * paramByte4.w));
  }
  
  public void negate()
  {
    this.x = ((byte)-this.x);
    this.y = ((byte)-this.y);
    this.z = ((byte)-this.z);
    this.w = ((byte)-this.w);
  }
  
  public void set(Byte4 paramByte4)
  {
    this.x = paramByte4.x;
    this.y = paramByte4.y;
    this.z = paramByte4.z;
    this.w = paramByte4.w;
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
    case 2: 
      this.z = paramByte;
      return;
    }
    this.w = paramByte;
  }
  
  public void setValues(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    this.x = paramByte1;
    this.y = paramByte2;
    this.z = paramByte3;
    this.w = paramByte4;
  }
  
  public void sub(byte paramByte)
  {
    this.x = ((byte)(this.x - paramByte));
    this.y = ((byte)(this.y - paramByte));
    this.z = ((byte)(this.z - paramByte));
    this.w = ((byte)(this.w - paramByte));
  }
  
  public void sub(Byte4 paramByte4)
  {
    this.x = ((byte)(this.x - paramByte4.x));
    this.y = ((byte)(this.y - paramByte4.y));
    this.z = ((byte)(this.z - paramByte4.z));
    this.w = ((byte)(this.w - paramByte4.w));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Byte4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */