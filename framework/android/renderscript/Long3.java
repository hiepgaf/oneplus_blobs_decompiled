package android.renderscript;

public class Long3
{
  public long x;
  public long y;
  public long z;
  
  public Long3() {}
  
  public Long3(long paramLong)
  {
    this.z = paramLong;
    this.y = paramLong;
    this.x = paramLong;
  }
  
  public Long3(long paramLong1, long paramLong2, long paramLong3)
  {
    this.x = paramLong1;
    this.y = paramLong2;
    this.z = paramLong3;
  }
  
  public Long3(Long3 paramLong3)
  {
    this.x = paramLong3.x;
    this.y = paramLong3.y;
    this.z = paramLong3.z;
  }
  
  public static Long3 add(Long3 paramLong3, long paramLong)
  {
    Long3 localLong3 = new Long3();
    paramLong3.x += paramLong;
    paramLong3.y += paramLong;
    paramLong3.z += paramLong;
    return localLong3;
  }
  
  public static Long3 add(Long3 paramLong31, Long3 paramLong32)
  {
    Long3 localLong3 = new Long3();
    paramLong31.x += paramLong32.x;
    paramLong31.y += paramLong32.y;
    paramLong31.z += paramLong32.z;
    return localLong3;
  }
  
  public static Long3 div(Long3 paramLong3, long paramLong)
  {
    Long3 localLong3 = new Long3();
    paramLong3.x /= paramLong;
    paramLong3.y /= paramLong;
    paramLong3.z /= paramLong;
    return localLong3;
  }
  
  public static Long3 div(Long3 paramLong31, Long3 paramLong32)
  {
    Long3 localLong3 = new Long3();
    paramLong31.x /= paramLong32.x;
    paramLong31.y /= paramLong32.y;
    paramLong31.z /= paramLong32.z;
    return localLong3;
  }
  
  public static long dotProduct(Long3 paramLong31, Long3 paramLong32)
  {
    return paramLong32.x * paramLong31.x + paramLong32.y * paramLong31.y + paramLong32.z * paramLong31.z;
  }
  
  public static Long3 mod(Long3 paramLong3, long paramLong)
  {
    Long3 localLong3 = new Long3();
    paramLong3.x %= paramLong;
    paramLong3.y %= paramLong;
    paramLong3.z %= paramLong;
    return localLong3;
  }
  
  public static Long3 mod(Long3 paramLong31, Long3 paramLong32)
  {
    Long3 localLong3 = new Long3();
    paramLong31.x %= paramLong32.x;
    paramLong31.y %= paramLong32.y;
    paramLong31.z %= paramLong32.z;
    return localLong3;
  }
  
  public static Long3 mul(Long3 paramLong3, long paramLong)
  {
    Long3 localLong3 = new Long3();
    paramLong3.x *= paramLong;
    paramLong3.y *= paramLong;
    paramLong3.z *= paramLong;
    return localLong3;
  }
  
  public static Long3 mul(Long3 paramLong31, Long3 paramLong32)
  {
    Long3 localLong3 = new Long3();
    paramLong31.x *= paramLong32.x;
    paramLong31.y *= paramLong32.y;
    paramLong31.z *= paramLong32.z;
    return localLong3;
  }
  
  public static Long3 sub(Long3 paramLong3, long paramLong)
  {
    Long3 localLong3 = new Long3();
    paramLong3.x -= paramLong;
    paramLong3.y -= paramLong;
    paramLong3.z -= paramLong;
    return localLong3;
  }
  
  public static Long3 sub(Long3 paramLong31, Long3 paramLong32)
  {
    Long3 localLong3 = new Long3();
    paramLong31.x -= paramLong32.x;
    paramLong31.y -= paramLong32.y;
    paramLong31.z -= paramLong32.z;
    return localLong3;
  }
  
  public void add(long paramLong)
  {
    this.x += paramLong;
    this.y += paramLong;
    this.z += paramLong;
  }
  
  public void add(Long3 paramLong3)
  {
    this.x += paramLong3.x;
    this.y += paramLong3.y;
    this.z += paramLong3.z;
  }
  
  public void addAt(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x += paramLong;
      return;
    case 1: 
      this.y += paramLong;
      return;
    }
    this.z += paramLong;
  }
  
  public void addMultiple(Long3 paramLong3, long paramLong)
  {
    this.x += paramLong3.x * paramLong;
    this.y += paramLong3.y * paramLong;
    this.z += paramLong3.z * paramLong;
  }
  
  public void copyTo(long[] paramArrayOfLong, int paramInt)
  {
    paramArrayOfLong[paramInt] = this.x;
    paramArrayOfLong[(paramInt + 1)] = this.y;
    paramArrayOfLong[(paramInt + 2)] = this.z;
  }
  
  public void div(long paramLong)
  {
    this.x /= paramLong;
    this.y /= paramLong;
    this.z /= paramLong;
  }
  
  public void div(Long3 paramLong3)
  {
    this.x /= paramLong3.x;
    this.y /= paramLong3.y;
    this.z /= paramLong3.z;
  }
  
  public long dotProduct(Long3 paramLong3)
  {
    return this.x * paramLong3.x + this.y * paramLong3.y + this.z * paramLong3.z;
  }
  
  public long elementSum()
  {
    return this.x + this.y + this.z;
  }
  
  public long get(int paramInt)
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
  
  public long length()
  {
    return 3L;
  }
  
  public void mod(long paramLong)
  {
    this.x %= paramLong;
    this.y %= paramLong;
    this.z %= paramLong;
  }
  
  public void mod(Long3 paramLong3)
  {
    this.x %= paramLong3.x;
    this.y %= paramLong3.y;
    this.z %= paramLong3.z;
  }
  
  public void mul(long paramLong)
  {
    this.x *= paramLong;
    this.y *= paramLong;
    this.z *= paramLong;
  }
  
  public void mul(Long3 paramLong3)
  {
    this.x *= paramLong3.x;
    this.y *= paramLong3.y;
    this.z *= paramLong3.z;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
  }
  
  public void set(Long3 paramLong3)
  {
    this.x = paramLong3.x;
    this.y = paramLong3.y;
    this.z = paramLong3.z;
  }
  
  public void setAt(int paramInt, long paramLong)
  {
    switch (paramInt)
    {
    default: 
      throw new IndexOutOfBoundsException("Index: i");
    case 0: 
      this.x = paramLong;
      return;
    case 1: 
      this.y = paramLong;
      return;
    }
    this.z = paramLong;
  }
  
  public void setValues(long paramLong1, long paramLong2, long paramLong3)
  {
    this.x = paramLong1;
    this.y = paramLong2;
    this.z = paramLong3;
  }
  
  public void sub(long paramLong)
  {
    this.x -= paramLong;
    this.y -= paramLong;
    this.z -= paramLong;
  }
  
  public void sub(Long3 paramLong3)
  {
    this.x -= paramLong3.x;
    this.y -= paramLong3.y;
    this.z -= paramLong3.z;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Long3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */