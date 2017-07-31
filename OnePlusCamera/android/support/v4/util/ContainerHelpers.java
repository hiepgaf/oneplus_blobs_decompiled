package android.support.v4.util;

class ContainerHelpers
{
  static final int[] EMPTY_INTS = new int[0];
  static final long[] EMPTY_LONGS = new long[0];
  static final Object[] EMPTY_OBJECTS = new Object[0];
  
  static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramInt1 - 1;
    paramInt1 = i;
    i = j;
    for (;;)
    {
      if (paramInt1 > i) {
        return paramInt1 ^ 0xFFFFFFFF;
      }
      j = paramInt1 + i >>> 1;
      int k = paramArrayOfInt[j];
      if (k >= paramInt2)
      {
        if (k <= paramInt2) {
          return j;
        }
      }
      else
      {
        paramInt1 = j + 1;
        continue;
      }
      i = j - 1;
    }
  }
  
  static int binarySearch(long[] paramArrayOfLong, int paramInt, long paramLong)
  {
    int i = paramInt - 1;
    paramInt = 0;
    if (paramInt > i) {
      return paramInt ^ 0xFFFFFFFF;
    }
    int k = paramInt + i >>> 1;
    long l = paramArrayOfLong[k];
    if (l >= paramLong) {}
    for (int j = 1;; j = 0)
    {
      if (j != 0) {
        break label60;
      }
      paramInt = k + 1;
      break;
    }
    label60:
    if (l <= paramLong) {}
    for (i = 1;; i = 0)
    {
      if (i != 0) {
        break label90;
      }
      i = k - 1;
      break;
    }
    label90:
    return k;
  }
  
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    boolean bool = false;
    if (paramObject1 == paramObject2) {}
    do
    {
      bool = true;
      do
      {
        return bool;
      } while (paramObject1 == null);
    } while (paramObject1.equals(paramObject2));
    return false;
  }
  
  public static int idealByteArraySize(int paramInt)
  {
    int i = 4;
    for (;;)
    {
      if (i >= 32) {
        return paramInt;
      }
      if (paramInt <= (1 << i) - 12) {
        break;
      }
      i += 1;
    }
    return (1 << i) - 12;
  }
  
  public static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/ContainerHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */