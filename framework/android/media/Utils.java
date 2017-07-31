package android.media;

import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

class Utils
{
  private static final String TAG = "Utils";
  
  static Range<Integer> alignRange(Range<Integer> paramRange, int paramInt)
  {
    return paramRange.intersect(Integer.valueOf(divUp(((Integer)paramRange.getLower()).intValue(), paramInt) * paramInt), Integer.valueOf(((Integer)paramRange.getUpper()).intValue() / paramInt * paramInt));
  }
  
  public static <T extends Comparable<? super T>> int binarySearchDistinctRanges(Range<T>[] paramArrayOfRange, T paramT)
  {
    Arrays.binarySearch(paramArrayOfRange, Range.create(paramT, paramT), new Comparator()
    {
      public int compare(Range<T> paramAnonymousRange1, Range<T> paramAnonymousRange2)
      {
        if (paramAnonymousRange1.getUpper().compareTo(paramAnonymousRange2.getLower()) < 0) {
          return -1;
        }
        if (paramAnonymousRange1.getLower().compareTo(paramAnonymousRange2.getUpper()) > 0) {
          return 1;
        }
        return 0;
      }
    });
  }
  
  static int divUp(int paramInt1, int paramInt2)
  {
    return (paramInt1 + paramInt2 - 1) / paramInt2;
  }
  
  static long divUp(long paramLong1, long paramLong2)
  {
    return (paramLong1 + paramLong2 - 1L) / paramLong2;
  }
  
  static Range<Integer> factorRange(Range<Integer> paramRange, int paramInt)
  {
    if (paramInt == 1) {
      return paramRange;
    }
    return Range.create(Integer.valueOf(divUp(((Integer)paramRange.getLower()).intValue(), paramInt)), Integer.valueOf(((Integer)paramRange.getUpper()).intValue() / paramInt));
  }
  
  static Range<Long> factorRange(Range<Long> paramRange, long paramLong)
  {
    if (paramLong == 1L) {
      return paramRange;
    }
    return Range.create(Long.valueOf(divUp(((Long)paramRange.getLower()).longValue(), paramLong)), Long.valueOf(((Long)paramRange.getUpper()).longValue() / paramLong));
  }
  
  static int gcd(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return 1;
    }
    int i = paramInt2;
    if (paramInt2 < 0) {
      i = -paramInt2;
    }
    paramInt2 = paramInt1;
    int j = i;
    if (paramInt1 < 0)
    {
      paramInt2 = -paramInt1;
      j = i;
    }
    for (;;)
    {
      paramInt1 = j;
      if (paramInt2 == 0) {
        break;
      }
      j = paramInt2;
      paramInt2 = paramInt1 % paramInt2;
    }
    return paramInt1;
  }
  
  static Range<Integer> intRangeFor(double paramDouble)
  {
    return Range.create(Integer.valueOf((int)paramDouble), Integer.valueOf((int)Math.ceil(paramDouble)));
  }
  
  public static <T extends Comparable<? super T>> Range<T>[] intersectSortedDistinctRanges(Range<T>[] paramArrayOfRange1, Range<T>[] paramArrayOfRange2)
  {
    int k = 0;
    int i = 0;
    Vector localVector = new Vector();
    int m = paramArrayOfRange2.length;
    for (;;)
    {
      Range<T> localRange;
      if (k < m)
      {
        localRange = paramArrayOfRange2[k];
        int j = i;
        for (;;)
        {
          i = j;
          if (j >= paramArrayOfRange1.length) {
            break;
          }
          i = j;
          if (paramArrayOfRange1[j].getUpper().compareTo(localRange.getLower()) >= 0) {
            break;
          }
          j += 1;
        }
        while ((i < paramArrayOfRange1.length) && (paramArrayOfRange1[i].getUpper().compareTo(localRange.getUpper()) < 0))
        {
          localVector.add(localRange.intersect(paramArrayOfRange1[i]));
          i += 1;
        }
        if (i != paramArrayOfRange1.length) {}
      }
      else
      {
        return (Range[])localVector.toArray(new Range[localVector.size()]);
      }
      if (paramArrayOfRange1[i].getLower().compareTo(localRange.getUpper()) <= 0) {
        localVector.add(localRange.intersect(paramArrayOfRange1[i]));
      }
      k += 1;
    }
  }
  
  private static long lcm(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      throw new IllegalArgumentException("lce is not defined for zero arguments");
    }
    return paramInt1 * paramInt2 / gcd(paramInt1, paramInt2);
  }
  
  static Range<Long> longRangeFor(double paramDouble)
  {
    return Range.create(Long.valueOf(paramDouble), Long.valueOf(Math.ceil(paramDouble)));
  }
  
  static Range<Integer> parseIntRange(Object paramObject, Range<Integer> paramRange)
  {
    try
    {
      Object localObject = (String)paramObject;
      int i = ((String)localObject).indexOf('-');
      if (i >= 0) {
        return Range.create(Integer.valueOf(Integer.parseInt(((String)localObject).substring(0, i), 10)), Integer.valueOf(Integer.parseInt(((String)localObject).substring(i + 1), 10)));
      }
      i = Integer.parseInt((String)localObject);
      localObject = Range.create(Integer.valueOf(i), Integer.valueOf(i));
      return (Range<Integer>)localObject;
    }
    catch (NullPointerException paramObject)
    {
      return paramRange;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse integer range '" + paramObject + "'");
      return paramRange;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  static int parseIntSafely(Object paramObject, int paramInt)
  {
    if (paramObject == null) {
      return paramInt;
    }
    try
    {
      int i = Integer.parseInt((String)paramObject);
      return i;
    }
    catch (NullPointerException paramObject)
    {
      return paramInt;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse integer '" + paramObject + "'");
      return paramInt;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
  }
  
  static Range<Long> parseLongRange(Object paramObject, Range<Long> paramRange)
  {
    try
    {
      Object localObject = (String)paramObject;
      int i = ((String)localObject).indexOf('-');
      if (i >= 0) {
        return Range.create(Long.valueOf(Long.parseLong(((String)localObject).substring(0, i), 10)), Long.valueOf(Long.parseLong(((String)localObject).substring(i + 1), 10)));
      }
      long l = Long.parseLong((String)localObject);
      localObject = Range.create(Long.valueOf(l), Long.valueOf(l));
      return (Range<Long>)localObject;
    }
    catch (NullPointerException paramObject)
    {
      return paramRange;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse long range '" + paramObject + "'");
      return paramRange;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  static Range<Rational> parseRationalRange(Object paramObject, Range<Rational> paramRange)
  {
    try
    {
      Object localObject = (String)paramObject;
      int i = ((String)localObject).indexOf('-');
      if (i >= 0) {
        return Range.create(Rational.parseRational(((String)localObject).substring(0, i)), Rational.parseRational(((String)localObject).substring(i + 1)));
      }
      localObject = Rational.parseRational((String)localObject);
      localObject = Range.create((Comparable)localObject, (Comparable)localObject);
      return (Range<Rational>)localObject;
    }
    catch (NullPointerException paramObject)
    {
      return paramRange;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse rational range '" + paramObject + "'");
      return paramRange;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  static Size parseSize(Object paramObject, Size paramSize)
  {
    try
    {
      Size localSize = Size.parseSize((String)paramObject);
      return localSize;
    }
    catch (NullPointerException paramObject)
    {
      return paramSize;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse size '" + paramObject + "'");
      return paramSize;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
  }
  
  static Pair<Size, Size> parseSizeRange(Object paramObject)
  {
    try
    {
      Object localObject = (String)paramObject;
      int i = ((String)localObject).indexOf('-');
      if (i >= 0) {
        return Pair.create(Size.parseSize(((String)localObject).substring(0, i)), Size.parseSize(((String)localObject).substring(i + 1)));
      }
      localObject = Size.parseSize((String)localObject);
      localObject = Pair.create(localObject, localObject);
      return (Pair<Size, Size>)localObject;
    }
    catch (NullPointerException paramObject)
    {
      return null;
    }
    catch (ClassCastException localClassCastException)
    {
      Log.w("Utils", "could not parse size range '" + paramObject + "'");
      return null;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  static Range<Rational> scaleRange(Range<Rational> paramRange, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return paramRange;
    }
    return Range.create(scaleRatio((Rational)paramRange.getLower(), paramInt1, paramInt2), scaleRatio((Rational)paramRange.getUpper(), paramInt1, paramInt2));
  }
  
  private static Rational scaleRatio(Rational paramRational, int paramInt1, int paramInt2)
  {
    int i = gcd(paramInt1, paramInt2);
    paramInt1 /= i;
    paramInt2 /= i;
    return new Rational((int)(paramRational.getNumerator() * paramInt1), (int)(paramRational.getDenominator() * paramInt2));
  }
  
  public static <T extends Comparable<? super T>> void sortDistinctRanges(Range<T>[] paramArrayOfRange)
  {
    Arrays.sort(paramArrayOfRange, new Comparator()
    {
      public int compare(Range<T> paramAnonymousRange1, Range<T> paramAnonymousRange2)
      {
        if (paramAnonymousRange1.getUpper().compareTo(paramAnonymousRange2.getLower()) < 0) {
          return -1;
        }
        if (paramAnonymousRange1.getLower().compareTo(paramAnonymousRange2.getUpper()) > 0) {
          return 1;
        }
        throw new IllegalArgumentException("sample rate ranges must be distinct (" + paramAnonymousRange1 + " and " + paramAnonymousRange2 + ")");
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */