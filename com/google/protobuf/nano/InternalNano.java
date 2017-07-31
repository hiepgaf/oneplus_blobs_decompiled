package com.google.protobuf.nano;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class InternalNano
{
  public static final Object LAZY_INIT_LOCK = new Object();
  
  public static byte[] bytesDefaultValue(String paramString)
  {
    try
    {
      paramString = paramString.getBytes("ISO-8859-1");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new IllegalStateException("Java VM does not support a standard character set.", paramString);
    }
  }
  
  public static void cloneUnknownFieldData(ExtendableMessageNano paramExtendableMessageNano1, ExtendableMessageNano paramExtendableMessageNano2)
  {
    if (paramExtendableMessageNano1.unknownFieldData != null) {
      paramExtendableMessageNano2.unknownFieldData = paramExtendableMessageNano1.unknownFieldData.clone();
    }
  }
  
  public static byte[] copyFromUtf8(String paramString)
  {
    try
    {
      paramString = paramString.getBytes("UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new RuntimeException("UTF-8 not supported?");
    }
  }
  
  public static boolean equals(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    if ((paramArrayOfDouble1 == null) || (paramArrayOfDouble1.length == 0)) {
      return (paramArrayOfDouble2 == null) || (paramArrayOfDouble2.length == 0);
    }
    return Arrays.equals(paramArrayOfDouble1, paramArrayOfDouble2);
  }
  
  public static boolean equals(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if ((paramArrayOfFloat1 == null) || (paramArrayOfFloat1.length == 0)) {
      return (paramArrayOfFloat2 == null) || (paramArrayOfFloat2.length == 0);
    }
    return Arrays.equals(paramArrayOfFloat1, paramArrayOfFloat2);
  }
  
  public static boolean equals(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((paramArrayOfInt1 == null) || (paramArrayOfInt1.length == 0)) {
      return (paramArrayOfInt2 == null) || (paramArrayOfInt2.length == 0);
    }
    return Arrays.equals(paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public static boolean equals(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if ((paramArrayOfLong1 == null) || (paramArrayOfLong1.length == 0)) {
      return (paramArrayOfLong2 == null) || (paramArrayOfLong2.length == 0);
    }
    return Arrays.equals(paramArrayOfLong1, paramArrayOfLong2);
  }
  
  public static boolean equals(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    int i = 0;
    int k;
    int j;
    int m;
    if (paramArrayOfObject1 == null)
    {
      k = 0;
      j = 0;
      if (paramArrayOfObject2 != null) {
        break label50;
      }
      m = 0;
    }
    for (;;)
    {
      int n = j;
      if (i < k)
      {
        n = j;
        if (paramArrayOfObject1[i] == null)
        {
          i += 1;
          continue;
          k = paramArrayOfObject1.length;
          break;
          label50:
          m = paramArrayOfObject2.length;
          continue;
        }
      }
      while ((n < m) && (paramArrayOfObject2[n] == null)) {
        n += 1;
      }
      if (i >= k)
      {
        j = 1;
        if (n < m) {
          break label114;
        }
      }
      label114:
      for (int i1 = 1;; i1 = 0)
      {
        if ((j == 0) || (i1 == 0)) {
          break label120;
        }
        return true;
        j = 0;
        break;
      }
      label120:
      if (j != i1) {
        return false;
      }
      if (!paramArrayOfObject1[i].equals(paramArrayOfObject2[n])) {
        return false;
      }
      i += 1;
      j = n + 1;
    }
  }
  
  public static boolean equals(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    if ((paramArrayOfBoolean1 == null) || (paramArrayOfBoolean1.length == 0)) {
      return (paramArrayOfBoolean2 == null) || (paramArrayOfBoolean2.length == 0);
    }
    return Arrays.equals(paramArrayOfBoolean1, paramArrayOfBoolean2);
  }
  
  public static boolean equals(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2)
  {
    int i = 0;
    int k;
    int j;
    int m;
    if (paramArrayOfByte1 == null)
    {
      k = 0;
      j = 0;
      if (paramArrayOfByte2 != null) {
        break label50;
      }
      m = 0;
    }
    for (;;)
    {
      int n = j;
      if (i < k)
      {
        n = j;
        if (paramArrayOfByte1[i] == null)
        {
          i += 1;
          continue;
          k = paramArrayOfByte1.length;
          break;
          label50:
          m = paramArrayOfByte2.length;
          continue;
        }
      }
      while ((n < m) && (paramArrayOfByte2[n] == null)) {
        n += 1;
      }
      if (i >= k)
      {
        j = 1;
        if (n < m) {
          break label114;
        }
      }
      label114:
      for (int i1 = 1;; i1 = 0)
      {
        if ((j == 0) || (i1 == 0)) {
          break label120;
        }
        return true;
        j = 0;
        break;
      }
      label120:
      if (j != i1) {
        return false;
      }
      if (!Arrays.equals(paramArrayOfByte1[i], paramArrayOfByte2[n])) {
        return false;
      }
      i += 1;
      j = n + 1;
    }
  }
  
  public static int hashCode(double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDouble == null) || (paramArrayOfDouble.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfDouble);
  }
  
  public static int hashCode(float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat == null) || (paramArrayOfFloat.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfFloat);
  }
  
  public static int hashCode(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfInt);
  }
  
  public static int hashCode(long[] paramArrayOfLong)
  {
    if ((paramArrayOfLong == null) || (paramArrayOfLong.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfLong);
  }
  
  public static int hashCode(Object[] paramArrayOfObject)
  {
    int k = 0;
    int j = 0;
    int i;
    if (paramArrayOfObject == null) {
      i = 0;
    }
    while (j < i)
    {
      Object localObject = paramArrayOfObject[j];
      int m = k;
      if (localObject != null) {
        m = k * 31 + localObject.hashCode();
      }
      j += 1;
      k = m;
      continue;
      i = paramArrayOfObject.length;
    }
    return k;
  }
  
  public static int hashCode(boolean[] paramArrayOfBoolean)
  {
    if ((paramArrayOfBoolean == null) || (paramArrayOfBoolean.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfBoolean);
  }
  
  public static int hashCode(byte[][] paramArrayOfByte)
  {
    int k = 0;
    int j = 0;
    int i;
    if (paramArrayOfByte == null) {
      i = 0;
    }
    while (j < i)
    {
      byte[] arrayOfByte = paramArrayOfByte[j];
      int m = k;
      if (arrayOfByte != null) {
        m = k * 31 + Arrays.hashCode(arrayOfByte);
      }
      j += 1;
      k = m;
      continue;
      i = paramArrayOfByte.length;
    }
    return k;
  }
  
  public static String stringDefaultValue(String paramString)
  {
    try
    {
      paramString = new String(paramString.getBytes("ISO-8859-1"), "UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new IllegalStateException("Java VM does not support a standard character set.", paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/google/protobuf/nano/InternalNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */