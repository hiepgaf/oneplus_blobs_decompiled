package android.renderscript;

public class Long2
{
  public long x;
  public long y;
  
  public Long2() {}
  
  public Long2(long paramLong)
  {
    this.y = paramLong;
    this.x = paramLong;
  }
  
  public Long2(long paramLong1, long paramLong2)
  {
    this.x = paramLong1;
    this.y = paramLong2;
  }
  
  public Long2(Long2 paramLong2)
  {
    this.x = paramLong2.x;
    this.y = paramLong2.y;
  }
  
  public static Long2 add(Long2 paramLong2, long paramLong)
  {
    Long2 localLong2 = new Long2();
    paramLong2.x += paramLong;
    paramLong2.y += paramLong;
    return localLong2;
  }
  
  public static Long2 add(Long2 paramLong21, Long2 paramLong22)
  {
    Long2 localLong2 = new Long2();
    paramLong21.x += paramLong22.x;
    paramLong21.y += paramLong22.y;
    return localLong2;
  }
  
  public static Long2 div(Long2 paramLong2, long paramLong)
  {
    Long2 localLong2 = new Long2();
    paramLong2.x /= paramLong;
    paramLong2.y /= paramLong;
    return localLong2;
  }
  
  public static Long2 div(Long2 paramLong21, Long2 paramLong22)
  {
    Long2 localLong2 = new Long2();
    paramLong21.x /= paramLong22.x;
    paramLong21.y /= paramLong22.y;
    return localLong2;
  }
  
  public static long dotProduct(Long2 paramLong21, Long2 paramLong22)
  {
    return paramLong22.x * paramLong21.x + paramLong22.y * paramLong21.y;
  }
  
  public static Long2 mod(Long2 paramLong2, long paramLong)
  {
    Long2 localLong2 = new Long2();
    paramLong2.x %= paramLong;
    paramLong2.y %= paramLong;
    return localLong2;
  }
  
  public static Long2 mod(Long2 paramLong21, Long2 paramLong22)
  {
    Long2 localLong2 = new Long2();
    paramLong21.x %= paramLong22.x;
    paramLong21.y %= paramLong22.y;
    return localLong2;
  }
  
  public static Long2 mul(Long2 paramLong2, long paramLong)
  {
    Long2 localLong2 = new Long2();
    paramLong2.x *= paramLong;
    paramLong2.y *= paramLong;
    return localLong2;
  }
  
  public static Long2 mul(Long2 paramLong21, Long2 paramLong22)
  {
    Long2 localLong2 = new Long2();
    paramLong21.x *= paramLong22.x;
    paramLong21.y *= paramLong22.y;
    return localLong2;
  }
  
  public static Long2 sub(Long2 paramLong2, long paramLong)
  {
    Long2 localLong2 = new Long2();
    paramLong2.x -= paramLong;
    paramLong2.y -= paramLong;
    return localLong2;
  }
  
  public static Long2 sub(Long2 paramLong21, Long2 paramLong22)
  {
    Long2 localLong2 = new Long2();
    paramLong21.x -= paramLong22.x;
    paramLong21.y -= paramLong22.y;
    return localLong2;
  }
  
  public void add(long paramLong)
  {
    this.x += paramLong;
    this.y += paramLong;
  }
  
  public void add(Long2 paramLong2)
  {
    this.x += paramLong2.x;
    this.y += paramLong2.y;
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
    }
    this.y += paramLong;
  }
  
  public void addMultiple(Long2 paramLong2, long paramLong)
  {
    this.x += paramLong2.x * paramLong;
    this.y += paramLong2.y * paramLong;
  }
  
  public void copyTo(long[] paramArrayOfLong, int paramInt)
  {
    paramArrayOfLong[paramInt] = this.x;
    paramArrayOfLong[(paramInt + 1)] = this.y;
  }
  
  public void div(long paramLong)
  {
    this.x /= paramLong;
    this.y /= paramLong;
  }
  
  public void div(Long2 paramLong2)
  {
    this.x /= paramLong2.x;
    this.y /= paramLong2.y;
  }
  
  public long dotProduct(Long2 paramLong2)
  {
    return this.x * paramLong2.x + this.y * paramLong2.y;
  }
  
  public long elementSum()
  {
    return this.x + this.y;
  }
  
  public long get(int paramInt)
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
  
  public long length()
  {
    return 2L;
  }
  
  public void mod(long paramLong)
  {
    this.x %= paramLong;
    this.y %= paramLong;
  }
  
  public void mod(Long2 paramLong2)
  {
    this.x %= paramLong2.x;
    this.y %= paramLong2.y;
  }
  
  public void mul(long paramLong)
  {
    this.x *= paramLong;
    this.y *= paramLong;
  }
  
  public void mul(Long2 paramLong2)
  {
    this.x *= paramLong2.x;
    this.y *= paramLong2.y;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
  }
  
  public void set(Long2 paramLong2)
  {
    this.x = paramLong2.x;
    this.y = paramLong2.y;
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
    }
    this.y = paramLong;
  }
  
  public void setValues(long paramLong1, long paramLong2)
  {
    this.x = paramLong1;
    this.y = paramLong2;
  }
  
  public void sub(long paramLong)
  {
    this.x -= paramLong;
    this.y -= paramLong;
  }
  
  public void sub(Long2 paramLong2)
  {
    this.x -= paramLong2.x;
    this.y -= paramLong2.y;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Long2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */