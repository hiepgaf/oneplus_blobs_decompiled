package android.app.usage;

import android.util.LongSparseArray;

public class TimeSparseArray<E>
  extends LongSparseArray<E>
{
  public TimeSparseArray() {}
  
  public TimeSparseArray(int paramInt)
  {
    super(paramInt);
  }
  
  public int closestIndexOnOrAfter(long paramLong)
  {
    int m = size();
    int i = 0;
    int j = m - 1;
    int k = -1;
    long l = -1L;
    while (i <= j)
    {
      k = i + (j - i) / 2;
      l = keyAt(k);
      if (paramLong > l) {
        i = k + 1;
      } else if (paramLong < l) {
        j = k - 1;
      } else {
        return k;
      }
    }
    if (paramLong < l) {
      return k;
    }
    if ((paramLong > l) && (i < m)) {
      return i;
    }
    return -1;
  }
  
  public int closestIndexOnOrBefore(long paramLong)
  {
    int i = closestIndexOnOrAfter(paramLong);
    if (i < 0) {
      return size() - 1;
    }
    if (keyAt(i) == paramLong) {
      return i;
    }
    return i - 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/TimeSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */