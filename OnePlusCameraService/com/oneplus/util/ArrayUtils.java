package com.oneplus.util;

public class ArrayUtils
{
  public static boolean contains(long[] paramArrayOfLong, long paramLong)
  {
    return indexOf(paramArrayOfLong, paramLong) != -1;
  }
  
  public static <T> boolean contains(T[] paramArrayOfT, T paramT)
  {
    return indexOf(paramArrayOfT, paramT) != -1;
  }
  
  public static int indexOf(long[] paramArrayOfLong, long paramLong)
  {
    if (paramArrayOfLong == null) {
      return -1;
    }
    int i = 0;
    while (i < paramArrayOfLong.length)
    {
      if (paramArrayOfLong[i] == paramLong) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static <T> int indexOf(T[] paramArrayOfT, T paramT)
  {
    if (paramArrayOfT == null) {
      return -1;
    }
    int i = 0;
    while (i < paramArrayOfT.length)
    {
      if (((paramArrayOfT[i] != null) && (paramArrayOfT[i].equals(paramT))) || ((paramArrayOfT[i] == null) && (paramT == null))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */