package android.renderscript;

public class Long4
{
  public long w;
  public long x;
  public long y;
  public long z;
  
  public Long4() {}
  
  public Long4(long paramLong)
  {
    this.w = paramLong;
    this.z = paramLong;
    this.y = paramLong;
    this.x = paramLong;
  }
  
  public Long4(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.x = paramLong1;
    this.y = paramLong2;
    this.z = paramLong3;
    this.w = paramLong4;
  }
  
  public Long4(Long4 paramLong4)
  {
    this.x = paramLong4.x;
    this.y = paramLong4.y;
    this.z = paramLong4.z;
    this.w = paramLong4.w;
  }
  
  public static Long4 add(Long4 paramLong4, long paramLong)
  {
    Long4 localLong4 = new Long4();
    paramLong4.x += paramLong;
    paramLong4.y += paramLong;
    paramLong4.z += paramLong;
    paramLong4.w += paramLong;
    return localLong4;
  }
  
  public static Long4 add(Long4 paramLong41, Long4 paramLong42)
  {
    Long4 localLong4 = new Long4();
    paramLong41.x += paramLong42.x;
    paramLong41.y += paramLong42.y;
    paramLong41.z += paramLong42.z;
    paramLong41.w += paramLong42.w;
    return localLong4;
  }
  
  public static Long4 div(Long4 paramLong4, long paramLong)
  {
    Long4 localLong4 = new Long4();
    paramLong4.x /= paramLong;
    paramLong4.y /= paramLong;
    paramLong4.z /= paramLong;
    paramLong4.w /= paramLong;
    return localLong4;
  }
  
  public static Long4 div(Long4 paramLong41, Long4 paramLong42)
  {
    Long4 localLong4 = new Long4();
    paramLong41.x /= paramLong42.x;
    paramLong41.y /= paramLong42.y;
    paramLong41.z /= paramLong42.z;
    paramLong41.w /= paramLong42.w;
    return localLong4;
  }
  
  public static long dotProduct(Long4 paramLong41, Long4 paramLong42)
  {
    return paramLong42.x * paramLong41.x + paramLong42.y * paramLong41.y + paramLong42.z * paramLong41.z + paramLong42.w * paramLong41.w;
  }
  
  public static Long4 mod(Long4 paramLong4, long paramLong)
  {
    Long4 localLong4 = new Long4();
    paramLong4.x %= paramLong;
    paramLong4.y %= paramLong;
    paramLong4.z %= paramLong;
    paramLong4.w %= paramLong;
    return localLong4;
  }
  
  public static Long4 mod(Long4 paramLong41, Long4 paramLong42)
  {
    Long4 localLong4 = new Long4();
    paramLong41.x %= paramLong42.x;
    paramLong41.y %= paramLong42.y;
    paramLong41.z %= paramLong42.z;
    paramLong41.w %= paramLong42.w;
    return localLong4;
  }
  
  public static Long4 mul(Long4 paramLong4, long paramLong)
  {
    Long4 localLong4 = new Long4();
    paramLong4.x *= paramLong;
    paramLong4.y *= paramLong;
    paramLong4.z *= paramLong;
    paramLong4.w *= paramLong;
    return localLong4;
  }
  
  public static Long4 mul(Long4 paramLong41, Long4 paramLong42)
  {
    Long4 localLong4 = new Long4();
    paramLong41.x *= paramLong42.x;
    paramLong41.y *= paramLong42.y;
    paramLong41.z *= paramLong42.z;
    paramLong41.w *= paramLong42.w;
    return localLong4;
  }
  
  public static Long4 sub(Long4 paramLong4, long paramLong)
  {
    Long4 localLong4 = new Long4();
    paramLong4.x -= paramLong;
    paramLong4.y -= paramLong;
    paramLong4.z -= paramLong;
    paramLong4.w -= paramLong;
    return localLong4;
  }
  
  public static Long4 sub(Long4 paramLong41, Long4 paramLong42)
  {
    Long4 localLong4 = new Long4();
    paramLong41.x -= paramLong42.x;
    paramLong41.y -= paramLong42.y;
    paramLong41.z -= paramLong42.z;
    paramLong41.w -= paramLong42.w;
    return localLong4;
  }
  
  public void add(long paramLong)
  {
    this.x += paramLong;
    this.y += paramLong;
    this.z += paramLong;
    this.w += paramLong;
  }
  
  public void add(Long4 paramLong4)
  {
    this.x += paramLong4.x;
    this.y += paramLong4.y;
    this.z += paramLong4.z;
    this.w += paramLong4.w;
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
    case 2: 
      this.z += paramLong;
      return;
    }
    this.w += paramLong;
  }
  
  public void addMultiple(Long4 paramLong4, long paramLong)
  {
    this.x += paramLong4.x * paramLong;
    this.y += paramLong4.y * paramLong;
    this.z += paramLong4.z * paramLong;
    this.w += paramLong4.w * paramLong;
  }
  
  public void copyTo(long[] paramArrayOfLong, int paramInt)
  {
    paramArrayOfLong[paramInt] = this.x;
    paramArrayOfLong[(paramInt + 1)] = this.y;
    paramArrayOfLong[(paramInt + 2)] = this.z;
    paramArrayOfLong[(paramInt + 3)] = this.w;
  }
  
  public void div(long paramLong)
  {
    this.x /= paramLong;
    this.y /= paramLong;
    this.z /= paramLong;
    this.w /= paramLong;
  }
  
  public void div(Long4 paramLong4)
  {
    this.x /= paramLong4.x;
    this.y /= paramLong4.y;
    this.z /= paramLong4.z;
    this.w /= paramLong4.w;
  }
  
  public long dotProduct(Long4 paramLong4)
  {
    return this.x * paramLong4.x + this.y * paramLong4.y + this.z * paramLong4.z + this.w * paramLong4.w;
  }
  
  public long elementSum()
  {
    return this.x + this.y + this.z + this.w;
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
    case 2: 
      return this.z;
    }
    return this.w;
  }
  
  public long length()
  {
    return 4L;
  }
  
  public void mod(long paramLong)
  {
    this.x %= paramLong;
    this.y %= paramLong;
    this.z %= paramLong;
    this.w %= paramLong;
  }
  
  public void mod(Long4 paramLong4)
  {
    this.x %= paramLong4.x;
    this.y %= paramLong4.y;
    this.z %= paramLong4.z;
    this.w %= paramLong4.w;
  }
  
  public void mul(long paramLong)
  {
    this.x *= paramLong;
    this.y *= paramLong;
    this.z *= paramLong;
    this.w *= paramLong;
  }
  
  public void mul(Long4 paramLong4)
  {
    this.x *= paramLong4.x;
    this.y *= paramLong4.y;
    this.z *= paramLong4.z;
    this.w *= paramLong4.w;
  }
  
  public void negate()
  {
    this.x = (-this.x);
    this.y = (-this.y);
    this.z = (-this.z);
    this.w = (-this.w);
  }
  
  public void set(Long4 paramLong4)
  {
    this.x = paramLong4.x;
    this.y = paramLong4.y;
    this.z = paramLong4.z;
    this.w = paramLong4.w;
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
    case 2: 
      this.z = paramLong;
      return;
    }
    this.w = paramLong;
  }
  
  public void setValues(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.x = paramLong1;
    this.y = paramLong2;
    this.z = paramLong3;
    this.w = paramLong4;
  }
  
  public void sub(long paramLong)
  {
    this.x -= paramLong;
    this.y -= paramLong;
    this.z -= paramLong;
    this.w -= paramLong;
  }
  
  public void sub(Long4 paramLong4)
  {
    this.x -= paramLong4.x;
    this.y -= paramLong4.y;
    this.z -= paramLong4.z;
    this.w -= paramLong4.w;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Long4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */